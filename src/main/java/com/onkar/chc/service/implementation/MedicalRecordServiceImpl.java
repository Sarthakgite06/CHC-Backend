package com.onkar.chc.service.implementation;

import com.onkar.chc.entity.MedicalRecordEntity;
import com.onkar.chc.entity.PatientEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.entity.MedicalImagingEntity;
import com.onkar.chc.repo.MedicalImagingRepo;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.repo.MedicalRecordRepo;
import com.onkar.chc.repo.PatientRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.responseDto.MedicalRecordResponseDTO;
import com.onkar.chc.responseDto.PatientResponseDTO;
import com.onkar.chc.service.MedicalRecordService;
import com.onkar.chc.service.FileStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    MedicalImagingRepo medicalImagingRepo;

    @Override
    public Boolean validatePatientAndDoctor(String userName, String healthCardNo, Long doctorRegNo) {
        UserEntity userData = userRepo.getUserDataForValidation(userName, healthCardNo).orElse(null);
        com.onkar.chc.entity.DoctorEntity doctorDetails = doctorRepo.findByDoctorRegiNo(doctorRegNo).orElse(null);

        return userData != null && doctorDetails != null;
    }

    @Override
    public String createNewMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO, String healthCardNo, MultipartFile file, String imagingType, String title, String description, String hospitalName, UserEntity doctor) {

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

        // Handle attachment file if present
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }
            String savedFileName = fileStorageService.storeFile(file);

            // Create and save MedicalImagingEntity in medical_imaging table
            MedicalImagingEntity imagingEntity = MedicalImagingEntity.builder()
                    .patient(userEntity)
                    .doctor(doctor)
                    .healthCardNo(healthCardNo)
                    .imagingType(imagingType != null ? imagingType : "Other")
                    .title(title != null ? title : "Prescription Scan")
                    .description(description)
                    .hospitalName(hospitalName != null ? hospitalName : "General Clinic")
                    .fileName(savedFileName)
                    .fileType(extension.length() > 1 ? extension.substring(1).toUpperCase() : "UNKNOWN")
                    .fileSize(file.getSize())
                    .uploadedAt(java.time.LocalDateTime.now())
                    .isDeleted(false)
                    .build();

            // First save to generate the ID
            MedicalImagingEntity savedImaging = medicalImagingRepo.save(imagingEntity);
            savedImaging.setFileUrl("/medical-imaging/download/" + savedImaging.getId());
            savedImaging = medicalImagingRepo.save(savedImaging);

            medicalRecordEntity.setMedicalImagingEntity(savedImaging);
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
        // Try to find in patient table, but fall back to user entity for brand new patients
        PatientEntity patientEntity = patientRepo.findById(healthCardNo).orElse(null);

        if (patientEntity == null) {
            // Patient exists in user table but hasn't been given a prescription yet
            UserEntity userEntity = userRepo.findByHealthCardNo(healthCardNo)
                    .orElseThrow(() -> new DataNotFoundException("Patient is not found."));
            patientEntity = PatientEntity.builder()
                    .healthCardNo(userEntity.getHealthCardNo())
                    .userName(userEntity.getUsername())
                    .dob(userEntity.getDob())
                    .gender(userEntity.getGender())
                    .build();
        }

        // Return empty list gracefully instead of throwing when no records exist yet
        List<MedicalRecordEntity> medicalRecordEntityList = medicalRecordRepo.findByPatientEntity(patientEntity)
                .orElse(List.of());

        List<MedicalRecordResponseDTO> medicalRecordResponseDTOList = medicalRecordEntityList.stream()
                .map(a -> {
                    // Manually build the DTO to avoid ModelMapper ambiguity with nested MedicalImagingEntity fields
                    MedicalRecordResponseDTO dto = MedicalRecordResponseDTO.builder()
                            .medicalRecordId(a.getMedicalRecordId())
                            .createdDate(a.getCreatedDate())
                            .doctorRegNo(a.getDoctorRegNo())
                            .medicineInfoEntities(
                                a.getMedicineInfoEntities() == null ? List.of() :
                                a.getMedicineInfoEntities().stream()
                                    .map(m -> modelMapper.map(m, com.onkar.chc.responseDto.MedicineInfoResponseDTO.class))
                                    .toList()
                            )
                            .build();
                    if (a.getMedicalImagingEntity() != null) {
                        dto.setFileName(a.getMedicalImagingEntity().getFileName());
                        dto.setFileUrl(a.getMedicalImagingEntity().getFileUrl());
                        dto.setFileType(a.getMedicalImagingEntity().getFileType());
                        dto.setFileSize(a.getMedicalImagingEntity().getFileSize());
                        dto.setImagingType(a.getMedicalImagingEntity().getImagingType());
                        dto.setTitle(a.getMedicalImagingEntity().getTitle());
                        dto.setDescription(a.getMedicalImagingEntity().getDescription());
                        dto.setHospitalName(a.getMedicalImagingEntity().getHospitalName());
                    }
                    return dto;
                }).toList();

        return MedicalHistoryResponseDTO.builder()
                .patientEntity(modelMapper.map(patientEntity, PatientResponseDTO.class))
                .medicalRecordResponseDTOList(medicalRecordResponseDTOList)
                .build();
    }
}
