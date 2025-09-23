package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.config.OtpProperties;
import com.hoangdung.movie_booking.dto.EmailDTO;
import com.hoangdung.movie_booking.dto.response.OTP.ResendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.SendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.VerifyOtpRequest;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.BusinessException;
import com.hoangdung.movie_booking.exception.OtpException;
import com.hoangdung.movie_booking.helper.OTP.OtpEmailTemplate;
import com.hoangdung.movie_booking.helper.OTP.OtpGenerator;
import com.hoangdung.movie_booking.helper.OTP.OtpPayload;
import com.hoangdung.movie_booking.repository.UserRepository;
import com.hoangdung.movie_booking.service.EmailService;
import com.hoangdung.movie_booking.service.OtpService;
import com.hoangdung.movie_booking.service.RedisService;
import com.hoangdung.movie_booking.service.UserService;
import com.hoangdung.movie_booking.utils.OtpRedisKeyUtil;
import com.hoangdung.movie_booking.utils.enums.OtpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static com.hoangdung.movie_booking.utils.OtpRedisKeyUtil.otpCountKey;

/**
 * Implementation of {@link OtpService} using Redis for OTP management.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Generate OTP codes and store them in Redis with TTL.</li>
 *   <li>Support resend logic with rate limiting via Redis counters.</li>
 *   <li>Verify OTP codes and return verification keys.</li>
 *   <li>Mark email as verified upon successful OTP validation.</li>
 *   <li>Confirm verify keys to fetch user information.</li>
 *   <li>Send OTP codes asynchronously via {@link EmailService}.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Redis Key Patterns:
 * <ul>
 *   <li>{@code otp:{email}:{type}} → JSON payload {otp, verifyKey, used} with TTL = 5 minutes.</li>
 *   <li>{@code otp:count:{email}:{type}} → resend counter with TTL = 5 minutes.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Send OTP for reset password
 * otpService.sendOtp(new SendOtpRequest("dung@example.com"), OtpType.RESET_PASSWORD);
 *
 * // Verify OTP
 * String verifyKey = otpService.verifyOtp(new VerifyOtpRequest("dung@example.com", "123456"));
 *
 * // Confirm verify key
 * User user = otpService.confirmVerifyKey(verifyKey);
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final OtpProperties otpProperties;

    /**
     * Send a new OTP to the user for the specified operation type.
     *
     * @param request request containing recipient email
     * @param type    OTP type (e.g. RESET_PASSWORD, VERIFY_EMAIL)
     */
    @Transactional
    @Override
    public void sendOtp(SendOtpRequest request, OtpType type) {
        log.info("Send OTP running with email: {}", request.getEmail());

        User user = userService.getUserByEmail(request.getEmail());
        checkSendAllowed(user.getEmail(), type);

        String otp = OtpGenerator.generate(otpProperties.getOtpLength());
        OtpPayload payload = new OtpPayload(otp, 0, 0, System.currentTimeMillis());

        storeOtp(user.getEmail(), payload, type);
        incrementSendCount(user.getEmail(), type);
        sendOtpEmail(user, payload, type);

        log.info("Sent OTP to {} and type {}", user.getEmail(), type);
    }

    /**
     * Resend OTP to the user if allowed by rate-limiting rules.
     *
     * @param request request containing recipient email and OTP type
     */
    @Transactional
    @Override
    public void resendOtp(ResendOtpRequest request) {
        log.info("Resend OTP running");
        sendOtp(new SendOtpRequest(request.getEmail()), request.getType());
    }

    /**
     * Verify the provided OTP code and return a temporary verify key.
     *
     * @param request request containing email and OTP code
     * @return verification key string (UUID)
     */
    @Transactional
    @Override
    public String verifyOtp(VerifyOtpRequest request) {
        log.info("Verify OTP running");

        validateOtp(request.getEmail(), request.getOtp(), OtpType.RESET_PASSWORD);
        String verifyKey = createVerifyKey(request.getEmail());
        log.info("Verified OTP for email={}, verifyKey={}", request.getEmail(), verifyKey);
        return verifyKey;
    }

    /**
     * Verify user email using OTP code.
     *
     * @param request request containing email and OTP code
     */
    @Transactional
    @Override
    public void verifyEmail(VerifyOtpRequest request) {
        log.info("Verify email running with email: {}", request.getEmail());

        validateOtp(request.getEmail(), request.getOtp(), OtpType.VERIFY_EMAIL);

        User user = userService.getUserByEmail(request.getEmail());
        user.setEmailVerified(true);
        userRepository.save(user);

        log.debug("Before save: emailVerified={}", user.isEmailVerified());


        log.info("User {} verified successfully", user.getEmail());
    }

    /**
     * Confirm a previously issued verify key and resolve the {@link User}.
     *
     * @param verifyKey temporary verification key
     * @return user associated with the verify key if valid
     */
    @Transactional
    @Override
    public User confirmVerifyKey(String verifyKey) {
        log.info("Confirm verify key running");

        String email = redisService.getString(OtpRedisKeyUtil.verifyKey(verifyKey));
        if (email == null) {
            throw new OtpException("Verify key invalid or expired.");
        }

        User user = userService.getUserByEmail(email);
        redisService.delete(OtpRedisKeyUtil.verifyKey(verifyKey));

        log.info("Confirmed verifyKey={} for user={}", verifyKey, user.getId());
        return user;
    }

    // ==================== PRIVATE HELPERS ====================

    /**
     * Check if sending OTP is allowed (rate-limiting + existing OTP check).
     */
    private void checkSendAllowed(String email, OtpType type) {
        String otpKey = OtpRedisKeyUtil.otpKey(email, type);
        if (redisService.existsKey(otpKey)) {
            throw new OtpException("OTP has been sent. Please check your email.");
        }

        String countKey = otpCountKey(email, type);
        int count = parseCount(redisService.getString(countKey));
        if (count >= otpProperties.getMaxSendCount()) {
            throw new OtpException("You have sent OTP too many times. Try again later.");
        }
    }

    /**
     * Validate the OTP input against stored payload in Redis.
     */
    private void validateOtp(String email, String inputOtp, OtpType type) {
        String key = "OTP:" + email + ":" + type.name();
        OtpPayload payload = redisService.getObject(key, OtpPayload.class);

        if (payload == null) {
            log.error("OTP not found in Redis for key={}", key);
            throw new OtpException("OTP expired or not found.");
        }

        log.debug("Validating OTP: input={} stored={}", inputOtp, payload.getCode());

        if (!payload.getCode().equals(inputOtp)) {
            throw new OtpException("Invalid OTP");
        }

        // ok → OTP hợp lệ
    }

    /**
     * Store OTP payload in Redis with expiry.
     */
    private void storeOtp(String email, OtpPayload payload, OtpType type) {
        String otpKey = OtpRedisKeyUtil.otpKey(email, type);
        redisService.set(otpKey, payload, otpProperties.getExpiryMinutes(), TimeUnit.MINUTES);
    }

    /**
     * Increment the resend count for the email/type in Redis.
     */
    private void incrementSendCount(String email, OtpType type) {
        String countKey = otpCountKey(email, type);
        int count = parseCount(redisService.getString(countKey));
        redisService.set(countKey, String.valueOf(count + 1),
                otpProperties.getResendLimitMinutes(), TimeUnit.MINUTES);
    }

    /**
     * Build and send OTP email asynchronously.
     */
    private void sendOtpEmail(User user, OtpPayload payload, OtpType type) {
        EmailDTO email = EmailDTO.builder()
                .to(List.of(user.getEmail()))
                .subject("Mã OTP xác thực")
                .textContent(OtpEmailTemplate.buildContent(user, payload.getCode(), type, otpProperties.getExpiryMinutes()))
                .isHtml(false)
                .build();

        emailService.sendEmailAsync(email);
    }

    /**
     * Create a temporary verify key for verified OTP and store in Redis.
     *
     * @param email email associated with verified OTP
     * @return UUID string as verify key
     */
    private String createVerifyKey(String email) {
        String verifyKey = UUID.randomUUID().toString();
        String redisKey = OtpRedisKeyUtil.verifyKey(verifyKey);
        redisService.set(redisKey, email, otpProperties.getVerifyKeyExpiryMinutes(), TimeUnit.MINUTES);
        return verifyKey;
    }

    /**
     * Parse integer safely from Redis string.
     */
    private int parseCount(String countStr) {
        return (countStr != null) ? Integer.parseInt(countStr) : 0;
    }
}
