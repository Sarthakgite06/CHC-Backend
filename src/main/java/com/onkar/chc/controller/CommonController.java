package com.onkar.chc.controller;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.responseDto.MedicalHistoryResponseDTO;
import com.onkar.chc.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chc")
public class CommonController {

    @Autowired
    MedicalRecordService medicalRecordService;

    @Autowired
    UserRepo userRepo;

    /**
     * Get patient medical history.
     * Flexible search: provide EITHER healthCardNo OR userName (or both).
     */
    @GetMapping("/getPatientMedicalHistory")
    public ResponseEntity<MedicalHistoryResponseDTO> getMedicalHistory(
            @RequestParam(required = false) String healthCardNo,
            @RequestParam(required = false) String userName) {

        // Block Pathologists — they only need lab reports, not prescriptions
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity currentUser) {
            String role = currentUser.getRole().replace("ROLE_", "");
            if ("Pathologist".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // Use whichever is provided for validation
        String cardId = (healthCardNo != null && !healthCardNo.isBlank()) ? healthCardNo : "";
        String name = (userName != null && !userName.isBlank()) ? userName : "";

        Boolean isPatientValid = medicalRecordService.validatePatient(name, cardId);
        if (isPatientValid) {
            String lookupId = cardId;
            if (lookupId.isBlank() && !name.isBlank()) {
                UserEntity user = userRepo.findByUserName(name).orElse(null);
                if (user != null && user.getHealthCardNo() != null) {
                    lookupId = user.getHealthCardNo();
                }
            }
            MedicalHistoryResponseDTO medicalHistoryResponseDTO = medicalRecordService.getMedicalRecord(lookupId);
            return new ResponseEntity<>(medicalHistoryResponseDTO, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
