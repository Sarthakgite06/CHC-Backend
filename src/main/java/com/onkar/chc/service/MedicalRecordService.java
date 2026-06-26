package com.onkar.chc.service;

import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;

public interface MedicalRecordService {

    public Boolean validatePatientAndDoctor(String userName, String healthCardNo, Long doctorRegNo);

    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO, String healthCardNo);

    public Boolean validatePatient(String userName, String healthCardNo);

    public MedicalHistoryResponseDTO getMedicalRecord(String healthCardNo);
}
