package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.dto.response.User.UserResponse;
import com.hoangdung.movie_booking.entity.User;

public interface UserService {

    /**
     * Find user by email
     * @param email email
     * @return Info user
     */
    User getUserByEmail(String email);

    /**
     * Find user by userId
     *
     * @param id userId
     * @return Get info user
     */
    UserResponse getUserById(Long id);

}
