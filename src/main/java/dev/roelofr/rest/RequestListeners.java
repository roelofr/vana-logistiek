package dev.roelofr.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

@Slf4j
@ApplicationScoped
public class RequestListeners {
    @Inject
    @Context
    SecurityContext securityContext;

    @ServerRequestFilter(preMatching = true)
    public void logIncomingRequest(ContainerRequestContext requestContext) {
        var secContext = requestContext.getSecurityContext();
        var reqUri = requestContext.getUriInfo();

        log.info("Recieved [{} {}] by user [{}]", requestContext.getMethod(), reqUri.getPath(), getUsernameFromContext(secContext));
    }

    @ServerResponseFilter
    public void logRequestResult(ContainerResponseContext responseContext) {
        log.info("Resolved response [{}] for user [{}]", responseContext.getStatus(), getUsernameFromContext(securityContext));
    }

    String getUsernameFromContext(SecurityContext context) {
        if (context == null)
            return "anonymous";

        var principal = context.getUserPrincipal();
        if (principal == null)
            return "anonymous";

        if (principal instanceof JsonWebToken jwt) {
            if (jwt.getClaim(Claims.full_name) != null)
                return jwt.getClaim(Claims.full_name).toString();
            if (jwt.getClaim(Claims.email) != null)
                return jwt.getClaim(Claims.email).toString();
        }

        return principal.getName();
    }
}
