package com.onkar.chc.controller;

import com.onkar.chc.helper.Messages;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    MedicalRecordService medicalRecordService;

    @PostMapping("/createMedicineRecord")
    public ResponseEntity<Messages> createMedicalRecord(
            @RequestParam String userName,
            @RequestParam String healthCardNo,
            @Valid @RequestBody MedicalRecordRequestDTO medicalRecordRequestDTO) {
        String returnMsg;
        Boolean validated = medicalRecordService.validatePatientAndDoctor(userName, healthCardNo, medicalRecordRequestDTO.getDoctorRegNo());
        if (validated) {
            returnMsg = medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO, healthCardNo);
            return new ResponseEntity<>(Messages.builder().msg(returnMsg).build(), HttpStatus.CREATED);
        } else {
            returnMsg = "Patient or Doctor is not valid.";
            return new ResponseEntity<>(Messages.builder().msg(returnMsg).build(), HttpStatus.NOT_FOUND);
        }
    }
}
