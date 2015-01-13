package com.anosym.elexar.mapperdomain;

import com.anosym.elexar.Constituency;
import com.anosym.elexar.Country;
import com.anosym.elexar.County;
import com.anosym.elexar.CountyWard;
import com.anosym.elexar.ElectiveRegionBoundary;
import com.anosym.elexar.PollingStation;
import com.anosym.elexar.facade.ConstituencyFacade;
import com.anosym.elexar.facade.CountryFacade;
import com.anosym.elexar.facade.CountyFacade;
import com.anosym.elexar.facade.CountyWardFacade;
import com.anosym.elexar.facade.ElectiveRegionBoundaryFacade;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.mapperdomain.facade.MappedConstituencyFacade;
import com.anosym.elexar.mapperdomain.facade.MappedCountyFacade;
import com.anosym.elexar.mapperdomain.facade.MappedPollingFacade;
import com.anosym.elexar.mapperdomain.facade.MappedWardFacade;
import com.anosym.jflemax.geometry.Coordinate;
import com.anosym.jflemax.geometry.Polygon;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.anosym.jflemax.geometry.Polygon.fromPolygonString;

/**
 *
 * @author marembo
 */
@Singleton
@Startup
public class MappedDomainManager {

    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;
    @EJB
    private MappedCountyFacade mappedCountyFacade;
    @EJB
    private MappedConstituencyFacade mappedConstituencyFacade;
    @EJB
    private MappedWardFacade mappedWardFacade;
    @EJB
    private MappedPollingFacade mappedPollingFacade;

    @EJB
    private CountryFacade countryFacade;
    @EJB
    private CountyFacade countyFacade;
    @EJB
    private ConstituencyFacade constituencyFacade;
    @EJB
    private CountyWardFacade countyWardFacade;
    @EJB
    private ElectiveRegionFacade electiveRegionFacade;
    @EJB
    private ElectiveRegionBoundaryFacade electiveRegionBoundaryFacade;

    @PostConstruct
    void mapDomains() {
        mapCounty();
    }

    void mapPollingStations(final CountyWard countyWard, final int wardId) {
        final List<MappedPolling> pollings = em.createNamedQuery("MappedPolling.findWardPollingStations")
                .setParameter("ward_id", wardId).getResultList();
        for (MappedPolling polling : pollings) {
            final PollingStation ps = new PollingStation();
            ps.setParentElectiveRegion(countyWard);
            ps.setRegionName(polling.getPolling_name());
            electiveRegionFacade.create(ps);
        }

    }

    void mapWards(final Constituency constituency, final int constituencyId) {
        final List<MappedWard> mappedWards = em.createNamedQuery("MappedWard.findConstituencyWards")
                .setParameter("constituency_id", constituency).getResultList();
        for (final MappedWard ward : mappedWards) {
            final CountyWard countyWard = new CountyWard();
            countyWard.setParentElectiveRegion(constituency);
            countyWard.setRegionName(ward.getWard_name());
            electiveRegionFacade.create(countyWard);

            //no boundaries
            mapPollingStations(countyWard, ward.getId());
        }
    }

    void mapConstituency(final County county) {
        final String sql = "SELECT id, constituency_name, const_code, county_code, AsText(geometry), longitude, latitude from constituency where county_code = :county_code";
        final List<Object[]> constituencies = em.createNativeQuery(sql).setParameter("county_code", county.getRegionCode()).getResultList();
        for (final Object[] constituencyData : constituencies) {
            final Constituency constituency = new Constituency();
            constituency.setParentElectiveRegion(county);

            final int constituencyId = Integer.parseInt(String.valueOf(constituencyData[0]));
            constituency.setRegionName(String.valueOf(constituencyData[1]));
            constituency.setRegionCode(String.valueOf(constituencyData[2]));
            electiveRegionFacade.create(constituency);

            final ElectiveRegionBoundary countyBoundary = new ElectiveRegionBoundary(constituency);
            final Polygon boundaries = fromPolygonString(String.valueOf(constituencyData[4]));
            final BigDecimal longitude = new BigDecimal(String.valueOf(constituencyData[5]));
            final BigDecimal latitude = new BigDecimal(String.valueOf(constituencyData[6]));
            final Coordinate centreCoordinate = new Coordinate(latitude, longitude);
            countyBoundary.setCentreCoordinate(centreCoordinate);
            countyBoundary.setRegionBoundaries(boundaries);
            addBoundary(countyBoundary);

            mapWards(constituency, constituencyId);

        }

    }

    void mapCounty() {
        //because of the geometry.
        final Country country = countryFacade.find(1l);
        final String sql = "SELECT id, county_name, county_code, AsText(geometry), longitude, latitude from county";
        final List<Object[]> counties = em.createNativeQuery(sql).getResultList();
        for (final Object[] countyData : counties) {
            final County county = new County();
            county.setParentElectiveRegion(country);
            county.setRegionCode(String.valueOf(countyData[2]));
            county.setRegionName(String.valueOf(countyData[1]));
            electiveRegionFacade.create(county);

            final ElectiveRegionBoundary countyBoundary = new ElectiveRegionBoundary(county);
            final Polygon boundaries = fromPolygonString(String.valueOf(countyData[3]));
            final BigDecimal longitude = new BigDecimal(String.valueOf(countyData[4]));
            final BigDecimal latitude = new BigDecimal(String.valueOf(countyData[5]));
            final Coordinate centreCoordinate = new Coordinate(latitude, longitude);
            countyBoundary.setCentreCoordinate(centreCoordinate);
            countyBoundary.setRegionBoundaries(boundaries);
            addBoundary(countyBoundary);

            mapConstituency(county);
        }
    }

    private void addBoundary(final ElectiveRegionBoundary boundary) {
        final String sql = String.format("INSERT INTO ElectiveRegionBoundary (boundaryId, centreCoordinate, regionBoundaries,electiveRegion_regionId)"
                + " VALUES (%s, GeomFromText('%s'), GeomFromText('%s'), %s);",
                                         boundary.getBoundaryId(), boundary.getCentreCoordinate().toPointString(),
                                         boundary.getRegionBoundaries().toPolygonString(), boundary.getElectiveRegion().getRegionId());
        em.createNativeQuery(sql).executeUpdate();

    }
}
