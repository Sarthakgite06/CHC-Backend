package com.onkar.chc.controller;

import com.onkar.chc.entity.MedicalImagingEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.MedicalImagingRepo;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.responseDto.MedicalImagingResponseDTO;
import com.onkar.chc.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/medical-imaging")
@Slf4j
public class MedicalImagingController {

    @Autowired
    private MedicalImagingRepo medicalImagingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileStorageService fileStorageService;

    // POST /medical-imaging/upload
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImaging(
            @RequestParam("file") MultipartFile file,
            @RequestParam("healthCardNo") String healthCardNo,
            @RequestParam("imagingType") String imagingType,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("hospitalName") String hospitalName) {

        log.info("Doctor uploading medical imaging of type: {} for patient card: {}", imagingType, healthCardNo);

        // Fetch logged-in doctor
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity doctor = (UserEntity) authentication.getPrincipal();

        // Check if user is a Doctor
        String role = doctor.getRole().replace("ROLE_", "");
        if (!"Doctor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only doctors are authorized to upload imaging records");
        }

        // Fetch patient
        UserEntity patient = userRepo.findByHealthCardNo(healthCardNo)
                .orElseThrow(() -> new DataNotFoundException("Patient not found with Health Card ID: " + healthCardNo));

        // Format validation
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name");
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name. Extension required.");
        }
        String extension = originalFilename.substring(dotIndex).toLowerCase();
        List<String> allowedExtensions = List.of(".pdf", ".jpg", ".jpeg", ".png", ".dcm");
        if (!allowedExtensions.contains(extension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported file format. Allowed: PDF, JPG, JPEG, PNG, DICOM (.dcm)");
        }

        // Size validation (50 MB limit)
        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds maximum limit of 50 MB");
        }

        // Save file
        String savedFileName = fileStorageService.storeFile(file);

        // Save database record
        MedicalImagingEntity entity = MedicalImagingEntity.builder()
                .patient(patient)
                .doctor(doctor)
                .healthCardNo(healthCardNo)
                .imagingType(imagingType)
                .title(title)
                .description(description)
                .hospitalName(hospitalName)
                .fileName(savedFileName)
                .fileType(extension.substring(1).toUpperCase())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        MedicalImagingEntity savedEntity = medicalImagingRepo.save(entity);

        // Set file url
        savedEntity.setFileUrl("/medical-imaging/download/" + savedEntity.getId());
        medicalImagingRepo.save(savedEntity);

        return new ResponseEntity<>(MedicalImagingResponseDTO.fromEntity(savedEntity), HttpStatus.CREATED);
    }

    // GET /medical-imaging/patient/{healthCardNo}
    @GetMapping("/patient/{healthCardNo}")
    public ResponseEntity<?> getPatientImagingRecords(@PathVariable String healthCardNo) {
        log.info("Fetching imaging records for patient: {}", healthCardNo);

        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        String role = currentUser.getRole().replace("ROLE_", "");

        // Access check: Patients can only view their own card. Doctors & Chemists can view patient records.
        boolean isOwner = currentUser.getHealthCardNo() != null && currentUser.getHealthCardNo().equalsIgnoreCase(healthCardNo);
        boolean isAllowedRole = "Doctor".equalsIgnoreCase(role) || "Chemist".equalsIgnoreCase(role);
        if (!isOwner && !isAllowedRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        List<MedicalImagingEntity> records = medicalImagingRepo.findByHealthCardNoAndIsDeletedFalse(healthCardNo);
        List<MedicalImagingResponseDTO> dtos = records.stream()
                .map(MedicalImagingResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // GET /medical-imaging/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getImagingRecordDetails(@PathVariable Long id) {
        log.info("Fetching details for imaging record ID: {}", id);

        MedicalImagingEntity imaging = medicalImagingRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException("Imaging record not found"));

        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        String role = currentUser.getRole().replace("ROLE_", "");

        // Access check: Owner, Doctor or Chemist
        boolean isOwner = currentUser.getHealthCardNo() != null && currentUser.getHealthCardNo().equalsIgnoreCase(imaging.getHealthCardNo());
        boolean isAllowedRole = "Doctor".equalsIgnoreCase(role) || "Chemist".equalsIgnoreCase(role);
        if (!isOwner && !isAllowedRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        return ResponseEntity.ok(MedicalImagingResponseDTO.fromEntity(imaging));
    }

    // GET /medical-imaging/download/{id}
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadImagingFile(@PathVariable Long id, HttpServletRequest request) {
        log.info("Downloading file for imaging record ID: {}", id);

        MedicalImagingEntity imaging = medicalImagingRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException("Imaging record not found"));

        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        String role = currentUser.getRole().replace("ROLE_", "");

        // Access check: patient (owner) and doctors can view/download; chemists see metadata only
        boolean isOwner = currentUser.getHealthCardNo() != null && currentUser.getHealthCardNo().equalsIgnoreCase(imaging.getHealthCardNo());
        boolean isDoctor = "Doctor".equalsIgnoreCase(role);
        if (!isOwner && !isDoctor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Only patients and doctors can download or preview these files");
        }

        Resource resource = fileStorageService.loadFileAsResource(imaging.getFileName());

        // Determine content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imaging.getFileName() + "\"")
                .body(resource);
    }

    // DELETE /medical-imaging/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImagingRecord(@PathVariable Long id) {
        log.info("Deleting imaging record ID: {}", id);

        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        String role = currentUser.getRole().replace("ROLE_", "");

        // Only doctors can delete
        if (!"Doctor".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only doctors are authorized to delete imaging records");
        }

        MedicalImagingEntity imaging = medicalImagingRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException("Imaging record not found"));

        // Soft delete in database
        imaging.setIsDeleted(true);
        medicalImagingRepo.save(imaging);

        // Delete physical file on disk to free up space
        if (imaging.getFileName() != null) {
            try {
                Path filePath = Paths.get("uploads").resolve(imaging.getFileName()).toAbsolutePath().normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Failed to delete physical file from disk: {}", imaging.getFileName(), e);
            }
        }

        return ResponseEntity.ok("Medical imaging record deleted successfully.");
    }
}
