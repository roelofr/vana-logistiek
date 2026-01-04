package dev.roelofr.domain.projections;

import io.quarkus.hibernate.orm.panache.common.NestedProjectedClass;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@RegisterForReflection
public record ListThread(Long id, String subject, LocalDateTime createdAt, LocalDateTime updatedAt,
                         LocalDateTime resolvedAt, ListThreadVendor vendor) {
    @Builder
    @AllArgsConstructor
    @NestedProjectedClass
    public record ListThreadVendor(Long id, String number, String name, ListThreadDistrict district) {
    }

    @Builder
    @AllArgsConstructor
    @NestedProjectedClass
    public record ListThreadDistrict(Long id, String name, String colour) {
    }
}
