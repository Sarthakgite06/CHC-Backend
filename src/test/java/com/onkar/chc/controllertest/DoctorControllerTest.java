package com.onkar.chc.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.requestDto.PatientRequestDTO;
import com.onkar.chc.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void testCreateMedicalRecordUnauthenticated() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "xray.png", "image/png", "fake".getBytes());
        mockMvc.perform(multipart("/doctor/createMedicineRecord")
                .file(file)
                .param("userName", "patient1")
                .param("healthCardNo", "PUN00000001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"Patient"})
    public void testCreateMedicalRecordForbiddenForNonDoctor() throws Exception {
        MockMultipartFile recordPart = new MockMultipartFile("record", "", "application/json", "{}".getBytes());

        mockMvc.perform(multipart("/doctor/createMedicineRecord")
                .file(recordPart)
                .param("userName", "patient1")
                .param("healthCardNo", "PUN00000001"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateMedicalRecordSuccessWithDoctorPrincipal() throws Exception {
        UserEntity doctorUser = UserEntity.builder()
                .userId(2)
                .userName("dr_smith")
                .role("Doctor")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                doctorUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_Doctor"))
        );

        MedicalRecordRequestDTO requestDTO = MedicalRecordRequestDTO.builder()
                .doctorRegNo(987654L)
                .patientEntity(PatientRequestDTO.builder().age(30).weight(70).bloodPressure("120/80").build())
                .medicineInfoEntities(new ArrayList<>())
                .build();

        MockMultipartFile recordPart = new MockMultipartFile(
                "record",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(requestDTO)
        );

        Mockito.when(medicalRecordService.validatePatientAndDoctor(anyString(), anyString(), anyLong())).thenReturn(true);
        Mockito.when(medicalRecordService.createNewMedicalRecord(any(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn("Data is saved.");

        mockMvc.perform(multipart("/doctor/createMedicineRecord")
                .file(recordPart)
                .param("userName", "patient1")
                .param("healthCardNo", "PUN00000001")
                .with(authentication(auth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("Data is saved."));
    }
}
