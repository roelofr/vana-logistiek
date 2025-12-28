package dev.roelofr.service;

import dev.roelofr.domain.User;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response.Status;
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
    private final DistrictRepository districtRepository;

    public Optional<User> findBySecurityIdentity(SecurityIdentity securityIdentity) {
        var principal = securityIdentity.getPrincipal();
        if (principal instanceof JsonWebToken jwtPrincipal)
            return userRepository.findByProviderId(jwtPrincipal.getSubject());

        log.warn("Tried to resolve user token from principal of type {}, which is not supported", principal.getClass());

        return Optional.empty();
    }

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

        if (!launchMode.equals(LaunchMode.TEST))
            throw new IllegalArgumentException("User was not found.");

        var userByEmail = findByEmail(token.getName());
        if (userByEmail.isPresent())
            return userByEmail.get();

        throw new IllegalArgumentException("User was not found.");
    }

    @Transactional
    public User setNameOfUser(Principal principal, Long userId, @NotBlank String name) {
        var currentUser = fromPrincipal(principal);

        if (userId <= 0 || name == null || name.isBlank())
            throw new BadRequestException("Request invalid");

        var userOptional = userRepository.findByIdOptional(userId);
        if (userOptional.isEmpty())
            throw new NotFoundException("User was not found");

        var user = userOptional.get();
        if (user.is(currentUser))
            throw new ClientErrorException("Cannot modify yourself", Status.CONFLICT);

        log.info("Update name of user #{} to {}", user.getId(), name);

        user.setName(name);

        return user;
    }
}
