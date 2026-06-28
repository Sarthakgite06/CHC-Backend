package com.onkar.chc.repo;

import com.onkar.chc.entity.MedicalImagingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalImagingRepo extends JpaRepository<MedicalImagingEntity, Long> {
    
    List<MedicalImagingEntity> findByHealthCardNoAndIsDeletedFalse(String healthCardNo);
    
    Optional<MedicalImagingEntity> findByIdAndIsDeletedFalse(Long id);
}
