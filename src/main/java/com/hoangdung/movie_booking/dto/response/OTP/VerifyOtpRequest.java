package com.hoangdung.movie_booking.dto.response.OTP;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Yêu cầu xác thực mã OTP")
public class VerifyOtpRequest {

    @Schema(description = "Email của người dùng", example = "example@gmail.com")
    @Email(message = "Invalid email")
    @NotBlank(message = "Email cannot null")
    private String email;

    @Schema(description = "Mã OTP", example = "123456")
    @NotBlank(message = "OTP cannot null")
    private String code;
}