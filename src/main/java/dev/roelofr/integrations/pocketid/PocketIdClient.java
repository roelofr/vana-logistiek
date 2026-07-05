package dev.roelofr.integrations.pocketid;


import dev.roelofr.integrations.pocketid.model.PocketUser;
import dev.roelofr.integrations.pocketid.responses.UsersResponse;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.io.File;

@RegisterRestClient(configKey = "pocket-id")
@ClientHeaderParam(name = "X-Api-Key", value = "${app.services.pocket-id.api-key}")
public interface PocketIdClient {
    @GET
    @Path("/api/users")
    UsersResponse getUsers(@QueryParam("pagination[page]") int page);

    @GET
    @Path("/api/users/{id}")
    PocketUser getUser(@PathParam("id") String id);

    @GET
    @Path("/api/users/{id}/profile-picture.png")
    File getUserProfilePicture(@PathParam("id") String id);

    @PUT
    @Path("/api/users/{id}/profile-picture")
    void setUserProfilePicture(@PathParam("id") String id, @FormParam("file") File profilePicture);

    @DELETE
    @Path("/api/users/{id}/profile-picture")
    void deleteUserProfilePicture(@PathParam("id") String id);
}
