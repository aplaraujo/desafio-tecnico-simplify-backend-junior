package io.github.aplaraujo.dto;

public record AuthResponseDTO(String token, String tokenType, Long expiresIn) {
}
