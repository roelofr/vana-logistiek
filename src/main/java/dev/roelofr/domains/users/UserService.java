package dev.roelofr.domains.users;

import dev.roelofr.domains.users.model.User;
import dev.roelofr.domains.users.model.UserRepository;
import dev.roelofr.events.ModelCreatedEvent;
import dev.roelofr.service.FileService;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LaunchMode launchMode;
    private final FileService fileService;
    private final Event<ModelCreatedEvent<User>> userCreatedEvent;

    public List<User> list() {
        var list = userRepository.listAllWithGroups();

        log.info("User list requested, result = {}", list.size());
        log.debug("List is {}", list);

        return list;
    }

    public User findById(Long id) {
        if (id == null)
            return null;

        return userRepository.findById(id);
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

        var user = findByProviderId(token.getSubject());
        if (user.isPresent())
            return user.get();

        if (!launchMode.equals(LaunchMode.TEST)) {
            log.info("Token details:");
            for (var claimName : token.getClaimNames())
                log.info("- {}: {}", claimName, token.getClaim(claimName));
            throw new IllegalArgumentException("User was not found.");
        }

        var userByEmail = findByEmail(token.getName());
        if (userByEmail.isPresent())
            return userByEmail.get();

        throw new IllegalArgumentException("User was not found.");
    }

    @Transactional
    public void save(User user) {
        if (user.getId() != null)
            return;

        userRepository.persist(user);

        userCreatedEvent.fire(new ModelCreatedEvent<User>(user));
        userCreatedEvent.fireAsync(new ModelCreatedEvent<User>(user));
    }

    @Transactional
    public Optional<User> findWithRelationsByPrincipal(Principal token) {
        if (token instanceof JsonWebToken jwt) {
            if (jwt.getSubject() != null)
                return userRepository.findByProviderIdWithRelations(jwt.getSubject());

            if (launchMode.equals(LaunchMode.TEST) && jwt.getName() != null)
                return userRepository.findByProviderIdWithRelations(token.getName());
        }

        return Optional.empty();
    }

    public List<User> listAllWithGroups() {
        return userRepository.list("#User.listAllWithGroup");
    }

    public File getAvatar(User user) {
        if (user == null || user.getAvatar() == null)
            return null;

        return fileService.resolveSafe(user.getAvatar());
    }
}
