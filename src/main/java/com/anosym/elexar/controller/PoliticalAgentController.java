/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.controller;

import com.anosym.elexar.Agent;
import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PoliticalAgent;
import com.anosym.elexar.facade.ElectiveRegionFacade;
import com.anosym.elexar.facade.PoliticalAgentFacade;
import com.anosym.elexar.facade.PollingStationFacade;
import com.anosym.elexar.util.AgentType;
import com.anosym.utilities.Utility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.inject.Named;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author marembo
 */
@Named(value = "politicalAgentController")
@SessionScoped
public class PoliticalAgentController implements Serializable {

  private class PoliticalAgentLazyDataModel extends LazyDataModel<PoliticalAgent> {

    public PoliticalAgentLazyDataModel() {
      setRowCount(politicalAgentFacade.count());
      setPageSize(50);
    }

    @Override
    public List<PoliticalAgent> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
      if (getRowCount() != politicalAgentFacade.count()) {
        setRowCount(politicalAgentFacade.count());
      }
      return politicalAgentFacade.findRange(new int[]{first, first + pageSize});
    }
  }
  private static final Map<String, Integer> CONFIGURATION_TABS = new HashMap<String, Integer>();

  static {
    CONFIGURATION_TABS.put("presidentialView", 0);
    CONFIGURATION_TABS.put("agentView", 1);
  }
  @EJB
  private ElectiveRegionFacade electiveRegionFacade;
  @EJB
  private PollingStationFacade pollingStationFacade;
  @EJB
  private PoliticalAgentFacade politicalAgentFacade;
  private PoliticalAgent politicalAgent;
  private boolean addPoliticalAgent;
  private boolean onEditMode;
  private int currentConfigurationTab;
  private DataModel<PoliticalAgent> politicalAgents;

  /**
   * Creates a new instance of PoliticalAgentController
   */
  public PoliticalAgentController() {
  }

  @PostConstruct
  void onStart() {
    politicalAgents = new PoliticalAgentLazyDataModel();
  }

  public void configurationTabChanged(javax.faces.event.AjaxBehaviorEvent event) {
  }

  public void configurationTabChanged(TabChangeEvent event) {
    Tab t = event.getTab();
    if (t != null) {
      String id = t.getId();
      if (id != null) {
        Integer i = CONFIGURATION_TABS.get(id);
        currentConfigurationTab = i != null ? i : 0;
      }
    }
  }

  public DataModel<PoliticalAgent> getPoliticalAgents() {
    return politicalAgents;
  }

  public void setCurrentConfigurationTab(int currentConfigurationTab) {
  }

  public int getCurrentConfigurationTab() {
    return currentConfigurationTab;
  }

  public boolean isOnEditMode() {
    return onEditMode;
  }

  public boolean isAllPoliticalAgentsAsigned() {
    return getElectiveRegions().isEmpty();
//    int count = electiveRegionFacade.countElectiveRegionsNotAssignedToAgents();
//    System.err.println("isAllPoliticalAgentsAsigned: " + count);
//    return count <= 0;
  }

  public void prepareAddPoliticalAgent() {
    politicalAgent = new PoliticalAgent();
    addPoliticalAgent = true;
  }

  public void prepareEditPoliticalAgent(PoliticalAgent politicalAgent1) {
    this.politicalAgent = politicalAgent1;
    setAddPoliticalAgent(onEditMode = true);
  }

  public List<ElectiveRegion> getElectiveRegions() {
    return new ArrayList<ElectiveRegion>(pollingStationFacade.findPollingStationNotAssignedToAgents());
  }

  public void setAddPoliticalAgent(boolean addPoliticalAgent) {
    this.addPoliticalAgent = addPoliticalAgent;
  }

  public void addAgent() {
    this.politicalAgent.getAgents().add(new Agent());
  }

  public AgentType[] getAgentTypes() {
    return AgentType.values();
  }

  public boolean isAddPoliticalAgent() {
    return addPoliticalAgent;
  }

  public File getPoliticalAgentResourceUrl() {
    File file = new File(Utility.getHomeDirectory() + File.separator + "agents" + File.separator + getPoliticalAgent().getAgentId());
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  public String getPoliticalAgentResourceContextUrl() {
    return File.separator + "agents" + File.separator + getPoliticalAgent().getAgentId();
  }

  public String getPoliticalAgentResourceContextUrl(PoliticalAgent agent) {
    return File.separator + "agents" + File.separator + agent.getAgentId();
  }

  public String getPoliticalAgentImageResourceContextUrl() {
    return File.separator + "agents" + File.separator + getPoliticalAgent().getAgentId() + File.separator + getPoliticalAgent().getAgentPhoto();
  }

  public String getPoliticalAgentImageResourceContextUrl(PoliticalAgent agent) {
    return File.separator + "agents" + File.separator + agent.getAgentId() + File.separator + agent.getAgentPhoto();
  }

  public void handleUploadCandidateImage(FileUploadEvent event) {
    UploadedFile file = event.getFile();
    getPoliticalAgent().setAgentPhoto(file.getFileName());
    try {
      FileOutputStream out = new FileOutputStream(new File(getPoliticalAgentResourceUrl(), file.getFileName()));
      byte[] data = file.getContents();
      out.write(data);
      out.close();
    } catch (Exception e) {
      ElexarController.logError(e);
    }
  }

  public void addElectiveRegionPoliticalAgent() {
    try {
      if (onEditMode) {
        politicalAgentFacade.edit(politicalAgent);
        JsfUtil.addSuccessMessage("Political Agent Successfully updated");
      } else {
        politicalAgentFacade.create(politicalAgent);
        JsfUtil.addSuccessMessage("Political Agent Successfully added");
      }
    } catch (Exception e) {
      ElexarController.logError(e);
      JsfUtil.addErrorMessage("Failed to add political agent");
    } finally {
      if (!onEditMode) {
        prepareAddPoliticalAgent();
      } else {
        onEditMode = addPoliticalAgent = false;
      }
    }
  }

  public PoliticalAgent getPoliticalAgent() {
    if (politicalAgent == null) {
      politicalAgent = new PoliticalAgent();
    }
    return politicalAgent;
  }

  public void setPoliticalAgent(PoliticalAgent politicalAgent) {
    this.politicalAgent = politicalAgent;
  }
}
