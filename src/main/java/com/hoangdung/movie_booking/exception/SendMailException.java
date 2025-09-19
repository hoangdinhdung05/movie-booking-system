package com.hoangdung.movie_booking.exception;

import org.springframework.http.HttpStatus;

public class SendMailException extends BaseException {

    private static final String CODE = "EMAIL_SEND_FAIL";

    public SendMailException(String message) {
        super(CODE, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SendMailException(String message, Throwable cause) {
        super(CODE, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
