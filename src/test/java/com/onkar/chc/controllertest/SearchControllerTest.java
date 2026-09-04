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
@SuppressWarnings("null")
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDistrictsPublic() throws Exception {
        mockMvc.perform(get("/chc/districts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Pune").value("PUN"))
                .andExpect(jsonPath("$.Mumbai").value("MUM"));
    }

    @Test
    public void testSearchUsersUnauthorized() throws Exception {
        mockMvc.perform(get("/chc/search-users")
                .param("q", "onkar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"Patient"})
    public void testSearchUsersShortQueryReturnsEmpty() throws Exception {
        mockMvc.perform(get("/chc/search-users")
                .param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"Patient"})
    public void testSearchUsersValidQuery() throws Exception {
        mockMvc.perform(get("/chc/search-users")
                .param("q", "admin"))
                .andExpect(status().isOk());
    }
}
