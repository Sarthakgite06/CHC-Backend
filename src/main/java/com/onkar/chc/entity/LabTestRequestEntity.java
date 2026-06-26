package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LAB_TEST_REQUESTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DOCTOR_USER_NAME", nullable = false)
    private String doctorUserName;

    @Column(name = "PATIENT_HEALTH_CARD_ID", nullable = false)
    private String patientHealthCardId;

    @Column(name = "TEST_NAME", nullable = false)
    private String testName;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Column(name = "REQUEST_DATE")
    private LocalDateTime requestDate = LocalDateTime.now();

    @Builder.Default
    @Column(name = "STATUS")
    private String status = "PENDING"; // PENDING, COMPLETED
}
