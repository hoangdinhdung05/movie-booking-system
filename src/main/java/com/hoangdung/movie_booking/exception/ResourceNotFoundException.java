package com.hoangdung.movie_booking.exception;

import com.hoangdung.movie_booking.utils.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(ErrorCode.RESOURCE_NOT_FOUND.getValue(),
                String.format("%s with id '%s' not found", resourceName, resourceId),
                HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND.getValue(), message, HttpStatus.NOT_FOUND);
    }
}
