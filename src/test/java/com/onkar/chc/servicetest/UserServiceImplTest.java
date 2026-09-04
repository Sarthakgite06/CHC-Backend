package com.onkar.chc.servicetest;

import com.onkar.chc.entity.DoctorEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.ChemistRepo;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.repo.PathologistRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;
import com.onkar.chc.service.HealthCardIdGenerator;
import com.onkar.chc.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DoctorRepo doctorRepo;

    @Mock
    private ChemistRepo chemistRepo;

    @Mock
    private PathologistRepo pathologistRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private HealthCardIdGenerator healthCardIdGenerator;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    public void signUPTest() {
        UserRequestDTO userDTO = getUserDTO();
        UserEntity userEntity = getUserEntity();

        Mockito.when(modelMapper.map(any(UserRequestDTO.class), any())).thenReturn(userEntity);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        Mockito.when(healthCardIdGenerator.generateHealthCardId(anyString())).thenReturn("PUN00000001");
        Mockito.when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);

        String returnMsg = userServiceImpl.signUp(userDTO);
        Assertions.assertEquals("User registered successfully. Your Health Card ID: PUN00000001", returnMsg);
    }

    @Test
    public void signUPDoctorRoleTest() {
        UserRequestDTO userDTO = getUserDTO();
        userDTO.setRole("Doctor");
        userDTO.setDoctorRegiNo(123456L);
        UserEntity userEntity = getUserEntity();
        userEntity.setRole("Doctor");

        Mockito.when(modelMapper.map(any(UserRequestDTO.class), any())).thenReturn(userEntity);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        Mockito.when(healthCardIdGenerator.generateHealthCardId(anyString())).thenReturn("PUN00000002");
        Mockito.when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);
        Mockito.when(doctorRepo.save(any(DoctorEntity.class))).thenReturn(new DoctorEntity());

        String returnMsg = userServiceImpl.signUp(userDTO);
        Assertions.assertEquals("User registered successfully. Your Health Card ID: PUN00000002", returnMsg);
    }

    @Test
    public void signUPNotSaveTest() {
        UserRequestDTO userDTO = getUserDTO();
        UserEntity userEntity = getUserEntity();

        Mockito.when(modelMapper.map(any(UserRequestDTO.class), any())).thenReturn(userEntity);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        Mockito.when(healthCardIdGenerator.generateHealthCardId(anyString())).thenReturn("PUN00000001");
        Mockito.when(userRepo.save(any(UserEntity.class))).thenReturn(null);

        String returnMsg = userServiceImpl.signUp(userDTO);
        Assertions.assertEquals("User is not registered.", returnMsg);
    }

    @Test
    public void loginSuccessTest() {
        String userName = "Onkar";
        String password = "Onkar123";

        UserEntity userEntity = getUserEntity();
        UserResponseDTO userResponseDTO = getUserResponseDTO();

        Mockito.when(userRepo.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        Mockito.when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(true);
        Mockito.when(modelMapper.map(userEntity, UserResponseDTO.class)).thenReturn(userResponseDTO);

        UserResponseDTO returnDTO = userServiceImpl.login(userName, password);

        Assertions.assertNotNull(returnDTO);
        Assertions.assertEquals("Onkar", returnDTO.getUserName());
    }

    @Test
    public void loginWrongPasswordTest() {
        String userName = "Onkar";
        String password = "wrongPassword";

        UserEntity userEntity = getUserEntity();

        Mockito.when(userRepo.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        Mockito.when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(false);

        UserResponseDTO returnDTO = userServiceImpl.login(userName, password);

        Assertions.assertNull(returnDTO);
    }

    @Test
    public void loginUserNotFoundTest() {
        Mockito.when(userRepo.findByUserName("unknown")).thenReturn(Optional.empty());

        UserResponseDTO returnDTO = userServiceImpl.login("unknown", "anyPass");

        Assertions.assertNull(returnDTO);
    }

    @Test
    public void getUserDataTest() {
        Integer id = 1;
        UserEntity userEntity = getUserEntity();
        UserResponseDTO userResponseDTO = getUserResponseDTO();

        Mockito.when(userRepo.findById(id)).thenReturn(Optional.of(userEntity));
        Mockito.when(modelMapper.map(userEntity, UserResponseDTO.class)).thenReturn(userResponseDTO);

        UserResponseDTO returnDTO = userServiceImpl.getUserData(id);

        Assertions.assertNotNull(returnDTO);
        Assertions.assertEquals("Onkar", returnDTO.getUserName());
    }

    @Test
    public void getUserNotFoundTest() {
        Mockito.when(userRepo.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> userServiceImpl.getUserData(999));
    }

    @Test
    public void updateUserDataTest() {
        Integer id = 1;
        UserRequestDTO userRequestDTO = getUserDTO();
        UserEntity userEntity = getUserEntity();

        Mockito.when(userRepo.findById(id)).thenReturn(Optional.of(userEntity));
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("newHashedPassword");
        Mockito.when(userRepo.save(any(UserEntity.class))).thenReturn(userEntity);

        String expectedMsg = "Data updated successfully.";
        String returnMsg = userServiceImpl.updateUserData(id, userRequestDTO);
        Assertions.assertEquals(expectedMsg, returnMsg);
    }

    @Test
    public void updateUserDataNotFoundTest() {
        UserRequestDTO userRequestDTO = getUserDTO();
        Mockito.when(userRepo.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> userServiceImpl.updateUserData(999, userRequestDTO));
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
                .bloodGroup("O+")
                .gender("Male")
                .district("Pune")
                .build();
    }

    public UserEntity getUserEntity() {
        return UserEntity.builder()
                .userName("Onkar")
                .password("$2a$10$dummyHashedPassword")
                .email("omkar@123")
                .contactNo(1234567890L)
                .firstName("Onkar")
                .lastName("Tagade")
                .role("Patient")
                .bloodGroup("O+")
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
                .bloodGroup("O+")
                .build();
    }
}
