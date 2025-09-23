package com.hoangdung.movie_booking.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {

    public BusinessException(String code, String message) {
        super(code, message, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message) {
        super("BUSINESS_EXCEPTION", message, HttpStatus.BAD_REQUEST);
    }


    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, HttpStatus.BAD_REQUEST, cause);
    }
}
