package com.onkar.chc.servicetest;

import com.onkar.chc.entity.MedicalRecordEntity;
import com.onkar.chc.entity.PatientEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.MedicalRecordRepo;
import com.onkar.chc.repo.PatientRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.requestDto.PatientRequestDTO;
import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;
import com.onkar.chc.service.implementation.MedicalRecordServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private com.onkar.chc.repo.DoctorRepo doctorRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MedicalRecordRepo medicalRecordRepo;

    @Mock
    private PatientRepo patientRepo;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    @Test
    public void validatePatientAndDoctorTest(){
        String userName="onkar";
        String healthCardNo="PUN00012345";
        Long doctorRegNo=98765L;

        UserEntity userEntity=getUserEntity();
        com.onkar.chc.entity.DoctorEntity doctorEntity = com.onkar.chc.entity.DoctorEntity.builder().userName(userEntity.getUsername()).build();
        Mockito.when(userRepo.getUserDataForValidation(userName,healthCardNo)).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(doctorRepo.findByDoctorRegiNo(doctorRegNo)).thenReturn(Optional.ofNullable(doctorEntity));

        Boolean actualResult=medicalRecordService.validatePatientAndDoctor(userName,healthCardNo,doctorRegNo);
        Assertions.assertTrue(actualResult);
    }

    @Test
    public void validatePatientAndDoctorNotFoundTest(){
        String userName="onkar";
        String healthCardNo="PUN00012345";
        Long doctorRegNo=98765L;

        Mockito.when(userRepo.getUserDataForValidation(userName,healthCardNo)).thenReturn(Optional.ofNullable(null));
        Mockito.when(doctorRepo.findByDoctorRegiNo(doctorRegNo)).thenReturn(Optional.ofNullable(null));

        Boolean actualResult=medicalRecordService.validatePatientAndDoctor(userName,healthCardNo,doctorRegNo);
        Assertions.assertFalse(actualResult);
    }

    @Test
    public void validatePatientTest(){
        String userName="onkar";
        String healthCardNo="PUN00012345";

        UserEntity userEntity=getUserEntity();
        Mockito.when(userRepo.getUserDataForValidation(userName,healthCardNo)).thenReturn(Optional.ofNullable(userEntity));

        Boolean actualResult=medicalRecordService.validatePatient(userName,healthCardNo);
        Assertions.assertTrue(actualResult);
    }

    @Test
    public void validatePatientNotFoundTest(){
        String userName="onkar";
        String healthCardNo="PUN00012345";

        Mockito.when(userRepo.getUserDataForValidation(userName,healthCardNo)).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(DataNotFoundException.class,()->{medicalRecordService.validatePatient(userName,healthCardNo);});
    }

    @Test
    public void createNewMedicalRecordTest(){

        String healthCardNo="PUN00123456";
        UserEntity userEntity=getUserEntity();
        MedicalRecordRequestDTO medicalRecordRequestDTO=getMedicalRecordRequestDTO();
        MedicalRecordEntity medicalRecordEntity=getMedicalRecordEntity();
        PatientEntity patientEntity=getPatientEntity();

        Mockito.when(modelMapper.map(Mockito.any(MedicalRecordRequestDTO.class),Mockito.any())).thenReturn(medicalRecordEntity);
        Mockito.when(userRepo.findByHealthCardNo(Mockito.anyString())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(patientRepo.findById(Mockito.anyString())).thenReturn(Optional.ofNullable(patientEntity));
        Mockito.when(patientRepo.save(Mockito.any(PatientEntity.class))).thenReturn(patientEntity);
        Mockito.when(medicalRecordRepo.save(Mockito.any(MedicalRecordEntity.class))).thenReturn(medicalRecordEntity);

        String returnMsg=medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO,healthCardNo);
        String exMsg="Medical record created successfully.";
        Assertions.assertEquals(exMsg,returnMsg);
    }

    @Test
    public void createNewMedicalRecordPatientNotFoundTest(){

        String healthCardNo="PUN00123456";
        UserEntity userEntity=getUserEntity();
        MedicalRecordRequestDTO medicalRecordRequestDTO=getMedicalRecordRequestDTO();
        MedicalRecordEntity medicalRecordEntity=getMedicalRecordEntity();
        PatientEntity patientEntity=getPatientEntity();

        Mockito.when(modelMapper.map(Mockito.any(MedicalRecordRequestDTO.class),Mockito.any())).thenReturn(medicalRecordEntity);
        Mockito.when(userRepo.findByHealthCardNo(Mockito.anyString())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(RuntimeException.class,()->{medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO,healthCardNo);});
    }



    
    public UserRequestDTO getUserDTO() {
        return UserRequestDTO.builder()
                .userName("Onkar")
                .password("Onkar123")
                .email("omkar@123")
                .contactNo(1234567890L)
                .firstName("Onkar")
                .lastName("Tagade")
                .role("Patient")
                .bloodGroup("o +")
                .gender("Male")
                .build();
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
                .bloodGroup("o +")
                .build();

    }

    public UserResponseDTO getUserResponseDTO() {
        return UserResponseDTO.builder()
                .userName("Onkar")
                .email("omkar@123")
                .contactNo(1234567890L)
                .firstName("Onkar")
                .lastName("Tagade")
                .role("Patient")
                .bloodGroup("o +")
                .build();
    }

    public MedicalRecordEntity getMedicalRecordEntity(){
        return MedicalRecordEntity.builder()
                .createdDate(LocalDate.now().toString())
                .doctorRegNo(123456L)
                .patientEntity(getPatientEntity())
                .build();

    }

    public MedicalRecordRequestDTO getMedicalRecordRequestDTO(){
        return MedicalRecordRequestDTO.builder()
                .doctorRegNo(123456L)
                .patientEntity(getPatientRequestDTO())
                .build();
    }

    public PatientEntity getPatientEntity(){
        return PatientEntity.builder()
                .age(30)
                .weight(70)
                .bloodPressure("120/80")
                .build();
    }

    public PatientRequestDTO getPatientRequestDTO(){
        return PatientRequestDTO.builder()
                .age(30)
                .weight(70)
                .bloodPressure("120/80")
                .build();
    }

}
