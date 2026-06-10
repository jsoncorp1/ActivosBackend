package com.phegondev.usermanagement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phegondev.usermanagement.dto.ExpoPushMessage;
import com.phegondev.usermanagement.entity.PushToken;
import com.phegondev.usermanagement.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;
    private final PushTokenRepository pushTokenRepository;
    private final ObjectMapper objectMapper;

    @Async("pushExecutor")
    public void notifyTecnicosNewRequest(String solicitudId, String titulo) {
        List<PushToken> tokens = pushTokenRepository.findActiveTokensForTecnicos();
        if (tokens.isEmpty()) {
            log.info("Sin tokens de técnicos; push omitido.");
            return;
        }
        List<ExpoPushMessage> messages = tokens.stream()
                .map(pt -> ExpoPushMessage.builder()
                        .to(pt.getToken())
                        .title("Nueva solicitud de mantenimiento")
                        .body(titulo)
                        .data(Map.of("solicitudId", solicitudId, "type", "NEW_REQUEST"))
                        .build())
                .toList();
        sendBatch(messages);
    }

    private void sendBatch(List<ExpoPushMessage> messages) {
        int batchSize = 100;
        for (int i = 0; i < messages.size(); i += batchSize) {
            List<ExpoPushMessage> batch = messages.subList(i, Math.min(i + batchSize, messages.size()));
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", "application/json");
                headers.set("Accept-Encoding", "gzip, deflate");
                HttpEntity<List<ExpoPushMessage>> request = new HttpEntity<>(batch, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);
                handleTickets(batch, response.getBody());
            } catch (Exception e) {
                log.error("Error enviando push a Expo: {}", e.getMessage(), e);
            }
        }
    }

    private void handleTickets(List<ExpoPushMessage> batch, String responseBody) {
        if (responseBody == null) return;
        try {
            JsonNode data = objectMapper.readTree(responseBody).path("data");
            for (int j = 0; j < data.size() && j < batch.size(); j++) {
                JsonNode ticket = data.get(j);
                if ("error".equals(ticket.path("status").asText())) {
                    String errorType = ticket.path("details").path("error").asText("");
                    String token = batch.get(j).getTo();
                    if ("DeviceNotRegistered".equals(errorType)) {
                        pushTokenRepository.findByToken(token).ifPresent(pt -> {
                            pt.setActive(false);
                            pushTokenRepository.save(pt);
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("No se pudo procesar respuesta de Expo: {}", e.getMessage());
        }
    }
}
