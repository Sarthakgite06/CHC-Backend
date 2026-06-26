package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LAB_REPORTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAB_TEST_REQUEST_ID", nullable = false, unique = true)
    private LabTestRequestEntity labTestRequest;

    @Column(name = "PATHOLOGIST_USER_NAME", nullable = false)
    private String pathologistUserName;

    @Column(name = "FINDINGS", columnDefinition = "TEXT")
    private String findings;

    @Column(name = "REMARKS", columnDefinition = "TEXT")
    private String remarks;

    // The name of the file saved on the local disk
    @Column(name = "ATTACHMENT_PATH")
    private String attachmentPath;

    @Column(name = "ATTACHMENT_ORIGINAL_NAME")
    private String attachmentOriginalName;

    @Builder.Default
    @Column(name = "REPORT_DATE")
    private LocalDateTime reportDate = LocalDateTime.now();
}
