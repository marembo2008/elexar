/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectionMalpractice;
import com.anosym.elexar.ElectionMalpracticeData;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectionMalpracticeType;
import com.anosym.elexar.util.ElectiveRegionType;
import java.util.ArrayList;
import java.util.List;
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
public class ElectionMalpracticeFacade extends AbstractFacade<ElectionMalpractice> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;
  @EJB
  private ElectionMalpracticeDataFacade electionMalpracticeDataFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ElectionMalpracticeFacade() {
    super(ElectionMalpractice.class);
  }

  public boolean isMalpracticeAdded(ElectionMalpracticeType malpracticeType, PoliticalAgent agent) {
    try {
      Long count = getEntityManager()
              .createNamedQuery("ELECTIONMALPRACTICE.COUNT_ELECTION_MALPRACTICE_BY_TYPE_AND_AGENT", Long.class)
              .setParameter("malpracticeType", malpracticeType)
              .setParameter("agentId", agent.getAgentId())
              .getSingleResult();
      return count != null ? count > 0 : false;
    } catch (Exception e) {
      ElexarController.logError(e);
      return false;
    }
  }

  public boolean isMalpracticeAdded(ElectiveRegion electiveRegion) {
    try {
      Long count = getEntityManager()
              .createNamedQuery("ELECTIONMALPRACTICE.COUNT_ELECTION_MALPRACTICE_BY_ELECTIVE_REGION", Long.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .getSingleResult();
      return count != null ? count > 0 : false;
    } catch (Exception e) {
      ElexarController.logError(e);
      return false;
    }
  }

  public boolean hasMalpractice(ElectiveRegion electiveRegion) {
    if (electiveRegion.getElectiveRegionType() == ElectiveRegionType.POLLING_STATION) {
      return isMalpracticeAdded(electiveRegion);
    }
    return electionMalpracticeDataFacade.hasElectionMalpractice(electiveRegion);
  }

  public List<ElectionMalpractice> getElectionMalpractices(ElectiveRegion electiveRegion) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIONMALPRACTICE.GET_ELECTION_MALPRACTICE_BY_ELECTIVE_REGION")
              .setParameter("regionId", electiveRegion.getRegionId())
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectionMalpractice>();
    }
  }

  @Override
  public void create(ElectionMalpractice entity) {
    super.create(entity);
    determineElectionMalpractices(entity);
  }

  public void determineElectionMalpractices(final ElectionMalpractice em) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        determineElectionMalpractice(em);
      }
    }).start();
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  private void determineElectionMalpractice(ElectionMalpractice em) {
    ElectiveRegion pollingStation = em.getPoliticalAgent().getElectiveRegion();
    ElectiveRegion electiveRegion = pollingStation.getParentElectiveRegion();
    while (electiveRegion != null) {
      if (!electionMalpracticeDataFacade.hasElectionMalpractice(electiveRegion)) {
        ElectionMalpracticeData emd = new ElectionMalpracticeData(electiveRegion, em);
        electionMalpracticeDataFacade.create(emd);
        electiveRegion = electiveRegion.getParentElectiveRegion();
      }
    }
  }
}
