package com.hoangdung.movie_booking.utils;

import com.hoangdung.movie_booking.utils.enums.OtpType;

public class OtpRedisKeyUtil {
    public static String otpKey(String email, OtpType type) {
        return "OTP:" + email + ":" + type;
    }

    public static String otpCountKey(String email, OtpType type) {
        return "OTP_SEND_COUNT:" + email + ":" + type;
    }

    public static String verifyKey(String key) {
        return "OTP_VERIFY_KEY:" + key;
    }
}
