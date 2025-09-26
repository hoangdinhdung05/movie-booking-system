package com.hoangdung.movie_booking.dto.response.User;

import com.hoangdung.movie_booking.utils.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class UserDetailsResponse {
    private String username;
    private String email;
    private String name;
    private String phone;
    private String avatarUrl;
    private LocalDate dateOfBirth;
}
