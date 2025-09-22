package com.hoangdung.movie_booking.helper.OTP;

import com.hoangdung.movie_booking.entity.User;
import com.hoangdung.movie_booking.utils.enums.OtpType;

public class OtpEmailTemplate {
    public static String buildContent(User user, String otp, OtpType type, int expiryMinutes) {
        String action = (type == OtpType.RESET_PASSWORD) ? "đặt lại mật khẩu" : "xác minh email";
        return "Xin chào " + user.getUsername() + ",\n\n"
                + "Mã OTP của bạn là: " + otp + "\n"
                + "Có hiệu lực trong: " + expiryMinutes + " phút\n\n"
                + "OTP này được dùng để " + action + ".\n"
                + "Vui lòng không chia sẻ mã này cho bất kỳ ai.";
    }
}
