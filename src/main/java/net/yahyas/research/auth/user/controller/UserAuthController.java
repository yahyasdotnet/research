package net.yahyas.research.auth.user.controller;

import jakarta.validation.Valid;
import net.yahyas.research.auth.user.dto.AuthResponse;
import net.yahyas.research.auth.user.dto.LoginRequest;
import net.yahyas.research.auth.user.dto.RegisterRequest;
import net.yahyas.research.auth.user.service.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

	private final UserAuthService userAuthService;

	public UserAuthController(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userAuthService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		try {
			return ResponseEntity.ok(userAuthService.login(request));
		}
		catch (AuthenticationException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}
	}
}
