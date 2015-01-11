/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.Candidate;
import com.anosym.elexar.CandidateElectionResult;
import com.anosym.elexar.ElectionResult;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PollingStation;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.elexar.util.ElectiveSeat;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

/**
 *
 * @author marembo
 */
public abstract class GeneralElectionResultFacade<T extends ElectionResult> {

    private Class<T> electionResultClass;

    protected abstract EntityManager getEntityManager();

    public abstract ElectiveRegionFacade getElectiveRegionFacade();

    public abstract CandidateFacade getCandidateFacade();

    public GeneralElectionResultFacade() {
        Type tt = getClass().getGenericSuperclass();
        if (tt instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) tt;
            electionResultClass = (Class<T>) type.getActualTypeArguments()[0];
        }
    }

    public void create(ElectionResult electionResult) {
        getEntityManager().persist(electionResult);
        addElectionResultToParentElectiveRegion(electionResult.getElectiveRegion().getParentElectiveRegion(), electionResult);
    }

    public void doCreate(ElectionResult electionResult) {
        getEntityManager().persist(electionResult);
    }

    public void edit(ElectionResult electionResult) {
        getEntityManager().merge(electionResult);
    }

    public void remove(ElectionResult electionResult) {
        getEntityManager().remove(getEntityManager().merge(electionResult));
    }

    public T find(Object id) {
        return getEntityManager().find(electionResultClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(electionResultClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(electionResultClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(electionResultClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public ElectionResult getElectionResults(ElectiveRegion electiveRegion, ElectiveSeat electiveSeat) {
        try {
            List<ElectionResult> electionResults = getEntityManager()
                    .createNamedQuery("ELECTIONRESULT.FIND_RESULTS_FOR_REGION_FOR_ELECTIVE_SEAT")
                    .setParameter("regionId", electiveRegion.getRegionId())
                    .setParameter("electiveSeat", electiveSeat)
                    .getResultList();
            return electionResults.size() == 1 ? electionResults.get(0) : null;
        } catch (Exception e) {
            ElexarController.logError(e);
            return null;
        }
    }

    public ElectionResult getPresidentialElectionResults(ElectiveRegion electiveRegion) {
        return getElectionResults(electiveRegion, ElectiveSeat.PRESIDENTAIL);
    }

//  public int findTotalVotesGained(ElectiveRegion electiveRegion) {
//    try {
//      Long sum = getEntityManager()
//              .createNamedQuery("ELECTIONRESULT.FIND_TOTAL_VOTES_CAST_FOR_REGION", Long.class)
//              .setParameter("regionId", electiveRegion.getRegionId())
//              .getSingleResult();
//      System.err.println("VotesGained in " + electiveRegion + " = " + sum);
//      return sum != null ? sum.intValue() : 0;
//    } catch (Exception e) {
//      ElexarController.logError(e);
//      return 0;
//    }
//  }
    public void addElectionResultToParentElectiveRegion(ElectiveRegion electiveRegion, ElectionResult e) {
        if (electiveRegion == null) {
            //we must have reached the upper most elective region
            return;
        }
        ElectionResult er = getPresidentialElectionResults(electiveRegion);
        if (er == null) {
            er = new ElectionResult(electiveRegion, e.getElectiveSeat(), e.getVotesDisputed(), e.getVotesRejected());
            for (CandidateElectionResult cer : e.getCandidateElectionResults()) {
                er.addCandidateElectionResults(new CandidateElectionResult(cer.getCandidate(), cer.getVotesGained()));
            }
            doCreate(er);
        } else {
            //add result to every candidate.
            for (CandidateElectionResult cer : e.getCandidateElectionResults()) {
                er.incrementCandidateResult(cer.getCandidate(), cer.getVotesGained());
            }
            er.incrementVotesDisputed(e.getVotesDisputed());
            er.incrementVotesRejected(e.getVotesRejected());
            edit(er);
        }
        addElectionResultToParentElectiveRegion(electiveRegion.getParentElectiveRegion(), e);
    }

    public List<ElectionResult> getHighestVotesGained(ElectiveRegion electiveRegion) {
        try {
            return getEntityManager()
                    .createNamedQuery("ELECTIONRESULT.FIND_ELECTION_RESULT_FOR_REGION")
                    .setParameter("regionId", electiveRegion.getRegionId())
                    .setMaxResults(1)
                    .getResultList();
        } catch (Exception e) {
            ElexarController.logError(e);
            return Collections.EMPTY_LIST;
        }
    }

    public void addTestElectionResultData() {
        try {
            ElectiveRegionFacade electiveRegionFacade = getElectiveRegionFacade();
            System.err.println("electiveRegionFacade: " + electiveRegionFacade);
            List<PollingStation> pollingStations = electiveRegionFacade.findPollingStationWithoutElectionResult();
            System.err.println("Polling Stations without result: " + pollingStations);
            for (ElectiveRegion e : pollingStations) {
                doAddTestElectionResultData(e);
            }
            System.err.println("addTestElectionResultData: returning");
        } catch (Exception e) {
            ElexarController.logError(e);
        }
    }

    public long getReportingPollingStations() {
        try {
            Long count = getEntityManager()
                    .createNamedQuery("ELECTIONRESULT.FIND_REPORTING_POLLING_STATION", Long.class)
                    .setParameter("electiveRegionType", ElectiveRegionType.POLLING_STATION)
                    .getSingleResult();
            return count != null ? count : 0l;
        } catch (Exception e) {
            ElexarController.logError(e);
            return 0l;
        }
    }

    private void doAddTestElectionResultData(ElectiveRegion e) {
        try {
            Random r = new Random(System.currentTimeMillis());
            int max = (int) e.getRegisteredVoters();
            ElectionResult er = getPresidentialElectionResults(e);
            int votes = er != null ? er.totalVotesGained() : 0;
            max -= votes;
            if (max > 0) {
                Map<Candidate, Integer> results = new HashMap<Candidate, Integer>();
                int votesGained;
                List<Candidate> candidates = getCandidateFacade().findCandidatesForElectiveSeats(ElectiveSeat.PRESIDENTAIL);
                Collections.shuffle(candidates);
                for (Candidate cn : candidates) {
                    votesGained = r.nextInt(max) / (r.nextInt(2) + 1);
                    results.put(cn, votesGained);
                    System.err.println(cn + "=votesGained: " + votesGained);
                    max -= votesGained;
                }
                int n = max / (r.nextInt(10) + 1);
                int votesDisputed = n > 0 ? r.nextInt(n) : 0;
                n = Math.min(max - votesDisputed, votesDisputed) / (r.nextInt(20) + 1);
                int votesRejected = n > 0 ? r.nextInt(n) : 0;
                System.err.println("votesDisputed: " + votesDisputed);
                System.err.println("votesRejected: " + votesRejected);
                addProvisionalElectionResult(e, results, votesDisputed, votesRejected);
            }
        } catch (Exception ex) {
            ElexarController.logError(ex);
        }
    }

    public void addProvisionalElectionResult(ElectiveRegion e, Candidate c, int votesGained, int votesDisputed, int votesRejected) {
        try {
            ElectionResult er = getPresidentialElectionResults(e);
            if (er == null) {
                er = new ElectionResult(e, ElectiveSeat.PRESIDENTAIL, votesDisputed, votesRejected);
                er.addCandidateElectionResults(new CandidateElectionResult(c, votesGained));
                create(er);
            } else {
                er.addCandidateElectionResults(new CandidateElectionResult(c, votesGained));
                edit(er);
            }
        } catch (Exception ex) {
            ElexarController.logError(ex);
        }
    }

    public void addProvisionalElectionResult(ElectiveRegion electiveRegion, Map<Candidate, Integer> candidateResults, int votesDisputed,
                                             int votesRejected) {
        try {
            ElectionResult er = getPresidentialElectionResults(electiveRegion);
            if (er == null) {
                er = new ElectionResult(electiveRegion, ElectiveSeat.PRESIDENTAIL, votesDisputed, votesRejected);
                for (Map.Entry<Candidate, Integer> e : candidateResults.entrySet()) {
                    er.addCandidateElectionResults(new CandidateElectionResult(e.getKey(), e.getValue()));
                }
                create(er);
            } else {
                for (Map.Entry<Candidate, Integer> e : candidateResults.entrySet()) {
                    er.addCandidateElectionResults(new CandidateElectionResult(e.getKey(), e.getValue()));
                }
                edit(er);
            }
        } catch (Exception ex) {
            ElexarController.logError(ex);
        }
    }

    public List<ElectionResult> findElectionResultsForElectiveRegionTypes(ElectiveRegionType electiveRegionType) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
            Root<ElectionResult> root = cq.from(ElectionResult.class);
            ParameterExpression<ElectiveRegionType> pe = cb.parameter(ElectiveRegionType.class, "electiveRegionType");
            cq.where(cb.equal(root.get("electiveRegion").get("electiveRegionType"), pe));
            return getEntityManager()
                    .createQuery(cq)
                    .setParameter("electiveRegionType", electiveRegionType)
                    .getResultList();
        } catch (Exception e) {
            ElexarController.logError(e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<ElectionResult> findElectionResultsForElectiveRegionTypes(ElectiveRegionType electiveRegionType, int first, int pageSize) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
            Root<ElectionResult> root = cq.from(ElectionResult.class);
            ParameterExpression<ElectiveRegionType> pe = cb.parameter(ElectiveRegionType.class, "electiveRegionType");
            cq.where(cb.equal(root.get("electiveRegion").get("electiveRegionType"), pe));
            return getEntityManager()
                    .createQuery(cq)
                    .setParameter("electiveRegionType", electiveRegionType)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            ElexarController.logError(e);
            return Collections.EMPTY_LIST;
        }
    }

    public int countElectionResultsForElectiveRegionTypes(ElectiveRegionType electiveRegionType) {
        try {
            String table = electiveRegionType.getEntityName();
            String sql = "SELECT COUNT(*) FROM ElectionResult AS r JOIN " + table + " AS c WHERE r.electiveRegion_regionId = c.regionId";
            Object count = getEntityManager()
                    .createNativeQuery(sql)
                    .getSingleResult();
            return count != null ? ((Long) count).intValue() : 0;
        } catch (Exception e) {
            ElexarController.logError(e);
            return 0;
        }
    }
}
