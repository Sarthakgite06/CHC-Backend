package com.onkar.chc.controller;

import com.onkar.chc.helper.Messages;
import com.onkar.chc.requestDto.MedicalRecordRequestDTO;
import com.onkar.chc.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    MedicalRecordService medicalRecordService;

    Messages messages=Messages.builder().build();

    @PostMapping("/createMedicineRecord")
    public ResponseEntity<Messages> createMedicalRecord(@RequestParam String userName, @RequestParam Integer healthCardNo, @Valid @RequestBody MedicalRecordRequestDTO medicalRecordRequestDTO){
        String returnMsg;
        Boolean validated= medicalRecordService.validatePatientAndDoctor(userName,healthCardNo, medicalRecordRequestDTO.getDoctorRegNo());
        if(validated){
            returnMsg = medicalRecordService.createNewMedicalRecord(medicalRecordRequestDTO,healthCardNo);
            messages.setMsg(returnMsg);
            return new ResponseEntity<>(messages, HttpStatus.CREATED);
        }

        else{
            returnMsg="Patient or Doctor is not valid.";
            messages.setMsg(returnMsg);
            return new ResponseEntity<>(messages, HttpStatus.NOT_FOUND);
        }

    }
}
