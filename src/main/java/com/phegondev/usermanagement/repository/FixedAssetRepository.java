package com.phegondev.usermanagement.repository;

import com.phegondev.usermanagement.entity.FixedAsset;
import com.phegondev.usermanagement.entity.FixedAssetCategoryEnum;
import com.phegondev.usermanagement.entity.FixedAssetStatusEnum;
import com.phegondev.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FixedAssetRepository extends JpaRepository<FixedAsset, UUID> {

    Page<FixedAsset> findByDeletedAtIsNull(Pageable pageable);

    Page<FixedAsset> findByStatusAndDeletedAtIsNull(FixedAssetStatusEnum status, Pageable pageable);

    Page<FixedAsset> findByCategoryAndDeletedAtIsNull(FixedAssetCategoryEnum category, Pageable pageable);

    Page<FixedAsset> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    Page<FixedAsset> findByStatusAndCategoryAndDeletedAtIsNull(FixedAssetStatusEnum status, FixedAssetCategoryEnum category, Pageable pageable);

    Optional<FixedAsset> findByCodeAndDeletedAtIsNull(String code);

    Optional<FixedAsset> findTopByDeletedAtIsNullOrderByCodeDesc();
}
