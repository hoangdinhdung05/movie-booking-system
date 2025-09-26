package com.hoangdung.movie_booking.dto.request.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Verify not blank")
    private String verifyKey;

    @NotBlank(message = "Password not blank")
    private String newPassword;

    @NotBlank(message = "Confirm password not blank")
    private String confirmPassword;
}

