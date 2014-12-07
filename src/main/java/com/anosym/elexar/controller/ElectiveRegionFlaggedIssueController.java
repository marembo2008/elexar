/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.controller;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.ElectiveRegionFlaggedIssue;
import com.anosym.elexar.facade.ElectiveRegionFlaggedIssueFacade;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author marembo
 */
@Named("electiveRegionFlaggedIssueController")
@SessionScoped
public class ElectiveRegionFlaggedIssueController implements Serializable {

  private class ElectiveRegionFlaggedIssueLazyDataModel extends LazyDataModel<ElectiveRegionFlaggedIssue> {

    public ElectiveRegionFlaggedIssueLazyDataModel() {
      setRowCount(electiveRegionFlaggedIssueFacade.countUnconfirmedFlaggedIssues());
    }

    @Override
    public List<ElectiveRegionFlaggedIssue> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
      return electiveRegionFlaggedIssueFacade.findUnconfirmedFlaggedIssues(first, pageSize);
    }
  }
  @EJB
  private ElectiveRegionFlaggedIssueFacade electiveRegionFlaggedIssueFacade;
  @Inject
  private ElectiveRegionController electiveRegionController;
  private ElectiveRegionFlaggedIssue electiveRegionFlaggedIssue;
  private DataModel<ElectiveRegionFlaggedIssue> electiveRegionFlaggedIssues;

  @PostConstruct
  void onStart() {
    electiveRegionFlaggedIssues = new ElectiveRegionFlaggedIssueLazyDataModel();
  }

  public boolean hasFlaggedIssue(ElectiveRegion electiveRegion) {
    return electiveRegionFlaggedIssueFacade.hasElectiveRegionFlaggedIssue(electiveRegion);
  }

  public DataModel<ElectiveRegionFlaggedIssue> getElectiveRegionFlaggedIssues() {
    return electiveRegionFlaggedIssues;
  }
}
