package dev.roelofr.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchants")
public class Merchant extends Model {
    public String name;

    public String number;

    @ManyToOne
    @JoinColumn(name = "district_id")
    public District district;
}
