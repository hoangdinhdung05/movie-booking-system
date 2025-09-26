package com.hoangdung.movie_booking.exception;

import com.hoangdung.movie_booking.utils.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class TokenException extends BaseException {
    public TokenException(String message) {
        super(ErrorCode.VALIDATION_ERROR.getValue(), message, HttpStatus.UNAUTHORIZED);
    }
}
