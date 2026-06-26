package com.onkar.chc.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabReportRequestDTO {

    @NotNull(message = "Lab Test Request ID is required")
    private Long labTestRequestId;

    @NotBlank(message = "Pathologist username is required")
    private String pathologistUserName;

    @NotBlank(message = "Findings are required")
    private String findings;

    private String remarks;
    
    // File upload handled separately or as part of a multipart form
}
