/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.utilities.Utility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class PoliticalAgentFacade extends AbstractFacade<PoliticalAgent> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PoliticalAgentFacade() {
    super(PoliticalAgent.class);
  }

  public PoliticalAgent findPoliticalAgentForElectiveRegion(ElectiveRegion electiveRegion) {
    try {
      return getEntityManager()
              .createNamedQuery("POLITICALAGENT.FIND_POLITICAL_AGENT_FOR_ELECTIVE_REGION", PoliticalAgent.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .getSingleResult();
    } catch (Exception e) {
      ElexarController.logError(e);
    }
    return null;
  }

  public PoliticalAgent findAgentFromTelephoneNumber(String phoneNumber) {
    if (Utility.isNullOrEmpty(phoneNumber)) {
      return null;
    }
    phoneNumber = phoneNumber.trim();
    PoliticalAgent agent;
    if ((agent = doFindAgentFromTelephoneNumber(phoneNumber)) == null) {
      if (phoneNumber.indexOf("254") == 1) {
        phoneNumber = "+" + phoneNumber;
      } else if (phoneNumber.indexOf('0') == 0) {
        phoneNumber = "+254" + phoneNumber.substring(1);
      } else {
        /**
         * We do not need to search the same number without modification.
         */
        return null;
      }
      if ((agent = doFindAgentFromTelephoneNumber(phoneNumber)) == null) {
        phoneNumber = phoneNumber.substring(1);
        return doFindAgentFromTelephoneNumber(phoneNumber);
      }
    }
    return agent;
  }

  private PoliticalAgent doFindAgentFromTelephoneNumber(String phoneNumber) {
    try {
      return getEntityManager()
              .createNamedQuery("POLITICALAGENT.FIND_POLITICAL_AGENT_FROM_PHONE_NUMBER", PoliticalAgent.class)
              .setParameter("phoneNumber", phoneNumber)
              .getSingleResult();
    } catch (Exception e) {
      ElexarController.logError(e);
      return null;
    }
  }
}
