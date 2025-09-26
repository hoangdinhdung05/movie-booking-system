package com.hoangdung.movie_booking.dto.request.User;

import com.hoangdung.movie_booking.utils.validator.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Yêu cầu thay đổi mật khẩu")
public class ChangePasswordRequest {
    @Schema(description = "Mật khẩu hiện tại của người dùng", example = "currentPassword123")
    @NotBlank(message = "Current password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 8 and 20 characters")
    private String currentPassword;

    @Schema(description = "Mật khẩu mới của người dùng", example = "newPassword123")
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 6, max = 20, message = "New password must be between 8 and 20 characters")
    private String newPassword;

    @Schema(description = "Xác nhận mật khẩu mới của người dùng", example = "newPassword123")
    @NotBlank(message = "Confirm password cannot be blank")
    @Size(min = 6, max = 20, message = "Confirm password must be between 8 and 20 characters")
    @ValidPassword
    private String confirmPassword;
}
