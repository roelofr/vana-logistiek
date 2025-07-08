package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Load a user from a Java Principal.
     */
    public User fromPrincipal(Principal principal) {
        var name = principal.getName();
        var userOptional = findByEmail(name);
        if (userOptional.isEmpty())
            throw new IllegalArgumentException("User was not found");

        var user = userOptional.get();
        if (!(principal instanceof JsonWebToken token))
            return user;

        if (!token.containsClaim(Claims.cnf.name()))
            throw new IllegalStateException("JWT claim is missing Confirm claim");

        var confirmationAsLong = Long.valueOf(token.getClaim(Claims.cnf));
        if (!user.getId().equals(confirmationAsLong)) {
            log.warn("JWT with kid {} tried to login as {} but confirmation ID is mismatching", token.getTokenID(), token.getName());
            throw new IllegalStateException("JWT claim is mismatching user.");
        }

        return user;
    }

    /**
     * Finds a user by the given email. Will be normalized.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailOptional(email);
    }

    public Optional<User> findById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findByIdOptional(id);
    }
}
