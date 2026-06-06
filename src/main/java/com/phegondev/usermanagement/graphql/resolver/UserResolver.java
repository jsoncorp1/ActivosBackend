package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.entity.Session;
import com.phegondev.usermanagement.entity.User;
import com.phegondev.usermanagement.service.SessionService;
import com.phegondev.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserService userService;
    private final SessionService sessionService;

    // ==================== QUERIES ====================

    @QueryMapping
    public User userById(@Argument UUID id) {
        return userService.findById(id).orElse(null);
    }

    @QueryMapping
    public User userByEmail(@Argument String email) {
        return userService.findByEmail(email).orElse(null);
    }

    @QueryMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public User createUser(
            @Argument String email,
            @Argument String password,
            @Argument String firstName,
            @Argument String lastName,
            @Argument UUID roleId) {
        return userService.createUser(email, password, firstName, lastName, roleId);
    }

    @MutationMapping
    public AuthResponse login(
            @Argument String email,
            @Argument String password) {
        Optional<User> user = userService.findByEmail(email);

        if (user.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User foundUser = user.get();

        if (!userService.validatePassword(password, foundUser.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        Session session = sessionService.createSession(foundUser);

        return new AuthResponse(
                foundUser.getId(),
                foundUser.getEmail(),
                foundUser.getFirstName(),
                foundUser.getLastName(),
                session.getToken()
        );
    }

    @MutationMapping
    public User updateUser(
            @Argument UUID id,
            @Argument String firstName,
            @Argument String lastName) {
        return userService.updateUser(id, firstName, lastName);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument UUID id) {
        try {
            userService.deleteUser(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== DTO ====================

    public record AuthResponse(
            UUID id,
            String email,
            String firstName,
            String lastName,
            String token
    ) {}
}
