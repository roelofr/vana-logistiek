package dev.roelofr.integrations.pocketid.responses;

import dev.roelofr.integrations.pocketid.model.PocketUser;

import java.util.List;

public record UsersResponse(
    List<PocketUser> data,
    Pagination pagination
) {
    public record Pagination(
        int totalPages,
        int totalItems,
        int currentPage,
        int itemsPerPage
    ) {
        public boolean hasNext() {
            return this.totalPages > this.currentPage;
        }
    }
}
