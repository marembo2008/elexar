/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectionMalpracticeData;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectionMalpracticeType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectionMalpracticeDataFacade extends AbstractFacade<ElectionMalpracticeData> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ElectionMalpracticeDataFacade() {
    super(ElectionMalpracticeData.class);
  }

  public boolean hasElectionMalpractice(ElectiveRegion electiveRegion, ElectionMalpracticeType electionMalpracticeType) {
    try {
      Long c = getEntityManager()
              .createNamedQuery("ELECTIONMALPRACTICEDATA.COUNT_ELECTION_MALPRACTICE_DATA_BY_REGION_AND_TYPE", Long.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .setParameter("malpracticeType", electionMalpracticeType)
              .getSingleResult();
      return c != null && c > 0;
    } catch (Exception e) {
      ElexarController.logError(e);
      return false;
    }
  }

  public boolean hasElectionMalpractice(ElectiveRegion electiveRegion) {
    try {
      Long c = getEntityManager()
              .createNamedQuery("ELECTIONMALPRACTICEDATA.COUNT_ELECTION_MALPRACTICE_DATA_BY_REGION", Long.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .getSingleResult();
      return c != null && c > 0;
    } catch (Exception e) {
      ElexarController.logError(e);
      return false;
    }
  }

  public ElectionMalpracticeData findElectionMalpractice(ElectiveRegion electiveRegion) {
    try {
      List<ElectionMalpracticeData> electionMalpracticeDatas = getEntityManager()
              .createNamedQuery("ELECTIONMALPRACTICEDATA.FIND_ELECTION_MALPRACTICE_DATA_BY_REGION", ElectionMalpracticeData.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .getResultList();
      return electionMalpracticeDatas != null && !electionMalpracticeDatas.isEmpty() ? electionMalpracticeDatas.get(0) : null;
    } catch (Exception e) {
      ElexarController.logError(e);
      return null;
    }
  }
}
