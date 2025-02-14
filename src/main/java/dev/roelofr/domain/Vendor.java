package dev.roelofr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "vendors")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Vendor extends Model {
    String name;

    @Column(length = 10)
    String number;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;

    public static Vendor create(String number, String name, District district) {
        return new Vendor(number, name, district);
    }
}
