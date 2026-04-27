package dev.roelofr.rest.resources;

import dev.roelofr.config.AppConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

@Path("/version")
@RequiredArgsConstructor
public class VersionResource {
    private final AppConfig appConfig;

    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Map<String, String>> get() {
        return RestResponse.ok(
            Map.ofEntries(
                Map.entry("version", appConfig.version())
            )
        );
    }
}
