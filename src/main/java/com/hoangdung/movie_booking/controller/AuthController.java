package com.hoangdung.movie_booking.controller;

import com.hoangdung.movie_booking.dto.request.Auth.LoginRequest;
import com.hoangdung.movie_booking.dto.request.Auth.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.request.Auth.ResetPasswordRequest;
import com.hoangdung.movie_booking.dto.response.Auth.AuthResponse;
import com.hoangdung.movie_booking.dto.response.OTP.SendOtpRequest;
import com.hoangdung.movie_booking.dto.response.System.BaseResponse;
import com.hoangdung.movie_booking.dto.response.OTP.VerifyOtpRequest;
import com.hoangdung.movie_booking.dto.response.Auth.RefreshTokenResponse;
import com.hoangdung.movie_booking.dto.response.User.RegisterRequest;
import com.hoangdung.movie_booking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication.
 * <p>
 * Provides endpoints for user login, refresh token, logout, register, active, forgot->verifyOtp->reset.
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

    /**
     * Verifies and activates a user's account using the provided OTP.
     * <p>
     * This endpoint accepts an OTP verification request and triggers the
     * activation logic. If the OTP is valid, the user's email is marked as
     * verified and the account becomes active.
     *
     * @param request the {@link VerifyOtpRequest} containing the email and OTP 
     *                to be validated
     * @return a {@link ResponseEntity} containing a success response message 
     *         if the account was activated successfully
     *
     * @apiNote This endpoint should be called after a user has registered 
     *          and received an OTP via email.
     *
     * @see com.hoangdung.movie_booking.service.OtpService#verifyEmail(VerifyOtpRequest) (VerifyOtpRequest)
     */
    @PostMapping("/active")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("[AUTH] Verifying email for: {}", request.getEmail());
        authService.active(request);
        return ResponseEntity.ok(BaseResponse.success("ACTIVE ACCOUNT SUCCESS"));
    }

    /**
     * Endpoint to request a forgot password OTP.
     *
     * <p>
     * This API triggers sending a one-time password (OTP) to the user's registered email
     * to start the password reset process.
     * </p>
     *
     * @param request the request containing the user's email
     * @return {@link ResponseEntity} with a success message indicating that the OTP has been sent
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid SendOtpRequest request) {
        log.info("[AUTH] Sending OTP to email: {}", request.getEmail());
        authService.forgotPassword(request);
        return ResponseEntity.ok(BaseResponse.success("CHECK FORGOT PASSWORD"));
    }

    /**
     * Endpoint to verify the OTP sent for password reset.
     *
     * <p>
     * This API verifies the OTP provided by the user for the password reset process.
     * If valid, it returns a temporary verify key to authorize the password reset.
     * </p>
     *
     * @param request the request containing the user's email and OTP code
     * @return {@link ResponseEntity} containing the temporary verify key
     */
    @PostMapping("/reset-password/otp/verify")
    public ResponseEntity<?> verifyResetPassword(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("[AUTH] Verifying OTP for reset password for email: {}", request.getEmail());
        return ResponseEntity.ok(BaseResponse.success(authService.verifyResetPassword(request)));
    }

    /**
     * Endpoint to reset the user's password using a valid verify key.
     *
     * <p>
     * This API allows the user to set a new password after successfully verifying the OTP
     * and obtaining a temporary verify key.
     * </p>
     *
     * @param request the request containing the verify key and the new password
     * @return {@link ResponseEntity} with a success message indicating that the password has been reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("[AUTH] Reset password request for verifyKey: {}", request.getVerifyKey());
        authService.resetPassword(request);
        return ResponseEntity.ok(BaseResponse.success("RESET PASSWORD SUCCESS"));
    }

}
