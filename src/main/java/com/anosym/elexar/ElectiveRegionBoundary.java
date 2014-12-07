package com.anosym.elexar;

import com.anosym.elexar.jpaconverter.CoordinateConverter;
import com.anosym.elexar.jpaconverter.PolygonConverter;
import com.anosym.jflemax.geometry.Coordinate;
import com.anosym.jflemax.geometry.Polygon;
import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ELECTIVEREGIONBOUNDARY.FIND_ELECTIVE_REGION_BOUNDARY",
                query = "SELECT b FROM ElectiveRegionBoundary b WHERE b.electiveRegion.regionId = :regionId"),
    @NamedQuery(name = "ELECTIVEREGIONBOUNDARY.FIND_ELECTIVE_REGION_BOUNDARIES",
                query = "SELECT b FROM ElectiveRegionBoundary b WHERE b.electiveRegion.electiveRegionType = :electiveRegionType")
})
@Converters({
    @Converter(name = "coordinateConverter", converterClass = CoordinateConverter.class),
    @Converter(name = "polygonConverter", converterClass = PolygonConverter.class)
})
public class ElectiveRegionBoundary implements Serializable {

    private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectiveRegionBoundary.class);
    @Id
    private Long boundaryId = IdGenerator.generateId();
    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private ElectiveRegion electiveRegion;
    @Column(columnDefinition = "GEOMETRY")
    @Convert("coordinateConverter")
    private Coordinate centreCoordinate;
    @Column(columnDefinition = "GEOMETRY")
    @Convert("polygonConverter")
    private Polygon regionBoundaries;

    public ElectiveRegionBoundary() {
    }

    public ElectiveRegionBoundary(ElectiveRegion electiveRegion) {
        this.electiveRegion = electiveRegion;
    }

    public ElectiveRegionBoundary(ElectiveRegion electiveRegion, Coordinate centreCoordinate, Polygon regionBoundaries) {
        this.electiveRegion = electiveRegion;
        this.centreCoordinate = centreCoordinate;
        this.regionBoundaries = regionBoundaries;
    }

    public Long getBoundaryId() {
        return boundaryId;
    }

    public void setBoundaryId(Long boundaryId) {
        this.boundaryId = boundaryId;
    }

    public ElectiveRegion getElectiveRegion() {
        return electiveRegion;
    }

    public void setElectiveRegion(ElectiveRegion electiveRegion) {
        this.electiveRegion = electiveRegion;
    }

    public Coordinate getCentreCoordinate() {
        return centreCoordinate;
    }

    public void setCentreCoordinate(Coordinate centreCoordinate) {
        this.centreCoordinate = centreCoordinate;
    }

    public Polygon getRegionBoundaries() {
        return regionBoundaries;
    }

    public void setRegionBoundaries(Polygon regionBoundaries) {
        this.regionBoundaries = regionBoundaries;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.boundaryId != null ? this.boundaryId.hashCode() : 0);
        hash = 41 * hash + (this.electiveRegion != null ? this.electiveRegion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ElectiveRegionBoundary other = (ElectiveRegionBoundary) obj;
        if (this.boundaryId != other.boundaryId && (this.boundaryId == null || !this.boundaryId.equals(other.boundaryId))) {
            return false;
        }
        if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
            return false;
        }
        return true;
    }
}
