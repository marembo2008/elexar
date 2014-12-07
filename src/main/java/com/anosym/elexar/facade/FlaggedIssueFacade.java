/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.FlaggedIssue;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class FlaggedIssueFacade extends AbstractFacade<FlaggedIssue> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public FlaggedIssueFacade() {
    super(FlaggedIssue.class);
  }

  public boolean hasFlaggedIssue(ElectiveRegion electiveRegion) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
