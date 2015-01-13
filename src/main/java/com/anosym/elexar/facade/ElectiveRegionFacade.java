/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.Candidate;
import com.anosym.elexar.Country;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.PollingStation;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectiveRegionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectiveRegionFacade extends AbstractFacade<ElectiveRegion> {

    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;
    @EJB
    PoliticalAgentFacade politicalAgentFacade;
    @EJB
    private PollingStationFacade pollingStationFacade;
    @EJB
    private CountyWardFacade countyWardFacade;
    @EJB
    private ConstituencyFacade constituencyFacade;
    @EJB
    private CountryFacade countryFacade;
    @EJB
    private CountyFacade countyFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ElectiveRegionFacade() {
        super(ElectiveRegion.class);
    }

    @Asynchronous
    public void normalizeRegisteredVoters() {
        final ElectiveRegion countryElectiveRegion = findCountryElectiveRegion();
        normalizeRegisteredVoters(countryElectiveRegion);

    }

    @TransactionAttribute(REQUIRES_NEW)
    public void normalizeElectiveRegions(final List<ElectiveRegion> electiveRegions) {
        for (final ElectiveRegion electiveRegion : electiveRegions) {
            final Long totalPopulation = getEntityManager()
                    .createNamedQuery("ElectiveRegion.findTotalPopulationFromChildElectiveRegion", Long.class)
                    .setParameter("regionId", electiveRegion.getRegionId())
                    .getSingleResult();
            final Long totalRegisteredVoters = getEntityManager()
                    .createNamedQuery("ElectiveRegion.findTotalRegisteredVotersFromChildElectiveRegion", Long.class)
                    .setParameter("regionId", electiveRegion.getRegionId())
                    .getSingleResult();
            electiveRegion.setPopulation(totalPopulation);
            electiveRegion.setRegisteredVoters(totalRegisteredVoters);
            edit(electiveRegion);
        }
    }

    private long normalizeRegisteredVoters(ElectiveRegion electiveRegion) {
        if (electiveRegion.getElectiveRegionType() == ElectiveRegionType.COUNTY_WARD) {
            System.err.println("normalizeRegisteredVoters: " + electiveRegion + ": " + electiveRegion.getElectiveRegionType());
            String sql = "SELECT SUM(P.registeredVoters) FROM PollingStation P, CountyWard W WHERE P.parentElectiveRegion_regionId = W.regionId AND W.regionId = " + electiveRegion
                    .getRegionId();
            BigDecimal pollingSumTotal = (BigDecimal) getEntityManager()
                    .createNativeQuery(sql)
                    .getSingleResult();
            if (pollingSumTotal != null) {
                long totalCountyVoters = pollingSumTotal.longValue();
                electiveRegion.setRegisteredVoters(totalCountyVoters);
                edit(electiveRegion);
                return totalCountyVoters;
            }
            return 0l;
        } else {
            long totalVotes = 0l;
            for (ElectiveRegion er : getElectiveRegionsForElectiveRegion(electiveRegion)) {
                totalVotes += normalizeRegisteredVoters(er);
            }
            electiveRegion.setRegisteredVoters(totalVotes);
            edit(electiveRegion);
            return totalVotes;
        }
    }

    public List<ElectiveRegion> getElectiveRegions(ElectiveRegionType electiveRegionType) {
        try {
            List<ElectiveRegion> l = getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.FIND_ELECTIVE_REGION_BY_TYPE", ElectiveRegion.class)
                    .setParameter("electiveRegionType", electiveRegionType)
                    .getResultList();
            return l;
        } catch (Exception e) {
            ElexarController.logError(e);
            return new ArrayList<ElectiveRegion>();
        }
    }

    public int countElectiveRegions(ElectiveRegionType electiveRegionType) {
        try {
            Long count = getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.COUNT_ELECTIVE_REGION_BY_TYPE", Long.class)
                    .setParameter("electiveRegionType", electiveRegionType)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            ElexarController.logError(e);
            return 0;
        }
    }

    public ElectiveRegion findElectiveRegionByName(String name) {
        try {
            return getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.FIND_ELECTIVE_REGION_BY_NAME", ElectiveRegion.class)
                    .setParameter("regionName", name)
                    .getSingleResult();
        } catch (Exception e) {
            ElexarController.logError(e);
            return null;
        }
    }

    public List<ElectiveRegion> getElectiveRegionsForElectiveRegion(ElectiveRegion parentElectiveRegion) {
        try {
            List<ElectiveRegion> electiveRegions = getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.FIND_ELECTIVE_REGIONS_FOR_ELECTIVE_REGION", ElectiveRegion.class)
                    .setParameter("regionId", parentElectiveRegion.getRegionId())
                    .getResultList();
            return electiveRegions;
        } catch (Exception e) {
            ElexarController.logError(e);
            return Collections.EMPTY_LIST;
        }
    }

    public int countElectiveRegionsForElectiveRegion(ElectiveRegion parentElectiveRegion) {
        try {
            Long count = getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.COUNT_ELECTIVE_REGIONS_FOR_ELECTIVE_REGION", Long.class)
                    .setParameter("regionId", parentElectiveRegion.getRegionId())
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            ElexarController.logError(e);
            return 0;
        }
    }

    public List<ElectiveRegion> getElectiveRegionsNotAssignedToAgents() {
        try {
            List<ElectiveRegion> electiveRegions = getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.GET_ELECTIVE_REGIONS_NOT_ASSIGNED_TO_AGENT")
                    .getResultList();
            List<PoliticalAgent> registeredAgents = politicalAgentFacade.findAll();
            for (PoliticalAgent pa : registeredAgents) {
                electiveRegions.remove(pa.getElectiveRegion());
            }
            return electiveRegions;
        } catch (Exception e) {
            ElexarController.logError(e);
            return new ArrayList<ElectiveRegion>();
        }
    }

    public int countElectiveRegionsNotAssignedToAgents() {
        try {
            Long count = getEntityManager()
                    .createNamedQuery("ELECTIVEREGION.COUNT_ELECTIVE_REGIONS_NOT_ASSIGNED_TO_AGENT", Long.class)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            ElexarController.logError(e);
            return 0;
        }
    }

    public List<ElectiveRegion> findElectiveRegion(String query) {
        String queries[] = query.trim().split(" ");
        String sql = "SELECT r FROM PollingStation r"; //we only work with polling station currently.
        String where = "";
        String alreadyResultsAdded = "NOT EXISTS (SELECT ps FROM ElectionResult er JOIN er.electiveRegion ps WHERE ps.regionId = r.regionId) ORDER BY r.regionName";
        for (String q : queries) {
            if (q.trim().isEmpty()) {
                continue;
            }
            if (!where.isEmpty()) {
                where += " AND ";
            }
            where += ("r.regionName LIKE '%" + q + "%'");
        }
        if (!where.isEmpty()) {
            sql += (" WHERE " + where + " AND " + alreadyResultsAdded);
            return getEntityManager()
                    .createQuery(sql)
                    .setFirstResult(0)
                    .setMaxResults(100)
                    .getResultList();
        }
        return new ArrayList<ElectiveRegion>();
    }

    /**
     * Returns elective regions for which this candidate has no result.
     *
     * @param query
     * @param candidate
     * @return
     */
    public List<ElectiveRegion> findElectiveRegion(String query, Candidate candidate) {
        if (query == null) {
            return new ArrayList<ElectiveRegion>();
        }
        if (candidate == null) {
            return findElectiveRegion(query);
        }
        String queries[] = query.trim().split(" ");
        String sql = "SELECT r FROM PollingStation r"; //we only work with polling station currently.
        String where = "";
        String alreadyResultsAdded = "NOT EXISTS (SELECT ps FROM ElectionResult er JOIN er.electiveRegion ps WHERE ps.regionId = r.regionId)";
        String candidateResults = "NOT EXISTS (SELECT ps0 FROM ElectionResult er0 JOIN er0.electiveRegion ps0 JOIN er0.candidate c WHERE ps0.regionId = r.regionId AND c.candidateId = :candidateId) ORDER BY r.regionName";
        for (String q : queries) {
            if (q.trim().isEmpty()) {
                continue;
            }
            if (!where.isEmpty()) {
                where += " AND ";
            }
            where += ("r.regionName LIKE '%" + q + "%'");
        }
        if (!where.isEmpty()) {
            sql += (" WHERE " + where + " AND " + alreadyResultsAdded);
            sql += (" AND " + candidateResults);
            return getEntityManager()
                    .createQuery(sql)
                    .setParameter("candidateId", candidate.getCandidateId())
                    .setFirstResult(0)
                    .setMaxResults(100)
                    .getResultList();
        }
        return new ArrayList<ElectiveRegion>();
    }

    public ElectiveRegion findCountryElectiveRegion() {
        List<Country> c = countryFacade.findAll();
        if (c.isEmpty()) {
            return null;
        }
        return c.get(0);
    }

    public List<ElectiveRegion> findCountyWardElectiveRegions() {
        return new ArrayList<ElectiveRegion>(countyWardFacade.findAll());
    }

    public List<ElectiveRegion> findCountryElectiveRegions() {
        return new ArrayList<ElectiveRegion>(countryFacade.findAll());
    }

    public List<ElectiveRegion> findCountyElectiveRegions() {
        return new ArrayList<ElectiveRegion>(countyFacade.findAll());
    }

    public List<ElectiveRegion> findConstituencyElectiveRegions() {
        return new ArrayList<ElectiveRegion>(constituencyFacade.findAll());
    }

    public List<ElectiveRegion> findCountyWardElectiveRegions(int first, int size) {
        return new ArrayList<ElectiveRegion>(countyWardFacade.findRange(new int[]{first, first + size}));
    }

    public List<ElectiveRegion> findPollingStationElectiveRegions() {
        return new ArrayList<ElectiveRegion>(pollingStationFacade.findAll());
    }

    public List<ElectiveRegion> findPollingStationElectiveRegions(int first, int size) {
        return new ArrayList<ElectiveRegion>(pollingStationFacade.findRange(new int[]{first, size + first}));
    }

    public List<PollingStation> findPollingStationWithoutElectionResult() {
        try {
            String sql = "SELECT * FROM PollingStation P WHERE NOT EXISTS (SELECT R.electiveRegion_regionId FROM ElectionResult R WHERE R.electiveRegion_regionId = P.regionId) LIMIT 100";
            List<PollingStation> pollingStations = getEntityManager()
                    .createNativeQuery(sql, PollingStation.class)
                    .getResultList();
            return pollingStations;
        } catch (Exception e) {
            ElexarController.logError(e);
            return Collections.EMPTY_LIST;
        }
    }
}
