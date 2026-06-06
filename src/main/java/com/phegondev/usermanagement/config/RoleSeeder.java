package com.phegondev.usermanagement.config;

import com.phegondev.usermanagement.entity.Role;
import com.phegondev.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRole("SUPERADMIN", "Acceso total al sistema");
        seedRole("GERENTE", "Gestión de usuarios y reportes");
        seedRole("TECNICO", "Soporte técnico");
        seedRole("ASISTENTE", "Asistente básico");
    }

    private void seedRole(String name, String description) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            roleRepository.save(role);
        }
    }
}
