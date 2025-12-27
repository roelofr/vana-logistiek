package dev.roelofr.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

@Slf4j
@ApplicationScoped
public class RequestListeners {
    @ServerRequestFilter
    public void logIncomingRequest(ContainerRequestContext requestContext) {
        var secContext = requestContext.getSecurityContext();
        var reqUri = requestContext.getUriInfo();

        log.info("Recieved [{} {}] by user [{}]", requestContext.getMethod(), reqUri.getPath(), getUsernameFromContext(secContext));
    }

    @ServerResponseFilter
    public void logRequestResult(ContainerResponseContext responseContext) {
        log.info("Resolved response [{}] for user [{}]", responseContext.getStatus(), null);
    }

    String getUsernameFromContext(SecurityContext context) {
        if (context == null)
            return "ANON";

        var principal = context.getUserPrincipal();
        if (principal == null)
            return "ANON";

        return principal.getName();
    }
}
