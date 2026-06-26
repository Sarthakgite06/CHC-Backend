package com.onkar.chc.controller;

import com.onkar.chc.entity.FeedbackEntity;
import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.repo.ChemistRepo;
import com.onkar.chc.repo.FeedbackRepo;
import com.onkar.chc.repo.MedicalRecordRepo;
import com.onkar.chc.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private ChemistRepo chemistRepo;

    @Autowired
    private MedicalRecordRepo medicalRecordRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * System-wide stats: total users, role counts, feedback counts
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        List<UserEntity> allUsers = userRepo.findAll();

        // Admins are team members, NOT counted as registered users
        long doctors = allUsers.stream().filter(u -> "Doctor".equals(u.getRole())).count();
        long chemists = allUsers.stream().filter(u -> "Chemist".equals(u.getRole())).count();
        long patients = allUsers.stream().filter(u -> "User".equals(u.getRole()) || "Patient".equals(u.getRole())).count();
        long teamMembers = allUsers.stream().filter(u -> "Admin".equals(u.getRole())).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRegistered", doctors + chemists + patients);  // only actual users
        stats.put("doctors", doctors);
        stats.put("chemists", chemists);
        stats.put("patients", patients);
        stats.put("teamMembers", teamMembers);  // admin team size (separate from users)
        stats.put("totalRecords", medicalRecordRepo.count());
        stats.put("feedbackOpen", feedbackRepo.countByStatus("OPEN"));
        stats.put("feedbackInProgress", feedbackRepo.countByStatus("IN_PROGRESS"));
        stats.put("feedbackResolved", feedbackRepo.countByStatus("RESOLVED"));

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * District-wise user distribution for charts
     */
    @GetMapping("/district-stats")
    public ResponseEntity<List<Map<String, Object>>> getDistrictStats() {
        List<Object[]> results = userRepo.countByDistrictGrouped();
        List<Map<String, Object>> districtStats = results.stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("district", row[0]);
            m.put("count", row[1]);
            return m;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(districtStats, HttpStatus.OK);
    }

    /**
     * Role-wise distribution for pie chart
     */
    @GetMapping("/role-stats")
    public ResponseEntity<List<Map<String, Object>>> getRoleStats() {
        List<Object[]> results = userRepo.countByRoleGrouped();
        List<Map<String, Object>> roleStats = results.stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("role", row[0]);
            m.put("count", row[1]);
            return m;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(roleStats, HttpStatus.OK);
    }

    /**
     * All users list — NO passwords, NO private medical data
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<UserEntity> allUsers = userRepo.findAll();

        List<Map<String, Object>> userList = allUsers.stream().map(u -> {
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("id", u.getUserId());
            userMap.put("userName", u.getUsername());
            userMap.put("firstName", u.getFirstName());
            userMap.put("lastName", u.getLastName());
            userMap.put("email", u.getEmail());
            userMap.put("role", u.getRole());
            userMap.put("healthCardNo", u.getHealthCardNo());
            userMap.put("district", u.getDistrict());
            userMap.put("contactNo", u.getContactNo());
            userMap.put("gender", u.getGender());
            userMap.put("bloodGroup", u.getBloodGroup());
            userMap.put("dob", u.getDob());
            // doctorRegiNo and chemistRegiNo removed from UserEntity
            userMap.put("createdAt", u.getCreatedAt());
            return userMap;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    /**
     * Verify doctor by registration number
     */
    @GetMapping("/verify-doctor/{regNo}")
    public ResponseEntity<Map<String, Object>> verifyDoctor(@PathVariable Long regNo) {
        Map<String, Object> result = new LinkedHashMap<>();
        com.onkar.chc.entity.DoctorEntity doctorEntity = doctorRepo.findByDoctorRegiNo(regNo).orElse(null);
        UserEntity doctor = null;
        if (doctorEntity != null) {
            doctor = userRepo.findByUserName(doctorEntity.getUserName()).orElse(null);
        }

        if (doctor != null && "Doctor".equals(doctor.getRole())) {
            result.put("verified", true);
            result.put("doctorName", doctor.getFirstName() + " " + doctor.getLastName());
            result.put("healthCardNo", doctor.getHealthCardNo());
            result.put("district", doctor.getDistrict());
            result.put("registeredOn", doctor.getCreatedAt());
        } else {
            result.put("verified", false);
            result.put("message", "No verified doctor found with registration number: " + regNo);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Verify chemist by registration number
     */
    @GetMapping("/verify-chemist/{regNo}")
    public ResponseEntity<Map<String, Object>> verifyChemist(@PathVariable Long regNo) {
        Map<String, Object> result = new LinkedHashMap<>();
        com.onkar.chc.entity.ChemistEntity chemistEntity = chemistRepo.findByChemistRegiNo(regNo).orElse(null);
        UserEntity chemist = null;
        if (chemistEntity != null) {
            chemist = userRepo.findByUserName(chemistEntity.getUserName()).orElse(null);
        }

        if (chemist != null && "Chemist".equals(chemist.getRole())) {
            result.put("verified", true);
            result.put("chemistName", chemist.getFirstName() + " " + chemist.getLastName());
            result.put("healthCardNo", chemist.getHealthCardNo());
            result.put("district", chemist.getDistrict());
            result.put("registeredOn", chemist.getCreatedAt());
        } else {
            result.put("verified", false);
            result.put("message", "No verified chemist found with registration number: " + regNo);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * All feedback tickets for admin to manage
     */
    @GetMapping("/feedbacks")
    public ResponseEntity<List<FeedbackEntity>> getAllFeedbacks() {
        List<FeedbackEntity> feedbacks = feedbackRepo.findAll();
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    /**
     * Admin responds to a feedback ticket
     */
    @PutMapping("/feedbacks/{id}/respond")
    public ResponseEntity<Map<String, String>> respondToFeedback(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        FeedbackEntity feedback = feedbackRepo.findById(id).orElse(null);
        if (feedback == null) {
            return new ResponseEntity<>(Map.of("msg", "Feedback not found"), HttpStatus.NOT_FOUND);
        }

        feedback.setAdminResponse(body.get("response"));
        feedback.setStatus(body.getOrDefault("status", "RESOLVED"));
        feedback.setResolvedDate(LocalDate.now().toString());
        feedbackRepo.save(feedback);

        return new ResponseEntity<>(Map.of("msg", "Response sent successfully"), HttpStatus.OK);
    }

    /**
     * Existing admin creates a new admin team member.
     * Admin is a monitoring platform — team members can self-register through this.
     */
    @PostMapping("/create-member")
    public ResponseEntity<Map<String, String>> createAdminMember(@RequestBody Map<String, String> body) {
        String userName = body.get("userName");
        String email = body.get("email");
        String password = body.get("password");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");

        if (userName == null || email == null || password == null) {
            return new ResponseEntity<>(Map.of("msg", "userName, email and password are required"), HttpStatus.BAD_REQUEST);
        }

        // Check if username already exists
        if (userRepo.findByUserName(userName).isPresent()) {
            return new ResponseEntity<>(Map.of("msg", "Username already exists"), HttpStatus.CONFLICT);
        }

        UserEntity newAdmin = UserEntity.builder()
                .userName(userName)
                .firstName(firstName != null ? firstName : userName)
                .lastName(lastName != null ? lastName : "")
                .password(passwordEncoder.encode(password))
                .email(email)
                .role("Admin")
                .createdAt(LocalDate.now().toString())
                .build();

        userRepo.save(newAdmin);

        return new ResponseEntity<>(Map.of("msg", "Team member " + userName + " added successfully"), HttpStatus.CREATED);
    }
}
