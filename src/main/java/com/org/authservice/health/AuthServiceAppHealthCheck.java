package com.org.authservice.health;

import com.codahale.metrics.health.HealthCheck;
import com.org.authservice.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthServiceAppHealthCheck extends HealthCheck {
    private static final String HEALTHY = "The Authentication Service is healthy for read and write";
    private static final String UNHEALTHY = "The Authentication Service is not healthy";
    private static final String MESSAGE_PLACEHOLDER = "{}";
    private final UserService userService;

    @Override
    public Result check() {
        try {
            userService.performHealthCheck();
            return Result.healthy(HEALTHY);
        } catch (Exception e) {
            return Result.unhealthy(UNHEALTHY + MESSAGE_PLACEHOLDER, e.getMessage());
        }
    }
}
