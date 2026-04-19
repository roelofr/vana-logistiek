package dev.roelofr.events;

import dev.roelofr.domain.Model;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
abstract class ModelEvent<T extends Model> {
    @Nonnull
    protected final T model;

    @Nonnull
    public <TAs extends Model> TAs getModel(Class<TAs> target) {
        if (!target.isInstance(model))
            throw new RuntimeException("Model");

        return target.cast(model);
    }

    public boolean isInstance(Class<? extends Model> clazz) {
        return clazz.isInstance(model);
    }
}
