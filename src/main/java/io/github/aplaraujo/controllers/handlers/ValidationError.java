package io.github.aplaraujo.controllers.handlers;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ValidationError(int status, String message, List<FieldMessage> errors) {
    public static ValidationError standardResponse(String message) {
        return new ValidationError(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ValidationError conflict(String message) {
        return new ValidationError(HttpStatus.CONFLICT.value(), message, List.of());
    }
}
