package com.onkar.chc.controller;

import com.onkar.chc.entity.ChatHistoryEntity;
import com.onkar.chc.requestDto.ChatRequestDTO;
import com.onkar.chc.responseDto.ChatResponseDTO;
import com.onkar.chc.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(
            Authentication authentication,
            @RequestBody ChatRequestDTO request) {
        String userName = authentication.getName();
        return ResponseEntity.ok(chatService.chat(userName, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryEntity>> getHistory(
            Authentication authentication) {
        String userName = authentication.getName();
        return ResponseEntity.ok(chatService.getHistory(userName));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHistory(
            Authentication authentication) {
        String userName = authentication.getName();
        chatService.clearHistory(userName);
        return ResponseEntity.ok().build();
    }
}
