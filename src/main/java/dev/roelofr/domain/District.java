package dev.roelofr.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "districts")
public class District extends Model {
    public String name;

    public String color;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    public List<User> users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    public List<Merchant> merchants;
}
