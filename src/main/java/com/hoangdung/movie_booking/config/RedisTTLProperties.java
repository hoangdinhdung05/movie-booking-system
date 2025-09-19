package com.hoangdung.movie_booking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.redis.cache-ttl")
public class RedisTTLProperties {
    private long session;
    private long permission;
    private long token;
    private long refresh;
}