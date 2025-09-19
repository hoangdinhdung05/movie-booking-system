package com.hoangdung.movie_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token không được bỏ trống")
    private String refreshToken;
}
