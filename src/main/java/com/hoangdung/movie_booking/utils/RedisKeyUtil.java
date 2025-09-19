package com.hoangdung.movie_booking.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyUtil {

    @Value("${app.redis.key-prefix}")
    private String keyPrefix;

    @Value("${app.redis.session-prefix}")
    private String sessionPrefix;

    @Value("${app.redis.permission-prefix}")
    private String permissionPrefix;

    @Value("${app.redis.token-prefix}")
    private String tokenPrefix;

    @Value("${app.redis.refresh-prefix}")
    private String refreshPrefix;

    public String accessTokenKey(String username) {
        return keyPrefix + tokenPrefix + username;
    }

    public String refreshTokenKey(String username) {
        return keyPrefix + refreshPrefix + username;
    }

    public String sessionKey(String username) {
        return keyPrefix + sessionPrefix + username;
    }

    public String permissionKey(String username) {
        return keyPrefix + permissionPrefix + username;
    }
}