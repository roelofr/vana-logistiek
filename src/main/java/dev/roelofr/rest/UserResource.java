package dev.roelofr.rest;

import dev.roelofr.domain.User;
import dev.roelofr.domain.dto.UserDto;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.rest.dtos.ErrorDto;
import dev.roelofr.rest.request.CreateUserRequest;
import io.quarkus.panache.common.exception.PanacheQueryException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Path("/api/users")
@RequiredArgsConstructor
public class UserResource {

    final UserRepository userRepository;
    final DistrictRepository districtRepository;

    @GET
    @RolesAllowed("user")
    @Path("/me")
    public String me(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }

    @GET
    @Path("/")
    public RestResponse<List<UserListDto>> list() {
        var result = userRepository.findAll()
            .project(UserListDto.class)
            .list();

        return RestResponse.ok(result);
    }

    @POST
    @Path("/")
    @Transactional
    public RestResponse create(CreateUserRequest request) {
        var cleanEmail = request.cleanEmail();

        if (userRepository.find("email", cleanEmail).firstResultOptional().isPresent())
            return RestResponse.status(
                Status.CONFLICT,
                ErrorDto.forField("email", "Email al in gebruik"));

        var createUser = User.builder()
            .name(request.name())
            .email(cleanEmail)
            .roles(request.roles());

        if (request.district().isPresent()) {
            var districtOptional = districtRepository.findByIdOptional(request.district().get());
            if (districtOptional.isEmpty())
                return RestResponse.status(
                    Status.CONFLICT,
                    ErrorDto.forField("email", "Email al in gebruik"));

            createUser.district(districtOptional.get());
        }

        var user = createUser.build();

        try {
            userRepository.persist(user);
        } catch (PanacheQueryException e) {
            log.error("Failed to persist user with email {}: {}", user.getEmail(), e.getMessage());

            return RestResponse.status(
                Status.INTERNAL_SERVER_ERROR,
                new ErrorDto(1, "Gebruiker kon niet worden opgeslagen: %s".formatted(e.getMessage()))
            );
        }

        try {
            var url = UriBuilder.fromMethod(getClass(), "find")
                .build(user.getId())
                .toURL();

            return RestResponse.created(url.toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            return RestResponse.status(
                Status.INTERNAL_SERVER_ERROR,
                new ErrorDto(e.hashCode(), "Gebruiker opslaan mislukt: %s".formatted(e.getMessage()))
            );
        }
    }

    @GET
    @Path("/{id}")
    public RestResponse<UserDto> find(@PathParam("id") Long id) {
        var result = userRepository.find("id", id)
            .project(UserDto.class)
            .firstResultOptional();

        if (result.isEmpty())
            return RestResponse.notFound();

        return RestResponse.ok(result.get());
    }
}
