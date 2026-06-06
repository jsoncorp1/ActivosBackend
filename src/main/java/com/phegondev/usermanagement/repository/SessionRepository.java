package com.phegondev.usermanagement.repository;

import com.phegondev.usermanagement.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByToken(String token);
    void deleteByUserId(UUID userId);
    Optional<Session> findByUserIdAndActiveTrue(UUID userId);
}
