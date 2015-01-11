/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author marembo
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
    @NamedQuery(name = "ELECTIVEREGION.FIND_ELECTIVE_REGION_BY_TYPE",
                query = "SELECT e FROM ElectiveRegion e WHERE e.electiveRegionType = :electiveRegionType ORDER BY e.regionName"),
    @NamedQuery(name = "ELECTIVEREGION.COUNT_ELECTIVE_REGION_BY_TYPE",
                query = "SELECT COUNT(e) FROM ElectiveRegion e WHERE e.electiveRegionType = :electiveRegionType"),
    @NamedQuery(name = "ELECTIVEREGION.FIND_ELECTIVE_REGION_BY_NAME",
                query = "SELECT e FROM ElectiveRegion e WHERE LOWER(e.regionName) = LOWER(:regionName)  ORDER BY e.regionName"),
    @NamedQuery(name = "ELECTIVEREGION.FIND_ELECTIVE_REGIONS_FOR_ELECTIVE_REGION",
                query = "SELECT DISTINCT e FROM ElectiveRegion e JOIN e.parentElectiveRegion p WHERE p.regionId = :regionId  ORDER BY e.regionName"),
    @NamedQuery(name = "ELECTIVEREGION.FIND_TOTAL_REGISTERED_VOTERS_FOR_ELECTIVE_REGION_FROM_ELECTIVE_REGIONS",
                query = "SELECT SUM(e.registeredVoters) FROM ElectiveRegion e JOIN e.parentElectiveRegion p WHERE p.regionId = :regionId"),
    @NamedQuery(name = "ELECTIVEREGION.COUNT_ELECTIVE_REGIONS_FOR_ELECTIVE_REGION",
                query = "SELECT COUNT(e) FROM ElectiveRegion e JOIN e.parentElectiveRegion p WHERE p.regionId = :regionId"),
    @NamedQuery(name = "ELECTIVEREGION.GET_ELECTIVE_REGIONS_NOT_ASSIGNED_TO_AGENT",
                query = "SELECT e FROM ElectiveRegion e WHERE NOT EXISTS (SELECT r FROM PoliticalAgent r WHERE r.electiveRegion.regionId = e.regionId)  ORDER BY e.regionName"),
    @NamedQuery(name = "ELECTIVEREGION.COUNT_ELECTIVE_REGIONS_NOT_ASSIGNED_TO_AGENT",
                query = "SELECT COUNT(e) FROM ElectiveRegion e WHERE NOT EXISTS (SELECT a FROM PoliticalAgent a WHERE a.electiveRegion.regionId = e.regionId)  ORDER BY e.regionName")
})
public class ElectiveRegion implements Serializable {

    private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectiveRegion.class);

    public static ElectiveRegion newInstance(ElectiveRegionType electiveRegionType) {
        switch (electiveRegionType) {
            case CONSTITUENCY:
                return new Constituency();
            case COUNTRY:
                return new Country();
            case COUNTY:
                return new County();
            case COUNTY_WARD:
                return new CountyWard();
            case POLLING_STATION:
                return new PollingStation();
            default:
                return new ElectiveRegion(electiveRegionType);
        }
    }
    @Id
    private Long regionId = IdGenerator.generateId();
    @Column(nullable = false)
    private String regionName;
    private long registeredVoters;
    /**
     * Total population of this region.
     */
    private long population;
    @OneToOne
    private ElectiveRegion parentElectiveRegion;
    private ElectiveRegionType electiveRegionType;
    private String regionCode;

    protected ElectiveRegion() {
    }

    public ElectiveRegion(ElectiveRegionType electiveRegionType) {
        this.electiveRegionType = electiveRegionType;
    }

    public ElectiveRegion(String regionName, long registeredVoters, ElectiveRegionType electiveRegionType) {
        this.regionName = regionName;
        this.registeredVoters = registeredVoters;
        this.electiveRegionType = electiveRegionType;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setParentElectiveRegion(ElectiveRegion parentElectiveRegion) {
        this.parentElectiveRegion = parentElectiveRegion;
    }

    public ElectiveRegion getParentElectiveRegion() {
        return parentElectiveRegion;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getRegionDescription() {
        return regionName + "(<span style=\"font-weight: bold; color: #ff4556\">" + electiveRegionType + "</span>)";
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public long getPopulation() {
        return population;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public void setElectiveRegionType(ElectiveRegionType electiveRegionType) {
        this.electiveRegionType = electiveRegionType;
    }

    public ElectiveRegionType getElectiveRegionType() {
        return electiveRegionType;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public long getRegisteredVoters() {
        return registeredVoters;
    }

    public void setRegisteredVoters(long registeredVoters) {
        this.registeredVoters = registeredVoters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionId, regionCode, regionName, electiveRegionType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ElectiveRegion)) {
            return false;
        }

        final ElectiveRegion er = (ElectiveRegion) obj;
        return Objects.equals(regionId, er.regionId)
                && Objects.equals(regionCode, er.regionCode)
                && Objects.equals(regionName, er.regionName)
                && Objects.equals(electiveRegionType, er.electiveRegionType);
    }

    @Override
    public String toString() {
        return regionName;
    }
}
