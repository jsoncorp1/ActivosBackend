package com.phegondev.usermanagement.entity;

import com.phegondev.usermanagement.converter.StatusChangeListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequest extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceRequestStatusEnum status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fixed_asset_id", nullable = false)
    private FixedAsset fixedAsset;

    @Convert(converter = StatusChangeListConverter.class)
    @Column(columnDefinition = "jsonb", name = "status_change_log")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<StatusChange> statusChangeLog = new ArrayList<>();
}
