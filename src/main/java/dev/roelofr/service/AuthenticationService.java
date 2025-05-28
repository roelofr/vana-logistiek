package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import io.quarkiverse.bucket4j.runtime.RateLimited;
import io.quarkiverse.bucket4j.runtime.resolver.IpResolver;
import io.quarkus.security.credential.PasswordCredential;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claims;

import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private final IdentityProviderManager identityProviderManager;

    private final JWTParser jwtParser;

    @Transactional
    @RateLimited(bucket = "authentication", identityResolver = IpResolver.class)
    public RegistrationResult register(ActingUser actingUser, String email, String password, String name) {
        var usernameNormalized = email.toLowerCase().trim();

        var userAlreadyExists = userRepository.findByEmailOptional(usernameNormalized);
        if (userAlreadyExists.isPresent())
            return RegistrationResult.failed(Reason.AccountAlreadyExists);

        var user = User.builder()
            .name(name)
            .email(email)
            .password(password)
            .active(false)
            .build();

        log.info("Registering user {}", email);

        try {
            userRepository.persist(user);

            return RegistrationResult.ok();
        } catch (Exception e) {
            log.error("Error while persisting user {}", email, e);

            return RegistrationResult.failed(Reason.SystemFailure);
        }
    }

    @RateLimited(bucket = "authentication", identityResolver = IpResolver.class)
    public AuthenticationResult authenticate(ActingUser actingUser, String username, String password) {
        var credential = new PasswordCredential(password.toCharArray());
        var authenticationRequest = new UsernamePasswordAuthenticationRequest(username, credential);

        log.info("Login on {} by {} started", username, actingUser.identifier());

        var identity = identityProviderManager.authenticateBlocking(authenticationRequest);
        if (identity == null) {
            log.info("Login on {} by IP {} failed: invalid credentials", username, actingUser.identifier());
            return new AuthenticationResult(Reason.InvalidLogin);
        }

        var principal = identity.getPrincipal();
        var userOptional = userRepository.findByEmailOptional(username);

        if (userOptional.isEmpty()) {
            log.warn("Login on {} by IP {} failed: user model not found", username, actingUser.identifier());
            return new AuthenticationResult(Reason.SystemFailure);
        }

        var user = userOptional.get();
        if (!user.isActive()) {
            log.info("Login on {} by IP {} failed: account locked", username, actingUser.identifier());
            return new AuthenticationResult(Reason.AccountLocked);
        }

        log.info("Login on {} by IP {} OK. Starting session...", username, actingUser.identifier());

        var expiration = determineExpiration();

        var token = buildJwt(principal, user, expiration);

        return new AuthenticationResult(
            username,
            token,
            OffsetDateTime.ofInstant(expiration, ZoneOffset.UTC)
        );
    }

    /**
     * Auto-expire tokens at 03:00 the next day.
     */
    Instant determineExpiration() {
        return Instant.now(Clock.system(ZoneId.of("Europe/Amsterdam")))
            .truncatedTo(ChronoUnit.DAYS)
            .plus(1, ChronoUnit.DAYS)
            .plus(3, ChronoUnit.HOURS);
    }

    /**
     * Build a JWT
     */
    String buildJwt(Principal principal, User user, Instant expiration) {
        return Jwt.upn(principal.getName())
            .groups(new HashSet<>(user.getRoles()))
            .subject(user.getName())
            .claim(Claims.full_name, user.getName())
            .claim(Claims.email, user.getEmail())
            .expiresAt(expiration)
            .sign();

    }

    @RequiredArgsConstructor
    public enum Reason {
        PreconditionFailed("Required conditions were not met."),
        InvalidLogin("Combinatie van e-mailadres en wachtwoord lijkt niet te bestaan."),
        SystemFailure("Er ging iets fout."),
        AccountLocked("Deze account is uitgeschakeld."),
        AccountAlreadyExists("Er bestaat al een account met dit e-mailadres.");

        @Getter
        private final String message;

        @Override
        public String toString() {
            return String.format(
                "Reason{name='%s', message='%s'}",
                name(),
                message
            );
        }
    }

    public record ActingUser(String name, String identifier) {
        public static ActingUser fromRequest(HttpServerRequest request) {
            return new ActingUser("web request", request.remoteAddress().toString());
        }
    }

    public record RegistrationResult(boolean success, Reason reason) {
        static RegistrationResult failed(Reason reason) {
            return new RegistrationResult(false, reason);
        }

        static RegistrationResult ok() {
            return new RegistrationResult(true, null);
        }
    }

    public record AuthenticationResult(boolean success,
                                       Reason reason,
                                       String username,
                                       String token,
                                       OffsetDateTime tokenExpiration) {

        public AuthenticationResult(Reason reason) {
            this(false, reason, null, null, null);
        }

        public AuthenticationResult(String username, String token, OffsetDateTime expiration) {
            this(true, null, username, token, expiration);

        }
    }
}
