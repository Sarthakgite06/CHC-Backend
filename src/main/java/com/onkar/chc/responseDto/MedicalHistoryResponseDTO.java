package com.onkar.chc.responseDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalHistoryResponseDTO {

    private PatientResponseDTO patientEntity;

    private List<MedicalRecordResponseDTO> medicalRecordResponseDTOList;
}
