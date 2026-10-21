package net.yahyas.research.auth.user.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}
