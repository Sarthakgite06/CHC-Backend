package com.onkar.chc.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    private String message;
    private String targetHealthCardId; // Used by doctor to specify which patient they are asking about
}
