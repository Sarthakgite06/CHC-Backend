package com.onkar.chc.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAdminStatsUnauthenticated() throws Exception {
        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"Patient"})
    public void testAdminStatsForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testAdminStatsSuccess() throws Exception {
        mockMvc.perform(get("/admin/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMembers").exists())
                .andExpect(jsonPath("$.totalRegistered").exists())
                .andExpect(jsonPath("$.doctors").exists());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testDistrictStatsSuccess() throws Exception {
        mockMvc.perform(get("/admin/district-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testRoleStatsSuccess() throws Exception {
        mockMvc.perform(get("/admin/role-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testGetAllUsersSuccess() throws Exception {
        mockMvc.perform(get("/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testVerifyDoctorNotFound() throws Exception {
        mockMvc.perform(get("/admin/verify-doctor/99999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false));
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testVerifyChemistNotFound() throws Exception {
        mockMvc.perform(get("/admin/verify-chemist/99999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false));
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testGetAllFeedbacksSuccess() throws Exception {
        mockMvc.perform(get("/admin/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testRespondToFeedbackNotFound() throws Exception {
        Map<String, String> body = Map.of("response", "Reviewed", "status", "RESOLVED");
        mockMvc.perform(put("/admin/feedbacks/non-existent-id/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("Feedback not found"));
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testCreateAdminMemberMissingFields() throws Exception {
        Map<String, String> body = Map.of("userName", "new_admin");
        mockMvc.perform(post("/admin/create-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("userName, email and password are required"));
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testCreateAdminMemberSuccess() throws Exception {
        String uniqueAdmin = "admin_member_" + System.currentTimeMillis();
        Map<String, String> body = Map.of(
                "userName", uniqueAdmin,
                "email", uniqueAdmin + "@example.com",
                "password", "AdminPass123",
                "firstName", "Admin",
                "lastName", "Member"
        );
        mockMvc.perform(post("/admin/create-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("Team member " + uniqueAdmin + " added successfully"));
    }
}
