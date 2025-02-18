package dev.roelofr.rest.resources;

import dev.roelofr.domain.Vendor;
import dev.roelofr.repository.VendorRepository;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Path("/vendor")
@RequiredArgsConstructor()
@Produces(MediaType.APPLICATION_JSON)
public class VendorResource {
    final VendorRepository vendorRepository;

    @GET
    @Path("/")
    public List<Vendor> listVendor() {
        return vendorRepository.list("order by cast(number as int)");
    }

    @GET
    @Path("/{id}")
    public Vendor getVendor(@PathParam("id") Long id) {
        return Optional.ofNullable(vendorRepository.findById(id))
            .orElseThrow(() -> new NotFoundException("User with id %d not found".formatted(id)));
    }
}
