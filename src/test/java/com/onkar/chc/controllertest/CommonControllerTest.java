package com.onkar.chc.controllertest;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void testGetMedicalHistoryUnauthenticated() throws Exception {
        mockMvc.perform(get("/chc/getPatientMedicalHistory")
                .param("healthCardNo", "PUN00000001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetMedicalHistoryBlocksPathologist() throws Exception {
        UserEntity pathologist = UserEntity.builder()
                .userName("pathologist1")
                .role("Pathologist")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                pathologist,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_Pathologist"))
        );

        mockMvc.perform(get("/chc/getPatientMedicalHistory")
                .param("healthCardNo", "PUN00000001")
                .with(authentication(auth)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetMedicalHistorySuccessForDoctor() throws Exception {
        UserEntity doctor = UserEntity.builder()
                .userName("dr_smith")
                .role("Doctor")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                doctor,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_Doctor"))
        );

        MedicalHistoryResponseDTO responseDTO = MedicalHistoryResponseDTO.builder()
                .medicalRecordResponseDTOList(new ArrayList<>())
                .build();

        Mockito.when(medicalRecordService.validatePatient(anyString(), anyString())).thenReturn(true);
        Mockito.when(medicalRecordService.getMedicalRecord(anyString())).thenReturn(responseDTO);

        mockMvc.perform(get("/chc/getPatientMedicalHistory")
                .param("healthCardNo", "PUN00000001")
                .with(authentication(auth)))
                .andExpect(status().isAccepted());
    }
}
