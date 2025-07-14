package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LaunchMode launchMode;

    public List<UserListDto> list() {
        var list = userRepository.listAll();

        log.info("User list requested, result = {}", list.size());
        log.debug("List is {}", list);

        return list.stream().map(UserListDto::new).toList();
    }

    public Optional<User> findById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findByIdOptional(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailOptional(email);
    }

    public Optional<User> findByProviderId(@NotBlank String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    /**
     * Load a user from a Java Principal.
     */
    public User fromPrincipal(Principal principal) {
        if (!(principal instanceof JsonWebToken token))
            throw new IllegalArgumentException("Principal not a web token.");

        var user = findByProviderId(token.getName());
        if (user.isPresent())
            return user.get();

        throw new IllegalArgumentException("User was not found.");
    }
}
