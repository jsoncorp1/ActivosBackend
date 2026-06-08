package com.phegondev.usermanagement.entity;

public enum FixedAssetCategoryEnum {
    ELECTRONIC_EQUIPMENT("electronic_equipment"),
    HVAC_EQUIPMENT("hvac_equipment"),
    FIXTURES("Fixtures"),
    OTHERS("others");

    private final String displayName;

    FixedAssetCategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
