package com.onkar.chc.service;

import com.onkar.chc.entity.ChatHistoryEntity;
import com.onkar.chc.requestDto.ChatRequestDTO;
import com.onkar.chc.responseDto.ChatResponseDTO;

import java.util.List;

public interface ChatService {
    ChatResponseDTO chat(String userName, ChatRequestDTO request);
    List<ChatHistoryEntity> getHistory(String userName);
    void clearHistory(String userName);
}
