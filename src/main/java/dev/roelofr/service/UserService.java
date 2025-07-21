package dev.roelofr.service;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.User;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LaunchMode launchMode;
    private final DistrictRepository districtRepository;

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

    @Transactional
    public User activateUser(Principal principal, long userId, List<String> roles, Long districtId) {
        var currentUser = fromPrincipal(principal);

        if (userId <= 0 || roles.isEmpty() || (districtId != null && districtId <= 0))
            throw new BadRequestException("Request invalid");

        var userOptional = userRepository.findByIdOptional(userId);
        if (userOptional.isEmpty())
            throw new NotFoundException("User was not found");

        var user = userOptional.get();
        if (user.is(currentUser))
            throw new ClientErrorException("Cannot modify yourself", Status.CONFLICT);

        user.setActive(true);

        var wantedRoles = new ArrayList<String>(roles);
        if (! wantedRoles.isEmpty() && !wantedRoles.contains(Roles.User))
            wantedRoles.add(Roles.User);

        user.setRoles(wantedRoles);

        log.info("Activated user {} and set roles to [{}]", user.getEmail(), user.getRoles());

        if (districtId == null)
            return user;

        var districtOptional = districtRepository.findByIdOptional(districtId);
        if (districtOptional.isEmpty())
            throw new BadRequestException("District was not found!?");

        user.setDistrict(districtOptional.get());

        return user;
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

        if (! launchMode.equals(LaunchMode.TEST))
            throw new IllegalArgumentException("User was not found.");

        var userByEmail = findByEmail(token.getName());
        if (userByEmail.isPresent())
            return userByEmail.get();

        throw new IllegalArgumentException("User was not found.");
    }
}
