package com.hoangdung.movie_booking.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    AUTHENTICATION_ERROR("AUTHENTICATION_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    TOKEN_EXPIRED("INTERNAL_ERROR");

    private final String value;
}
