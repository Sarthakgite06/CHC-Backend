package com.onkar.chc.repo;

import com.onkar.chc.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<PatientEntity, String> {
    Optional<PatientEntity> findByUserName(String userName);
}
