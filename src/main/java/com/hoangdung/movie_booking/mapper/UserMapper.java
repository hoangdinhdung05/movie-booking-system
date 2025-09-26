package com.hoangdung.movie_booking.mapper;

import com.hoangdung.movie_booking.dto.response.User.UserResponse;
import com.hoangdung.movie_booking.entity.User;

public class UserMapper {

    public static UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

}
