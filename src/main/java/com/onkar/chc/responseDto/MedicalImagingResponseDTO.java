package com.onkar.chc.responseDto;

import com.onkar.chc.entity.MedicalImagingEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalImagingResponseDTO {

    private Long id;
    private String healthCardNo;
    private String patientUserName;
    private String doctorUserName;
    private String imagingType;
    private String title;
    private String description;
    private String hospitalName;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    public static MedicalImagingResponseDTO fromEntity(MedicalImagingEntity entity) {
        if (entity == null) return null;
        return MedicalImagingResponseDTO.builder()
                .id(entity.getId())
                .healthCardNo(entity.getHealthCardNo())
                .patientUserName(entity.getPatient() != null ? entity.getPatient().getUsername() : null)
                .doctorUserName(entity.getDoctor() != null ? entity.getDoctor().getUsername() : null)
                .imagingType(entity.getImagingType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .hospitalName(entity.getHospitalName())
                .fileName(entity.getFileName())
                .fileUrl(entity.getFileUrl())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }
}
