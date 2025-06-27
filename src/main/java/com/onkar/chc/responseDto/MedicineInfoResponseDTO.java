package com.onkar.chc.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineInfoResponseDTO {

    private String medicineInfoId;

    private String medicineName;

    private DosageResponseDTO dosageEntity;

    private Integer days;

    private String remark;
}

