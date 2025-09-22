package com.hoangdung.movie_booking.service;


import com.hoangdung.movie_booking.dto.response.OTP.ResendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.SendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.VerifyOtpRequest;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.utils.enums.OtpType;

/**
 * Service interface for managing One-Time Password (OTP) operations.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Generate and send OTP codes to users via email.</li>
 *   <li>Support resend OTP with spam protection and rate-limiting.</li>
 *   <li>Verify OTP codes and issue temporary verification keys.</li>
 *   <li>Confirm email verification via OTP.</li>
 *   <li>Resolve verification keys to authenticated {@link User} entities.</li>
 * </ul>
 *
 * <p>Supported OTP Types:</p>
 * <ul>
 *   <li>{@link OtpType#RESET_PASSWORD} – Reset forgotten passwords.</li>
 *   <li>{@link OtpType#VERIFY_EMAIL} – Verify new user email address.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Send OTP for reset password
 * otpService.sendOtp(new SendOtpRequest("dung@example.com"), OtpType.RESET_PASSWORD);
 *
 * // Resend OTP
 * otpService.resendOtp(new ResendOtpRequest("dung@example.com", OtpType.RESET_PASSWORD));
 *
 * // Verify OTP and receive verifyKey
 * String verifyKey = otpService.verifyOtp(new VerifyOtpRequest("dung@example.com", "123456"));
 *
 * // Confirm verifyKey to get User
 * User user = otpService.confirmVerifyKey(verifyKey);
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
public interface OtpService {

    /**
     * Send a new OTP to the user for the specified operation type.
     *
     * @param request request containing recipient email
     * @param type OTP type (e.g. RESET_PASSWORD, VERIFY_EMAIL)
     */
    void sendOtp(SendOtpRequest request, OtpType type);

    /**
     * Resend OTP to the user if allowed by rate-limiting rules.
     *
     * @param request request containing recipient email and OTP type
     */
    void resendOtp(ResendOtpRequest request);

    /**
     * Verify the provided OTP code and return a temporary verify key.
     *
     * @param request request containing email and OTP code
     * @return verification key string (UUID)
     */
    String verifyOtp(VerifyOtpRequest request);

    /**
     * Verify user email using OTP code.
     *
     * @param request request containing email and OTP code
     */
    void verifyEmail(VerifyOtpRequest request);

    /**
     * Confirm a previously issued verify key and resolve the {@link User}.
     *
     * @param verifyKey temporary verification key
     * @return user associated with the verify key if valid
     */
    User confirmVerifyKey(String verifyKey);
}
