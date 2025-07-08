package dev.roelofr.rest.resources;

import dev.roelofr.config.Roles;
import dev.roelofr.domain.User;
import dev.roelofr.domain.dto.UserDto;
import dev.roelofr.domain.dto.UserListDto;
import dev.roelofr.repository.DistrictRepository;
import dev.roelofr.repository.UserRepository;
import dev.roelofr.rest.request.ActivateUserRequest;
import dev.roelofr.rest.request.UpdateUserRequest;
import dev.roelofr.rest.validation.UserExists;
import dev.roelofr.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Slf4j
@Authenticated
@Path("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserResource {
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final UserService userService;

    @GET
    @Path("/")
    public RestResponse<List<UserListDto>> list() {
        return RestResponse.ok(userService.list());
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

    @POST
    @Path("/{id}")
    @Transactional
    @RolesAllowed(Roles.Admin)
    public RestResponse<User> update(@PathParam("id") @UserExists Long id, UpdateUserRequest request) {
        final var user = userRepository.findById(id);
        if (user == null)
            throw new BadRequestException("User not found");

        if (request.name().isPresent())
            user.setName(request.name().get());

        var cleanEmail = request.cleanEmail();
        if (cleanEmail != null) {
            if (userRepository.find("email", cleanEmail).count() > 0)
                throw new BadRequestException("Email [%s] is already in use".formatted(cleanEmail));

            user.setEmail(cleanEmail);
        }

        user.setRoles(request.roles());

        if (request.district() != null) {
            var newDistrict = districtRepository.findById(request.district());
            if (newDistrict == null)
                throw new BadRequestException("District set, but district [%d] does not exist".formatted(request.district()));

            user.setDistrict(newDistrict);
        }

        return RestResponse.ok(user);
    }

    @POST
    @Path("/{id}/activate")
    @Transactional
    @RolesAllowed(Roles.Admin)
    public RestResponse<User> activateUser(@PathParam("id") Long id, ActivateUserRequest request) {
        final var user = userRepository.findById(id);
        if (user == null)
            throw new BadRequestException("User not found");

        user.setRoles(request.roles());

        if (request.district() != null) {
            var newDistrict = districtRepository.findById(request.district());
            if (newDistrict == null)
                throw new BadRequestException("District set, but district [%d] does not exist".formatted(request.district()));

            user.setDistrict(newDistrict);
        }

        user.setActive(true);

        return RestResponse.ok(user);
    }
}
