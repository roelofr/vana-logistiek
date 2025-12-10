package dev.roelofr.integrations.pocketid;


import dev.roelofr.integrations.pocketid.model.PocketUser;
import dev.roelofr.integrations.pocketid.responses.UsersResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "${app.pocket-id.url}")
@ClientHeaderParam(name = "header-from-properties", value = "${app.pocket-id.api-key")
public interface PocketIdClient {
    @GET
    @Path("/api/users")
    UsersResponse getUsers(@QueryParam("pagination[page]") int page);

    @GET
    @Path("/api/users/{id}")
    PocketUser getUser(@PathParam("id") String id);
}
