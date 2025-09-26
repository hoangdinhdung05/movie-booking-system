package com.hoangdung.movie_booking.dto.response.User;

import com.hoangdung.movie_booking.utils.enums.RoleType;
import com.hoangdung.movie_booking.utils.validator.ValidEmail;
import com.hoangdung.movie_booking.utils.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.Set;

@Getter
public class AdminCreateUserRequest {

    @NotBlank(message = "Name not blank")
    private String name;

    @NotBlank(message = "Username not blank")
    private String username;

    @NotBlank(message = "Email not blank")
    @ValidEmail
    private String email;

    @NotBlank(message = "Password not blank")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    @ValidPassword
    private String password;

    @NotNull(message = "Roles must not be null")
    @Size(min = 1, message = "At least one role is required")
    private Set<RoleType> roles;
}
