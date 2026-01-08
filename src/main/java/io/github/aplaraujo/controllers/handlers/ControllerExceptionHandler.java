package io.github.aplaraujo.controllers.handlers;

import io.github.aplaraujo.services.exceptions.OperationNotAllowedException;
import io.github.aplaraujo.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationError handleMethodargumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<FieldMessage> list = fieldErrors.stream().map(fe -> new FieldMessage(fe.getField(), fe.getDefaultMessage())).toList();
        return new ValidationError(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid data", list);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationError handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ValidationError(HttpStatus.NOT_FOUND.value(), "Resource not found", List.of());
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ValidationError handleOperationNotAllowedException(OperationNotAllowedException e) {
        return ValidationError.standardResponse(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ValidationError handleAccessDeniedException(AccessDeniedException e) {
        return new ValidationError(HttpStatus.FORBIDDEN.value(), "Denied Access", List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ValidationError handleNotTreatedErrors(RuntimeException e) {
        return new ValidationError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please contact the system administration.", List.of());
    }
}
