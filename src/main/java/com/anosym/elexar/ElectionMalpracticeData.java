/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * This table is primarily used to generate state for malpractice in elective regions where the
 * polling station is part.
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "ELECTIONMALPRACTICEDATA.COUNT_ELECTION_MALPRACTICE_DATA_BY_REGION_AND_TYPE",
  query = "SELECT COUNT(d) FROM ElectionMalpracticeData d WHERE d.electiveRegion.regionId = :regionId AND d.electionMalpractice.malpracticeType = :malpracticeType"),
  @NamedQuery(name = "ELECTIONMALPRACTICEDATA.COUNT_ELECTION_MALPRACTICE_DATA_BY_REGION",
  query = "SELECT COUNT(d) FROM ElectionMalpracticeData d WHERE d.electiveRegion.regionId = :regionId"),
  @NamedQuery(name = "ELECTIONMALPRACTICEDATA.FIND_ELECTION_MALPRACTICE_DATA_BY_REGION",
  query = "SELECT d FROM ElectionMalpracticeData d WHERE d.electiveRegion.regionId = :regionId")
})
public class ElectionMalpracticeData implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectionMalpracticeData.class);
  @Id
  private Long dataId = IdGenerator.generateId();
  @OneToOne
  private ElectiveRegion electiveRegion;
  /**
   * This shows the latest election malpractice for this region, if it is not a polling station.
   */
  @OneToOne
  private ElectionMalpractice electionMalpractice;
  /**
   * If there are more than one election malpractice, this keeps the count.
   */
  private int numberOfElectionMalpractices;

  public ElectionMalpracticeData(ElectiveRegion electiveRegion, ElectionMalpractice electionMalpractice) {
    this();
    this.electiveRegion = electiveRegion;
    this.electionMalpractice = electionMalpractice;
  }

  public ElectionMalpracticeData(ElectiveRegion electiveRegion) {
    this();
    this.electiveRegion = electiveRegion;
  }

  public ElectionMalpracticeData() {
    numberOfElectionMalpractices = 1;
  }

  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  public void setElectionMalpractice(ElectionMalpractice electionMalpractice) {
    this.electionMalpractice = electionMalpractice;
  }

  public ElectionMalpractice getElectionMalpractice() {
    return electionMalpractice;
  }

  public int getNumberOfElectionMalpractices() {
    return numberOfElectionMalpractices;
  }

  public void setNumberOfElectionMalpractices(int numberOfElectionMalpractices) {
    this.numberOfElectionMalpractices = numberOfElectionMalpractices;
  }

  public void incrementElectionMalpractice() {
    this.numberOfElectionMalpractices++;
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
    hash = 53 * hash + (this.dataId != null ? this.dataId.hashCode() : 0);
    hash = 53 * hash + (this.electiveRegion != null ? this.electiveRegion.hashCode() : 0);
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
    final ElectionMalpracticeData other = (ElectionMalpracticeData) obj;
    if (this.dataId != other.dataId && (this.dataId == null || !this.dataId.equals(other.dataId))) {
      return false;
    }
    if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
      return false;
    }
    return true;
  }
}
