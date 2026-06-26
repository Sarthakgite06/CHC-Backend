package com.onkar.chc.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestRequestDTO {

    @NotBlank(message = "Patient Health Card ID is required")
    private String patientHealthCardId;

    @NotBlank(message = "Doctor username is required")
    private String doctorUserName;

    @NotBlank(message = "Test name is required")
    private String testName;

    private String notes;
}
