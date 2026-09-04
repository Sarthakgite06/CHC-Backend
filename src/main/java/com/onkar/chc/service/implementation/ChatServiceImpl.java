package com.onkar.chc.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onkar.chc.entity.*;
import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.repo.*;
import com.onkar.chc.requestDto.ChatRequestDTO;
import com.onkar.chc.responseDto.ChatResponseDTO;
import com.onkar.chc.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

        private final ChatClient.Builder chatClientBuilder;
        private final ChatHistoryRepo chatHistoryRepo;
        private final PatientRepo patientRepo;
        private final UserRepo userRepo;
        private final MedicalRecordRepo medicalRecordRepo;
        private final LabReportRepo labReportRepo;

        private final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

        @Override
        @Transactional
        public ChatResponseDTO chat(String userName, ChatRequestDTO request) {
                UserEntity user = userRepo.findByUserName(userName)
                                .orElseThrow(() -> new DataNotFoundException("User not found"));

                saveChatHistory(user, "USER", request.getMessage());

                String systemPrompt = buildSystemPrompt(user, request);

                ChatClient chatClient = chatClientBuilder.build();

                String aiResponse = "";
                try {
                        aiResponse = chatClient.prompt()
                                        .system(systemPrompt)
                                        .user(request.getMessage())
                                        .call()
                                        .content();
                } catch (Exception e) {
                        log.error("AI prompt failed", e);
                        aiResponse = "I am currently unable to process your request. Please try again later. (Error: "
                                        + e.getMessage() + ")";
                }

                saveChatHistory(user, "AI", aiResponse);

                return ChatResponseDTO.builder().reply(aiResponse).build();
        }

        private String buildSystemPrompt(UserEntity user, ChatRequestDTO request) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                String basePrompt = "You are an AI Health Assistant for the Centralized Health Card System. " +
                                "You help users by answering questions based on their data and guiding them through the website. "
                                +
                                "Here is a guide to the website's pages:\n" +
                                "- Dashboard (/dashboard): Home overview of health cards and appointments.\n" +
                                "- Medical History (/medical-history): View past medical records, prescriptions, and lab reports.\n"
                                +
                                "- Create Record (/create-record): Doctors create new medical records/prescriptions.\n"
                                +
                                "- Profile (/profile): View personal details and download health card.\n" +
                                "- Admin Panel (/admin-panel): Administrators manage platform settings.\n" +
                                "- Feedback (/feedback): Submit feedback or complaints.\n" +
                                "- Verify Prescription (/verify-prescription): Check authenticity of prescriptions.\n" +
                                "- Upload Report (/upload-report): Pathologists upload test results.\n" +
                                "- Healthcare Test (/health-test): Find or book healthcare lab tests.\n\n" +
                                "Do not diagnose diseases or prescribe medicines. " +
                                "Always display: 'This information is for educational purposes only. Please consult your doctor for medical advice.' at the end of medical answers.\n\n"
                                +
                                "STRICT RESTRICTION: You must ONLY answer questions directly related to this application (Centralized Health Card System), website navigation, page guides, or the patient's medical records/data provided to you. "
                                +
                                "If the user asks questions about general knowledge, general science, history, geography, sports, math, coding, or any other topic unrelated to this project or healthcare, you must politely decline to answer, stating that you can only help with the Centralized Health Card System and healthcare assistance.\n\n";

                try {
                        String role = user.getRole() != null ? user.getRole().replace("ROLE_", "") : "";
                        if ("Patient".equalsIgnoreCase(role) || "User".equalsIgnoreCase(role)) {
                                PatientEntity patient = patientRepo.findByUserName(user.getUsername()).orElse(null);
                                String healthCardNo = patient != null ? patient.getHealthCardNo()
                                                : user.getHealthCardNo();

                                String profile = String.format(
                                                "HealthCardNo: %s, Name: %s, DOB: %s, Gender: %s, BloodGroup: %s",
                                                healthCardNo, user.getUsername(), user.getDob(), user.getGender(),
                                                user.getBloodGroup());

                                List<MedicalRecordEntity> records = patient != null
                                                ? medicalRecordRepo.findByPatientEntity(patient).orElse(List.of())
                                                : List.of();
                                StringBuilder prescriptionsBuf = new StringBuilder();
                                for (MedicalRecordEntity r : records) {
                                        prescriptionsBuf.append(
                                                        String.format("[RecordID: %s, Date: %s, DoctorReg: %s] ",
                                                                        r.getMedicalRecordId(), r.getCreatedDate(),
                                                                        r.getDoctorRegNo()));
                                }

                                List<LabReportEntity> labs = (healthCardNo != null && !healthCardNo.isBlank())
                                                ? labReportRepo.findByLabTestRequest_PatientHealthCardId(healthCardNo)
                                                : List.of();
                                StringBuilder labBuf = new StringBuilder();
                                for (LabReportEntity lab : labs) {
                                        labBuf.append(String.format("[Findings: %s, Remarks: %s, Date: %s] ",
                                                        lab.getFindings(), lab.getRemarks(), lab.getReportDate()));
                                }

                                return basePrompt + "Here is the patient's data:\n" +
                                                "Profile: " + profile + "\n" +
                                                "Prescriptions: "
                                                + (prescriptionsBuf.length() > 0 ? prescriptionsBuf.toString() : "None")
                                                + "\n" +
                                                "Lab Reports: " + (labBuf.length() > 0 ? labBuf.toString() : "None");

                        } else if ("Doctor".equalsIgnoreCase(role)) {
                                if (request.getTargetHealthCardId() == null
                                                || request.getTargetHealthCardId().isBlank()) {
                                        return basePrompt
                                                        + "You are assisting a doctor. The doctor has not selected a patient yet. "
                                                        +
                                                        "Tell the doctor to search for a patient using their Health Card ID to view their records.";
                                }

                                PatientEntity patient = patientRepo.findById(request.getTargetHealthCardId())
                                                .orElse(null);
                                UserEntity patientUser = userRepo.findByHealthCardNo(request.getTargetHealthCardId())
                                                .orElse(null);

                                if (patient == null && patientUser == null) {
                                        return basePrompt
                                                        + "You are assisting a doctor. The patient with Health Card ID: "
                                                        + request.getTargetHealthCardId() + " was not found.";
                                }

                                String profile = patient != null
                                                ? String.format("HealthCardNo: %s, Name: %s, DOB: %s, Age: %s, Gender: %s, BloodPressure: %s",
                                                                patient.getHealthCardNo(), patient.getUserName(),
                                                                patient.getDob(), patient.getAge(), patient.getGender(),
                                                                patient.getBloodPressure())
                                                : String.format("HealthCardNo: %s, Name: %s, DOB: %s, Gender: %s, BloodGroup: %s",
                                                                patientUser.getHealthCardNo(),
                                                                patientUser.getUsername(), patientUser.getDob(),
                                                                patientUser.getGender(), patientUser.getBloodGroup());

                                List<MedicalRecordEntity> records = patient != null
                                                ? medicalRecordRepo.findByPatientEntity(patient).orElse(List.of())
                                                : List.of();
                                StringBuilder prescriptionsBuf = new StringBuilder();
                                for (MedicalRecordEntity r : records) {
                                        prescriptionsBuf.append(
                                                        String.format("[RecordID: %s, Date: %s, DoctorReg: %s] ",
                                                                        r.getMedicalRecordId(), r.getCreatedDate(),
                                                                        r.getDoctorRegNo()));
                                }

                                List<LabReportEntity> labs = labReportRepo.findByLabTestRequest_PatientHealthCardId(
                                                request.getTargetHealthCardId());
                                StringBuilder labBuf = new StringBuilder();
                                for (LabReportEntity lab : labs) {
                                        labBuf.append(String.format("[Findings: %s, Remarks: %s, Date: %s] ",
                                                        lab.getFindings(), lab.getRemarks(), lab.getReportDate()));
                                }

                                return basePrompt
                                                + "You are assisting a doctor. The doctor is querying about a patient with Health Card ID: "
                                                + request.getTargetHealthCardId() + ".\n" +
                                                "Here is the patient's data:\n" +
                                                "Profile: " + profile + "\n" +
                                                "Prescriptions: "
                                                + (prescriptionsBuf.length() > 0 ? prescriptionsBuf.toString() : "None")
                                                + "\n" +
                                                "Lab Reports: " + (labBuf.length() > 0 ? labBuf.toString() : "None");
                        }
                } catch (Exception e) {
                        log.error("Failed to parse data for context", e);
                        return basePrompt + " (Error loading patient data into context)";
                }

                return basePrompt;
        }

        private void saveChatHistory(UserEntity user, String sender, String message) {
                chatHistoryRepo.save(ChatHistoryEntity.builder()
                                .user(user)
                                .sender(sender)
                                .message(message)
                                .build());
        }

        @Override
        public List<ChatHistoryEntity> getHistory(String userName) {
                UserEntity user = userRepo.findByUserName(userName)
                                .orElseThrow(() -> new DataNotFoundException("User not found"));
                return chatHistoryRepo.findByUserOrderByTimestampAsc(user);
        }

        @Override
        @Transactional
        public void clearHistory(String userName) {
                UserEntity user = userRepo.findByUserName(userName)
                                .orElseThrow(() -> new DataNotFoundException("User not found"));
                chatHistoryRepo.deleteByUser(user);
        }
}
