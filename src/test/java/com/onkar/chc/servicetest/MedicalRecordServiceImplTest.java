package com.onkar.chc.servicetest;

import com.onkar.chc.entity.*;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.*;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.requestDto.PatientRequestDTO;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.service.FileStorageService;
import com.onkar.chc.service.implementation.MedicalRecordServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class MedicalRecordServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private DoctorRepo doctorRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MedicalRecordRepo medicalRecordRepo;

    @Mock
    private PatientRepo patientRepo;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MedicalImagingRepo medicalImagingRepo;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    @Test
    public void validatePatientAndDoctorTest() {
        String userName = "onkar";
        String healthCardNo = "PUN00012345";
        Long doctorRegNo = 98765L;

        UserEntity userEntity = getUserEntity();
        DoctorEntity doctorEntity = DoctorEntity.builder().userName(userEntity.getUsername()).build();
        Mockito.when(userRepo.getUserDataForValidation(userName, healthCardNo)).thenReturn(Optional.of(userEntity));
        Mockito.when(doctorRepo.findByDoctorRegiNo(doctorRegNo)).thenReturn(Optional.of(doctorEntity));

        Boolean actualResult = medicalRecordService.validatePatientAndDoctor(userName, healthCardNo, doctorRegNo);
        Assertions.assertTrue(actualResult);
    }

    @Test
    public void validatePatientAndDoctorNotFoundTest() {
        String userName = "onkar";
        String healthCardNo = "PUN00012345";
        Long doctorRegNo = 98765L;

        Mockito.when(userRepo.getUserDataForValidation(userName, healthCardNo)).thenReturn(Optional.empty());
        Mockito.when(doctorRepo.findByDoctorRegiNo(doctorRegNo)).thenReturn(Optional.empty());

        Boolean actualResult = medicalRecordService.validatePatientAndDoctor(userName, healthCardNo, doctorRegNo);
        Assertions.assertFalse(actualResult);
    }

    @Test
    public void validatePatientTest() {
        String userName = "onkar";
        String healthCardNo = "PUN00012345";

        UserEntity userEntity = getUserEntity();
        Mockito.when(userRepo.getUserDataForValidation(userName, healthCardNo)).thenReturn(Optional.of(userEntity));

        Boolean actualResult = medicalRecordService.validatePatient(userName, healthCardNo);
        Assertions.assertTrue(actualResult);
    }

    @Test
    public void validatePatientNotFoundTest() {
        String userName = "onkar";
        String healthCardNo = "PUN00012345";

        Mockito.when(userRepo.getUserDataForValidation(userName, healthCardNo)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> medicalRecordService.validatePatient(userName, healthCardNo));
    }

    @Test
    public void createNewMedicalRecordTest() {
        String healthCardNo = "PUN00123456";
        UserEntity userEntity = getUserEntity();
        MedicalRecordRequestDTO medicalRecordRequestDTO = getMedicalRecordRequestDTO();
        MedicalRecordEntity medicalRecordEntity = getMedicalRecordEntity();
        PatientEntity patientEntity = getPatientEntity();

        Mockito.when(modelMapper.map(any(MedicalRecordRequestDTO.class), any())).thenReturn(medicalRecordEntity);
        Mockito.when(userRepo.findByHealthCardNo(healthCardNo)).thenReturn(Optional.of(userEntity));
        Mockito.when(patientRepo.findById(anyString())).thenReturn(Optional.of(patientEntity));
        Mockito.when(patientRepo.save(any(PatientEntity.class))).thenReturn(patientEntity);
        Mockito.when(medicalRecordRepo.save(any(MedicalRecordEntity.class))).thenReturn(medicalRecordEntity);

        String returnMsg = medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO, healthCardNo, null, null, null, null, null, userEntity);
        Assertions.assertEquals("Data is saved.", returnMsg);
    }

    @Test
    public void createNewMedicalRecordWithFileTest() {
        String healthCardNo = "PUN00123456";
        UserEntity userEntity = getUserEntity();
        MedicalRecordRequestDTO medicalRecordRequestDTO = getMedicalRecordRequestDTO();
        MedicalRecordEntity medicalRecordEntity = getMedicalRecordEntity();
        PatientEntity patientEntity = getPatientEntity();

        MockMultipartFile file = new MockMultipartFile("file", "xray.png", "image/png", "fake-image".getBytes());
        MedicalImagingEntity imagingEntity = MedicalImagingEntity.builder().id(10L).build();

        Mockito.when(modelMapper.map(any(MedicalRecordRequestDTO.class), any())).thenReturn(medicalRecordEntity);
        Mockito.when(userRepo.findByHealthCardNo(healthCardNo)).thenReturn(Optional.of(userEntity));
        Mockito.when(patientRepo.findById(anyString())).thenReturn(Optional.of(patientEntity));
        Mockito.when(patientRepo.save(any(PatientEntity.class))).thenReturn(patientEntity);
        Mockito.when(fileStorageService.storeFile(any())).thenReturn("uuid-xray.png");
        Mockito.when(medicalImagingRepo.save(any(MedicalImagingEntity.class))).thenReturn(imagingEntity);
        Mockito.when(medicalRecordRepo.save(any(MedicalRecordEntity.class))).thenReturn(medicalRecordEntity);

        String returnMsg = medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO, healthCardNo, file, "X-Ray", "Chest X-Ray", "Clear lungs", "City Hospital", userEntity);
        Assertions.assertEquals("Data is saved.", returnMsg);
    }

    @Test
    public void createNewMedicalRecordPatientNotFoundTest() {
        String healthCardNo = "PUN00123456";
        UserEntity userEntity = getUserEntity();
        MedicalRecordRequestDTO medicalRecordRequestDTO = getMedicalRecordRequestDTO();
        MedicalRecordEntity medicalRecordEntity = getMedicalRecordEntity();

        Mockito.when(modelMapper.map(any(MedicalRecordRequestDTO.class), any())).thenReturn(medicalRecordEntity);
        Mockito.when(userRepo.findByHealthCardNo(healthCardNo)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO, healthCardNo, null, null, null, null, null, userEntity));
    }

    @Test
    public void getMedicalRecordTest() {
        String healthCardNo = "PUN00123456";
        PatientEntity patientEntity = getPatientEntity();
        MedicalRecordEntity recordEntity = getMedicalRecordEntity();
        List<MedicalRecordEntity> records = new ArrayList<>();
        records.add(recordEntity);

        Mockito.when(patientRepo.findById(healthCardNo)).thenReturn(Optional.of(patientEntity));
        Mockito.when(medicalRecordRepo.findByPatientEntity(patientEntity)).thenReturn(Optional.of(records));

        MedicalHistoryResponseDTO response = medicalRecordService.getMedicalRecord(healthCardNo);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getMedicalRecordResponseDTOList().size());
    }

    @Test
    public void getMedicalRecordNotFoundTest() {
        String healthCardNo = "PUN00123456";
        Mockito.when(patientRepo.findById(healthCardNo)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> medicalRecordService.getMedicalRecord(healthCardNo));
    }

    public UserEntity getUserEntity() {
        return UserEntity.builder()
                .userName("Onkar")
                .password("Onkar123")
                .email("omkar@123")
                .contactNo(1234567890L)
                .firstName("Onkar")
                .lastName("Tagade")
                .role("Patient")
                .bloodGroup("O+")
                .build();
    }

    public MedicalRecordEntity getMedicalRecordEntity() {
        return MedicalRecordEntity.builder()
                .createdDate(LocalDate.now().toString())
                .doctorRegNo(123456L)
                .patientEntity(getPatientEntity())
                .medicineInfoEntities(new ArrayList<>())
                .build();
    }

    public MedicalRecordRequestDTO getMedicalRecordRequestDTO() {
        return MedicalRecordRequestDTO.builder()
                .doctorRegNo(123456L)
                .patientEntity(getPatientRequestDTO())
                .medicineInfoEntities(new ArrayList<>())
                .build();
    }

    public PatientEntity getPatientEntity() {
        return PatientEntity.builder()
                .healthCardNo("PUN00123456")
                .age(30)
                .weight(70)
                .bloodPressure("120/80")
                .build();
    }

    public PatientRequestDTO getPatientRequestDTO() {
        return PatientRequestDTO.builder()
                .age(30)
                .weight(70)
                .bloodPressure("120/80")
                .build();
    }
}
