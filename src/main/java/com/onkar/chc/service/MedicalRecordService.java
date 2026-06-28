package com.onkar.chc.service;

import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;

import com.onkar.chc.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MedicalRecordService {

    public Boolean validatePatientAndDoctor(String userName, String healthCardNo, Long doctorRegNo);

    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO, String healthCardNo, MultipartFile file, String imagingType, String title, String description, String hospitalName, UserEntity doctor);

    public Boolean validatePatient(String userName, String healthCardNo);

    public MedicalHistoryResponseDTO getMedicalRecord(String healthCardNo);
}
