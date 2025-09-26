package com.hoangdung.movie_booking.dto.request.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token cannot blank")
    private String refreshToken;
}
