package com.phegondev.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fixed_asset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FixedAsset extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixedAssetCategoryEnum category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixedAssetStatusEnum status;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;
}
