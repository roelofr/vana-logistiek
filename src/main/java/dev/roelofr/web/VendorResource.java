package dev.roelofr.web;

import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.VendorRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/vendor")
@RequiredArgsConstructor()
@Produces(MediaType.APPLICATION_JSON)
public class VendorResource {
    final VendorRepository vendorRepository;

    @GET
    @WithSession
    public Uni<List<Vendor>> listVendor() {
        return vendorRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Vendor> getVendor(@PathParam("id") Long id) {
        return vendorRepository.findById(id)
            .onFailure()
            .invoke(() -> new NotFoundException());
    }
}
