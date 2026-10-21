package net.yahyas.research.auth.user.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import net.yahyas.research.auth.user.dto.AuthResponse;
import net.yahyas.research.auth.user.dto.LoginRequest;
import net.yahyas.research.auth.user.dto.RegisterRequest;
import net.yahyas.research.auth.user.entity.UserAccount;
import net.yahyas.research.auth.user.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserAuthService {

	private final UserAccountRepository userAccountRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtEncoder jwtEncoder;
	private final Duration tokenLifetime;

	public UserAuthService(
		UserAccountRepository userAccountRepository,
		PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager,
		JwtEncoder jwtEncoder,
		@Value("${app.security.jwt.expiration:PT15M}") Duration tokenLifetime
	) {
		this.userAccountRepository = userAccountRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtEncoder = jwtEncoder;
		if (tokenLifetime.isZero() || tokenLifetime.isNegative()) {
			throw new IllegalArgumentException("JWT expiration must be a positive duration");
		}
		this.tokenLifetime = tokenLifetime;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		if (userAccountRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
		}
		if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at most 72 UTF-8 bytes");
		}

		UserAccount account = userAccountRepository.save(
			new UserAccount(email, passwordEncoder.encode(request.password())));
		return createToken(account.getEmail());
	}

	public AuthResponse login(LoginRequest request) {
		if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
			throw new BadCredentialsException("Invalid email or password");
		}
		Authentication authentication = authenticationManager.authenticate(
			UsernamePasswordAuthenticationToken.unauthenticated(
				normalizeEmail(request.email()), request.password()));
		return createToken(authentication.getName());
	}

	private AuthResponse createToken(String subject) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(tokenLifetime);
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.subject(subject)
			.claim("roles", List.of("USER"))
			.issuedAt(issuedAt)
			.expiresAt(expiresAt)
			.build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(
			JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
		return new AuthResponse(token, "Bearer", tokenLifetime.toSeconds());
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
