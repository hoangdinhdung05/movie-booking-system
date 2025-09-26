package com.hoangdung.movie_booking.exception;

import com.hoangdung.movie_booking.utils.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {

    public InternalServerException(String message) {
        super(ErrorCode.INTERNAL_ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public InternalServerException(String code, String message) {
        super(code, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String code, String message, Throwable cause) {
        super(code, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}