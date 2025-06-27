package com.onkar.chc.service;

import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;

public interface UserService {

    //signup    DTO-> ENTITY
    public String signUp(UserRequestDTO userRequestDTO);

    //login
    public UserResponseDTO login(String userName, String password);

    //get       ENTITY-> DTO
    public UserResponseDTO getUserData(Integer id);

    //update    DTO-> ENTITY
    public String updateUserData(Integer userId, UserRequestDTO userRequestDTO);

}
