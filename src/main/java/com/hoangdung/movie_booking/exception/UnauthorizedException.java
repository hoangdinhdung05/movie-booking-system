package com.hoangdung.movie_booking.exception;

import com.hoangdung.movie_booking.utils.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(String.valueOf(ErrorCode.AUTHENTICATION_ERROR), message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        super(String.valueOf(ErrorCode.AUTHENTICATION_ERROR), "Access denied. Authentication required.", HttpStatus.UNAUTHORIZED);
    }
}