package com.onkar.chc.security.controller;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.security.dto.JwtRequestDTO;
import com.onkar.chc.security.dto.JwtResponseDTO;
import com.onkar.chc.security.jwtaction.JWTStuff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JWTStuff jwtStuff;

    @Autowired
    private UserRepo userRepo;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequestDTO request) {

        String identifier = request.getUserName() != null ? request.getUserName().trim() : "";
        // Check if user exists first to give specific error
        UserEntity userEntity = userRepo.findByUserNameOrEmail(identifier, identifier).orElse(null);
        if (userEntity == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        try {
            this.doAuthenticate(userEntity.getUsername(), request.getPassword());
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getUsername());
        String token = this.jwtStuff.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        JwtResponseDTO response = JwtResponseDTO.builder()
                .token(token)
                .userName(userEntity.getUsername())
                .role(role)
                .healthCardNo(userEntity.getHealthCardNo() != null ? userEntity.getHealthCardNo() : "")
                .district(userEntity.getDistrict() != null ? userEntity.getDistrict() : "")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        manager.authenticate(authentication);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> exceptionHandler() {
        return new ResponseEntity<>("Credentials Invalid !!", HttpStatus.UNAUTHORIZED);
    }
}
