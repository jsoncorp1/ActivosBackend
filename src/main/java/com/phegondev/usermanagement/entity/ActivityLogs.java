package com.phegondev.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogs extends BaseEntity {

    @Column(nullable = false)
    private String actionName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityTypeEnum activityType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private String userEmail;
}
