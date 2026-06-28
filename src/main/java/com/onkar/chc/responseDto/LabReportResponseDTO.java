package com.onkar.chc.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabReportResponseDTO {
    private Long id;
    private Long labTestRequestId;
    private String patientHealthCardId;
    private String testName;
    private String reportName;
    private LocalDateTime uploadDateTime;
    private String uploadedBy;
    private String fileType;
    private String findings;
    private String remarks;

    public static LabReportResponseDTO fromEntity(com.onkar.chc.entity.LabReportEntity entity) {
        String originalName = entity.getAttachmentOriginalName();
        String name = (originalName != null && !originalName.isBlank()) ? originalName : "Lab Report";
        
        String fileType = "Image";
        if (originalName != null && originalName.toLowerCase().endsWith(".pdf")) {
            fileType = "PDF";
        }
        
        return LabReportResponseDTO.builder()
                .id(entity.getId())
                .labTestRequestId(entity.getLabTestRequest() != null ? entity.getLabTestRequest().getId() : null)
                .patientHealthCardId(entity.getLabTestRequest() != null ? entity.getLabTestRequest().getPatientHealthCardId() : null)
                .testName(entity.getLabTestRequest() != null ? entity.getLabTestRequest().getTestName() : "Unknown Test")
                .reportName(name)
                .uploadDateTime(entity.getReportDate())
                .uploadedBy(entity.getPathologistUserName())
                .fileType(fileType)
                .findings(entity.getFindings())
                .remarks(entity.getRemarks())
                .build();
    }
}
