/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectionTurnout;
import com.anosym.elexar.ElectionTurnoutReport;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.PollingStation;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectiveRegionType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectionTurnoutFacade extends AbstractFacade<ElectionTurnout> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;
  @EJB
  private PollingStationFacade pollingStationFacade;
  @EJB
  private ElectiveRegionFacade electiveRegionFacade;
  @EJB
  private ElectionTurnoutReportFacade electionTurnoutReportFacade;
  @EJB
  private PoliticalAgentFacade politicalAgentFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ElectionTurnoutFacade() {
    super(ElectionTurnout.class);
  }

  public ElectionTurnout getElectionTurnout(PoliticalAgent politicalAgent) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUT.FIND_ELECTION_TURNOUT", ElectionTurnout.class)
              .setParameter("regionId", politicalAgent.getElectiveRegion().getRegionId())
              .setParameter("agentId", politicalAgent.getAgentId())
              .getSingleResult();
    } catch (Exception e) {
      ElexarController.logError(e);
      return null;
    }
  }

  public ElectionTurnout getElectionTurnout(ElectiveRegion electiveRegion) {
    try {
      List<ElectionTurnout> electionTurnouts = getEntityManager()
              .createNamedQuery("ELECTIONTURNOUT.GET_ELECTION_TURNOUT_FOR_REGION", ElectionTurnout.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .getResultList();
      return electionTurnouts.isEmpty() ? null : electionTurnouts.get(0);
    } catch (Exception e) {
      ElexarController.logError(e);
      return null;
    }
  }

  public void addElectionTurnoutToParentElectiveRegion(ElectiveRegion electiveRegion, long votesCast) {
    ElectiveRegion parentElectiveRegion = electiveRegion.getParentElectiveRegion();
    if (parentElectiveRegion == null) {
      //we must have reached the upper most elective region
      return;
    }
    ElectionTurnout electionTurnout = getElectionTurnout(parentElectiveRegion);
    if (electionTurnout == null) {
      electionTurnout = new ElectionTurnout(parentElectiveRegion, null);
    }
    ElectionTurnoutReport report = new ElectionTurnoutReport(votesCast, electionTurnout);
    electionTurnoutReportFacade.create(report);
    //add to the parent of this parent too
    addElectionTurnoutToParentElectiveRegion(parentElectiveRegion, votesCast);
  }

  @Asynchronous
  public void addTestTurnoutData() {
    try {
      int[] range = {0, 100};
      int count = pollingStationFacade.count();
      while (range[0] < count) {
        for (Iterator<PollingStation> it = pollingStationFacade.findRange(range).iterator(); it.hasNext();) {
          ElectiveRegion e = it.next();
          doAddTestTurnoutData(e);
        }
        range[0] = range[1];
        range[1] += 100;
        System.err.println("addTestTurnoutData-count: " + count);
        System.err.println("addTestTurnoutData-first: " + range[0]);
        System.err.println("addTestTurnoutData-page: " + range[1]);
      }
    } catch (Exception e) {
      ElexarController.logError(e);
    }
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void doAddTestTurnoutData(ElectiveRegion e) {
    ElectionTurnout et = getElectionTurnout(e);
    Random r = new Random(System.currentTimeMillis());
    long votesCast = 0l;
    if (et == null) {
      int max = (int) (e.getRegisteredVoters() / (r.nextInt(4) + 1));
      System.out.println("addTestTurnoutData: " + max);
      votesCast = r.nextInt(max + 1);
      System.out.println("addTestTurnoutData(votesCast): " + votesCast);
      et = new ElectionTurnout(e, null);
      ElectionTurnoutReport etr = new ElectionTurnoutReport(votesCast, et);
      electionTurnoutReportFacade.create(etr);
    } else {
      int max = (int) (e.getRegisteredVoters() - electionTurnoutReportFacade.getTotalVotesCast(et)) / (r.nextInt(4) + 1);
      if ((max * 100) / (e.getRegisteredVoters() + 1) > 85) {
        return;
      }
      if (max > 0) {
        System.out.println("addTestTurnoutData: " + max);
        votesCast = r.nextInt(max);
        System.out.println("addTestTurnoutData(votesCast): " + votesCast);
        ElectionTurnoutReport etr = new ElectionTurnoutReport(votesCast, et);
        electionTurnoutReportFacade.create(etr);
      }
    }
    if (votesCast > 0) {
      addElectionTurnoutToParentElectiveRegion(e, votesCast);
    }
  }

  public void addElectionTurnoutData(PoliticalAgent agent, int votesCast) {
    ElectionTurnout et = getElectionTurnout(agent.getElectiveRegion());
    System.out.println("addElectionTurnoutData: " + votesCast);
    if (et == null) {
      et = new ElectionTurnout(agent.getElectiveRegion(), agent);
      ElectionTurnoutReport etr = new ElectionTurnoutReport(votesCast, et);
      electionTurnoutReportFacade.create(etr);
    } else {
      ElectionTurnoutReport etr = new ElectionTurnoutReport(votesCast, et);
      electionTurnoutReportFacade.create(etr);
    }
    if (votesCast > 0) {
      /**
       * We cannot add the political agent to the upper elective regions since the turnout would be
       * sent from different stations.
       */
      addElectionTurnoutToParentElectiveRegion(agent.getElectiveRegion(), votesCast);
    }
  }

  public List<ElectionTurnout> findElectionTurnoutForElectiveRegionTypes(ElectiveRegionType electiveRegionType) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUT.FIND_ELECTION_TURNOUT_BY_ELECTIVE_REGION_TYPE")
              .setParameter("electiveRegionType", electiveRegionType)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnout>();
    }
  }

  public List<ElectionTurnout> findElectionTurnoutForElectiveRegionTypes(ElectiveRegionType electiveRegionType, int first, int pageSize) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUT.FIND_ELECTION_TURNOUT_BY_ELECTIVE_REGION_TYPE")
              .setParameter("electiveRegionType", electiveRegionType)
              .setFirstResult(first)
              .setMaxResults(pageSize)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnout>();
    }
  }
}
