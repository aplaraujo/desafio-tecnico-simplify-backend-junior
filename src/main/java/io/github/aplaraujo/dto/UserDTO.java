package io.github.aplaraujo.dto;

public record UserDTO(
        Long id,
        String name,
        String email,
        String password
) {
}
