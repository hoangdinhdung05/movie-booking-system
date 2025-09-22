package com.hoangdung.movie_booking.exception;

import org.springframework.http.HttpStatus;

public class OtpException extends BaseException {
    public OtpException(String message) {
        super("OTP_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
