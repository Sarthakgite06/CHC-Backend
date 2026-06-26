package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FEEDBACK_TICKETS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FEEDBACK_ID")
    private String feedbackId;

    @Column(name = "HEALTH_CARD_ID", nullable = false)
    private String healthCardNo;

    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @Column(name = "SUBJECT", nullable = false)
    private String subject;

    @Column(name = "MESSAGE", nullable = false, length = 2000)
    private String message;

    // OPEN, IN_PROGRESS, RESOLVED
    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "ADMIN_RESPONSE", length = 2000)
    private String adminResponse;

    @Column(name = "CREATED_DATE", nullable = false)
    private String createdDate;

    @Column(name = "RESOLVED_DATE")
    private String resolvedDate;
}
