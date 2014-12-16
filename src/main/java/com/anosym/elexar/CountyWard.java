package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveRegionType;
import javax.persistence.Entity;

/**
 *
 * @author marembo
 */
@Entity
public class CountyWard extends ElectiveRegion {

    public CountyWard() {
        super(ElectiveRegionType.COUNTY_WARD);
    }

    public CountyWard(String regionName, long registeredVoters) {
        super(regionName, registeredVoters, ElectiveRegionType.COUNTY_WARD);
    }
}
