package com.onkar.chc.repo;

import com.onkar.chc.entity.ChemistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChemistRepo extends JpaRepository<ChemistEntity, Long> {
    Optional<ChemistEntity> findByUserName(String userName);
    Optional<ChemistEntity> findByChemistRegiNo(Long chemistRegiNo);
}
