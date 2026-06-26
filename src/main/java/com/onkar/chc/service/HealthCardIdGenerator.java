package com.onkar.chc.service;

import com.onkar.chc.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates unique Health Card IDs in the format: {DISTRICT_PREFIX}{8-digit sequential}
 * Example: PUN00000001, MUM00000002, DEL00000003
 */
@Service
public class HealthCardIdGenerator {

    @Autowired
    private UserRepo userRepo;

    // District name → 3-letter prefix mapping
    private static final Map<String, String> DISTRICT_PREFIX_MAP = new LinkedHashMap<>();

    static {
        // Maharashtra
        DISTRICT_PREFIX_MAP.put("Pune", "PUN");
        DISTRICT_PREFIX_MAP.put("Mumbai", "MUM");
        DISTRICT_PREFIX_MAP.put("Nagpur", "NAG");
        DISTRICT_PREFIX_MAP.put("Nashik", "NSK");
        DISTRICT_PREFIX_MAP.put("Thane", "THN");
        DISTRICT_PREFIX_MAP.put("Aurangabad", "AUR");
        DISTRICT_PREFIX_MAP.put("Solapur", "SOL");
        DISTRICT_PREFIX_MAP.put("Kolhapur", "KOL");
        // Delhi NCR
        DISTRICT_PREFIX_MAP.put("Delhi", "DEL");
        DISTRICT_PREFIX_MAP.put("Noida", "NOI");
        DISTRICT_PREFIX_MAP.put("Gurgaon", "GUR");
        // Karnataka
        DISTRICT_PREFIX_MAP.put("Bangalore", "BLR");
        DISTRICT_PREFIX_MAP.put("Mysore", "MYS");
        // Tamil Nadu
        DISTRICT_PREFIX_MAP.put("Chennai", "CHN");
        DISTRICT_PREFIX_MAP.put("Coimbatore", "CBE");
        // Telangana
        DISTRICT_PREFIX_MAP.put("Hyderabad", "HYD");
        // West Bengal
        DISTRICT_PREFIX_MAP.put("Kolkata", "KOL");
        // Gujarat
        DISTRICT_PREFIX_MAP.put("Ahmedabad", "AHM");
        DISTRICT_PREFIX_MAP.put("Surat", "SUR");
        // Rajasthan
        DISTRICT_PREFIX_MAP.put("Jaipur", "JAI");
        DISTRICT_PREFIX_MAP.put("Jodhpur", "JOD");
        // Uttar Pradesh
        DISTRICT_PREFIX_MAP.put("Lucknow", "LKN");
        DISTRICT_PREFIX_MAP.put("Varanasi", "VRN");
        DISTRICT_PREFIX_MAP.put("Kanpur", "KNP");
        // Madhya Pradesh
        DISTRICT_PREFIX_MAP.put("Bhopal", "BPL");
        DISTRICT_PREFIX_MAP.put("Indore", "IDR");
        // Punjab
        DISTRICT_PREFIX_MAP.put("Chandigarh", "CHD");
        DISTRICT_PREFIX_MAP.put("Amritsar", "AMR");
        // Kerala
        DISTRICT_PREFIX_MAP.put("Kochi", "KCH");
        DISTRICT_PREFIX_MAP.put("Thiruvananthapuram", "TVM");
        // Others
        DISTRICT_PREFIX_MAP.put("Patna", "PAT");
        DISTRICT_PREFIX_MAP.put("Bhubaneswar", "BBS");
        DISTRICT_PREFIX_MAP.put("Guwahati", "GHY");
        DISTRICT_PREFIX_MAP.put("Dehradun", "DDN");
        DISTRICT_PREFIX_MAP.put("Shimla", "SML");
        DISTRICT_PREFIX_MAP.put("Ranchi", "RNC");
        DISTRICT_PREFIX_MAP.put("Raipur", "RPR");
        DISTRICT_PREFIX_MAP.put("Goa", "GOA");
    }

    /**
     * Returns the list of supported districts for the signup form dropdown.
     */
    public static Map<String, String> getSupportedDistricts() {
        return DISTRICT_PREFIX_MAP;
    }

    /**
     * Generates a unique health card ID for the given district.
     * Format: PUN00000001 (3-letter prefix + 8-digit zero-padded sequence)
     */
    @Transactional
    public String generateHealthCardId(String district) {
        String prefix = DISTRICT_PREFIX_MAP.getOrDefault(district, district.substring(0, Math.min(3, district.length())).toUpperCase());

        long count = userRepo.countByDistrict(district);
        long nextSeq = count + 1;

        return String.format("%s%08d", prefix, nextSeq);
    }
}
