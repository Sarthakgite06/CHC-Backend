package com.onkar.chc.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkar.chc.requestDto.UserRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignUpValidationFailure() throws Exception {
        UserRequestDTO invalidUser = new UserRequestDTO(); // missing required fields

        mockMvc.perform(post("/user/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testSignUpSuccess() throws Exception {
        UserRequestDTO validUser = UserRequestDTO.builder()
                .userName("john_doe_test")
                .firstName("John")
                .lastName("Doe")
                .password("Password123")
                .email("johndoe@example.com")
                .contactNo(9876543210L)
                .district("Pune")
                .role("Patient")
                .dob("1995-05-15")
                .address("123 Test St, Pune")
                .gender("Male")
                .bloodGroup("O+")
                .build();

        mockMvc.perform(post("/user/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").isNotEmpty());
    }

    @Test
    public void testLoginEndpointPublic() throws Exception {
        mockMvc.perform(get("/user/login")
                .param("userName", "nonexistent")
                .param("password", "dummy"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPersonalInfoUnauthorized() throws Exception {
        mockMvc.perform(get("/user/getPersonalInfo")
                .param("id", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin07", roles = {"Admin"})
    public void testGetDoctorRegNoNotFound() throws Exception {
        mockMvc.perform(get("/user/getDoctorRegNo")
                .param("userName", "non_doctor_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUserDataUnauthorized() throws Exception {
        UserRequestDTO userRequest = new UserRequestDTO();
        mockMvc.perform(put("/user/updateUserData")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized());
    }
}
