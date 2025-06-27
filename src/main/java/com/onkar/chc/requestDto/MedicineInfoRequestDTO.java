package com.onkar.chc.requestDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineInfoRequestDTO {

    @NotBlank
    private String medicineName;

    private DosageRequestDTO dosageEntity;

    @Min(1)
    private Integer days;

    private String remark;
}
