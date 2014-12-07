/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.Candidate;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.ElectiveSeat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class CandidateFacade extends AbstractFacade<Candidate> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public CandidateFacade() {
    super(Candidate.class);
  }

  public List<Candidate> findCandidatesForElectiveSeats(ElectiveSeat electiveSeat) {
    try {
      return getEntityManager()
              .createNamedQuery("CANDIDATE.FIND_CANDIDATES_FOR_ELECTIVE_SEATS")
              .setParameter("electiveSeat", electiveSeat)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<Candidate>();
    }
  }

  public Candidate findCandidateForCode(String code) {
    try {
      return getEntityManager()
              .createNamedQuery("CANDIDATE.FIND_CANDIDATE_WITH_CODE", Candidate.class)
              .setParameter("code", code)
              .getSingleResult();
    } catch (Exception e) {
      ElexarController.logError(e);
      return null;
    }
  }
}
