/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "POLITICALAGENT.FIND_POLITICAL_AGENT_FROM_PHONE_NUMBER",
  query = "SELECT a FROM PoliticalAgent a WHERE a.agentPhoneNumber = :phoneNumber"),
  @NamedQuery(name = "POLITICALAGENT.FIND_POLITICAL_AGENT_FOR_ELECTIVE_REGION",
  query = "SELECT a FROM PoliticalAgent a WHERE a.electiveRegion.regionId = :regionId")
})
public class PoliticalAgent implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(PoliticalAgent.class);
  @Id
  private Long agentId = IdGenerator.generateId();
  private String agentName;
  @Column(nullable = false, unique = true)
  private String agentPhoneNumber; //the phone number used to transfer the results
  /**
   * A region can only be assigned a single agent.
   */
  @OneToOne
  @JoinColumn(nullable = false, unique = true)
  private ElectiveRegion electiveRegion; // the region where the agent has been assigned.
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Agent> agents;
  private String agentPhoto;

  public PoliticalAgent(String agentName, String agentPhoneNumber, ElectiveRegion electiveRegion) {
    this.agentName = agentName;
    this.agentPhoneNumber = agentPhoneNumber;
    this.electiveRegion = electiveRegion;
  }

  public PoliticalAgent() {
  }

  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }

  public void setAgentPhoto(String agentPhoto) {
    this.agentPhoto = agentPhoto;
  }

  public String getAgentPhoto() {
    return agentPhoto;
  }

  public void setAgents(List<Agent> agents) {
    this.agents = agents;
  }

  public List<Agent> getAgents() {
    if (agents == null) {
      agents = new ArrayList<Agent>();
    }
    return agents;
  }

  public String getAgentName() {
    return agentName;
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public String getAgentPhoneNumber() {
    return agentPhoneNumber;
  }

  public void setAgentPhoneNumber(String agentPhoneNumber) {
    this.agentPhoneNumber = agentPhoneNumber;
  }

  public ElectiveRegion getElectiveRegion() {
    return electiveRegion;
  }

  public void setElectiveRegion(ElectiveRegion electiveRegion) {
    this.electiveRegion = electiveRegion;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + (this.agentId != null ? this.agentId.hashCode() : 0);
    hash = 79 * hash + (this.agentName != null ? this.agentName.hashCode() : 0);
    hash = 79 * hash + (this.agentPhoneNumber != null ? this.agentPhoneNumber.hashCode() : 0);
    hash = 79 * hash + (this.electiveRegion != null ? this.electiveRegion.hashCode() : 0);
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
    final PoliticalAgent other = (PoliticalAgent) obj;
    if (this.agentId != other.agentId && (this.agentId == null || !this.agentId.equals(other.agentId))) {
      return false;
    }
    if ((this.agentName == null) ? (other.agentName != null) : !this.agentName.equals(other.agentName)) {
      return false;
    }
    if ((this.agentPhoneNumber == null) ? (other.agentPhoneNumber != null) : !this.agentPhoneNumber.equals(other.agentPhoneNumber)) {
      return false;
    }
    if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return agentName + "(" + electiveRegion + ")";
  }
}
