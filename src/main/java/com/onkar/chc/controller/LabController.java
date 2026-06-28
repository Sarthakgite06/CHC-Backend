package com.onkar.chc.controller;

import com.onkar.chc.entity.LabReportEntity;
import com.onkar.chc.entity.LabTestRequestEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.LabReportRepo;
import com.onkar.chc.repo.LabTestRequestRepo;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.repo.MedicalRecordRepo;
import com.onkar.chc.requestDto.LabReportRequestDTO;
import com.onkar.chc.requestDto.LabTestRequestDTO;
import com.onkar.chc.responseDto.LabReportResponseDTO;
import com.onkar.chc.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/lab")
@Validated
@Slf4j
public class LabController {

    @Autowired
    private LabTestRequestRepo labTestRequestRepo;

    @Autowired
    private LabReportRepo labReportRepo;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private MedicalRecordRepo medicalRecordRepo;

    // Doctor requests a lab test
    @PostMapping("/requestTest")
    public ResponseEntity<LabTestRequestEntity> requestTest(@Valid @RequestBody LabTestRequestDTO requestDTO) {
        log.info("Creating lab test request by doctor: {}", requestDTO.getDoctorUserName());
        
        LabTestRequestEntity entity = LabTestRequestEntity.builder()
                .doctorUserName(requestDTO.getDoctorUserName())
                .patientHealthCardId(requestDTO.getPatientHealthCardId())
                .testName(requestDTO.getTestName())
                .notes(requestDTO.getNotes())
                .status("PENDING")
                .build();
                
        LabTestRequestEntity savedEntity = labTestRequestRepo.save(entity);
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
    }

    // Pathologist uploads a lab report with file
    @PostMapping("/uploadReport")
    public ResponseEntity<String> uploadReport(
            @RequestParam("labTestRequestId") Long labTestRequestId,
            @RequestParam("pathologistUserName") String pathologistUserName,
            @RequestParam("findings") String findings,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam("file") MultipartFile file) {
            
        log.info("Uploading lab report for test ID: {} by Pathologist: {}", labTestRequestId, pathologistUserName);

        LabTestRequestEntity testRequest = labTestRequestRepo.findById(labTestRequestId)
                .orElseThrow(() -> new DataNotFoundException("Lab test request not found"));

        String fileName = fileStorageService.storeFile(file);

        LabReportEntity reportEntity = LabReportEntity.builder()
                .labTestRequest(testRequest)
                .pathologistUserName(pathologistUserName)
                .findings(findings)
                .remarks(remarks)
                .attachmentPath(fileName)
                .attachmentOriginalName(file.getOriginalFilename())
                .build();

        labReportRepo.save(reportEntity);
        
        // Update request status
        testRequest.setStatus("COMPLETED");
        labTestRequestRepo.save(testRequest);

        return new ResponseEntity<>("Lab report uploaded successfully.", HttpStatus.CREATED);
    }

    // Download lab report file
    @GetMapping("/downloadReport/{reportId}")
    public ResponseEntity<?> downloadReportFile(@PathVariable Long reportId, HttpServletRequest request) {
        log.info("Downloading file for report ID: {}", reportId);

        LabReportEntity report = labReportRepo.findById(reportId)
                .orElseThrow(() -> new DataNotFoundException("Lab report not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Check if user is authorized to view this specific report
        if (!isAuthorizedToViewReport(currentUser, report)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        if (report.getAttachmentPath() == null) {
            throw new RuntimeException("No file attached to this report");
        }

        Resource resource = fileStorageService.loadFileAsResource(report.getAttachmentPath());

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getAttachmentOriginalName() + "\"")
                .body(resource);
    }
    
    // Get tests for a patient
    @GetMapping("/patient/{healthCardId}/reports")
    public ResponseEntity<?> getReportsForPatient(@PathVariable String healthCardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Check general authorization to access this patient's reports
        if (!isAuthorizedToViewPatientReports(currentUser, healthCardId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        List<LabReportEntity> reports = labReportRepo.findByLabTestRequest_PatientHealthCardId(healthCardId);

        // Filter for pathologist: they only see their own uploaded reports
        String role = currentUser.getRole().replace("ROLE_", "");
        if ("Pathologist".equalsIgnoreCase(role)) {
            reports = reports.stream()
                    .filter(r -> currentUser.getUsername().equalsIgnoreCase(r.getPathologistUserName()))
                    .toList();
        }

        List<LabReportResponseDTO> dtos = reports.stream()
                .map(LabReportResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // Pathologist deletes a lab report
    @DeleteMapping("/deleteReport/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long reportId) {
        log.info("Deleting lab report ID: {}", reportId);

        LabReportEntity report = labReportRepo.findById(reportId)
                .orElseThrow(() -> new DataNotFoundException("Lab report not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        String role = currentUser.getRole().replace("ROLE_", "");

        // Only a pathologist can delete a report
        if (!"Pathologist".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        // Revert status of lab test request back to PENDING
        LabTestRequestEntity testRequest = report.getLabTestRequest();
        if (testRequest != null) {
            testRequest.setStatus("PENDING");
            labTestRequestRepo.save(testRequest);
        }

        // Delete file on disk
        if (report.getAttachmentPath() != null) {
            try {
                Path filePath = Paths.get("uploads").resolve(report.getAttachmentPath()).toAbsolutePath().normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Failed to delete file from disk: {}", report.getAttachmentPath(), e);
            }
        }

        // Delete from database
        labReportRepo.delete(report);

        return ResponseEntity.ok("Lab report deleted successfully.");
    }

    private boolean isAuthorizedToViewPatientReports(UserEntity currentUser, String healthCardId) {
        String role = currentUser.getRole().replace("ROLE_", "");
        
        // 1. Respective patient
        if (currentUser.getHealthCardNo() != null && currentUser.getHealthCardNo().equalsIgnoreCase(healthCardId)) {
            return true;
        }
        
        // 2. Pathologist
        if ("Pathologist".equalsIgnoreCase(role)) {
            return true;
        }
        
        // 3. Doctor and Chemist (general permission to view patient records)
        if ("Doctor".equalsIgnoreCase(role) || "Chemist".equalsIgnoreCase(role)) {
            return true;
        }
        
        return false;
    }

    private boolean isAuthorizedToViewReport(UserEntity currentUser, LabReportEntity report) {
        if (report == null || report.getLabTestRequest() == null) {
            return false;
        }
        
        String healthCardId = report.getLabTestRequest().getPatientHealthCardId();
        String role = currentUser.getRole().replace("ROLE_", "");
        
        // 1. Respective patient
        if (currentUser.getHealthCardNo() != null && currentUser.getHealthCardNo().equalsIgnoreCase(healthCardId)) {
            return true;
        }
        
        // 2. Uploading pathologist
        if ("Pathologist".equalsIgnoreCase(role)) {
            return currentUser.getUsername().equalsIgnoreCase(report.getPathologistUserName());
        }
        
        // 3. Doctor and Chemist (general permission to view report)
        if ("Doctor".equalsIgnoreCase(role) || "Chemist".equalsIgnoreCase(role)) {
            return true;
        }
        
        return false;
    }
}
