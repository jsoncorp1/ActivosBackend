package com.phegondev.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpoPushMessage {

    private String to;
    private String title;
    private String body;

    @JsonProperty("data")
    private Map<String, Object> data;

    @Builder.Default
    private String sound = "default";

    private Integer badge;

    @Builder.Default
    private String priority = "high";
}
