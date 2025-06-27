package com.onkar.chc.repo;

import com.onkar.chc.entity.MedicineInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineInfoRepo extends JpaRepository<MedicineInfoEntity, String> {
}
