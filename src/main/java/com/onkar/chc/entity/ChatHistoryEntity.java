package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHAT_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    @Column(name = "SENDER", nullable = false)
    private String sender; // "USER" or "AI"

    @Column(name = "MESSAGE", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "TIMESTAMP")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
