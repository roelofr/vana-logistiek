package dev.roelofr.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchant")
public class Merchant extends Model {
    public String name;
    public String identifier;
}
