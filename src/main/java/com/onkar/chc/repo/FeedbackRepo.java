package com.onkar.chc.repo;

import com.onkar.chc.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepo extends JpaRepository<FeedbackEntity, String> {

    List<FeedbackEntity> findByHealthCardNo(String healthCardNo);

    List<FeedbackEntity> findBySubjectContainingIgnoreCase(String keyword);

    List<FeedbackEntity> findByStatus(String status);

    long countByStatus(String status);
}
