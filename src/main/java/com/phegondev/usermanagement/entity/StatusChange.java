package com.phegondev.usermanagement.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusChange {
    private String fromStatus;
    private String toStatus;
    private LocalDateTime changedAt;
    private String changedBy;
}
