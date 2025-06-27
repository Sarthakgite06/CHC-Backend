package com.onkar.chc.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequestDTO {

    @NotNull
    private Long doctorRegNo;

    private List<MedicineInfoRequestDTO> medicineInfoEntities;

    private PatientRequestDTO patientEntity;
}
