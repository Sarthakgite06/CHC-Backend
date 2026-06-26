package com.onkar.chc.repo;

import com.onkar.chc.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepo extends JpaRepository<DoctorEntity, Long> {
    Optional<DoctorEntity> findByUserName(String userName);
    Optional<DoctorEntity> findByDoctorRegiNo(Long doctorRegiNo);
}
