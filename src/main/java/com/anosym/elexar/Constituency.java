package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveRegionType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author marembo
 */
@Entity
@Table(name = "constituency")
public class Constituency extends ElectiveRegion implements Serializable {

    public Constituency() {
        super(ElectiveRegionType.CONSTITUENCY);
    }

    public Constituency(String regionName, long registeredVoters) {
        super(regionName, registeredVoters, ElectiveRegionType.CONSTITUENCY);
    }
}
