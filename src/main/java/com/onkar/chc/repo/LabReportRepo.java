package com.onkar.chc.repo;

import com.onkar.chc.entity.LabReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabReportRepo extends JpaRepository<LabReportEntity, Long> {
    List<LabReportEntity> findByPathologistUserName(String pathologistUserName);
    List<LabReportEntity> findByLabTestRequest_PatientHealthCardId(String patientHealthCardId);
}
