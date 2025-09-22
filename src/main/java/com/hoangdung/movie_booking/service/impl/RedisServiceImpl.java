package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link RedisService} using Spring's {@link StringRedisTemplate}.
 *
 * <p>
 * This service provides utility methods for interacting with Redis as a key-value store,
 * primarily designed for storing authentication tokens (JWT access/refresh tokens),
 * user sessions, and other short-lived String-based values.
 * </p>
 *
 * <h2>Features</h2>
 * <ul>
 *     <li>Store and retrieve raw {@code String} values without extra JSON serialization (avoids unwanted quotes).</li>
 *     <li>Support for TTL (time-to-live) so keys automatically expire after a given duration.</li>
 *     <li>Utility methods for deleting keys, checking existence, and fetching TTL.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * redisService.set("jwt:refresh:admin", refreshToken, 7, TimeUnit.DAYS);
 * String token = redisService.getString("jwt:refresh:admin");
 * boolean exists = redisService.existsKey("jwt:refresh:admin");
 * long ttl = redisService.getTTL("jwt:refresh:admin");
 * }</pre>
 *
 * <p>
 * Internally, this implementation uses {@link StringRedisTemplate}, which is optimized
 * for handling string keys and values. This avoids serialization overhead and ensures
 * compatibility with JWT tokens stored as plain text.
 * </p>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Save a key-value pair in Redis without TTL.
     *
     * @param key   Redis key
     * @param value Value to store (converted to String via {@code toString()})
     */
    @Override
    public void set(String key, Object value) {
        try {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
            log.debug("Set key={} value={}", key, value);
        } catch (Exception e) {
            log.error("Error setting key={} value={}", key, value, e);
        }
    }

    /**
     * Save a key-value pair in Redis with TTL.
     *
     * @param key      Redis key
     * @param value    Value to store (converted to String via {@code toString()})
     * @param timeout  Expiration duration
     * @param timeUnit Time unit of expiration
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value), timeout, timeUnit);
            log.debug("Set key={} with TTL={} {}", key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Error setting key={} with TTL", key, e);
        }
    }

    /**
     * Get value from Redis by key (as Object).
     *
     * @param key Redis key
     * @return Stored value (String), or {@code null} if not found
     */
    @Override
    public Object get(String key) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            log.debug("Get key={} success", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting key={}", key, e);
            return null;
        }
    }

    /**
     * Get value from Redis by key (as String).
     *
     * @param key Redis key
     * @return Stored value, or {@code null} if not found
     */
    @Override
    public String getString(String key) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            log.debug("Get key={} as String success", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting key={} as String", key, e);
            return null;
        }
    }

    /**
     * Delete a key from Redis.
     *
     * @param key Redis key
     * @return {@code true} if deleted successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(String key) {
        try {
            Boolean result = stringRedisTemplate.delete(key);
            boolean deleted = Boolean.TRUE.equals(result);
            log.debug("Delete key={} success={}", key, deleted);
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting key={}", key, e);
            return false;
        }
    }

    /**
     * Check if a key exists in Redis.
     *
     * @param key Redis key
     * @return {@code true} if exists, {@code false} otherwise
     */
    @Override
    public boolean existsKey(String key) {
        try {
            Boolean result = stringRedisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking existence of key={}", key, e);
            return false;
        }
    }

    /**
     * Set expiration (TTL) for a key.
     *
     * @param key      Redis key
     * @param timeout  Expiration duration
     * @param timeUnit Time unit
     * @return {@code true} if successful, {@code false} otherwise
     */
    @Override
    public boolean expire(String key, Long timeout, TimeUnit timeUnit) {
        try {
            Boolean result = stringRedisTemplate.expire(key, timeout, timeUnit);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expire for key={}", key, e);
            return false;
        }
    }

    /**
     * Get TTL (time-to-live) of a key in seconds.
     *
     * @param key Redis key
     * @return TTL in seconds, or {@code -1} if not found
     */
    @Override
    public long getTTL(String key) {
        try {
            Long result = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            return result != null ? result : -1;
        } catch (Exception e) {
            log.error("Error getting TTL for key={}", key, e);
            return -1;
        }
    }
}