package dev.roelofr.integrations.pocketid.responses;

public record PocketIdPagination(
    Integer currentPage,
    Integer itemsPerPage,
    Integer totalItems,
    Integer totalPages
) {
    public boolean hasNextPage() {
        return currentPage < totalPages;
    }
}
