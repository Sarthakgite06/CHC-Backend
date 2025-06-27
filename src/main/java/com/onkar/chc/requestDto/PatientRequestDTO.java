package com.onkar.chc.requestDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDTO {

    @Min(0)
    private Integer age;

    @Min(0)
    private Integer weight;

    @NotBlank
    private String bloodPressure;
}
