package com.hoangdung.movie_booking.exception;

import org.springframework.http.HttpStatus;

public class EmailTemplateException extends BaseException {

    private static final String CODE = "EMAIL_TEMPLATE_ERROR";

    public EmailTemplateException(String message) {
        super(CODE, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EmailTemplateException(String message, Throwable cause) {
        super(CODE, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
