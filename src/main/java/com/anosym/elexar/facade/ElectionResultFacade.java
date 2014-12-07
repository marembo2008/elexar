/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectionResult;
import com.anosym.elexar.ManualEntryElectionResult;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectiveRegionType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectionResultFacade extends GeneralElectionResultFacade<ElectionResult> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;
  @EJB
  private ManualEntryElectionResultFacade manualEntryElectionResultFacade;
  @EJB
  private CandidateFacade candidateFacade;
  @EJB
  private ElectiveRegionFacade electiveRegionFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public CandidateFacade getCandidateFacade() {
    return candidateFacade;
  }

  @Override
  public ElectiveRegionFacade getElectiveRegionFacade() {
    return electiveRegionFacade;
  }

  public ElectionResultFacade() {
  }

  /**
   * @throws ClassCastException if the election result specified is not an instance of
   * {@link ManualEntryElectionResult}
   * @param electionResult
   */
  @Override
  public void create(ElectionResult electionResult) {
    super.create(electionResult);
    //add this to the manual scenario
//    manualEntryElectionResultFacade.create(electionResult);
  }
}
