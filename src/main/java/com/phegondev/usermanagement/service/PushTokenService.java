package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.entity.PushToken;
import com.phegondev.usermanagement.entity.User;
import com.phegondev.usermanagement.repository.PushTokenRepository;
import com.phegondev.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PushTokenService {

    private final PushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;

    public void registerToken(String email, String token, String platform) {
        UUID userId = userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        pushTokenRepository.findByToken(token).ifPresentOrElse(
            existing -> {
                existing.setActive(true);
                existing.setUserId(userId);
                pushTokenRepository.save(existing);
            },
            () -> pushTokenRepository.save(PushToken.builder()
                .userId(userId).token(token).platform(platform).active(true).build())
        );
    }

    public void deactivateTokens(String email) {
        UUID userId = userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(User::getId)
                .orElse(null);
        if (userId == null) return;
        List<PushToken> tokens = pushTokenRepository.findByUserIdAndActiveTrue(userId);
        tokens.forEach(t -> t.setActive(false));
        pushTokenRepository.saveAll(tokens);
    }
}
