package com.phegondev.usermanagement.config;

import com.phegondev.usermanagement.repository.RoleRepository;
import com.phegondev.usermanagement.repository.UserRepository;
import com.phegondev.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    @Override
    public void run(String... args) {
        seedUser("SUPERADMIN");
        seedUser("GERENTE");
        seedUser("TECNICO");
        seedUser("ASISTENTE");
    }

    private void seedUser(String roleName) {
        String emailBase = roleName.toLowerCase();
        String email = emailBase + "@" + emailBase;

        if (userRepository.existsByEmail(email)) {
            return;
        }

        roleRepository.findByName(roleName).ifPresent(role -> {
            String password = emailBase + "12345";
            userService.createUser(email, password, roleName, "User", role.getId());
        });
    }
}
