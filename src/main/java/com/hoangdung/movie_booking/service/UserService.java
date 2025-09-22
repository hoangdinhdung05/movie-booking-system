package com.hoangdung.movie_booking.service;

import com.hoangdung.movie_booking.entity.User;

public interface UserService {
    User getUserByEmail(String email);
}
