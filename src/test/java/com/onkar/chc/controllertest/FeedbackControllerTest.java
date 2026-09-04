package com.onkar.chc.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @BeforeEach
    public void setUp() {
        if (userRepo.findByUserName("feedback_user").isEmpty()) {
            userRepo.save(UserEntity.builder()
                    .userName("feedback_user")
                    .firstName("Feedback")
                    .lastName("Tester")
                    .password("Pass1234")
                    .role("Patient")
                    .healthCardNo("PUN00000099")
                    .build());
        }
    }

    @Test
    public void testSubmitFeedbackUnauthorized() throws Exception {
        Map<String, String> feedback = Map.of("subject", "Issue", "message", "Test message");

        mockMvc.perform(post("/feedback/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "feedback_user", roles = {"Patient"})
    public void testSubmitFeedbackSuccess() throws Exception {
        Map<String, String> feedback = Map.of(
                "subject", "App Review",
                "message", "Great experience using CHC portal"
        );

        mockMvc.perform(post("/feedback/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("Feedback submitted successfully"));
    }

    @Test
    @WithMockUser(username = "feedback_user", roles = {"Patient"})
    public void testGetMyFeedbacksSuccess() throws Exception {
        mockMvc.perform(get("/feedback/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "feedback_user", roles = {"Patient"})
    public void testGetDoctorFeedbacksForbiddenForPatient() throws Exception {
        mockMvc.perform(get("/feedback/doctor"))
                .andExpect(status().isForbidden());
    }
}
