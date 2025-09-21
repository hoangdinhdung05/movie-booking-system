package com.hoangdung.movie_booking.controller;

import com.hoangdung.movie_booking.dto.response.OTP.ResendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.SendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.VerifyOtpRequest;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.service.OtpService;
import com.hoangdung.movie_booking.utils.enums.OtpType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing OTP (One-Time Password) operations.
 *
 * <p>
 * Endpoints provided:
 * <ul>
 *   <li>{@code POST /api/otp/send} - Send a new OTP to user email.</li>
 *   <li>{@code POST /api/otp/resend} - Resend an OTP with rate limiting.</li>
 *   <li>{@code POST /api/otp/verify} - Verify an OTP code and return a verifyKey.</li>
 *   <li>{@code POST /api/otp/verify-email} - Verify user email with OTP code.</li>
 *   <li>{@code GET /api/otp/confirm/{verifyKey}} - Confirm a verifyKey and resolve user information.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Send OTP
 * POST /api/otp/send
 * {
 *   "email": "dung@example.com",
 *   "type": "RESET_PASSWORD"
 * }
 *
 * // Verify OTP
 * POST /api/otp/verify
 * {
 *   "email": "dung@example.com",
 *   "code": "123456"
 * }
 *
 * // Confirm verify key
 * GET /api/otp/confirm/7fa1fbe2-b0e1-11ee-bd41-0242ac120002
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    /**
     * Send a new OTP to the given email.
     *
     * @param request request with email and OTP type
     * @return HTTP 200 if OTP sent successfully
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendOtp(@RequestBody SendOtpRequest request,
                                        @RequestParam OtpType type) {
        otpService.sendOtp(request, type);
        return ResponseEntity.ok().build();
    }

    /**
     * Resend OTP with rate limiting protection.
     *
     * @param request request containing email and type
     * @return HTTP 200 if resend successful
     */
    @PostMapping("/resend")
    public ResponseEntity<Void> resendOtp(@RequestBody ResendOtpRequest request) {
        otpService.resendOtp(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Verify OTP and return verification key.
     *
     * @param request request with email and OTP code
     * @return verifyKey string
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        String verifyKey = otpService.verifyOtp(request);
        return ResponseEntity.ok(verifyKey);
    }

    /**
     * Verify user email with OTP code.
     *
     * @param request request with email and OTP code
     * @return HTTP 200 if email verified
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyOtpRequest request) {
        otpService.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Confirm verifyKey and resolve the associated user.
     *
     * @param verifyKey verification key issued after OTP verification
     * @return User if key is valid
     */
    @GetMapping("/confirm/{verifyKey}")
    public ResponseEntity<User> confirmVerifyKey(@PathVariable String verifyKey) {
        User user = otpService.confirmVerifyKey(verifyKey);
        return ResponseEntity.ok(user);
    }
}
