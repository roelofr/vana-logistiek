package dev.roelofr.events;

import dev.roelofr.domain.Model;
import org.jspecify.annotations.NonNull;

public class ModelUpdatedEvent<T extends Model> extends ModelEvent<T> {
    public ModelUpdatedEvent(@NonNull T model) {
        super(model);
    }
    //
}
