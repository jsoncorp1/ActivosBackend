package com.phegondev.usermanagement.entity;

public enum MaintenanceRequestStatusEnum {
    PENDING("pendiente"),
    APPROVED("aprobado"),
    REJECTED("rechazado"),
    COMPLETED("completado");

    private final String displayName;

    MaintenanceRequestStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
