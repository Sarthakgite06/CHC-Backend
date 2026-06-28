package com.onkar.chc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.helper.Messages;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/createMedicineRecord", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Messages> createMedicalRecord(
            @RequestParam String userName,
            @RequestParam String healthCardNo,
            @RequestPart("record") String recordJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "imagingType", required = false) String imagingType,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "hospitalName", required = false) String hospitalName) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserEntity doctor = (UserEntity) authentication.getPrincipal();

        MedicalRecordRequestDTO medicalRecordRequestDTO = objectMapper.readValue(recordJson, MedicalRecordRequestDTO.class);

        String returnMsg;
        Boolean validated = medicalRecordService.validatePatientAndDoctor(userName, healthCardNo, medicalRecordRequestDTO.getDoctorRegNo());
        if (validated) {
            returnMsg = medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO, healthCardNo, file, imagingType, title, description, hospitalName, doctor);
            return new ResponseEntity<>(Messages.builder().msg(returnMsg).build(), HttpStatus.CREATED);
        } else {
            returnMsg = "Patient or Doctor is not valid.";
            return new ResponseEntity<>(Messages.builder().msg(returnMsg).build(), HttpStatus.NOT_FOUND);
        }
    }
}
