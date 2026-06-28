package com.onkar.chc.repo;

import com.onkar.chc.entity.LabTestRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestRequestRepo extends JpaRepository<LabTestRequestEntity, Long> {
    List<LabTestRequestEntity> findByDoctorUserName(String doctorUserName);
    List<LabTestRequestEntity> findByPatientHealthCardId(String patientHealthCardId);
    boolean existsByPatientHealthCardIdAndDoctorUserName(String patientHealthCardId, String doctorUserName);
}
