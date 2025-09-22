package com.hoangdung.movie_booking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for OTP management.
 * <p>
 * Values are loaded from application.yml under prefix {@code otp}.
 * </p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "otp")
public class OtpProperties {
    private int expiryMinutes;
    private int verifyKeyExpiryMinutes;
    private int maxSendCount;
    private int resendLimitMinutes;
}
