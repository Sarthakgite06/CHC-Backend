package com.onkar.chc.service.implementation;

import com.onkar.chc.repo.MedicalRecordRepo;
import com.onkar.chc.repo.MedicineInfoRepo;
import com.onkar.chc.repo.PatientRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.service.MedicalRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.onkar.chc.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Alternative (unused) implementation with self-managed logic.
 * Not annotated with @Service — the primary impl is MedicalRecordServiceImpl.
 */
public class MedicalRecordServiceImplSelfLogic implements MedicalRecordService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PatientRepo patientRepo;

    @Autowired
    MedicalRecordRepo medicalRecordRepo;

    @Autowired
    MedicineInfoRepo medicineInfoRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Boolean validatePatientAndDoctor(String userName, String healthCardNo, Long doctorRegiNo) {
        return null;
    }

    @Override
    public Boolean validatePatient(String userName, String healthCardNo) {
        return null;
    }

    @Override
    public MedicalHistoryResponseDTO getMedicalRecord(String healthCardNo) {
        return null;
    }

    @Override
    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO, String healthCardNo, MultipartFile file, String imagingType, String title, String description, String hospitalName, UserEntity doctor) {
        return "";
    }
}
