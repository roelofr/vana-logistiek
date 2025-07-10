package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.integrations.hanko.HankoClient;
import dev.roelofr.integrations.hanko.model.HankoUser;
import dev.roelofr.repository.UserRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JWTParser jwtParser;
    private final JwtSubjectUserCache jwtSubjectUserCache;

    @Context
    private final Instance<SecurityContext> securityContextInstance;

    @Inject
    @RestClient
    HankoClient hankoClient;

    public Optional<User> getCurrentUser() {
        final var securityContext = securityContextInstance.get();

        if (securityContext.getUserPrincipal() == null) {
            log.info("userPrincipal is null");
            return Optional.empty();
        }

        try {
            return Optional.of(userService.fromPrincipal(securityContext.getUserPrincipal()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Verify a JWT's validity and use the claims to create or find the correct user.
     */
    @Transactional
    public User consume(@NotBlank String token) {
        try {
            var jwt = jwtParser.parse(token);

            log.debug("Mapped JWT string [{}] into [{}]", token, jwt);

            if (jwt.getExpirationTime() < Instant.now().getEpochSecond()) {
                throw new AuthenticationException(Reason.InvalidLogin, "JWT has expired.");
            }

            log.info("Recieved token for subject [{}]", jwt.getSubject());
            log.debug("JWT parsed to token {}", jwt);

            if (!verifyJwt(jwt))
                throw new AuthenticationException(Reason.InvalidLogin, "JWT was not verified by broker.");

            var user = resolveJwtUser(jwt);

            log.info("User {} is active? {}", user.getName(), user.isActive());

            if (!user.isActive())
                throw new AuthenticationException(Reason.AccountLocked);

            jwtSubjectUserCache.put(jwt, user);

            return user;
        } catch (ParseException jwtException) {
            log.warn("Failed to parse JWT: {}", jwtException.getMessage(), jwtException);
            log.debug("JWT = {}", token);

            throw new AuthenticationException(Reason.InvalidLogin, "JWT failed to verify.");
        }
    }

    private User resolveJwtUser(JsonWebToken jwt) {
        final var providerId = jwt.getSubject();
        log.debug("Trying to find user by subject {}", providerId);

        var knownUser = userRepository.findByProviderId(providerId);
        if (knownUser.isPresent())
            return knownUser.get();

        log.info("Downloading user information for [{}]", providerId);
        var userInformation = downloadUser(jwt);

        if (userInformation == null)
            throw new AuthenticationException(Reason.InvalidLogin, "Failed to retrieve account from broker.");

        var email = userInformation.email();
        log.debug("Trying to find user by email {}", email);

        var emailUserOptional = userRepository.findByEmailOptional(email);
        if (emailUserOptional.isPresent()) {
            var user = emailUserOptional.get();
            user.setProviderId(providerId);
            return user;
        }

        log.info("Registering user [{}]...", userInformation.email());

        return createUserFromJwt(userInformation);
    }

    private boolean verifyJwt(JsonWebToken token) {
        try {
            var response = hankoClient.validate(token.getRawToken());
            if (!response.isValid() && !response.isExpired()) {
                log.warn("JWT token [{}] was invalid!", token.getTokenID());
            }

            log.info("JWT token [{}] is valid", token.getTokenID());
            return true;
        } catch (ClientWebApplicationException e) {
            log.warn("JWT token [{}] was rejected by the upstream!", token.getTokenID());
            return false;
        }
    }

    private HankoUser downloadUser(JsonWebToken token) {
        var userId = token.getSubject();

        try {
            return hankoClient.getUser(userId, token.getRawToken());
        } catch (ClientWebApplicationException e) {
            log.warn("Got HTTP status {} when looking up user [{}]", e.getMessage(), userId);
            return null;
        }
    }

    private User createUserFromJwt(HankoUser hankoUser) {
        var user = User.builder()
            .active(true)
            .name(hankoUser.email())
            .name(hankoUser.email())
            .build();

        userRepository.persist(user);

        return user;
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

    @Getter
    public static class AuthenticationException extends RuntimeException {
        private final Reason reason;

        public AuthenticationException(Reason reason, String message) {
            super(message);

            this.reason = reason;
        }

        public AuthenticationException(Reason reason) {
            this(reason, reason.getMessage());
        }
    }
}
