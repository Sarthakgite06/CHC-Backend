package com.onkar.chc.service.implementation;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;
import com.onkar.chc.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepo userRepo;


    @Override
    public String signUp(UserRequestDTO userRequestDTO) {

        UserEntity userEntity=modelMapper.map(userRequestDTO, UserEntity.class);

        UserEntity save = userRepo.save(userEntity);

        String msg;
        if(save!=null) {
            msg = "User is registered successfully.";
            log.info("Data is converted into User Entity.");
        }
        else{
            msg="User is not registered.";
            log.info("User is not converted into User Entity.");

        }

        return msg;
    }

    @Override
    public UserResponseDTO login(String userName, String password) {

        //User name -> User valid or not by accessing data by user name.
        UserEntity userEntity=userRepo.findByUserName(userName).get();

        //check if password is same or not.
        if(userEntity.getPassword().equals(password)){
            return modelMapper.map(userEntity, UserResponseDTO.class);
        }
        else
            return null;
    }

    @Override
    public UserResponseDTO getUserData(Integer id) {
        UserEntity userEntity=userRepo.findById(id).orElseThrow(()->new DataNotFoundException("User is not found."));
        return modelMapper.map(userEntity, UserResponseDTO.class);
    }

    @Override
    public String updateUserData(Integer userId, UserRequestDTO userRequestDTO) {
        // Fetch existing user data from the database
        UserEntity userEntity = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields from DTO
        userEntity.setUserName(userRequestDTO.getUserName());
        userEntity.setHealthCardNo(userRequestDTO.getHealthCardNo());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setPassword(userRequestDTO.getPassword());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setContactNo(userRequestDTO.getContactNo());
        userEntity.setDob(userRequestDTO.getDob());
        userEntity.setAddress(userRequestDTO.getAddress());
        userEntity.setGender(userRequestDTO.getGender());
        userEntity.setBloodGroup(userRequestDTO.getBloodGroup());

        // Save updated user entity
        userRepo.save(userEntity);

        // Return success message
        return "Data updated successfully.";
    }

}
