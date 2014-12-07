/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.facade;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.PollingStation;
import com.anosym.elexar.controller.ElexarController;
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
public class PollingStationFacade extends AbstractFacade<PollingStation> {

  @PersistenceContext(unitName = "ElexarPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PollingStationFacade() {
    super(PollingStation.class);
  }

  public List<PollingStation> findPollingStationNotAssignedToAgents() {
    try {
      String sql = "SELECT DISTINCT * FROM POLLINGSTATION AS S WHERE NOT EXISTS (SELECT A.ELECTIVEREGION_REGIONID FROM POLITICALAGENT A WHERE A.ELECTIVEREGION_REGIONID = S.REGIONID) ORDER BY REGIONNAME LIMIT 30";
      List<PollingStation> pollingStations = getEntityManager()
              .createNativeQuery(sql, PollingStation.class)
              .getResultList();
      System.err.println("Polling Stations: " + pollingStations);
      return pollingStations;
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<PollingStation>();
    }
  }

  public List<ElectiveRegion> searchPollingStations(String name) {
    name = "'%" + name + "%'";
    try {
      String sql = "SELECT DISTINCT * FROM POLLINGSTATION AS S WHERE REGIONNAME LIKE " + name + " AND NOT EXISTS (SELECT A.ELECTIVEREGION_REGIONID FROM POLITICALAGENT A WHERE A.ELECTIVEREGION_REGIONID = S.REGIONID) ORDER BY REGIONNAME LIMIT 30";
      List<PollingStation> pollingStations = getEntityManager()
              .createNativeQuery(sql, PollingStation.class)
              .getResultList();
      System.err.println("Polling Stations: " + pollingStations);
      return new ArrayList<ElectiveRegion>(pollingStations);
    } catch (Exception e) {
      ElexarController.logError(e);
      return new ArrayList<ElectiveRegion>();
    }
  }
}
