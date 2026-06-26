package com.onkar.chc.repo;

import com.onkar.chc.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUserName(String userName);
    
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.userName) = LOWER(:userName) OR LOWER(u.email) = LOWER(:email)")
    Optional<UserEntity> findByUserNameOrEmail(@Param("userName") String userName, @Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByHealthCardNo(String healthCardNo);

    long countByDistrict(String district);

    long countByRole(String role);

    // Flexible validation: match by userName OR healthCardNo
    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName OR u.healthCardNo = :healthCardNo")
    Optional<UserEntity> getUserDataForValidation(@Param("userName") String userName, @Param("healthCardNo") String healthCardNo);

    // Search autocomplete: find users by partial name or health card ID
    @Query("SELECT u FROM UserEntity u WHERE " +
           "LOWER(u.userName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.healthCardNo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<UserEntity> searchUsers(@Param("query") String query);

    // District-wise counts for admin charts
    @Query("SELECT u.district, COUNT(u) FROM UserEntity u GROUP BY u.district")
    List<Object[]> countByDistrictGrouped();

    // Role-wise counts for admin charts
    @Query("SELECT u.role, COUNT(u) FROM UserEntity u GROUP BY u.role")
    List<Object[]> countByRoleGrouped();
}
