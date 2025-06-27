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
    public Boolean validatePatientAndDoctor(String userName, Integer healthCardNo, Long doctorRegiNo) {

       /* boolean validPatient=false;
        boolean validDoctor;
        UserEntity userEntity=userRepo.findByUserName(userName).get();
        if(userEntity.getHealthCardNo().equals(healthCardNo)){
            validPatient=true;
        }
        validDoctor =userRepo.findByDoctorRegiNo(doctorRegiNo);
        return validPatient&&validDoctor;

        */
        return null;
    }

    @Override
    public Boolean validatePatient(String userName, Integer healthCardNo) {
        return null;
    }

    @Override
    public MedicalHistoryResponseDTO getMedicalRecord(Integer healthCardNo) {
        return null;
    }

    @Override
    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO,Integer healthCardNo) {

       /* PatientEntity patientEntity=patientRepo.findById(healthCardNo).get();
        if (patientEntity == null) {
            UserEntity userEntity = userRepo.findById(healthCardNo).get();

            patientEntity=PatientEntity.builder()
                    .healthCardNo(healthCardNo)
                    .userName(userEntity.getUserName())
                    .dob(userEntity.getDob())
                    .gender(userEntity.getGender())
                    .age(medicalRecordRequestDTO.getPatientRequestDTO().getAge())
                    .weight(medicalRecordRequestDTO.getPatientRequestDTO().getWeight())
                    .bloodPressure(medicalRecordRequestDTO.getPatientRequestDTO().getBloodPressure())
                    .build();

            patientRepo.save(patientEntity);
        }

        List<MedicineInfoEntity> savedMedicines = medicalRecordRequestDTO.getMedicineInfoRequestDTOList().stream()
                .map(medicineDTO -> {
                    MedicineInfoEntity medicineEntity = modelMapper.map(medicineDTO, MedicineInfoEntity.class);
                    return medicineInfoRepo.save(medicineEntity);
                })
                .toList();

        MedicalRecordEntity medicalRecordEntity=modelMapper.map(medicalRecordRequestDTO,MedicalRecordEntity.class);
        medicalRecordRepo.save(medicalRecordEntity);

        */
        return "";
    }
}
