package com.onkar.chc.controller;

import com.onkar.chc.entity.LabReportEntity;
import com.onkar.chc.entity.LabTestRequestEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.LabReportRepo;
import com.onkar.chc.repo.LabTestRequestRepo;
import com.onkar.chc.requestDto.LabReportRequestDTO;
import com.onkar.chc.requestDto.LabTestRequestDTO;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    // Doctor requests a lab test
    @PostMapping("/requestTest")
    public ResponseEntity<String> requestTest(@Valid @RequestBody LabTestRequestDTO requestDTO) {
        log.info("Creating lab test request by doctor: {}", requestDTO.getDoctorUserName());
        
        LabTestRequestEntity entity = LabTestRequestEntity.builder()
                .doctorUserName(requestDTO.getDoctorUserName())
                .patientHealthCardId(requestDTO.getPatientHealthCardId())
                .testName(requestDTO.getTestName())
                .notes(requestDTO.getNotes())
                .status("PENDING")
                .build();
                
        labTestRequestRepo.save(entity);
        return new ResponseEntity<>("Lab test requested successfully.", HttpStatus.CREATED);
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
    public ResponseEntity<Resource> downloadReportFile(@PathVariable Long reportId, HttpServletRequest request) {
        log.info("Downloading file for report ID: {}", reportId);

        LabReportEntity report = labReportRepo.findById(reportId)
                .orElseThrow(() -> new DataNotFoundException("Lab report not found"));

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
    public ResponseEntity<List<LabReportEntity>> getReportsForPatient(@PathVariable String healthCardId) {
        List<LabReportEntity> reports = labReportRepo.findByLabTestRequest_PatientHealthCardId(healthCardId);
        return ResponseEntity.ok(reports);
    }
}
