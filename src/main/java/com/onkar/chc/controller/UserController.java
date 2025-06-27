package com.onkar.chc.controller;

import com.onkar.chc.helper.Messages;
import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;
import com.onkar.chc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    //signup
    @PostMapping("/signUp")
    public ResponseEntity<Messages> signUp(@Valid @RequestBody UserRequestDTO userRequestDTO){
        String returnMsg=userService.signUp(userRequestDTO);
        //Builder method is used to initialize fields in the class.
        //Build method returns object of that class.
        Messages messages= Messages.builder()
                            .msg(returnMsg)
                            .build();
        return new ResponseEntity<>(messages, HttpStatus.CREATED);

    }

    //login
    @GetMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestParam String userName, @RequestParam String password){

        UserResponseDTO userResponseDTO=userService.login(userName, password);

        if(userResponseDTO!=null)
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

    }

    //get
    @GetMapping("/getPersonalInfo")
    public ResponseEntity<UserResponseDTO> getUserData(@RequestParam Integer id){
        UserResponseDTO userResponseDTO= userService.getUserData(id);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.ACCEPTED);
    }

    //update
    @PutMapping("/updateUserData")
    public ResponseEntity<String> updateUser(@RequestParam Integer id, @RequestBody UserRequestDTO userRequestDTO){
        String msg = userService.updateUserData(id,userRequestDTO);
        return new ResponseEntity<>(msg, HttpStatus.ACCEPTED);
    }


}
