package com.onkar.chc.responseDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponseDTO {

    private String medicalRecordId;

    private String createdDate;

    private Long doctorRegNo;

    private List<MedicineInfoResponseDTO> medicineInfoEntities;

}
