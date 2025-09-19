package com.hoangdung.movie_booking.service.impl;

import com.hoangdung.movie_booking.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Lưu dạng key-value
     *
     * @param key   tên-định danh
     * @param value giá trị
     */
    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.info("Set key-value: {}, {}", key, value);
        } catch (Exception e) {
            log.error("Error setting key:{}", key, e);
        }
    }

    /**
     * Lưu dạng key-value với TTL
     *
     * @param key      tên-định danh
     * @param value    giá trị
     * @param timeUnit TTL sống
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            log.info("Set key: {} with TTL: {} {}", key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Error setting key with TTL: {}", key, e);
        }
    }

    /**
     * Lấy value từ key
     *
     * @param key key
     * @return value
     */
    @Override
    public Object get(String key) {
        try {

            Object value = redisTemplate.opsForValue().get(key);
            log.info("Getting successfully with key: {}", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting key: {}", key, e);
            return null;
        }
    }

    /**
     * Lấy String value từ key
     *
     * @param key key
     * @return string value
     */
    @Override
    public String getString(String key) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            log.info("Getting String value successfully with key: {}", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting key: {}", key, e);
            return null;
        }
    }

    /**
     * Xóa key
     *
     * @param key key
     * @return true/false
     */
    @Override
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            boolean deleted = Boolean.TRUE.equals(result);
            log.info("Deleting key: {} - Success: {}", key, deleted);
            return true;
        } catch (Exception e) {
            log.error("Error deleting key: {}", key, e);
            return false;
        }
    }

    /**
     * Check key
     *
     * @param key key
     * @return true/false
     */
    @Override
    public boolean existsKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            boolean exists = Boolean.TRUE.equals(result);
            log.info("Checking key exists:{} - Result: {}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking exists key:{}", key, e);
            return false;
        }
    }

    /**
     * Set TTL cho key
     */
    @Override
    public boolean expire(String key, Long timeout, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.expire(key, timeout, timeUnit);
            boolean success = Boolean.TRUE.equals(result);
            log.debug("Set expire for key: {} - TTL: {} {} - Success: {}",
                    key, timeout, timeUnit, success);
            return success;
        } catch (Exception e) {
            log.error("Error setting expiration for key: {}", key, e);
            return false;
        }    }

    /**
     * Lấy TTL
     *
     * @param key key
     * @return TTL
     */
    @Override
    public long getTTL(String key) {
        try {
            long result = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.debug("Get TTL for key: {} - TTL: {} seconds", key, result);
            return result;
        } catch (Exception e) {
            log.error("Error getting TTL for key: {}", key, e);
            return -1;
        }
    }
}
