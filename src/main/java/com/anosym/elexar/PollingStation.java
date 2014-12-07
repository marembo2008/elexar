/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveRegionType;
import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "POLLINGSTATION.FIND_POLLING_STATION_NOT_ASSIGNED_AGENT",
  query = "SELECT p FROM PoliticalAgent a JOIN a.electiveRegion p WHERE a.electiveRegion.regionId != p.regionId "),
  @NamedQuery(name = "POLLINGSTATION.FIND_POLLING_STATION_BY_NAME",
  query = "SELECT a FROM PoliticalAgent a WHERE a.agentName LIKE :name"),
  @NamedQuery(name = "POLLINGSTATION.FIND_POLLING_STATION_WITHOUT_ELECTION_RESULTS",
  query = "SELECT e FROM ElectionResult r JOIN r.electiveRegion e WHERE e.regionId != r.electiveRegion.regionId ")
})
public class PollingStation extends ElectiveRegion implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(PollingStation.class);

  public PollingStation() {
    super(ElectiveRegionType.POLLING_STATION);
  }

  public PollingStation(String regionName, long registeredVoters) {
    super(regionName, registeredVoters, ElectiveRegionType.POLLING_STATION);
  }
}
