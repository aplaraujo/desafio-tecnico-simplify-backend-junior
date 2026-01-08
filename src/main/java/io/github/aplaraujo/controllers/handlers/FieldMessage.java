package io.github.aplaraujo.controllers.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FieldMessage {
    private String fieldName;
    private String message;
}
