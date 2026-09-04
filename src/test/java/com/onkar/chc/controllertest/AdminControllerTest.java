package com.onkar.chc.controllertest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}
