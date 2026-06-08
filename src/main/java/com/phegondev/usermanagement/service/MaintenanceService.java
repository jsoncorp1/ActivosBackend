package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.*;
import com.phegondev.usermanagement.repository.MaintenanceRepository;
import com.phegondev.usermanagement.repository.MaintenanceRequestRepository;
import com.phegondev.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final ActivityLogsService activityLogsService;

    public Maintenance createMaintenance(MaintenanceTypeEnum type, String description, String imageUrl,
                                          UUID maintenanceRequestId, UUID userId) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                .findByIdAndDeletedAtIsNull(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("MaintenanceRequest no encontrado"));

        if (maintenanceRepository.findByMaintenanceRequestIdAndDeletedAtIsNull(maintenanceRequestId).isPresent()) {
            throw new RuntimeException("Esta solicitud de mantenimiento ya tiene un mantenimiento registrado");
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        Maintenance maintenance = new Maintenance();
        maintenance.setType(type);
        maintenance.setDescription(description);
        maintenance.setImageUrl(imageUrl);
        maintenance.setMaintenanceRequest(maintenanceRequest);
        maintenance.setUser(user);

        Maintenance saved = maintenanceRepository.save(maintenance);

        activityLogsService.logCreate("Maintenance", saved.getId(),
                "Creó Maintenance tipo " + type + " para solicitud " + maintenanceRequestId,
                "{\"type\":\"" + type + "\",\"maintenanceRequestId\":\"" + maintenanceRequestId + "\""
                        + (userId != null ? ",\"userId\":\"" + userId + "\"" : "") + "}");

        return saved;
    }

    public PageResponse<Maintenance> getAllMaintenances(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<Maintenance> page = maintenanceRepository.findByDeletedAtIsNull(pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<Maintenance> getMaintenancesByUser(UUID userId, int offset, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<Maintenance> page = maintenanceRepository.findByUserAndDeletedAtIsNull(user, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<Maintenance> getMaintenancesByType(MaintenanceTypeEnum type, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<Maintenance> page = maintenanceRepository.findByTypeAndDeletedAtIsNull(type, pageable);
        return buildPageResponse(page, offset);
    }

    public Optional<Maintenance> getMaintenanceById(UUID id) {
        return maintenanceRepository.findByIdAndDeletedAtIsNull(id);
    }

    public Optional<Maintenance> getMaintenanceByMaintenanceRequest(UUID maintenanceRequestId) {
        return maintenanceRepository.findByMaintenanceRequestIdAndDeletedAtIsNull(maintenanceRequestId);
    }

    public Maintenance updateMaintenance(UUID id, String description, String imageUrl) {
        Maintenance maintenance = getMaintenanceById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance no encontrado"));

        String oldValue = "{\"description\":\"" + maintenance.getDescription() + "\",\"imageUrl\":\"" + maintenance.getImageUrl() + "\"}";

        if (description != null) maintenance.setDescription(description);
        if (imageUrl != null) maintenance.setImageUrl(imageUrl);
        Maintenance saved = maintenanceRepository.save(maintenance);

        activityLogsService.logUpdate("Maintenance", saved.getId(),
                "Actualizó Maintenance",
                oldValue, "{\"description\":\"" + saved.getDescription() + "\",\"imageUrl\":\"" + saved.getImageUrl() + "\"}");

        return saved;
    }

    public Boolean deleteMaintenance(UUID id) {
        Optional<Maintenance> maintenance = getMaintenanceById(id);
        if (maintenance.isPresent()) {
            Maintenance m = maintenance.get();
            m.setDeletedAt(LocalDateTime.now());
            maintenanceRepository.save(m);
            activityLogsService.logDelete("Maintenance", m.getId(),
                    "Eliminó Maintenance tipo " + m.getType());
            return true;
        }
        return false;
    }

    private PageResponse<Maintenance> buildPageResponse(Page<Maintenance> page, int currentOffset) {
        PageResponse<Maintenance> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setCurrentPage(currentOffset);
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
