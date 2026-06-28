package com.onkar.chc.repo;

import com.onkar.chc.entity.ChatHistoryEntity;
import com.onkar.chc.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepo extends JpaRepository<ChatHistoryEntity, Long> {
    List<ChatHistoryEntity> findByUserOrderByTimestampAsc(UserEntity user);
    void deleteByUser(UserEntity user);
}
