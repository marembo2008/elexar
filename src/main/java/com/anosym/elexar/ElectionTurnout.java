/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "ELECTIONTURNOUT.FIND_ELECTION_TURNOUT",
  query = "SELECT e FROM ElectionTurnout e WHERE e.electiveRegion.regionId = :regionId AND e.politicalAgent.agentId = :agentId"),
  @NamedQuery(name = "ELECTIONTURNOUT.FIND_ELECTION_TURNOUT_BY_ELECTIVE_REGION_TYPE",
  query = "SELECT e FROM ElectionTurnout e JOIN e.electiveRegion r WHERE r.electiveRegionType = :electiveRegionType"),
  @NamedQuery(name = "ELECTIONTURNOUT.GET_ELECTION_TURNOUT_FOR_REGION",
  query = "SELECT e FROM ElectionTurnout e WHERE e.electiveRegion.regionId = :regionId")
})
public class ElectionTurnout implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectionTurnout.class);
  @Id
  private Long turnoutId = IdGenerator.generateId();
  @OneToOne
  @JoinColumn(nullable = false, unique = true)
  private ElectiveRegion electiveRegion;
  @OneToOne
  @JoinColumn(unique = true)
  private PoliticalAgent politicalAgent;

  public ElectionTurnout() {
  }

  public ElectionTurnout(ElectiveRegion electiveRegion, PoliticalAgent politicalAgent) {
    this.electiveRegion = electiveRegion;
    this.politicalAgent = politicalAgent;
  }

  public Long getTurnoutId() {
    return turnoutId;
  }

  public void setTurnoutId(Long turnoutId) {
    this.turnoutId = turnoutId;
  }

  public ElectiveRegion getElectiveRegion() {
    return electiveRegion;
  }

  public void setElectiveRegion(ElectiveRegion electiveRegion) {
    this.electiveRegion = electiveRegion;
  }

  public PoliticalAgent getPoliticalAgent() {
    return politicalAgent;
  }

  public void setPoliticalAgent(PoliticalAgent politicalAgent) {
    this.politicalAgent = politicalAgent;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + (this.turnoutId != null ? this.turnoutId.hashCode() : 0);
    hash = 79 * hash + (this.electiveRegion != null ? this.electiveRegion.hashCode() : 0);
    hash = 79 * hash + (this.politicalAgent != null ? this.politicalAgent.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ElectionTurnout other = (ElectionTurnout) obj;
    if (this.turnoutId != other.turnoutId && (this.turnoutId == null || !this.turnoutId.equals(other.turnoutId))) {
      return false;
    }
    if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
      return false;
    }
    if (this.politicalAgent != other.politicalAgent && (this.politicalAgent == null || !this.politicalAgent.equals(other.politicalAgent))) {
      return false;
    }
    return true;
  }
}
