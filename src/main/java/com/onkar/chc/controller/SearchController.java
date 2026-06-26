package com.onkar.chc.controller;

import com.onkar.chc.entity.UserEntity;
import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.service.HealthCardIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chc")
public class SearchController {

    @Autowired
    private UserRepo userRepo;

    /**
     * Search autocomplete — returns matching users by name or health card ID prefix.
     * Returns ONLY public-safe fields (no passwords, no medical data).
     */
    @GetMapping("/search-users")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam String q) {
        if (q == null || q.trim().length() < 2) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }

        List<UserEntity> results = userRepo.searchUsers(q.trim());

        // Limit results + strip private data
        List<Map<String, Object>> suggestions = results.stream()
                .limit(10)
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("userName", u.getUsername());
                    m.put("firstName", u.getFirstName());
                    m.put("lastName", u.getLastName());
                    m.put("healthCardNo", u.getHealthCardNo());
                    m.put("role", u.getRole());
                    m.put("district", u.getDistrict());
                    return m;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(suggestions, HttpStatus.OK);
    }

    /**
     * Returns list of supported districts for the signup dropdown
     */
    @GetMapping("/districts")
    public ResponseEntity<Map<String, String>> getDistricts() {
        return new ResponseEntity<>(HealthCardIdGenerator.getSupportedDistricts(), HttpStatus.OK);
    }
}
