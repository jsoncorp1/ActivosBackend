package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.ActivityLogs;
import com.phegondev.usermanagement.entity.ActivityTypeEnum;
import com.phegondev.usermanagement.entity.User;
import com.phegondev.usermanagement.repository.ActivityLogsRepository;
import com.phegondev.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ActivityLogsService {

    private final ActivityLogsRepository activityLogsRepository;
    private final UserRepository userRepository;

    public ActivityLogs logActivity(ActivityTypeEnum activityType, String actionName, String entityType,
                                     UUID entityId, String description, String oldValue, String newValue) {
        ActivityLogs entry = new ActivityLogs();
        entry.setActivityType(activityType);
        entry.setActionName(actionName);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDescription(description);
        entry.setOldValue(oldValue);
        entry.setNewValue(newValue);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            entry.setUserEmail(email);
            userRepository.findByEmail(email).ifPresent(entry::setUser);
        }

        return activityLogsRepository.save(entry);
    }

    public ActivityLogs logCreate(String entityType, UUID entityId, String description, String newValue) {
        return logActivity(ActivityTypeEnum.CREATE, "CREATE_" + entityType.toUpperCase(),
                entityType, entityId, description, null, newValue);
    }

    public ActivityLogs logRead(String entityType, UUID entityId) {
        return logActivity(ActivityTypeEnum.READ, "READ_" + entityType.toUpperCase(),
                entityType, entityId, "Lectura del registro", null, null);
    }

    public ActivityLogs logUpdate(String entityType, UUID entityId, String description, String oldValue, String newValue) {
        return logActivity(ActivityTypeEnum.UPDATE, "UPDATE_" + entityType.toUpperCase(),
                entityType, entityId, description, oldValue, newValue);
    }

    public ActivityLogs logDelete(String entityType, UUID entityId, String description) {
        return logActivity(ActivityTypeEnum.DELETE, "DELETE_" + entityType.toUpperCase(),
                entityType, entityId, description, null, null);
    }

    public ActivityLogs logStatusChange(String entityType, UUID entityId, String fromStatus, String toStatus) {
        String actionName = "STATUS_CHANGED_" + entityType.toUpperCase() + "_" + fromStatus + "_TO_" + toStatus;
        String description = "Cambió estado de " + fromStatus + " a " + toStatus;
        return logActivity(ActivityTypeEnum.STATUS_CHANGE, actionName,
                entityType, entityId, description, fromStatus, toStatus);
    }

    public PageResponse<ActivityLogs> getAllActivities(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository.findByDeletedAtIsNull(pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<ActivityLogs> getActivitiesByUser(UUID userId, int offset, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository.findByUserAndDeletedAtIsNull(user, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<ActivityLogs> getActivitiesByType(ActivityTypeEnum activityType, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository.findByActivityTypeAndDeletedAtIsNull(activityType, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<ActivityLogs> getActivitiesByEntityType(String entityType, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository.findByEntityTypeAndDeletedAtIsNull(entityType, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<ActivityLogs> getActivitiesByEntity(String entityType, UUID entityId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository
                .findByEntityTypeAndEntityIdAndDeletedAtIsNull(entityType, entityId, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<ActivityLogs> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate,
                                                                int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository
                .findByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<ActivityLogs> searchActivities(String searchTerm, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<ActivityLogs> page = activityLogsRepository
                .findByActionNameContainingIgnoreCaseAndDeletedAtIsNull(searchTerm, pageable);
        return buildPageResponse(page, offset);
    }

    public Optional<ActivityLogs> getActivityById(UUID id) {
        return activityLogsRepository.findByIdAndDeletedAtIsNull(id);
    }

    private PageResponse<ActivityLogs> buildPageResponse(Page<ActivityLogs> page, int currentOffset) {
        PageResponse<ActivityLogs> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setCurrentPage(currentOffset);
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
