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

    private String fileName;

    private String fileUrl;

    private String fileType;

    private Long fileSize;

    private String imagingType;

    private String title;

    private String description;

    private String hospitalName;
}
