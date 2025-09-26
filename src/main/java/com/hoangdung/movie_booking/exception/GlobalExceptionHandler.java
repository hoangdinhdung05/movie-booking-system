package com.hoangdung.movie_booking.exception;

import com.hoangdung.movie_booking.dto.response.System.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that intercepts exceptions thrown in controllers
 * and translates them into consistent {@link ErrorResponse} objects.
 * Provides handling for:
 * <ul>
 *   <li>Custom {@link BaseException}</li>
 *   <li>Spring validation exceptions (e.g. {@link MethodArgumentNotValidException}, {@link BindException})</li>
 *   <li>JPA validation exceptions (e.g. {@link ConstraintViolationException})</li>
 *   <li>Common runtime errors (e.g. {@link IllegalArgumentException})</li>
 *   <li>Unexpected {@link Exception}</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom business exceptions.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, WebRequest request) {
        log.error("Business exception occurred: {}", ex.getMessage());
        return buildErrorResponse(ex.getCode(), ex.getMessage(), ex.getHttpStatus(), request, null);
    }

    /**
     * Handle validation errors when @Valid fails in @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s (rejected: %s)",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());

        log.error("Validation failed: {}", String.join("; ", messages));

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        return buildErrorResponse("VALIDATION_ERROR", "Validation failed", HttpStatus.BAD_REQUEST, request, fieldErrors);
    }

    /**
     * Handle validation errors when @Valid fails in form-data or query params.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, WebRequest request) {
        log.error("Bind exception occurred: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        return buildErrorResponse("VALIDATION_ERROR", "Validation failed", HttpStatus.BAD_REQUEST, request, fieldErrors);
    }

    /**
     * Handle validation errors thrown directly by Hibernate Validator.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("Constraint violation exception occurred: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> ErrorResponse.FieldError.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .rejectedValue(violation.getInvalidValue())
                        .build())
                .collect(Collectors.toList());

        return buildErrorResponse("CONSTRAINT_VIOLATION", "Constraint validation failed", HttpStatus.BAD_REQUEST, request, fieldErrors);
    }

    /**
     * Handle illegal argument errors.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument exception occurred: {}", ex.getMessage());
        return buildErrorResponse("ILLEGAL_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    /**
     * Handle all unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }

    /**
     * Hand login failure errors (username/password error).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.warn("Login failed: {}", ex.getMessage());
        return buildErrorResponse(
                "BAD_CREDENTIALS",
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED,
                request,
                null
        );
    }

    /**
     * Handle authenticate (User not found).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return buildErrorResponse(
                "AUTHENTICATION_FAILED",
                "Authentication failed",
                HttpStatus.UNAUTHORIZED,
                request,
                null
        );
    }

    /**
     * Handle authorization errors when user doesn't have required permissions.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        log.warn("Authorization denied: {} for path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN,
                request,
                null
        );
    }

    /**
     * Handle access denied errors (fallback for older Spring Security versions).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {} for path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN,
                request,
                null
        );
    }

    /**
     * Helper method to build {@link ErrorResponse}.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String code,
            String message,
            HttpStatus status,
            WebRequest request,
            List<ErrorResponse.FieldError> fieldErrors
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
