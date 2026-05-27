package dev.roelofr.integrations.pocketid.responses;

import dev.roelofr.integrations.pocketid.model.PocketUser;

import java.util.List;

public record UsersResponse(
    List<PocketUser> data,
    PocketIdPagination pagination
) {
}
