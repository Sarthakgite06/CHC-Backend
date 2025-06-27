package com.onkar.chc.repo;

import com.onkar.chc.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {
    public Optional<UserEntity> findByUserName(String userName);

    public Optional<UserEntity> findByDoctorRegiNo(Long doctorRegiNo);

    public Optional<UserEntity> findByHealthCardNo(Integer healthCardNo);

    @Query(value = "SELECT * FROM CHC.user_entity where user_id=? and health_card_no=?;", nativeQuery = true)
    public Optional<UserEntity> getUserDataForValidation(String userName, Integer healthCardNo);

}
