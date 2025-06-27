package com.onkar.chc.service;

import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;

public interface MedicalRecordService {

    public Boolean validatePatientAndDoctor(String userName,Integer healthCardNo,Long doctorRegNo);

    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO,Integer healthCardNo);

    public Boolean validatePatient(String userName,Integer healthCardNo);

    public MedicalHistoryResponseDTO getMedicalRecord(Integer healthCardNo);
}
