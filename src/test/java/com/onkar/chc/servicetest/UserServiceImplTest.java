package com.onkar.chc.servicetest;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;
import com.onkar.chc.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ThrowingConsumer;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.concurrent.Executor;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;


    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Test
    public void signUPTest(){

        UserRequestDTO userDTO = getUserDTO();
        UserEntity userEntity=getUserEntity();

        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);
        Mockito.when(modelMapper.map(Mockito.any(UserRequestDTO.class),Mockito.any())).thenReturn(userEntity);

        String returnMsg = userServiceImpl.signUp(userDTO);
        String exMsg="User is registered successfully.";
        Assertions.assertEquals(exMsg,returnMsg);

    }
    @Test
    public void signUPNotSaveTest(){

        UserRequestDTO userDTO = getUserDTO();
        UserEntity userEntity=getUserEntity();

        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(null);
        Mockito.when(modelMapper.map(Mockito.any(UserRequestDTO.class),Mockito.any())).thenReturn(userEntity);

        String returnMsg = userServiceImpl.signUp(userDTO);
        String exMsg="User is not registered.";
        Assertions.assertEquals(exMsg,returnMsg);

    }

    @Test
    public void loginTest(){

        String userName="Onkar";
        String password="Onkar123";

        UserEntity userEntity=getUserEntity();
        UserResponseDTO userResponseDTO=getUserResponseDTO();

        Mockito.when(userRepo.findByUserName(Mockito.anyString())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(modelMapper.map(Mockito.any(UserEntity.class),Mockito.any())).thenReturn(null);

        UserResponseDTO returnDTO = userServiceImpl.login(userName, password);

        //Assertions.assertNotNull(returnDTO);
        Assertions.assertNull(returnDTO);
    }

    @Test
    public void getUserDataTest(){

        Integer id=1;
        UserEntity userEntity=getUserEntity();
        UserResponseDTO userResponseDTO=getUserResponseDTO();

        Mockito.when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(modelMapper.map(Mockito.any(UserEntity.class),Mockito.any())).thenReturn(userResponseDTO);

        UserResponseDTO returnDTO = userServiceImpl.getUserData(id);

        Assertions.assertNotNull(returnDTO);
       // Assertions.assertNull(returnDTO);
    }

    @Test
    public void updateUserDataTest(){

        Integer id=1;
        UserRequestDTO userRequestDTO=getUserDTO();
        UserEntity userEntity=getUserEntity();
        UserResponseDTO userResponseDTO = getUserResponseDTO();

        Mockito.when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);

        String expectedMsg="Data updated successfully.";
        String returnMsg = userServiceImpl.updateUserData(id,userRequestDTO);
        Assertions.assertEquals(expectedMsg,returnMsg);

    }


    @Test
    public void getUserNotFoundTest()
    {
        Mockito.when(userRepo.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(DataNotFoundException.class,()-> userServiceImpl.getUserData(1));
    }

    public  UserRequestDTO getUserDTO() {
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



}
