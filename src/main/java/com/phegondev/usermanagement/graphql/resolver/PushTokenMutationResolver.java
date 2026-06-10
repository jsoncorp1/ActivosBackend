package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.dto.RegisterPushTokenInput;
import com.phegondev.usermanagement.service.PushTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PushTokenMutationResolver {

    private final PushTokenService pushTokenService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean registerPushToken(
            @Argument RegisterPushTokenInput input,
            Authentication authentication) {
        pushTokenService.registerToken(authentication.getName(), input.token(), input.platform());
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean unregisterPushTokens(Authentication authentication) {
        pushTokenService.deactivateTokens(authentication.getName());
        return true;
    }
}
