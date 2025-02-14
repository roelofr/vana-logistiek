package dev.roelofr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendors")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Vendor extends Model {
    public String name;

    @Column(length = 10)
    public String number;

    @ManyToOne
    @JoinColumn(name = "district_id")
    public District district;

    public static Vendor create(String number, String name, District district) {
        return new Vendor(number, name, district);
    }
}
