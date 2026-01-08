package io.github.aplaraujo.controllers.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class CustomError {
    private final Instant timestamp;
    private final Integer status;
    private final String error;
    private final String path;
}
