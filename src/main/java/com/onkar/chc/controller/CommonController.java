package com.onkar.chc.controller;

import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.responseDto.MedicalRecordResponseDTO;
import com.onkar.chc.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chc")
public class CommonController {

    @Autowired
    MedicalRecordService medicalRecordService;

    @GetMapping("/getPatientMedicalHistory")
    public ResponseEntity<MedicalHistoryResponseDTO> getMedicalHitory(@RequestParam Integer healthCardNo, @RequestParam String userName){

        MedicalHistoryResponseDTO medicalHistoryResponseDTO;

        Boolean isPatientValid=medicalRecordService.validatePatient(userName,healthCardNo);
        if(isPatientValid){
            medicalHistoryResponseDTO =medicalRecordService.getMedicalRecord(healthCardNo);
            return new ResponseEntity<>(medicalHistoryResponseDTO, HttpStatus.ACCEPTED);
        }
        else
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }
}
