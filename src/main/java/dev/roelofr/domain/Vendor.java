package dev.roelofr.domain;

import dev.roelofr.AppUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name = "vendors")
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NamedQueries({
    @NamedQuery(name = "Vendor.getAllSorted", query = "from Vendor v order by v.numberNumeric, v.number")
})
public class Vendor extends Model {
    String name;

    @Column(length = 10)
    String number;

    @Setter(AccessLevel.NONE)
    @Column(name = "number_numeric", columnDefinition = "smallint")
    Integer numberNumeric;

    @ManyToOne
    @JoinColumn(name = "district_id")
    District district;

    @PrePersist
    public void determineNumberNumeric() {
        numberNumeric = AppUtil.parseVendorNumberToInteger(number);
    }
}
