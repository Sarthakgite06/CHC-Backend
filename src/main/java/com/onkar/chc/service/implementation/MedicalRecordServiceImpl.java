package com.onkar.chc.service.implementation;

import com.onkar.chc.entity.MedicalRecordEntity;
import com.onkar.chc.entity.PatientEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.repo.MedicalRecordRepo;
import com.onkar.chc.repo.PatientRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.responseDto.MedicalRecordResponseDTO;
import com.onkar.chc.responseDto.PatientResponseDTO;
import com.onkar.chc.service.MedicalRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MedicalRecordRepo medicalRecordRepo;

    @Autowired
    PatientRepo patientRepo;


    @Autowired
    DoctorRepo doctorRepo;

    @Override
    public Boolean validatePatientAndDoctor(String userName, String healthCardNo, Long doctorRegNo) {
        UserEntity userData = userRepo.getUserDataForValidation(userName, healthCardNo).orElse(null);
        com.onkar.chc.entity.DoctorEntity doctorDetails = doctorRepo.findByDoctorRegiNo(doctorRegNo).orElse(null);

        return userData != null && doctorDetails != null;
    }

    @Override
    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO, String healthCardNo) {

        //DTO-> ENTITY -> FULFILL -> SAVE.
        MedicalRecordEntity medicalRecordEntity = modelMapper.map(medicalRecordRequestDTO, MedicalRecordEntity.class);

        //FULFILL ENTITY.
        //DATE OF CREATION. -> USE DATE OR LOCALDATE.
        medicalRecordEntity.setCreatedDate(LocalDate.now().toString());

        //PATIENT OBJECT.
        UserEntity userEntity = userRepo.findByHealthCardNo(healthCardNo)
                .orElseThrow(() -> new RuntimeException("User is not found."));

        //Pull patient object from Medical Records.
        PatientEntity patientData = medicalRecordEntity.getPatientEntity();
        PatientEntity existingPatient = patientRepo.findById(healthCardNo).orElse(null);

        //Fulfill it using userEntity.
        patientData.setUserName(userEntity.getUsername());
        patientData.setHealthCardNo(userEntity.getHealthCardNo());
        patientData.setDob(userEntity.getDob());
        patientData.setGender(userEntity.getGender());

        medicalRecordEntity.setPatientEntity(patientData);

        //LOGIC TO SAVE PATIENT IN PATIENT TABLE IF DOESN'T EXIST OR UPDATE DATA IF EXISTS.
        if (existingPatient == null)
            patientRepo.save(patientData);
        else {
            existingPatient.setAge(patientData.getAge());
            existingPatient.setWeight(patientData.getWeight());
            existingPatient.setBloodPressure(patientData.getBloodPressure());
            patientRepo.save(existingPatient);
        }

        medicalRecordRepo.save(medicalRecordEntity);

        return "Data is saved.";
    }

    @Override
    public Boolean validatePatient(String userName, String healthCardNo) {
        UserEntity userData = userRepo.getUserDataForValidation(userName, healthCardNo)
                .orElseThrow(() -> new DataNotFoundException("Patient details are invalid."));
        return true;
    }

    @Override
    public MedicalHistoryResponseDTO getMedicalRecord(String healthCardNo) {
        PatientEntity patientEntity = patientRepo.findById(healthCardNo)
                .orElseThrow(() -> new DataNotFoundException("Patient is not found."));

        List<MedicalRecordEntity> medicalRecordEntityList = medicalRecordRepo.findByPatientEntity(patientEntity)
                .orElseThrow(() -> new RuntimeException("No medical records found."));

        List<MedicalRecordResponseDTO> medicalRecordResponseDTOList = medicalRecordEntityList.stream()
                .map(a -> modelMapper.map(a, MedicalRecordResponseDTO.class)).toList();

        return MedicalHistoryResponseDTO.builder()
                .patientEntity(modelMapper.map(patientEntity, PatientResponseDTO.class))
                .medicalRecordResponseDTOList(medicalRecordResponseDTOList)
                .build();
    }
}
