/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectionTurnout;
import com.anosym.elexar.ElectionTurnoutReport;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.utilities.Application;
import com.anosym.utilities.IdGenerator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectionTurnoutReportFacade extends AbstractFacade<ElectionTurnoutReport> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ElectionTurnoutReportFacade() {
    super(ElectionTurnoutReport.class);
  }

  public List<ElectionTurnoutReport> getElectionTurnoutReports(ElectionTurnout electionTurnout) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT")
              .setParameter("turnoutId", electionTurnout.getTurnoutId())
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnoutReport>();
    }
  }

  public List<ElectionTurnoutReport> getElectionTurnoutReports(ElectionTurnout electionTurnout, Calendar fromReportDate) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_FROM")
              .setParameter("turnoutId", electionTurnout.getTurnoutId())
              .setParameter("reportDate", fromReportDate)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnoutReport>();
    }
  }

  public long getTotalVotesCast(ElectionTurnout electionTurnout) {
    try {
      List<Long> totalVotes = getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_TOTAL_ELECTION_TURNOUT_REPORT", Long.class)
              .setParameter("turnoutId", electionTurnout.getTurnoutId())
              .getResultList();
      return (totalVotes != null && totalVotes.isEmpty()) ? 0l : totalVotes.get(0);
    } catch (Exception e) {
      ElexarController.logError(e);
      return 0l;
    }
  }

  public List<ElectionTurnoutReport> getElectionTurnoutReportsWithin(Calendar from, Calendar to) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_WITHIN")
              .setParameter("reportDate0", from)
              .setParameter("reportDate1", to)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnoutReport>();
    }
  }

  public List<ElectionTurnoutReport> getElectiveRegionElectionTurnoutReportsWithin(Calendar from, Calendar to, ElectiveRegionType electionRegionType) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_WITHIN_FOR_ELECTIVE_REGION_TYPE")
              .setParameter("reportDate0", from)
              .setParameter("reportDate1", to)
              .setParameter("electionRegionType", electionRegionType)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnoutReport>();
    }
  }

  public List<ElectionTurnoutReport> getElectiveRegionElectionTurnoutReportsWithin(Calendar from, Calendar to, ElectiveRegion electiveRegion) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_WITHIN_FOR_ELECTIVE_REGION")
              .setParameter("reportDate0", from)
              .setParameter("reportDate1", to)
              .setParameter("regionId", electiveRegion.getRegionId())
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionTurnoutReport>();
    }
  }

  public long getTotalVotesCast(ElectionTurnout electionTurnout, Calendar from, Calendar to) {
    try {
      List<Long> totalVotes = getEntityManager()
              .createNamedQuery("ELECTIONTURNOUTREPORT.FIND_TOTAL_ELECTION_TURNOUT_REPORT_WITHIN", Long.class)
              .setParameter("turnoutId", electionTurnout.getTurnoutId())
              .setParameter("reportDate0", from)
              .setParameter("reportDate1", to)
              .getResultList();
      return (totalVotes != null && totalVotes.isEmpty()) ? 0l : totalVotes.get(0);
    } catch (Exception e) {
      ElexarController.logError(e);
      return 0l;
    }
  }
}
