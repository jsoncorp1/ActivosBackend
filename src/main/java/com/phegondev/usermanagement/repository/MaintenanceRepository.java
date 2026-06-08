package com.phegondev.usermanagement.repository;

import com.phegondev.usermanagement.entity.Maintenance;
import com.phegondev.usermanagement.entity.MaintenanceRequest;
import com.phegondev.usermanagement.entity.MaintenanceTypeEnum;
import com.phegondev.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {

    Page<Maintenance> findByDeletedAtIsNull(Pageable pageable);

    Page<Maintenance> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    Page<Maintenance> findByTypeAndDeletedAtIsNull(MaintenanceTypeEnum type, Pageable pageable);

    Page<Maintenance> findByMaintenanceRequestAndDeletedAtIsNull(MaintenanceRequest maintenanceRequest, Pageable pageable);

    Optional<Maintenance> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Maintenance> findByMaintenanceRequestIdAndDeletedAtIsNull(UUID maintenanceRequestId);
}
