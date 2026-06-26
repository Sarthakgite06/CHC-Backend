package com.onkar.chc.service.implementation;

import com.onkar.chc.entity.DoctorEntity;
import com.onkar.chc.entity.ChemistEntity;
import com.onkar.chc.entity.PathologistEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.repo.ChemistRepo;
import com.onkar.chc.repo.PathologistRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.requestDto.UserRequestDTO;
import com.onkar.chc.responseDto.UserResponseDTO;
import com.onkar.chc.service.HealthCardIdGenerator;
import com.onkar.chc.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepo userRepo;

    @Autowired
    DoctorRepo doctorRepo;

    @Autowired
    ChemistRepo chemistRepo;

    @Autowired
    PathologistRepo pathologistRepo;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    HealthCardIdGenerator healthCardIdGenerator;

    @Override
    public String signUp(UserRequestDTO userRequestDTO) {

        UserEntity userEntity = modelMapper.map(userRequestDTO, UserEntity.class);

        // Hash the password before saving — store only BCrypt hash in the DB
        userEntity.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        // Auto-generate Health Card ID from district
        String healthCardId = healthCardIdGenerator.generateHealthCardId(userRequestDTO.getDistrict());
        userEntity.setHealthCardNo(healthCardId);
        userEntity.setDistrict(userRequestDTO.getDistrict());

        // Set registration timestamp
        userEntity.setCreatedAt(LocalDate.now().toString());

        UserEntity save = userRepo.save(userEntity);

        if (save != null) {
            String role = userRequestDTO.getRole();
            if (role != null) {
                if (role.contains("Doctor") && userRequestDTO.getDoctorRegiNo() != null) {
                    DoctorEntity doctor = DoctorEntity.builder()
                            .userName(save.getUsername())
                            .doctorRegiNo(userRequestDTO.getDoctorRegiNo())
                            .build();
                    doctorRepo.save(doctor);
                }
                if (role.contains("Chemist") && userRequestDTO.getChemistRegiNo() != null) {
                    ChemistEntity chemist = ChemistEntity.builder()
                            .userName(save.getUsername())
                            .chemistRegiNo(userRequestDTO.getChemistRegiNo())
                            .build();
                    chemistRepo.save(chemist);
                }
                if (role.contains("Pathologist") && userRequestDTO.getPathologistLicenseNo() != null) {
                    PathologistEntity pathologist = PathologistEntity.builder()
                            .userName(save.getUsername())
                            .licenseNo(userRequestDTO.getPathologistLicenseNo())
                            .build();
                    pathologistRepo.save(pathologist);
                }
            }
        }

        String msg;
        if (save != null) {
            msg = "User registered successfully. Your Health Card ID: " + healthCardId;
            log.info("User registered successfully: {} | HealthCard: {}", save.getUsername(), healthCardId);
        } else {
            msg = "User is not registered.";
            log.info("User registration failed.");
        }

        return msg;
    }

    @Override
    public UserResponseDTO login(String userName, String password) {
        // Fetch user by username
        UserEntity userEntity = userRepo.findByUserName(userName)
                .orElse(null);

        if (userEntity == null) {
            return null;
        }

        // Use BCrypt matcher to compare plain-text input against stored hash
        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            return modelMapper.map(userEntity, UserResponseDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public UserResponseDTO getUserData(Integer id) {
        UserEntity userEntity = userRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User is not found."));
        return modelMapper.map(userEntity, UserResponseDTO.class);
    }

    @Override
    public String updateUserData(Integer userId, UserRequestDTO userRequestDTO) {
        UserEntity userEntity = userRepo.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Update fields from DTO (healthCardNo is immutable — never changes)
        userEntity.setUserName(userRequestDTO.getUserName());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());

        // Hash the new password if it's being updated
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setContactNo(userRequestDTO.getContactNo());
        userEntity.setDob(userRequestDTO.getDob());
        userEntity.setAddress(userRequestDTO.getAddress());
        userEntity.setGender(userRequestDTO.getGender());
        userEntity.setBloodGroup(userRequestDTO.getBloodGroup());

        userRepo.save(userEntity);

        return "Data updated successfully.";
    }
}
