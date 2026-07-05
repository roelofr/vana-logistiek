package dev.roelofr.validation;

public interface CanSelfValidate {
    /**
     * Performs self-validation logic.
     *
     * @return true if valid, false otherwise
     */
    boolean isValid();
}
