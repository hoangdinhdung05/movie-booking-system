package com.hoangdung.movie_booking.exception;

import com.hoangdung.movie_booking.dto.response.ErrorResponse;
import com.hoangdung.movie_booking.utils.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.List;

@Getter
public class ValidationException extends BaseException {

    private final List<ErrorResponse.FieldError> fieldErrors;

    public ValidationException(String message) {
        super(String.valueOf(ErrorCode.VALIDATION_ERROR), message, HttpStatus.BAD_REQUEST);
        this.fieldErrors = null;
    }

    public ValidationException(String field, String message, Object rejectedValue) {
        super(String.valueOf(ErrorCode.VALIDATION_ERROR),
                String.format("Validation failed for field '%s': %s", field, message),
                HttpStatus.BAD_REQUEST);
        this.fieldErrors = List.of(
                new ErrorResponse.FieldError(field, message, rejectedValue)
        );
    }

    public ValidationException(List<ErrorResponse.FieldError> fieldErrors) {
        super(String.valueOf(ErrorCode.VALIDATION_ERROR), "Validation failed", HttpStatus.BAD_REQUEST);
        this.fieldErrors = fieldErrors;
    }

    public List<ErrorResponse.FieldError> getFieldErrors() {
        return fieldErrors;
    }
}
