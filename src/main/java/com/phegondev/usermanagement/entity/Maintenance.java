package com.phegondev.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceTypeEnum type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maintenance_request_id", nullable = false, unique = true)
    private MaintenanceRequest maintenanceRequest;
}
