package dev.roelofr.integrations.hanko;

import dev.roelofr.integrations.hanko.model.HankoUser;
import dev.roelofr.integrations.hanko.model.SessionValidationResponse;
import io.quarkus.rest.client.reactive.NotBody;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "hanko")
public interface HankoClient {
    @POST
    @Path("/sessions/validate")
    @Produces(MediaType.APPLICATION_JSON)
    SessionValidationResponse validate(String sessionToken);

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    HankoUser getUser(@PathParam("id") String id, @NotBody String token);

}
