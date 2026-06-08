package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.FixedAsset;
import com.phegondev.usermanagement.entity.FixedAssetCategoryEnum;
import com.phegondev.usermanagement.entity.FixedAssetStatusEnum;
import com.phegondev.usermanagement.service.FixedAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FixedAssetResolver {

    private final FixedAssetService fixedAssetService;

    // ==================== QUERIES ====================

    @QueryMapping
    public PageResponse<FixedAsset> getAllFixedAssets(
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return fixedAssetService.getAllFixedAssets(
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<FixedAsset> getFixedAssetsByStatus(
            @Argument FixedAssetStatusEnum status,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return fixedAssetService.getFixedAssetsByStatus(
                status,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<FixedAsset> getFixedAssetsByCategory(
            @Argument FixedAssetCategoryEnum category,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return fixedAssetService.getFixedAssetsByCategory(
                category,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public PageResponse<FixedAsset> getFixedAssetsByUser(
            @Argument UUID userId,
            @Argument(name = "offset") Integer offset,
            @Argument(name = "limit") Integer limit) {
        return fixedAssetService.getFixedAssetsByUser(
                userId,
                offset != null ? offset : 0,
                limit != null ? limit : 10);
    }

    @QueryMapping
    public FixedAsset getFixedAssetById(@Argument UUID id) {
        return fixedAssetService.getFixedAssetById(id).orElse(null);
    }

    @QueryMapping
    public FixedAsset getFixedAssetByCode(@Argument String code) {
        return fixedAssetService.getFixedAssetByCode(code).orElse(null);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public FixedAsset createFixedAsset(
            @Argument String name,
            @Argument FixedAssetCategoryEnum category,
            @Argument String description,
            @Argument String location,
            @Argument FixedAssetStatusEnum status,
            @Argument String acquisitionDate,
            @Argument String imageUrl) {
        return fixedAssetService.createFixedAsset(
                name, category, description, location, status,
                LocalDate.parse(acquisitionDate), imageUrl);
    }

    @MutationMapping
    public FixedAsset createFixedAssetWithUser(
            @Argument String name,
            @Argument FixedAssetCategoryEnum category,
            @Argument String description,
            @Argument String location,
            @Argument FixedAssetStatusEnum status,
            @Argument String acquisitionDate,
            @Argument String imageUrl,
            @Argument UUID userId) {
        return fixedAssetService.createFixedAssetWithUser(
                name, category, description, location, status,
                LocalDate.parse(acquisitionDate), imageUrl, userId);
    }

    @MutationMapping
    public FixedAsset updateFixedAsset(
            @Argument UUID id,
            @Argument String name,
            @Argument FixedAssetCategoryEnum category,
            @Argument String description,
            @Argument String location,
            @Argument FixedAssetStatusEnum status,
            @Argument String acquisitionDate,
            @Argument String imageUrl) {
        return fixedAssetService.updateFixedAsset(
                id, name, category, description, location, status,
                acquisitionDate != null ? LocalDate.parse(acquisitionDate) : null,
                imageUrl);
    }

    @MutationMapping
    public FixedAsset assignFixedAssetToUser(
            @Argument UUID fixedAssetId,
            @Argument UUID userId) {
        return fixedAssetService.assignFixedAssetToUser(fixedAssetId, userId);
    }

    @MutationMapping
    public Boolean deleteFixedAsset(@Argument UUID id) {
        return fixedAssetService.deleteFixedAsset(id);
    }
}
