package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.entity.Role;
import com.phegondev.usermanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RoleResolver {

    private final RoleService roleService;

    @QueryMapping
    public Role roleById(@Argument UUID id) {
        return roleService.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @QueryMapping
    public Role roleByName(@Argument String name) {
        return roleService.findByName(name).orElse(null);
    }

    @MutationMapping
    public Role createRole(@Argument String name, @Argument String description) {
        return roleService.createRole(name, description);
    }

    @MutationMapping
    public Boolean deleteRole(@Argument UUID id) {
        try {
            roleService.deleteRole(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
