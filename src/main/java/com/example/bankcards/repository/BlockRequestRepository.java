package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRequestRepository extends JpaRepository<BlockRequest, Long> {
    List<BlockRequest> findByProcessedFalse();
    boolean existsByUserAndCard(User user, Card card);
}