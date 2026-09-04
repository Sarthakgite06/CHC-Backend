package com.onkar.chc.servicetest;

import com.onkar.chc.entity.ChatHistoryEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.*;
import com.onkar.chc.requestDto.ChatRequestDTO;
import com.onkar.chc.responseDto.ChatResponseDTO;
import com.onkar.chc.service.implementation.ChatServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatHistoryRepo chatHistoryRepo;

    @Mock
    private PatientRepo patientRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private MedicalRecordRepo medicalRecordRepo;

    @Mock
    private LabReportRepo labReportRepo;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    public void testGetHistorySuccess() {
        UserEntity user = UserEntity.builder().userName("Onkar").build();
        ChatHistoryEntity message = ChatHistoryEntity.builder()
                .id(1L)
                .user(user)
                .sender("USER")
                .message("Hello")
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(userRepo.findByUserName("Onkar")).thenReturn(Optional.of(user));
        Mockito.when(chatHistoryRepo.findByUserOrderByTimestampAsc(user)).thenReturn(List.of(message));

        List<ChatHistoryEntity> history = chatService.getHistory("Onkar");

        Assertions.assertNotNull(history);
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals("Hello", history.get(0).getMessage());
    }

    @Test
    public void testGetHistoryUserNotFound() {
        Mockito.when(userRepo.findByUserName("unknown")).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> chatService.getHistory("unknown"));
    }

    @Test
    public void testClearHistorySuccess() {
        UserEntity user = UserEntity.builder().userName("Onkar").build();
        Mockito.when(userRepo.findByUserName("Onkar")).thenReturn(Optional.of(user));

        chatService.clearHistory("Onkar");

        Mockito.verify(chatHistoryRepo, Mockito.times(1)).deleteByUser(user);
    }

    @Test
    public void testChatUserNotFound() {
        Mockito.when(userRepo.findByUserName("unknown")).thenReturn(Optional.empty());

        ChatRequestDTO request = ChatRequestDTO.builder().message("Hello").build();
        Assertions.assertThrows(DataNotFoundException.class, () -> chatService.chat("unknown", request));
    }

    @Test
    public void testChatAIFallbackOnError() {
        UserEntity user = UserEntity.builder()
                .userName("Onkar")
                .role("Patient")
                .healthCardNo("PUN00012345")
                .build();

        ChatClient mockChatClient = Mockito.mock(ChatClient.class);
        Mockito.when(userRepo.findByUserName("Onkar")).thenReturn(Optional.of(user));
        Mockito.when(chatClientBuilder.build()).thenReturn(mockChatClient);
        Mockito.when(mockChatClient.prompt()).thenThrow(new RuntimeException("AI service unavailable"));

        ChatRequestDTO request = ChatRequestDTO.builder().message("Help me find doctor").build();
        ChatResponseDTO response = chatService.chat("Onkar", request);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getReply().contains("unable to process your request"));
        Mockito.verify(chatHistoryRepo, Mockito.times(2)).save(any(ChatHistoryEntity.class));
    }
}
