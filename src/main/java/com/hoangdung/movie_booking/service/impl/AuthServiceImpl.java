package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.dto.request.LoginRequest;
import com.hoangdung.movie_booking.dto.request.RefreshTokenRequest;
import com.hoangdung.movie_booking.dto.response.AuthResponse;
import com.hoangdung.movie_booking.dto.response.RefreshTokenResponse;
import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.exception.BusinessException;
import com.hoangdung.movie_booking.exception.ResourceNotFoundException;
import com.hoangdung.movie_booking.exception.TokenException;
import com.hoangdung.movie_booking.repository.UserRepository;
import com.hoangdung.movie_booking.security.JwtProvider;
import com.hoangdung.movie_booking.service.AuthService;
import com.hoangdung.movie_booking.service.RedisService;
import com.hoangdung.movie_booking.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final RedisKeyUtil redisKeyUtil;

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
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        saveTokensInRedis(user.getUsername(), accessToken, refreshToken);

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
        log.info("Refresh token running");

        log.info("Request refresh token: {}", request.getRefreshToken());

        String username = validateRefreshToken(request.getRefreshToken());
        String storedRefresh = redisService.getString(redisKeyUtil.refreshTokenKey(username));

        log.info("Stored refresh token: {}", storedRefresh);

        if (storedRefresh == null || !storedRefresh.equals(request.getRefreshToken())) {
            throw new TokenException("Refresh token invalid or expired");
        }

        // Generate new tokens (rotation)
        String newAccessToken = jwtProvider.generateAccessToken(buildAuthenticationFromUser(username));
        String newRefreshToken = jwtProvider.generateRefreshToken(username);

        // Update both tokens in Redis
        saveTokensInRedis(username, newAccessToken, newRefreshToken);

        log.info("Refresh token success for username={}", username);
        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
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
     * Validate a refresh token and return the username.
     *
     * @param refreshToken JWT refresh token
     * @return username encoded in the token
     * @throws TokenException if token is invalid or expired
     */
    private String validateRefreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new TokenException("Invalid or expired refresh token");
        }
        return jwtProvider.getUsernameFromJwtToken(refreshToken);
    }

    /**
     * Save both access token and refresh token in Redis with TTL.
     *
     * @param username     username of the user
     * @param accessToken  JWT access token
     * @param refreshToken JWT refresh token
     */
    private void saveTokensInRedis(String username, String accessToken, String refreshToken) {
        setTokenInRedis(redisKeyUtil.accessTokenKey(username), accessToken);
        setTokenInRedis(redisKeyUtil.refreshTokenKey(username), refreshToken);
    }

    /**
     * Set a single token into Redis with TTL based on token expiration.
     *
     * @param key   Redis key
     * @param token JWT token
     */
    private void setTokenInRedis(String key, String token) {
        long ttl = jwtProvider.getExpirationFromToken(token);
        log.info("Set token in Redis key={} ttl(ms)={}", key, ttl); // Only log key + TTL
        redisService.set(key, token, ttl, TimeUnit.MILLISECONDS);
    }
}
