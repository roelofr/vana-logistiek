package dev.roelofr.domain.projections;

import io.quarkus.hibernate.orm.panache.common.NestedProjectedClass;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@RegisterForReflection
public record ListThread(
    Long id,
    String subject,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime resolvedAt,
    ListThreadVendor vendor,
    ListThreadNamedModel user,
    ListThreadNamedModel team,
    ListThreadNamedModel assignedUser,
    ListThreadNamedModel assignedTeam
) {
    //

    @Builder
    @NestedProjectedClass
    public record ListThreadVendor(
        Long id,
        String number,
        String name,
        ListThreadDistrict district
    ) {
        //
    }

    @Builder
    @NestedProjectedClass
    public record ListThreadDistrict(
        Long id,
        String name,
        String colour
    ) {
        //
    }

    @Builder
    @NestedProjectedClass
    public record ListThreadNamedModel(
        Long id
    ) {
        //
    }
}
