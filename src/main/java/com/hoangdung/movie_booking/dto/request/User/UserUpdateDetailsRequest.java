package com.hoangdung.movie_booking.dto.request.User;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class UserUpdateDetailsRequest {
    @Size(min = 4, max = 50)
    private String username;
    private String name;
    private String phone;
    private LocalDate dateOfBirth;
    private String avatarUrl;
}
