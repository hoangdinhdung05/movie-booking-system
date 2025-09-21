package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.dto.request.LoginRequest;
import com.hoangdung.movie_booking.dto.request.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.response.AuthResponse;
import com.hoangdung.movie_booking.dto.response.RefreshTokenResponse;
import com.hoangdung.movie_booking.exception.BusinessException;
import com.hoangdung.movie_booking.exception.TokenException;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Service for authentication and token management.
 * Provides methods for user login and refresh token flow.
 */
public interface AuthService {

    /**
     * Authenticate user and generate JWT tokens.
     * <p>
     * Flow:
     * <ul>
     *     <li>Check if user exists</li>
     *     <li>Verify email is activated</li>
     *     <li>Authenticate username and password using Spring Security</li>
     *     <li>Generate access token and refresh token</li>
     *     <li>Save tokens into Redis with TTL</li>
     * </ul>
     *
     * @param request LoginRequest containing username and password
     * @return AuthResponse containing accessToken and refreshToken
     * @throws BadCredentialsException if username or password is incorrect
     * @throws BusinessException if email is not verified
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Refresh access token using a valid refresh token.
     * <p>
     * Flow:
     * <ul>
     *     <li>Validate refresh token JWT</li>
     *     <li>Check stored refresh token in Redis</li>
     *     <li>Generate new access token and new refresh token (rotation)</li>
     *     <li>Update tokens in Redis</li>
     * </ul>
     *
     * @param request RefreshTokenRequest containing old refresh token
     * @return RefreshTokenResponse containing new accessToken and refreshToken
     * @throws TokenException if refresh token is invalid or expired
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
