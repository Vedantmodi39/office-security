package com.simform.office.exception.handler;

import com.simform.office.dto.GenericResponse;
import com.simform.office.exception.ResourceNotFoundException;
import com.simform.office.exception.UserAlreadyExistsException;
import com.simform.office.util.EmptyJsonBody;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, @NonNull HttpHeaders headers, HttpStatus status, @NonNull WebRequest request) {
        String errorMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(new GenericResponse(false, "Invalid input details", errorMessage, status.value(), LocalDateTime.now()), status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return new ResponseEntity<>(new GenericResponse(false, exception.getMessage(), new EmptyJsonBody(), HttpStatus.METHOD_NOT_ALLOWED.value(), LocalDateTime.now()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse> resourceNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(new GenericResponse(true, exception.getMessage(), new EmptyJsonBody(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<GenericResponse> userAlreadyExistsException(UserAlreadyExistsException exception) {
        return new ResponseEntity<>(new GenericResponse(true, exception.getMessage(), new EmptyJsonBody(), HttpStatus.CONFLICT.value(), LocalDateTime.now()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<GenericResponse> handleBadCredentialException(BadCredentialsException exception) {
        return new ResponseEntity<>(new GenericResponse(false, exception.getMessage(), new EmptyJsonBody(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public final ResponseEntity<GenericResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return new ResponseEntity<>(new GenericResponse(false, exception.getMessage(), new EmptyJsonBody(), HttpStatus.FORBIDDEN.value(), LocalDateTime.now()), HttpStatus.FORBIDDEN);
    }
}
