package com.onkar.chc.repo;

import com.onkar.chc.entity.PathologistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PathologistRepo extends JpaRepository<PathologistEntity, Long> {
    Optional<PathologistEntity> findByUserName(String userName);
    Optional<PathologistEntity> findByLicenseNo(String licenseNo);
}
