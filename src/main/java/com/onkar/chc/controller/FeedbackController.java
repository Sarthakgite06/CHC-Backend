package com.onkar.chc.controller;

import com.onkar.chc.entity.FeedbackEntity;
import com.onkar.chc.repo.FeedbackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.onkar.chc.repo.DoctorRepo;
import com.onkar.chc.entity.DoctorEntity;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    /**
     * Any authenticated user can submit feedback
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {

        UserEntity user = userRepo.findByUserName(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(Map.of("msg", "User not found"), HttpStatus.NOT_FOUND);
        }

        FeedbackEntity feedback = FeedbackEntity.builder()
                .healthCardNo(user.getHealthCardNo())
                .userName(user.getUsername())
                .subject(body.get("subject"))
                .message(body.get("message"))
                .status("OPEN")
                .createdDate(LocalDate.now().toString())
                .build();

        feedbackRepo.save(feedback);

        return new ResponseEntity<>(Map.of("msg", "Feedback submitted successfully"), HttpStatus.CREATED);
    }

    /**
     * User sees their own feedback tickets
     */
    @GetMapping("/my")
    public ResponseEntity<List<FeedbackEntity>> getMyFeedbacks(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity user = userRepo.findByUserName(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
        }

        List<FeedbackEntity> feedbacks = feedbackRepo.findByHealthCardNo(user.getHealthCardNo());
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    /**
     * Doctor sees feedback intended for them (by finding their RegNo in the subject)
     */
    @GetMapping("/doctor")
    public ResponseEntity<List<FeedbackEntity>> getDoctorFeedbacks(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity user = userRepo.findByUserName(userDetails.getUsername()).orElse(null);
        if (user == null || !"Doctor".equals(user.getRole().replace("ROLE_", ""))) {
            return new ResponseEntity<>(List.of(), HttpStatus.FORBIDDEN);
        }

        // Get the doctor's registration number
        DoctorEntity doc = doctorRepo.findByUserName(user.getUsername()).orElse(null);
        if (doc == null) {
            return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
        }

        // Search for feedbacks where subject contains "[Doctor {regNo}]"
        String searchKeyword = "[Doctor " + doc.getDoctorRegiNo() + "]";
        List<FeedbackEntity> feedbacks = feedbackRepo.findBySubjectContainingIgnoreCase(searchKeyword);
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }
}
