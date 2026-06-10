package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.*;
import com.phegondev.usermanagement.repository.FixedAssetRepository;
import com.phegondev.usermanagement.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final FixedAssetRepository fixedAssetRepository;
    private final ActivityLogsService activityLogsService;
    private final PushNotificationService pushNotificationService;

    public MaintenanceRequest createMaintenanceRequest(String title, String description, UUID fixedAssetId) {
        FixedAsset fixedAsset = fixedAssetRepository.findById(fixedAssetId)
                .orElseThrow(() -> new RuntimeException("FixedAsset no encontrado"));

        MaintenanceRequest request = new MaintenanceRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setStatus(MaintenanceRequestStatusEnum.PENDING);
        request.setFixedAsset(fixedAsset);

        StatusChange initialChange = new StatusChange();
        initialChange.setFromStatus(null);
        initialChange.setToStatus(MaintenanceRequestStatusEnum.PENDING.name());
        initialChange.setChangedAt(LocalDateTime.now());
        initialChange.setChangedBy(getCurrentUser());

        request.setStatusChangeLog(new ArrayList<>(List.of(initialChange)));

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        activityLogsService.logCreate("MaintenanceRequest", saved.getId(),
                "Creó MaintenanceRequest: " + title,
                "{\"title\":\"" + title + "\",\"status\":\"PENDING\",\"fixedAssetId\":\"" + fixedAssetId + "\"}");

        pushNotificationService.notifyTecnicosNewRequest(saved.getId().toString(), saved.getTitle());

        return saved;
    }

    public MaintenanceRequest updateStatus(UUID requestId, MaintenanceRequestStatusEnum newStatus) {
        MaintenanceRequest request = getMaintenanceRequestById(requestId)
                .orElseThrow(() -> new RuntimeException("MaintenanceRequest no encontrado"));

        MaintenanceRequestStatusEnum oldStatus = request.getStatus();
        request.setStatus(newStatus);

        StatusChange statusChange = new StatusChange();
        statusChange.setFromStatus(oldStatus.name());
        statusChange.setToStatus(newStatus.name());
        statusChange.setChangedAt(LocalDateTime.now());
        statusChange.setChangedBy(getCurrentUser());

        request.getStatusChangeLog().add(statusChange);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        activityLogsService.logStatusChange("MaintenanceRequest", saved.getId(),
                oldStatus.name(), newStatus.name());

        return saved;
    }

    public PageResponse<MaintenanceRequest> getAllMaintenanceRequests(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<MaintenanceRequest> page = maintenanceRequestRepository.findByDeletedAtIsNull(pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByFixedAsset(UUID fixedAssetId, int offset, int limit) {
        FixedAsset fixedAsset = fixedAssetRepository.findById(fixedAssetId)
                .orElseThrow(() -> new RuntimeException("FixedAsset no encontrado"));
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<MaintenanceRequest> page = maintenanceRequestRepository.findByFixedAssetAndDeletedAtIsNull(fixedAsset, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByStatus(MaintenanceRequestStatusEnum status, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<MaintenanceRequest> page = maintenanceRequestRepository.findByStatusAndDeletedAtIsNull(status, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByCreatedBy(String createdBy, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<MaintenanceRequest> page = maintenanceRequestRepository.findByCreatedByAndDeletedAtIsNull(createdBy, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByStatusAndCreatedBy(
            MaintenanceRequestStatusEnum status, String createdBy, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<MaintenanceRequest> page = maintenanceRequestRepository
                .findByStatusAndCreatedByAndDeletedAtIsNull(status, createdBy, pageable);
        return buildPageResponse(page, offset);
    }

    public Optional<MaintenanceRequest> getMaintenanceRequestById(UUID id) {
        return maintenanceRequestRepository.findByIdAndDeletedAtIsNull(id);
    }

    public MaintenanceRequest updateMaintenanceRequest(UUID id, String title, String description) {
        MaintenanceRequest request = getMaintenanceRequestById(id)
                .orElseThrow(() -> new RuntimeException("MaintenanceRequest no encontrado"));

        String oldValue = "{\"title\":\"" + request.getTitle() + "\"}";
        if (title != null) request.setTitle(title);
        if (description != null) request.setDescription(description);
        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        activityLogsService.logUpdate("MaintenanceRequest", saved.getId(),
                "Actualizó MaintenanceRequest: " + saved.getTitle(),
                oldValue, "{\"title\":\"" + saved.getTitle() + "\"}");

        return saved;
    }

    public Boolean deleteMaintenanceRequest(UUID id) {
        Optional<MaintenanceRequest> request = getMaintenanceRequestById(id);
        if (request.isPresent()) {
            MaintenanceRequest mr = request.get();
            mr.setDeletedAt(LocalDateTime.now());
            maintenanceRequestRepository.save(mr);
            activityLogsService.logDelete("MaintenanceRequest", mr.getId(),
                    "Eliminó MaintenanceRequest: " + mr.getTitle());
            return true;
        }
        return false;
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "SYSTEM";
        String name = auth.getName();
        return "anonymousUser".equals(name) ? "SYSTEM" : name;
    }

    private PageResponse<MaintenanceRequest> buildPageResponse(Page<MaintenanceRequest> page, int currentOffset) {
        PageResponse<MaintenanceRequest> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setCurrentPage(currentOffset);
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
