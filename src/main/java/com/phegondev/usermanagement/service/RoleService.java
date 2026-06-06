package com.phegondev.usermanagement.service;

import com.phegondev.usermanagement.entity.Role;
import com.phegondev.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public Role createRole(String name, String description) {
        String normalizedName = name.toUpperCase().trim();
        if (roleRepository.findByName(normalizedName).isPresent()) {
            throw new RuntimeException("El rol " + normalizedName + " ya existe");
        }
        Role role = new Role();
        role.setName(normalizedName);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findByDeletedAtIsNull();
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        role.setDeletedAt(LocalDateTime.now());
        roleRepository.save(role);
    }
}
