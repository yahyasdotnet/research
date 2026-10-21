package net.yahyas.research.auth.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
	@Email @NotBlank @Size(max = 320) String email,
	@NotBlank @Size(min = 12, max = 64) String password
) {
}
