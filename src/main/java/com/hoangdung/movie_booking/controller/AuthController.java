package com.hoangdung.movie_booking.controller;

import com.hoangdung.movie_booking.dto.request.LoginRequest;
import com.hoangdung.movie_booking.dto.request.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.response.AuthResponse;
import com.hoangdung.movie_booking.dto.response.BaseResponse;
import com.hoangdung.movie_booking.dto.response.RefreshTokenResponse;
import com.hoangdung.movie_booking.dto.response.User.RegisterRequest;
import com.hoangdung.movie_booking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication.
 * <p>
 * Provides endpoints for user login, refresh token, and logout.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint.
     * <p>
     * Accepts username and password, returns access token and refresh token.
     * </p>
     *
     * @param request LoginRequest with username and password
     * @return ResponseEntity with BaseResponse containing AuthResponse
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {
        log.info("Call api login server username={} running", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * Refresh token endpoint.
     * <p>
     * Accepts old refresh token, returns new access token and new refresh token (rotation).
     * </p>
     *
     * @param refreshToken RefreshTokenRequest with refresh token
     * @return ResponseEntity with BaseResponse containing RefreshTokenResponse
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshToken) {
        log.info("Call api refresh-token running");
        RefreshTokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * Logout endpoint.
     * <p>
     * Accepts refresh token, validates it, and revokes it from Redis storage.
     * <br>
     * This prevents the token from being used to generate new access tokens.
     * Access tokens are not revoked immediately but will expire naturally.
     * </p>
     *
     * @param request RefreshTokenRequest containing the refresh token to revoke
     * @return ResponseEntity with BaseResponse (no payload on success)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        log.info("Call api logout running");
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * API endpoint for user registration.
     * <p>
     * Endpoint: {@code POST /api/auth/register}
     * <p>
     * Workflow:
     * <ul>
     *   <li>Accepts user information from {@link RegisterRequest} (name, username, email, password, ...).</li>
     *   <li>Delegates the registration process to {@link AuthService#register(RegisterRequest)}
     *       which creates a new user with the default {@code USER} role.</li>
     *   <li>After successful registration, the system sends an OTP to the provided email
     *       for account verification.</li>
     * </ul>
     *
     * @param request {@link RegisterRequest} containing user registration details:
     *                <ul>
     *                  <li>{@code username} - unique username.</li>
     *                  <li>{@code email} - unique email, also used for OTP delivery.</li>
     *                  <li>{@code password} - user password (validated for length and format).</li>
     *                  <li>{@code name} - full name of the user.</li>
     *                </ul>
     * @return {@link ResponseEntity} with a success message if registration and OTP sending succeed.
     * @throws com.hoangdung.movie_booking.exception.OtpException if sending the OTP email fails.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Call api register running");
        authService.register(request);
        return ResponseEntity.ok(BaseResponse.success("Register new account successfully"));
    }
}
