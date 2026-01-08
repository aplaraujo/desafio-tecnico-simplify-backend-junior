package io.github.aplaraujo.dto;

import io.github.aplaraujo.entities.enums.PriorityType;
import jakarta.validation.constraints.NotBlank;

public record TodoDTO(
      Long id,

      @NotBlank(message = "This field should not be empty")
      String name,

      @NotBlank(message = "This field should not be empty")
      String description,
      Boolean done,

      @NotBlank(message = "This field should not be empty")
      PriorityType priority,
      Long userId
) {
}
