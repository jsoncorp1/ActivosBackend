package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.dto.PageResponse;
import com.phegondev.usermanagement.entity.*;
import com.phegondev.usermanagement.repository.FixedAssetRepository;
import com.phegondev.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FixedAssetService {

    private final FixedAssetRepository fixedAssetRepository;
    private final UserRepository userRepository;

    private String generateCode() {
        Optional<FixedAsset> lastAsset = fixedAssetRepository.findTopByDeletedAtIsNullOrderByCodeDesc();
        if (lastAsset.isPresent()) {
            long lastCode = Long.parseLong(lastAsset.get().getCode());
            return String.valueOf(lastCode + 1);
        }
        return "21700";
    }

    public FixedAsset createFixedAsset(String name, FixedAssetCategoryEnum category, String description,
                                        String location, FixedAssetStatusEnum status,
                                        LocalDate acquisitionDate, String imageUrl) {
        FixedAsset fixedAsset = new FixedAsset();
        fixedAsset.setCode(generateCode());
        fixedAsset.setName(name);
        fixedAsset.setCategory(category);
        fixedAsset.setDescription(description);
        fixedAsset.setLocation(location);
        fixedAsset.setStatus(status);
        fixedAsset.setAcquisitionDate(acquisitionDate);
        fixedAsset.setImageUrl(imageUrl);
        return fixedAssetRepository.save(fixedAsset);
    }

    public FixedAsset createFixedAssetWithUser(String name, FixedAssetCategoryEnum category, String description,
                                                String location, FixedAssetStatusEnum status,
                                                LocalDate acquisitionDate, String imageUrl, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        FixedAsset fixedAsset = new FixedAsset();
        fixedAsset.setCode(generateCode());
        fixedAsset.setName(name);
        fixedAsset.setCategory(category);
        fixedAsset.setDescription(description);
        fixedAsset.setLocation(location);
        fixedAsset.setStatus(status);
        fixedAsset.setAcquisitionDate(acquisitionDate);
        fixedAsset.setImageUrl(imageUrl);
        fixedAsset.setUser(user);
        fixedAsset.setAssignmentDate(LocalDate.now());
        return fixedAssetRepository.save(fixedAsset);
    }

    public PageResponse<FixedAsset> getAllFixedAssets(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("code").descending());
        Page<FixedAsset> page = fixedAssetRepository.findByDeletedAtIsNull(pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<FixedAsset> getFixedAssetsByStatus(FixedAssetStatusEnum status, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("code").descending());
        Page<FixedAsset> page = fixedAssetRepository.findByStatusAndDeletedAtIsNull(status, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<FixedAsset> getFixedAssetsByCategory(FixedAssetCategoryEnum category, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("code").descending());
        Page<FixedAsset> page = fixedAssetRepository.findByCategoryAndDeletedAtIsNull(category, pageable);
        return buildPageResponse(page, offset);
    }

    public PageResponse<FixedAsset> getFixedAssetsByUser(UUID userId, int offset, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("code").descending());
        Page<FixedAsset> page = fixedAssetRepository.findByUserAndDeletedAtIsNull(user, pageable);
        return buildPageResponse(page, offset);
    }

    public Optional<FixedAsset> getFixedAssetById(UUID id) {
        return fixedAssetRepository.findById(id).filter(fa -> fa.getDeletedAt() == null);
    }

    public Optional<FixedAsset> getFixedAssetByCode(String code) {
        return fixedAssetRepository.findByCodeAndDeletedAtIsNull(code);
    }

    public FixedAsset updateFixedAsset(UUID id, String name, FixedAssetCategoryEnum category, String description,
                                        String location, FixedAssetStatusEnum status,
                                        LocalDate acquisitionDate, String imageUrl) {
        FixedAsset fixedAsset = getFixedAssetById(id)
                .orElseThrow(() -> new RuntimeException("FixedAsset no encontrado"));
        if (name != null) fixedAsset.setName(name);
        if (category != null) fixedAsset.setCategory(category);
        if (description != null) fixedAsset.setDescription(description);
        if (location != null) fixedAsset.setLocation(location);
        if (status != null) fixedAsset.setStatus(status);
        if (acquisitionDate != null) fixedAsset.setAcquisitionDate(acquisitionDate);
        if (imageUrl != null) fixedAsset.setImageUrl(imageUrl);
        return fixedAssetRepository.save(fixedAsset);
    }

    public FixedAsset assignFixedAssetToUser(UUID fixedAssetId, UUID userId) {
        FixedAsset fixedAsset = getFixedAssetById(fixedAssetId)
                .orElseThrow(() -> new RuntimeException("FixedAsset no encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        fixedAsset.setUser(user);
        fixedAsset.setAssignmentDate(LocalDate.now());
        return fixedAssetRepository.save(fixedAsset);
    }

    public Boolean deleteFixedAsset(UUID id) {
        Optional<FixedAsset> fixedAsset = getFixedAssetById(id);
        if (fixedAsset.isPresent()) {
            FixedAsset fa = fixedAsset.get();
            fa.setDeletedAt(LocalDateTime.now());
            fixedAssetRepository.save(fa);
            return true;
        }
        return false;
    }

    private PageResponse<FixedAsset> buildPageResponse(Page<FixedAsset> page, int currentOffset) {
        PageResponse<FixedAsset> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setCurrentPage(currentOffset);
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
