package com.hoangdung.movie_booking.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(String message) {
        super("USER_EXISTS_ERROR", message, HttpStatus.BAD_REQUEST);
    }

}
