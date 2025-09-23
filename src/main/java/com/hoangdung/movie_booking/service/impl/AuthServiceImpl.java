package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.dto.request.Auth.LoginRequest;
import com.hoangdung.movie_booking.dto.request.Auth.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.request.Auth.ResetPasswordRequest;
import com.hoangdung.movie_booking.dto.response.Auth.AuthResponse;
import com.hoangdung.movie_booking.dto.response.OTP.SendOtpRequest;
import com.hoangdung.movie_booking.dto.response.OTP.VerifyOtpRequest;
import com.hoangdung.movie_booking.dto.response.Auth.RefreshTokenResponse;
import com.hoangdung.movie_booking.dto.response.User.RegisterRequest;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.*;
import com.hoangdung.movie_booking.repository.UserRepository;
import com.hoangdung.movie_booking.security.JwtProvider;
import com.hoangdung.movie_booking.service.AuthService;
import com.hoangdung.movie_booking.service.OtpService;
import com.hoangdung.movie_booking.service.RedisService;
import com.hoangdung.movie_booking.service.UserService;
import com.hoangdung.movie_booking.utils.RedisKeyUtil;
import com.hoangdung.movie_booking.utils.enums.OtpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link AuthService} providing authentication and authorization logic.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Register new users with encoded password.</li>
 *   <li>Authenticate user credentials via {@link AuthenticationManager}.</li>
 *   <li>Generate and refresh JWT access & refresh tokens.</li>
 *   <li>Store refresh tokens in Redis with TTL.</li>
 *   <li>Support logout by invalidating refresh tokens.</li>
 *   <li>Fetch authenticated user information.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Register user
 * RegisterRequest req = new RegisterRequest("dung", "123456", "dung@example.com");
 * UserResponse user = authService.register(req);
 *
 * // Login
 * LoginRequest loginReq = new LoginRequest("dung", "123456");
 * AuthResponse auth = authService.login(loginReq);
 *
 * // Refresh token
 * AuthResponse refreshed = authService.refreshToken(new RefreshTokenRequest(auth.getRefreshToken()));
 *
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final RedisKeyUtil redisKeyUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;
    private static final long REDIS_TTL_BUFFER_MS = 2000L;

    /**
     * Authenticate user and generate JWT access + refresh tokens.
     * Saves tokens in Redis with TTL equal to their expiration time.
     *
     * @param request LoginRequest containing username and password
     * @return AuthResponse with accessToken and refreshToken
     * @throws BadCredentialsException if username or password is incorrect
     * @throws BusinessException if email is not verified
     */
    @Override
    public AuthResponse authenticate(LoginRequest request) {
        log.info("Authenticate user (LOGIN) with username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username or password incorrect"));

        if (!user.isEmailVerified()) {
            throw new BusinessException("BAD_REQUEST", "Please activate email before login");
        }

        Authentication authentication = authenticateUser(request);

        String accessToken = jwtProvider.generateAccessToken(authentication);
        String sessionId = UUID.randomUUID().toString();
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername(), sessionId);

        saveRefreshTokenInRedis(user.getUsername(), sessionId, refreshToken);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Refresh the access token using a valid refresh token.
     * Implements refresh token rotation by generating new access and refresh tokens.
     *
     * @param request RefreshTokenRequest containing old refresh token
     * @return RefreshTokenResponse with new accessToken and refreshToken
     * @throws TokenException if refresh token is invalid, expired, or not matching Redis
     */
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token attempt");

        String refreshToken = request.getRefreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new TokenException("Invalid or expired refresh token");
        }

        String username = jwtProvider.getUsernameFromJwtToken(refreshToken);
        String sessionId = jwtProvider.getSessionIdFromJwtToken(refreshToken);
        String key = redisKeyUtil.refreshTokenKey(username, sessionId);

        String storedRefresh = redisService.getString(key);
        log.info("Stored refresh token exists: {}", storedRefresh != null);

        if (storedRefresh == null || !storedRefresh.equals(refreshToken)) {
            throw new TokenException("Refresh token invalid or already used");
        }

        // build new tokens
        String newAccessToken = jwtProvider.generateAccessToken(buildAuthenticationFromUser(username));
        String newRefreshToken = jwtProvider.generateRefreshToken(username, sessionId);

        long ttl = jwtProvider.getExpirationFromToken(newRefreshToken);
        long ttlWithBuffer = Math.max(ttl - REDIS_TTL_BUFFER_MS, 0);

        boolean replaced = tryAtomicReplace(key, refreshToken, newRefreshToken, ttlWithBuffer);
        if (!replaced) {
            throw new TokenException("Refresh token invalid or already used");
        }

        log.info("Refresh token rotated for username={} sessionId={}", username, sessionId);
        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * Logout a user by revoking their refresh token.
     * <p>
     * Validates the provided refresh token, checks it against Redis,
     * and deletes it if valid. This ensures the token cannot be reused
     * to obtain new access tokens. The access token is not revoked
     * immediately, but will expire naturally after its short TTL.
     * </p>
     *
     * @param request RefreshTokenRequest containing the refresh token to revoke
     * @throws TokenException if the refresh token is invalid, expired, or already rotated
     */
    @Override
    public void logout(RefreshTokenRequest request) {
        log.info("Logout account running");
        String refreshToken = request.getRefreshToken();
        log.info("Refresh token in request: {}", refreshToken);

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new TokenException("Invalid or expired refresh token");
        }

        String username = jwtProvider.getUsernameFromJwtToken(refreshToken);
        String sessionId = jwtProvider.getSessionIdFromJwtToken(refreshToken);

        String key = redisKeyUtil.refreshTokenKey(username, sessionId);
        boolean deleted = redisService.delete(key);

        if (!deleted) {
            throw new TokenException("Refresh token invalid or already used");
        }

        log.info("User {} logged out. Refresh token revoked for session {}", username, sessionId);
    }

    /**
     * Registers a new user in the system.
     * <p>
     * This method is typically invoked during the user creation flow inside
     * {@link UserService}, where it handles the registration logic such as:
     * <ul>
     *     <li>Validating the provided {@link RegisterRequest} data.</li>
     *     <li>Creating a new user entity and persisting it into the database.</li>
     *     <li>Applying necessary business rules (e.g., encoding password, setting default roles).</li>
     *     <li>Triggering additional post-registration steps (e.g., sending verification email or OTP).</li>
     * </ul>
     *
     * @param request the {@link RegisterRequest} containing user details (e.g., email, password, name)
     *                required for creating a new account; must not be {@code null}.
     * @throws IllegalArgumentException   if the request contains invalid or incomplete data.
     * @throws UserAlreadyExistsException if a user with the same unique identifier (e.g., email)
     *                                    already exists in the system.
     */
    @Override
    public void register(RegisterRequest request) {
        log.info("Call logic register userService");

        userService.createUser(request);
        log.info("Register successfully with username: {}", request.getUsername());

        log.info("Sendmail active email running");

        try {
            otpService.sendOtp(SendOtpRequest.builder()
                            .email(request.getEmail())
                    .build(), OtpType.VERIFY_EMAIL);
            log.info("Sendmail success");
        } catch (Exception e) {
            throw new OtpException("Sendmail register error");
        }
    }

    /**
     * Activates (verifies) a user's email using the provided OTP request.
     * <p>
     * This method is typically called from the OTP service logic to validate
     * the OTP associated with the given email. If the OTP is valid, the user’s
     * email is marked as verified in the system.
     *
     * @param request the {@link VerifyOtpRequest} containing the email and OTP
     *                used for verification
     * @throws OtpException              if the provided OTP is invalid or expired
     * @throws ResourceNotFoundException if no user is found for the provided email
     * @see OtpServiceImpl#verifyEmail(VerifyOtpRequest)
     */
    @Override
    public void active(VerifyOtpRequest request) {
        log.info("Call logic active account");
        otpService.verifyEmail(request);
    }

    /**
     * Sends a one-time password (OTP) to the user's email for password reset.
     * <p>
     * This method delegates the OTP generation and sending logic to the {@code otpService}.
     * The OTP type used is {@link OtpType#RESET_PASSWORD}.
     *
     * @param request the request containing the user's email to send the OTP to
     * @throws OtpException if sending the OTP fails (e.g., email service failure)
     */
    @Override
    public void forgotPassword(SendOtpRequest request) {
        log.info("Call logic SendOTP");
        otpService.sendOtp(request, OtpType.RESET_PASSWORD);
    }

    /**
     * Verifies the OTP provided by the user for password reset.
     * <p>
     * This method delegates the verification logic to the {@code otpService}.
     *
     * @param request the request containing the user's email and the OTP to verify
     * @return a message indicating whether the OTP verification was successful
     * @throws OtpException if the OTP is invalid or expired
     */
    @Override
    public String verifyResetPassword(VerifyOtpRequest request) {
        log.info("Call logic verify otp");
        return otpService.verifyOtp(request);
    }

    /**
     * Resets the user's password.
     * <p>
     * This method should contain the logic to update the user's password directly in the authentication system.
     * Before calling this method, it is expected that the OTP verification has already succeeded.
     *
     * @param request the request containing the user's email and the new password
     * @throws BusinessException if resetting the password fails (e.g., user not found, validation error)
     */
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (!Objects.equals(request.getConfirmPassword(), request.getNewPassword())) {
            throw new BusinessException("Confirmation password does not match");
        }

        //check verifyKey
        User user = otpService.confirmVerifyKey(request.getVerifyKey());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    //================ PRIVATE METHODS =================//

    /**
     * Authenticate user credentials using Spring Security AuthenticationManager.
     *
     * @param request LoginRequest
     * @return Authentication object if successful
     * @throws BadCredentialsException if authentication fails
     */
    private Authentication authenticateUser(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (AuthenticationException e) {
            log.error("Authentication failed for username: {}", request.getUsername());
            throw new BadCredentialsException("Username or password incorrect");
        }
    }

    /**
     * Build Authentication object from User entity.
     *
     * @param username username of the user
     * @return Authentication with authorities
     * @throws ResourceNotFoundException if user not found
     */
    private Authentication buildAuthenticationFromUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    /**
     * Save both access token and refresh token in Redis with TTL.
     *
     * @param username     username of the user
     * @param sessionId    session id of the user
     * @param refreshToken JWT refresh token
     */
    private void saveRefreshTokenInRedis(String username, String sessionId, String refreshToken) {
        String key = redisKeyUtil.refreshTokenKey(username, sessionId);
        long ttl = jwtProvider.getExpirationFromToken(refreshToken);
        long ttlWithBuffer = Math.max(ttl - REDIS_TTL_BUFFER_MS, 0);
        log.info("Saving refresh token for user={} session={} ttl(ms)={}", username, sessionId, ttlWithBuffer);
        redisService.set(key, refreshToken, ttlWithBuffer, TimeUnit.MILLISECONDS);
    }

    /**
     * Atomically replaces the stored refresh token in Redis if it matches the expected old value.
     * <p>
     * Ensures refresh tokens are single-use (rotation). If the current value in Redis equals
     * {@code expectedOldValue}, it is replaced with {@code newValue} and a new TTL is set.
     * Otherwise, returns false.
     * </p>
     *
     * @param key              Redis key for the refresh token
     * @param expectedOldValue The old refresh token provided by the client
     * @param newValue         The new refresh token to store
     * @param ttlMs            Time-to-live (ms) for the new refresh token
     * @return {@code true} if replaced successfully, {@code false} if the token was already used or mismatched
     */
    private boolean tryAtomicReplace(String key, String expectedOldValue, String newValue, long ttlMs) {
        if (stringRedisTemplate != null) {
            String script = "if redis.call('GET', KEYS[1]) == ARGV[1] then " +
                    "redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3]); return 1; else return 0; end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            Long result = stringRedisTemplate.execute(redisScript,
                    Collections.singletonList(key),
                    expectedOldValue, newValue, String.valueOf(ttlMs));
            return result == 1L;
        } else {
            // fallback (non-atomic): acceptable for dev, not for concurrent refresh in production
            String current = redisService.getString(key);
            if (expectedOldValue.equals(current)) {
                redisService.set(key, newValue, ttlMs, TimeUnit.MILLISECONDS);
                return true;
            }
            return false;
        }
    }
}
