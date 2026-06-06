package com.phegondev.usermanagement.graphql.resolver;

import com.phegondev.usermanagement.entity.Session;
import com.phegondev.usermanagement.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SessionResolver {

    private final SessionService sessionService;

    @QueryMapping
    public Session sessionByToken(@Argument String token) {
        return sessionService.findByToken(token).orElse(null);
    }
}
