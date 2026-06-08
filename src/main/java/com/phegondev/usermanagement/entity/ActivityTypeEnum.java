package com.phegondev.usermanagement.entity;

public enum ActivityTypeEnum {
    CREATE("Creación"),
    READ("Lectura"),
    UPDATE("Actualización"),
    DELETE("Eliminación"),
    STATUS_CHANGE("Cambio de estado");

    private final String displayName;

    ActivityTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
