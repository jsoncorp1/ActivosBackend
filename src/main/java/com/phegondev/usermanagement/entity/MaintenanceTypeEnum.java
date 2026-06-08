package com.phegondev.usermanagement.entity;

public enum MaintenanceTypeEnum {
    CORRECTIVE("correctivo"),
    PREVENTIVE("preventivo");

    private final String displayName;

    MaintenanceTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
