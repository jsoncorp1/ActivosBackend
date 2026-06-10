package com.phegondev.usermanagement.repository;

import com.phegondev.usermanagement.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByToken(String token);

    List<PushToken> findByUserIdAndActiveTrue(UUID userId);

    // VARIANT B: User has @ManyToOne Role with field role.name
    @Query("""
        SELECT pt FROM PushToken pt
        WHERE pt.active = true
          AND pt.userId IN (
              SELECT u.id FROM User u WHERE u.role.name = 'TECNICO'
          )
        """)
    List<PushToken> findActiveTokensForTecnicos();
}
