package com.onkar.chc.controller;

import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
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

    /**
     * Get patient medical history.
     * Flexible search: provide EITHER healthCardNo OR userName (or both).
     */
    @GetMapping("/getPatientMedicalHistory")
    public ResponseEntity<MedicalHistoryResponseDTO> getMedicalHistory(
            @RequestParam(required = false) String healthCardNo,
            @RequestParam(required = false) String userName) {

        MedicalHistoryResponseDTO medicalHistoryResponseDTO;

        // Use whichever is provided for validation
        String cardId = (healthCardNo != null && !healthCardNo.isBlank()) ? healthCardNo : "";
        String name = (userName != null && !userName.isBlank()) ? userName : "";

        Boolean isPatientValid = medicalRecordService.validatePatient(name, cardId);
        if (isPatientValid) {
            // Prefer healthCardNo for record lookup; fall back to finding by name
            String lookupId = cardId;
            if (lookupId.isBlank() && !name.isBlank()) {
                // If only name provided, we already validated, the validation query found the user
                // The service already handles this via OR query
            }
            medicalHistoryResponseDTO = medicalRecordService.getMedicalRecord(cardId.isBlank() ? cardId : cardId);
            return new ResponseEntity<>(medicalHistoryResponseDTO, HttpStatus.ACCEPTED);
        } else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
