package com.phegondev.usermanagement.repository;

import com.phegondev.usermanagement.entity.FixedAsset;
import com.phegondev.usermanagement.entity.MaintenanceRequest;
import com.phegondev.usermanagement.entity.MaintenanceRequestStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {

    Page<MaintenanceRequest> findByDeletedAtIsNull(Pageable pageable);

    Page<MaintenanceRequest> findByFixedAssetAndDeletedAtIsNull(FixedAsset fixedAsset, Pageable pageable);

    Page<MaintenanceRequest> findByStatusAndDeletedAtIsNull(MaintenanceRequestStatusEnum status, Pageable pageable);

    Page<MaintenanceRequest> findByCreatedByAndDeletedAtIsNull(String createdBy, Pageable pageable);

    Page<MaintenanceRequest> findByStatusAndCreatedByAndDeletedAtIsNull(MaintenanceRequestStatusEnum status, String createdBy, Pageable pageable);

    Optional<MaintenanceRequest> findByIdAndDeletedAtIsNull(UUID id);
}
