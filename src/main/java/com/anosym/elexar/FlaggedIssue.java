/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.elexar.util.FlaggedIssueType;
import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "FLAGGEDISSUE.FIND_FLAGGED_ISSUE_BY_TYPE",
  query = "SELECT i FROM FlaggedIssue i WHERE i.flaggedIssueType = :flaggedIssueType")
})
public class FlaggedIssue implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(FlaggedIssue.class);
  @Id
  private Long issueId = IdGenerator.generateId();
  private boolean issueConfirmed;
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  private Calendar issueDate;
  private FlaggedIssueType flaggedIssueType;
  private String issueDescription;

  public FlaggedIssue(FlaggedIssueType flaggedIssueType, String issueDescription) {
    this();
    this.flaggedIssueType = flaggedIssueType;
    this.issueDescription = issueDescription;
  }

  public FlaggedIssue() {
    issueDate = Calendar.getInstance();
    issueConfirmed = false;
  }

  public Long getIssueId() {
    return issueId;
  }

  public void setIssueId(Long issueId) {
    this.issueId = issueId;
  }

  public boolean isIssueConfirmed() {
    return issueConfirmed;
  }

  public void setIssueConfirmed(boolean issueConfirmed) {
    this.issueConfirmed = issueConfirmed;
  }

  public Calendar getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(Calendar issueDate) {
    this.issueDate = issueDate;
  }

  public FlaggedIssueType getFlaggedIssueType() {
    return flaggedIssueType;
  }

  public void setFlaggedIssueType(FlaggedIssueType flaggedIssueType) {
    this.flaggedIssueType = flaggedIssueType;
  }

  public String getIssueDescription() {
    return issueDescription;
  }

  public void setIssueDescription(String issueDescription) {
    this.issueDescription = issueDescription;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + (this.issueId != null ? this.issueId.hashCode() : 0);
    hash = 97 * hash + (this.issueConfirmed ? 1 : 0);
    hash = 97 * hash + (this.issueDate != null ? this.issueDate.hashCode() : 0);
    hash = 97 * hash + (this.flaggedIssueType != null ? this.flaggedIssueType.hashCode() : 0);
    hash = 97 * hash + (this.issueDescription != null ? this.issueDescription.hashCode() : 0);
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
    final FlaggedIssue other = (FlaggedIssue) obj;
    if (this.issueId != other.issueId && (this.issueId == null || !this.issueId.equals(other.issueId))) {
      return false;
    }
    if (this.issueConfirmed != other.issueConfirmed) {
      return false;
    }
    if (this.issueDate != other.issueDate && (this.issueDate == null || !this.issueDate.equals(other.issueDate))) {
      return false;
    }
    if (this.flaggedIssueType != other.flaggedIssueType) {
      return false;
    }
    if ((this.issueDescription == null) ? (other.issueDescription != null) : !this.issueDescription.equals(other.issueDescription)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "FlaggedIssue{" + "issueId=" + issueId + ", issueConfirmed=" + issueConfirmed + ", issueDate=" + issueDate + ", flaggedIssueType=" + flaggedIssueType + ", issueDescription=" + issueDescription + '}';
  }
}
