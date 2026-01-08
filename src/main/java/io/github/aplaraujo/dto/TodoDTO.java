package io.github.aplaraujo.dto;

import io.github.aplaraujo.entities.enums.PriorityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoDTO(
      Long id,

      @NotBlank(message = "This field should not be empty")
      String name,

      @NotBlank(message = "This field should not be empty")
      String description,

      @NotNull(message = "This field should not be null")
      Boolean done,

      @NotNull(message = "This field should not be null")
      PriorityType priority,
      Long userId
) {
}
