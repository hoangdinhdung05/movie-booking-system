package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.dto.response.User.UserResponse;

public interface UserService {

    /**
     * Find user by userId
     *
     * @param id userId
     * @return Get info user
     */
    UserResponse getUserById(Long id);

}
