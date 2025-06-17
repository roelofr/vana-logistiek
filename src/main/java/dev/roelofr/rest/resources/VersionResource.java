package dev.roelofr.rest.resources;

import dev.roelofr.config.AppConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.NoCache;

@Path("/version")
@RequiredArgsConstructor
public class VersionResource {
    private final AppConfig appConfig;

    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public VersionResponse get() {
        return new VersionResponse(appConfig.version());
    }

    public record VersionResponse(String version) {
    }
}
