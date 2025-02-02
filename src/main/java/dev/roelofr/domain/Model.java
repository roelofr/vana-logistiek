package dev.roelofr.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
}
