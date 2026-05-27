package dev.roelofr.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

public record Pagination(
    long totalItems,
    int totalPages,
    int currentPage
) {
    public Pagination(PanacheQuery<?> query) {
        this(
            query.count(),
            query.pageCount(),
            query.page().index + 1
        );
    }

    @JsonInclude
    boolean hasNextPage() {
        return currentPage < totalPages;
    }

    @JsonInclude
    boolean hasPreviousPage() {
        return currentPage > 0;
    }
}
