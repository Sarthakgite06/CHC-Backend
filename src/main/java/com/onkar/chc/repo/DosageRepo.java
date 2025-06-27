package com.onkar.chc.repo;

import com.onkar.chc.entity.DosageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DosageRepo extends JpaRepository<DosageEntity, Integer> {
}
