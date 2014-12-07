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
  @NamedQuery(name = "ELECTIVEREGIONFLAGGEDISSUE.FIND_ELECTIVE_REGION_FLAGGED_ISSUE",
  query = "SELECT i FROM ElectiveRegionFlaggedIssue i, IN(i.flaggedIssues) fi WHERE i.electiveRegion.regionId = :regionId AND fi.issueConfirmed = :issueConfirmed"),
  @NamedQuery(name = "ELECTIVEREGIONFLAGGEDISSUE.COUNT_ELECTIVE_REGION_FLAGGED_ISSUE",
  query = "SELECT COUNT(i) FROM ElectiveRegionFlaggedIssue i, IN(i.flaggedIssues) fi WHERE i.electiveRegion.regionId = :regionId AND fi.issueConfirmed = :issueConfirmed"),
  @NamedQuery(name = "ELECTIVEREGIONFLAGGEDISSUE.COUNT_ELECTIVE_REGION_FLAGGED_ISSUE_BY_TYPE",
  query = "SELECT COUNT(fi) FROM ElectiveRegionFlaggedIssue i, IN(i.flaggedIssues) fi WHERE i.electiveRegion.regionId = :regionId AND fi.flaggedIssueType = :flaggedIssueType"),
  @NamedQuery(name = "ELECTIVEREGIONFLAGGEDISSUE.COUNT_UNCONFIRMED_ELECTIVE_REGION_FLAGGED_ISSUE",
  query = "SELECT COUNT(i) FROM ElectiveRegionFlaggedIssue i, IN(i.flaggedIssues) fi WHERE fi.issueConfirmed = :issueConfirmed"),
  @NamedQuery(name = "ELECTIVEREGIONFLAGGEDISSUE.FIND_UNCONFIRMED_ELECTIVE_REGION_FLAGGED_ISSUE",
  query = "SELECT i FROM ElectiveRegionFlaggedIssue i, IN(i.flaggedIssues) fi WHERE fi.issueConfirmed = :issueConfirmed")
})
public class ElectiveRegionFlaggedIssue implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectiveRegionFlaggedIssue.class);
  @Id
  private Long issueId = IdGenerator.generateId();
  @OneToOne
  @JoinColumn(nullable = false, unique = true)
  private ElectiveRegion electiveRegion;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<FlaggedIssue> flaggedIssues;

  public ElectiveRegionFlaggedIssue(ElectiveRegion electiveRegion) {
    this();
    this.electiveRegion = electiveRegion;
  }

  public ElectiveRegionFlaggedIssue() {
    flaggedIssues = new ArrayList<FlaggedIssue>();
  }

  public Long getIssueId() {
    return issueId;
  }

  public void setIssueId(Long issueId) {
    this.issueId = issueId;
  }

  public ElectiveRegion getElectiveRegion() {
    return electiveRegion;
  }

  public void setElectiveRegion(ElectiveRegion electiveRegion) {
    this.electiveRegion = electiveRegion;
  }

  public List<FlaggedIssue> getFlaggedIssues() {
    return flaggedIssues;
  }

  public void setFlaggedIssues(List<FlaggedIssue> flaggedIssues) {
    this.flaggedIssues = flaggedIssues;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 71 * hash + (this.issueId != null ? this.issueId.hashCode() : 0);
    hash = 71 * hash + (this.electiveRegion != null ? this.electiveRegion.hashCode() : 0);
    hash = 71 * hash + (this.flaggedIssues != null ? this.flaggedIssues.hashCode() : 0);
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
    final ElectiveRegionFlaggedIssue other = (ElectiveRegionFlaggedIssue) obj;
    if (this.issueId != other.issueId && (this.issueId == null || !this.issueId.equals(other.issueId))) {
      return false;
    }
    if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
      return false;
    }
    if (this.flaggedIssues != other.flaggedIssues && (this.flaggedIssues == null || !this.flaggedIssues.equals(other.flaggedIssues))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ElectiveRegionFlaggedIssue{" + "issueId=" + issueId + ", electiveRegion=" + electiveRegion + ", flaggedIssues=" + flaggedIssues + '}';
  }
}
