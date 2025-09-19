package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.dto.request.LoginRequest;
import com.hoangdung.movie_booking.dto.request.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.response.AuthResponse;
import com.hoangdung.movie_booking.dto.response.RefreshTokenResponse;

public interface AuthService {
    /**
     * User login
     * @param request info user
     * @return accessToken
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Refresh token
     * @param request token old
     * @return new token
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
