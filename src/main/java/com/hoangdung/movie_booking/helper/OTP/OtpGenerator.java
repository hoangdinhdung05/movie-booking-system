package com.hoangdung.movie_booking.helper.OTP;

import java.util.Random;

/**
 * Utility class for generating numeric OTP codes.
 */
public class OtpGenerator {

    /**
     * Generates a random numeric OTP of specified length.
     *
     * @param length length of the OTP
     * @return OTP as a string
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(10));  // chỉ số 0-9
        }
        return sb.toString();
    }
}
