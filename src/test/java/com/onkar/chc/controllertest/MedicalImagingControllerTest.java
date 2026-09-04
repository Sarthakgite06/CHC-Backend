package com.onkar.chc.controllertest;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalImagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Test
    public void testUploadImagingUnauthorized() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "scan.pdf", "application/pdf", "dummy".getBytes());

        mockMvc.perform(multipart("/medical-imaging/upload")
                .file(file)
                .param("healthCardNo", "PUN00000001")
                .param("imagingType", "MRI")
                .param("title", "Brain MRI")
                .param("hospitalName", "City Hospital"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUploadImagingForbiddenForNonDoctor() throws Exception {
        UserEntity patient = UserEntity.builder()
                .userName("patient_user")
                .role("Patient")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                patient,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_Patient"))
        );

        MockMultipartFile file = new MockMultipartFile("file", "scan.pdf", "application/pdf", "dummy".getBytes());

        mockMvc.perform(multipart("/medical-imaging/upload")
                .file(file)
                .param("healthCardNo", "PUN00000001")
                .param("imagingType", "MRI")
                .param("title", "Brain MRI")
                .param("hospitalName", "City Hospital")
                .with(authentication(auth)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUploadImagingUnsupportedExtension() throws Exception {
        UserEntity doctor = UserEntity.builder()
                .userName("dr_smith")
                .role("Doctor")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                doctor,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_Doctor"))
        );

        // Create dummy patient in DB for lookup
        UserEntity patient = UserEntity.builder()
                .userName("imaging_patient")
                .firstName("Imaging")
                .lastName("Patient")
                .password("Pass1234")
                .healthCardNo("PUN99999999")
                .role("Patient")
                .build();
        userRepo.save(patient);

        MockMultipartFile file = new MockMultipartFile("file", "virus.exe", "application/octet-stream", "bad".getBytes());

        mockMvc.perform(multipart("/medical-imaging/upload")
                .file(file)
                .param("healthCardNo", "PUN99999999")
                .param("imagingType", "MRI")
                .param("title", "Brain MRI")
                .param("hospitalName", "City Hospital")
                .with(authentication(auth)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetPatientImagingsUnauthorized() throws Exception {
        mockMvc.perform(get("/medical-imaging/patient/PUN00000001"))
                .andExpect(status().isUnauthorized());
    }
}
