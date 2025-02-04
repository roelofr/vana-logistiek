package dev.roelofr.repository;

import dev.roelofr.domain.Vendor;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class VendorRepository implements PanacheRepository<Vendor> {
    private Multi<Vendor> fromUni(Uni<List<Vendor>> vendorList) {
        return vendorList
            .onItem()
            .transformToMulti((list) -> Multi.createFrom().iterable(list));
    }


    public Multi<Vendor> streamAll() {
        return fromUni(listAll());
    }
}
