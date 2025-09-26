package com.hoangdung.movie_booking.service;

import java.util.concurrent.TimeUnit;

/**
 * Service interface for working with Redis as a key-value store.
 *
 * <p>Main features:</p>
 * <ul>
 *     <li>Store and retrieve key-value pairs.</li>
 *     <li>Support for TTL (time-to-live) expiration.</li>
 *     <li>Check key existence and delete keys.</li>
 *     <li>Special support for String retrieval to avoid serialization issues.</li>
 * </ul>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * redisService.set("otp:user1", "123456", 5, TimeUnit.MINUTES);
 * String otp = redisService.getString("otp:user1");
 * long ttl = redisService.getTTL("otp:user1");
 * boolean exists = redisService.existsKey("otp:user1");
 * redisService.delete("otp:user1");
 * }</pre>
 *
 * @author
 *   Hoàng Đình Dũng
 * @since 1.0
 */
public interface RedisService {

    /**
     * Store a key-value pair in Redis (no expiration).
     *
     * @param key   Redis key (identifier)
     * @param value Value to store
     */
    void set(String key, Object value);

    /**
     * Store a key-value pair in Redis with TTL.
     *
     * @param key      Redis key (identifier)
     * @param value    Value to store
     * @param timeout  Expiration time
     * @param timeUnit Time unit of expiration
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * Retrieve value from Redis.
     *
     * @param key Redis key
     * @return Stored value (Object), or {@code null} if not found
     */
    Object get(String key);

    /**
     * Retrieve value from Redis as String.
     * <p>Useful when storing plain text tokens/OTPs.</p>
     *
     * @param key Redis key
     * @return Stored String value, or {@code null} if not found
     */
    String getString(String key);

    /**
     * Lấy object từ Redis và deserialize JSON
     * @param key khóa để get
     * @param clazz lớp
     * @return trả về Object
     * @param <T> Generics
     */
    <T> T getObject(String key, Class<T> clazz);

    /**
     * Delete a key from Redis.
     *
     * @param key Redis key
     * @return {@code true} if deleted, {@code false} otherwise
     */
    boolean delete(String key);

    /**
     * Check key
     * @param key key
     * @return true/false
     */
    boolean existsKey(String key);

    /**
     * Set expiration (TTL) for a key.
     *
     * @param key      Redis key
     * @param timeout  Expiration time
     * @param timeUnit Time unit
     * @return {@code true} if successful, {@code false} otherwise
     */
    boolean expire(String key, Long timeout, TimeUnit timeUnit);

    /**
     * Get TTL (time-to-live) of a key in seconds.
     *
     * @param key Redis key
     * @return TTL in seconds, or {@code -1} if key not found/expired
     */
    long getTTL(String key);
}
