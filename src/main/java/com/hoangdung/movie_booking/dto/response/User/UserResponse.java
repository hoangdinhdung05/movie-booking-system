package com.hoangdung.movie_booking.dto.response.User;

import com.hoangdung.movie_booking.utils.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
}
