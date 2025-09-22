package com.hoangdung.movie_booking.dto.response.OTP;

import com.hoangdung.movie_booking.utils.enums.OtpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu resend OTP")
public class ResendOtpRequest {

    @Schema(description = "Email người dùng", example = "example@gmail.com")
    @Email(message = "Invalid email")
    @NotBlank(message = "Email cannot be left blank")
    private String email;

    @Schema(description = "Loại OTP", example = "REGISTER")
    @NotNull(message = "Type otp cannot be left blank")
    private OtpType type;
}