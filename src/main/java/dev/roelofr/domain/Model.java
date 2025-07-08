package dev.roelofr.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Checks if an object is the same Persistence Entity as this one.
     */
    public boolean is(Model model) {
        // Invalid input?
        if (model == null)
            return false;

        // Not the same class?
        if (!getClass().equals(model.getClass()))
            return false;

        // Not persisted?
        if (getId() == null)
            return false;

        // Not the same ID?
        return model.getId().equals(getId());
    }
}
