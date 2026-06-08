package com.phegondev.usermanagement.repository;

import com.phegondev.usermanagement.entity.ActivityLogs;
import com.phegondev.usermanagement.entity.ActivityTypeEnum;
import com.phegondev.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, UUID> {

    Page<ActivityLogs> findByDeletedAtIsNull(Pageable pageable);

    Page<ActivityLogs> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    Page<ActivityLogs> findByActivityTypeAndDeletedAtIsNull(ActivityTypeEnum activityType, Pageable pageable);

    Page<ActivityLogs> findByEntityTypeAndDeletedAtIsNull(String entityType, Pageable pageable);

    Page<ActivityLogs> findByEntityTypeAndEntityIdAndDeletedAtIsNull(String entityType, UUID entityId, Pageable pageable);

    Page<ActivityLogs> findByActionNameContainingIgnoreCaseAndDeletedAtIsNull(String actionName, Pageable pageable);

    Page<ActivityLogs> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Optional<ActivityLogs> findByIdAndDeletedAtIsNull(UUID id);
}
