package com.phegondev.usermanagement.entity;

public enum FixedAssetStatusEnum {
    ACTIVE("active"),
    IN_STORAGE("inStorage"),
    UNDER_MAINTENANCE("underMaintenance"),
    DAMAGED("damaged"),
    RETIRED("retired"),
    LOST("lost");

    private final String displayName;

    FixedAssetStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
