package com.hoangdung.movie_booking.controller;

import com.hoangdung.movie_booking.dto.response.BaseResponse;
import com.hoangdung.movie_booking.dto.response.OTP.ResendOtpRequest;
import com.hoangdung.movie_booking.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing OTP (One-Time Password) operations.
 *
 * <p>
 * Endpoints provided:
 * <ul>
 *   <li>{@code POST /api/otp/resend} - Resend an OTP with rate limiting.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Resend OTP
 * POST /api/otp/resend
 * {
 *   "email": "dung@example.com",
 *   "type": "RESET_PASSWORD"
 * }
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OtpController {

    private final OtpService otpService;

    /**
     * Resend OTP with rate limiting protection.
     *
     * @param request request containing email and type
     * @return HTTP 200 if resend successful
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendOtp(@RequestBody @Valid ResendOtpRequest request) {
        log.info("[OTP] Sending OTP to email: {}", request.getEmail());
        otpService.resendOtp(request);
        return ResponseEntity.ok(BaseResponse.success("OTP has been sent"));
    }
}
