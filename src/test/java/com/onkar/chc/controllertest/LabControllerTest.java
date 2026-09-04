package com.onkar.chc.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkar.chc.entity.LabTestRequestEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.LabTestRequestRepo;
import com.onkar.chc.requestDto.LabTestRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabTestRequestRepo labTestRequestRepo;

    @Test
    public void testRequestTestUnauthorized() throws Exception {
        LabTestRequestDTO dto = LabTestRequestDTO.builder()
                .patientHealthCardId("PUN00000001")
                .doctorUserName("dr_smith")
                .testName("Blood Glucose")
                .build();

        mockMvc.perform(post("/lab/requestTest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRequestTestSuccess() throws Exception {
        UserEntity doctor = UserEntity.builder()
                .userName("dr_smith")
                .role("Doctor")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                doctor,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_Doctor"))
        );

        LabTestRequestDTO dto = LabTestRequestDTO.builder()
                .patientHealthCardId("PUN00000001")
                .doctorUserName("dr_smith")
                .testName("Complete Blood Count (CBC)")
                .notes("Fasting required")
                .build();

        mockMvc.perform(post("/lab/requestTest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(authentication(auth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.testName").value("Complete Blood Count (CBC)"));
    }

    @Test
    public void testGetReportsForPatientUnauthorized() throws Exception {
        mockMvc.perform(get("/lab/patient/PUN00000001/reports"))
                .andExpect(status().isUnauthorized());
    }
}
