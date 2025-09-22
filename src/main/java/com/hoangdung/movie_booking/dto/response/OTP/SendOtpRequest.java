package com.hoangdung.movie_booking.dto.response.OTP;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu gửi mã OTP")
public class SendOtpRequest {
    @Schema(description = "Email người dùng", example = "example@gmail.com")
    @Email(message = "Invalid email")
    @NotBlank(message = "Email cannot null")
    private String email;
}