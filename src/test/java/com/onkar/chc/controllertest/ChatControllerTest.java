package com.onkar.chc.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkar.chc.entity.ChatHistoryEntity;
import com.onkar.chc.requestDto.ChatRequestDTO;
import com.onkar.chc.responseDto.ChatResponseDTO;
import com.onkar.chc.service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @Test
    public void testChatUnauthorized() throws Exception {
        ChatRequestDTO request = ChatRequestDTO.builder().message("Hi").build();
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"Patient"})
    public void testChatSuccess() throws Exception {
        ChatRequestDTO request = ChatRequestDTO.builder().message("Where is my record?").build();
        ChatResponseDTO response = ChatResponseDTO.builder().reply("You can view records under /medical-history").build();

        Mockito.when(chatService.chat(eq("patient1"), any(ChatRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("You can view records under /medical-history"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"Patient"})
    public void testGetHistorySuccess() throws Exception {
        ChatHistoryEntity item = ChatHistoryEntity.builder()
                .id(1L)
                .sender("USER")
                .message("Hello")
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(chatService.getHistory("patient1")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/chat/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Hello"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"Patient"})
    public void testClearHistorySuccess() throws Exception {
        mockMvc.perform(delete("/api/chat/clear"))
                .andExpect(status().isOk());

        Mockito.verify(chatService, Mockito.times(1)).clearHistory("patient1");
    }
}
