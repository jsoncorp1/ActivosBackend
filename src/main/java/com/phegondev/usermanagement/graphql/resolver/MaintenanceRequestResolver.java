package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.MaintenanceRequest;
import com.phegondev.usermanagement.entity.MaintenanceRequestStatusEnum;
import com.phegondev.usermanagement.service.MaintenanceRequestService;
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
public class MaintenanceRequestResolver {

    private final MaintenanceRequestService maintenanceRequestService;

    // ==================== QUERIES ====================

    @QueryMapping
    public PageResponse<MaintenanceRequest> getAllMaintenanceRequests(
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceRequestService.getAllMaintenanceRequests(
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByFixedAsset(
            @Argument UUID fixedAssetId,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceRequestService.getMaintenanceRequestsByFixedAsset(
                fixedAssetId,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByStatus(
            @Argument MaintenanceRequestStatusEnum status,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceRequestService.getMaintenanceRequestsByStatus(
                status,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<MaintenanceRequest> getMaintenanceRequestsByCreatedBy(
            @Argument String createdBy,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return maintenanceRequestService.getMaintenanceRequestsByCreatedBy(
                createdBy,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public MaintenanceRequest getMaintenanceRequestById(@Argument UUID id) {
        return maintenanceRequestService.getMaintenanceRequestById(id).orElse(null);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public MaintenanceRequest createMaintenanceRequest(
            @Argument String title,
            @Argument String description,
            @Argument UUID fixedAssetId) {
        return maintenanceRequestService.createMaintenanceRequest(title, description, fixedAssetId);
    }

    @MutationMapping
    public MaintenanceRequest updateMaintenanceRequest(
            @Argument UUID id,
            @Argument String title,
            @Argument String description) {
        return maintenanceRequestService.updateMaintenanceRequest(id, title, description);
    }

    @MutationMapping
    public MaintenanceRequest updateMaintenanceRequestStatus(
            @Argument UUID id,
            @Argument MaintenanceRequestStatusEnum newStatus) {
        return maintenanceRequestService.updateStatus(id, newStatus);
    }

    @MutationMapping
    public Boolean deleteMaintenanceRequest(@Argument UUID id) {
        return maintenanceRequestService.deleteMaintenanceRequest(id);
    }
}
