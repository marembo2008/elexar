/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.ElectiveRegionFlaggedIssue;
import com.anosym.elexar.FlaggedIssue;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.elexar.util.FlaggedIssueType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectiveRegionFlaggedIssueFacade extends AbstractFacade<ElectiveRegionFlaggedIssue> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ElectiveRegionFlaggedIssueFacade() {
    super(ElectiveRegionFlaggedIssue.class);
  }

  public ElectiveRegionFlaggedIssue getElectiveRegionFlaggedIssue(ElectiveRegion electiveRegion) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIVEREGIONFLAGGEDISSUE.FIND_ELECTIVE_REGION_FLAGGED_ISSUE", ElectiveRegionFlaggedIssue.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .setParameter("issueConfirmed", false)
              .getSingleResult();
    } catch (Exception e) {
      ElexarController.logError(e);
      return null;
    }
  }

  public boolean hasElectiveRegionFlaggedIssue(ElectiveRegion electiveRegion) {
    try {
      Long count = getEntityManager()
              .createNamedQuery("ELECTIVEREGIONFLAGGEDISSUE.COUNT_ELECTIVE_REGION_FLAGGED_ISSUE", Long.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .setParameter("issueConfirmed", false)
              .getSingleResult();
      return count != null ? count > 0 : false;
    } catch (Exception e) {
      ElexarController.logError(e);
      return false;
    }
  }

  public int countUnconfirmedFlaggedIssues() {
    try {
      Long count = getEntityManager()
              .createNamedQuery("ELECTIVEREGIONFLAGGEDISSUE.COUNT_UNCONFIRMED_ELECTIVE_REGION_FLAGGED_ISSUE", Long.class)
              .setParameter("issueConfirmed", false)
              .getSingleResult();
      return count != null ? count.intValue() : 0;
    } catch (Exception e) {
      ElexarController.logError(e);
      return 0;
    }
  }

  public List<ElectiveRegionFlaggedIssue> findUnconfirmedFlaggedIssues(int first, int pageSize) {
    try {
      return getEntityManager()
              .createNamedQuery("ELECTIVEREGIONFLAGGEDISSUE.FIND_UNCONFIRMED_ELECTIVE_REGION_FLAGGED_ISSUE")
              .setParameter("issueConfirmed", false)
              .setFirstResult(first)
              .setMaxResults(pageSize)
              .getResultList();
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectiveRegionFlaggedIssue>();
    }
  }

  public boolean hasFlaggedIssue(ElectiveRegion electiveRegion, FlaggedIssueType flaggedIssueType) {
    try {
      Long count = getEntityManager()
              .createNamedQuery("ELECTIVEREGIONFLAGGEDISSUE.COUNT_ELECTIVE_REGION_FLAGGED_ISSUE_BY_TYPE", Long.class)
              .setParameter("regionId", electiveRegion.getRegionId())
              .setParameter("flaggedIssueType", flaggedIssueType)
              .getSingleResult();
      return count != null ? count > 0 : false;
    } catch (Exception e) {
      ElexarController.logError(e);
      return false;
    }
  }

  public void addFlaggedIssue(ElectiveRegion electiveRegion, FlaggedIssue flaggedIssue) {
    if (electiveRegion == null) {
      /**
       * We must have reached the end of the top most flagged issue.
       */
      return;
    }
    ElectiveRegionFlaggedIssue issue = getElectiveRegionFlaggedIssue(electiveRegion);
    if (issue != null) {
      issue.getFlaggedIssues().add(flaggedIssue);
      edit(issue);
    } else {
      issue = new ElectiveRegionFlaggedIssue(electiveRegion);
      issue.getFlaggedIssues().add(flaggedIssue);
      create(issue);
    }
    addFlaggedIssue(electiveRegion.getParentElectiveRegion(), flaggedIssue);
  }
}
