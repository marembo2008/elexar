package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveRegionType;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author marembo
 */
@Entity
public class County extends ElectiveRegion implements Serializable {

    public County() {
        super(ElectiveRegionType.COUNTY);
    }

    public County(String regionName, long registeredVoters) {
        super(regionName, registeredVoters, ElectiveRegionType.COUNTY);
    }
}
