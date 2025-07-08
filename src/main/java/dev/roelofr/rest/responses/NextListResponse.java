package dev.roelofr.rest.responses;

import dev.roelofr.domain.Model;

import java.util.List;

public record NextListResponse<T extends Model>(
    List<T> items,
    long itemCount
) {
}
