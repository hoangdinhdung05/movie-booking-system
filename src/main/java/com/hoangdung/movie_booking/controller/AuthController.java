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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        log.info("User login server username={}", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshToken) {
        log.info("User refresh-token");
        RefreshTokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
