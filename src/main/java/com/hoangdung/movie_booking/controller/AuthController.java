package com.hoangdung.movie_booking.controller;

import com.hoangdung.movie_booking.dto.request.LoginRequest;
import com.hoangdung.movie_booking.dto.request.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.response.AuthResponse;
import com.hoangdung.movie_booking.dto.response.BaseResponse;
import com.hoangdung.movie_booking.dto.response.RefreshTokenResponse;
import com.hoangdung.movie_booking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication.
 * Provides endpoints for user login and refresh token.
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
     *
     * @param request LoginRequest with username and password
     * @return ResponseEntity with BaseResponse containing AuthResponse
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {
        log.info("User login server username={}", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * Refresh token endpoint.
     * <p>
     * Accepts old refresh token, returns new access token and new refresh token (rotation).
     *
     * @param refreshToken RefreshTokenRequest with refresh token
     * @return ResponseEntity with BaseResponse containing RefreshTokenResponse
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshToken) {
        log.info("User refresh-token");
        RefreshTokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
