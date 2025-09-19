package com.hoangdung.movie_booking.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    /**
     * Lưu dạng key-value
     * @param key tên-định danh
     * @param value giá trị
     */
    void set(String key, Object value);

    /**
     * Lưu dạng key-value với TTL
     * @param key tên-định danh
     * @param value giá trị
     * @param timeUnit TTL sống
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * Lấy value từ key
     * @param key key
     * @return value
     */
    Object get(String key);

    /**
     * Lấy String value từ key
     * @param key key
     * @return string value
     */
    String getString(String key);

    /**
     * Xóa key
     * @param key key
     * @return true/false
     */
    boolean delete(String key);

    /**
     * Check key
     * @param key key
     * @return true/false
     */
    boolean existsKey(String key);

    /**
     * Set TTL cho key
     */
    boolean expire(String key, Long timeout, TimeUnit timeUnit);

    /**
     * Lấy TTL
     *
     * @param key key
     * @return TTL
     */
    long getTTL(String key);
}
