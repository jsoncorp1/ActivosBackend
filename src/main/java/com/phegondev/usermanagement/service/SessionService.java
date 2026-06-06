package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.entity.Session;
import com.phegondev.usermanagement.entity.User;
import com.phegondev.usermanagement.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final JwtService jwtService;

    @Transactional
    public Session createSession(User user) {
        sessionRepository.deleteByUserId(user.getId());

        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setExpiresAt(LocalDateTime.now().plusHours(24));
        session.setActive(true);

        return sessionRepository.save(session);
    }

    public Optional<Session> findByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    public Optional<Session> findActiveSessionByToken(String token) {
        Optional<Session> session = sessionRepository.findByToken(token);

        if (session.isPresent() && session.get().isActive() && LocalDateTime.now().isBefore(session.get().getExpiresAt())) {
            return session;
        }

        return Optional.empty();
    }

    @Transactional
    public void invalidateSession(String token) {
        Optional<Session> session = sessionRepository.findByToken(token);
        if (session.isPresent()) {
            session.get().setActive(false);
            sessionRepository.save(session.get());
        }
    }

    public void deleteExpiredSessions() {
        sessionRepository.findAll().forEach(session -> {
            if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
                sessionRepository.delete(session);
            }
        });
    }
}
