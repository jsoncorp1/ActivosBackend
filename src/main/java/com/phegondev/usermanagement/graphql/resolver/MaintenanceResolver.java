package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.Maintenance;
import com.phegondev.usermanagement.entity.MaintenanceTypeEnum;
import com.phegondev.usermanagement.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MaintenanceResolver {

    private final MaintenanceService maintenanceService;

    // ==================== QUERIES ====================

    @QueryMapping
    public PageResponse<Maintenance> getAllMaintenances(
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceService.getAllMaintenances(
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<Maintenance> getMaintenancesByUser(
            @Argument UUID userId,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceService.getMaintenancesByUser(
                userId,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<Maintenance> getMaintenancesByType(
            @Argument MaintenanceTypeEnum type,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceService.getMaintenancesByType(
                type,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public Maintenance getMaintenanceById(@Argument UUID id) {
        return maintenanceService.getMaintenanceById(id).orElse(null);
    }

    @QueryMapping
    public Maintenance getMaintenanceByMaintenanceRequest(@Argument UUID maintenanceRequestId) {
        return maintenanceService.getMaintenanceByMaintenanceRequest(maintenanceRequestId).orElse(null);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public Maintenance createMaintenance(
            @Argument MaintenanceTypeEnum type,
            @Argument String description,
            @Argument String imageUrl,
            @Argument UUID maintenanceRequestId,
            @Argument UUID userId) {
        return maintenanceService.createMaintenance(type, description, imageUrl, maintenanceRequestId, userId);
    }

    @MutationMapping
    public Maintenance updateMaintenance(
            @Argument UUID id,
            @Argument String description,
            @Argument String imageUrl) {
        return maintenanceService.updateMaintenance(id, description, imageUrl);
    }

    @MutationMapping
    public Boolean deleteMaintenance(@Argument UUID id) {
        return maintenanceService.deleteMaintenance(id);
    }
}
