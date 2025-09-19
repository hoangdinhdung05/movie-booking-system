package com.hoangdung.movie_booking.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super("FORBIDDEN", message, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException() {
        super("FORBIDDEN", "Access forbidden. Insufficient permissions.", HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String code, String message) {
        super(code, message, HttpStatus.FORBIDDEN);
    }
}