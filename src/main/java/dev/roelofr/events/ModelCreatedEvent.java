package dev.roelofr.events;

import dev.roelofr.domain.Model;
import org.jspecify.annotations.NonNull;

public class ModelCreatedEvent<T extends Model> extends ModelEvent<T> {
    public ModelCreatedEvent(@NonNull T model) {
        super(model);
    }
    //
}
