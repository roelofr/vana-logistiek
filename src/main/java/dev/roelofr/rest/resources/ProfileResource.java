package dev.roelofr.rest.resources;

import dev.roelofr.domains.users.UserService;
import dev.roelofr.domains.users.model.Group;
import dev.roelofr.domains.users.model.User;
import dev.roelofr.integrations.pocketid.PocketIdException;
import dev.roelofr.integrations.pocketid.PocketIdService;
import dev.roelofr.jobs.UpdatePocketUsers;
import dev.roelofr.rest.responses.ProfileResponse;
import dev.roelofr.service.FileService;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.net.URI;
import java.util.Set;

@Slf4j
@Authenticated
@Path("/profile")
@RequiredArgsConstructor
public class ProfileResource {
    private final Set<String> allowedProfilePictureMimeTypes = Set.of("image/jpeg", "image/png", "image/webp");
    private final PocketIdService pocketIdService;
    private final FileService fileService;
    private final UserService userService;
    private final UpdatePocketUsers updatePocketUsers;

    @Transactional
    String getAvatarUrl(User user) {
        if (user.getAvatar() == null) {
            var avatarFromPocket = pocketIdService.getUserAvatar(user);
            if (avatarFromPocket == null)
                return null;

            user.setAvatar(avatarFromPocket);
        }

        return user.getAvatar();
    }

    @GET
    public RestResponse<ProfileResponse> get(@Context UriInfo uriInfo, @Context User user) {
        return RestResponse.ok(
            new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                user.getGroups().stream().map(Group::getName).toList(),
                getAvatarUrl(user)
            )
        );
    }

    @GET
    @Path("/{id}/picture")
    public RestResponse<Void> getProfilePicture(@PathParam("id") long id) {
        var user = userService.findById(id);
        if (user == null)
            return RestResponse.notFound();

        var avatar = getAvatarUrl(user);
        if (avatar == null)
            return RestResponse.noContent();

        return RestResponse.seeOther(URI.create(avatar));
    }

    @PUT
    @Path("/picture")
    public RestResponse<Void> setProfilePicture(@Context User user, @Valid @NotNull @FormParam("picture") File picture) {
        if (!pocketIdService.isEnabled())
            return RestResponse.status(RestResponse.Status.SERVICE_UNAVAILABLE);

        var mime = fileService.getFileMime(picture);

        // Allow invalid mimes, that happens sometimes somehow
        if (mime != null && !allowedProfilePictureMimeTypes.contains(mime))
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);

        try {
            log.info("Upload new profile picture for {}", user.getName());
            pocketIdService.setUserAvatar(user, picture);

            log.info("Update user profile");
            updatePocketUsers.downloadProfile(user);

            log.info("Finished changing profile picture");
            return RestResponse.status(RestResponse.Status.RESET_CONTENT);
        } catch (PocketIdException e) {
            log.warn("Failed to change avatar of {}: {}", user.getName(), e.getMessage());
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }
    }
}
