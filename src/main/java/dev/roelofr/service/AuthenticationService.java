package dev.roelofr.service;

import dev.roelofr.repository.UserRepository;
import io.quarkiverse.bucket4j.runtime.RateLimited;
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver;
import io.quarkus.security.credential.PasswordCredential;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private final IdentityProviderManager identityProviderManager;

    private final JWTParser jwtParser;

    @RateLimited(bucket = "authentication", identityResolver = IpResolver.class)
    public AuthenticationResult authenticate(ActingUser actingUser, String username, String password) {
        var credential = new PasswordCredential(password.toCharArray());
        var authenticationRequest = new UsernamePasswordAuthenticationRequest(username, credential);

        log.info("Login on {} by {} started", username, actingUser.identifier());

        var identity = identityProviderManager.authenticateBlocking(authenticationRequest);
        if (identity == null) {
            log.info("Login on {} by IP {} failed: invalid credentials", username, actingUser.identifier());
            return new AuthenticationResult(AuthenticationFailureReason.InvalidLogin);
        }

        var principal = identity.getPrincipal();
        var userOptional = userRepository.findByEmailOptional(username);

        if (userOptional.isEmpty()) {
            log.warn("Login on {} by IP {} failed: user model not found", username, actingUser.identifier());
            return new AuthenticationResult(AuthenticationFailureReason.SystemFailure);
        }

        var user = userOptional.get();
        if (!user.isActive()) {
            log.info("Login on {} by IP {} failed: account locked", username, actingUser.identifier());
            return new AuthenticationResult(AuthenticationFailureReason.AccountLocked);
        }

        log.info("Login on {} by IP {} OK. Starting session...", username, actingUser.identifier());

        var token = Jwt.upn(principal.getName())
            .groups(new HashSet<>(user.getRoles()))
            .subject(user.getName())
            .claim(Claims.full_name, user.getName())
            .claim(Claims.email, user.getEmail())
            .sign();

        long jwtExpiration;
        try {
            jwtExpiration = jwtParser.parse(token)
                .getExpirationTime();
        } catch (ParseException e) {
            log.warn("Failed to parse generated JWT for username [{}]", username, e);
            return new AuthenticationResult(AuthenticationFailureReason.SystemFailure);
        }

        return new AuthenticationResult(
            username,
            token,
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(jwtExpiration), ZoneOffset.UTC)
        );
    }

    public enum AuthenticationFailureReason {
        InvalidLogin,
        SystemFailure,
        AccountLocked
    }

    public record ActingUser(String name, String identifier) {
        public static ActingUser fromRequest(HttpServerRequest request) {
            return new ActingUser("web request", request.remoteAddress().toString());
        }
    }

    public record AuthenticationResult(boolean success, AuthenticationFailureReason reason, String username,
                                       String token,
                                       OffsetDateTime tokenExpiration) {

        public AuthenticationResult(AuthenticationFailureReason reason) {
            this(false, reason, null, null, null);
        }

        public AuthenticationResult(String username, String token, OffsetDateTime expiration) {
            this(true, null, username, token, expiration);

        }
    }
}
