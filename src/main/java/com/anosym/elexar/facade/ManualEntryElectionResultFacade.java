/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ManualEntryElectionResult;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ManualEntryElectionResultFacade extends GeneralElectionResultFacade<ManualEntryElectionResult> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;
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

  public ManualEntryElectionResultFacade() {
  }
}
