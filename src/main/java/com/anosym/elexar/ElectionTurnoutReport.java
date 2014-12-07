/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT",
  query = "SELECT r FROM ElectionTurnoutReport r WHERE r.electionTurnout.turnoutId = :turnoutId"),
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_FROM",
  query = "SELECT r FROM ElectionTurnoutReport r WHERE r.electionTurnout.turnoutId = :turnoutId AND r.reportDate > :reportDate"),
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_TOTAL_ELECTION_TURNOUT_REPORT",
  query = "SELECT SUM(r.votesCast) FROM ElectionTurnoutReport r WHERE r.electionTurnout.turnoutId = :turnoutId"),
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_TOTAL_ELECTION_TURNOUT_REPORT_WITHIN",
  query = "SELECT SUM(r.votesCast) FROM ElectionTurnoutReport r WHERE r.electionTurnout.turnoutId = :turnoutId AND r.reportDate BETWEEN :reportDate0 AND :reportDate1"),
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_WITHIN",
  query = "SELECT r FROM ElectionTurnoutReport r WHERE r.reportDate BETWEEN :reportDate0 AND :reportDate1"),
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_WITHIN_FOR_ELECTIVE_REGION_TYPE",
  query = "SELECT r FROM ElectionTurnoutReport r WHERE r.electionTurnout.electiveRegion.electiveRegionType = :electiveRegionType AND r.reportDate BETWEEN :reportDate0 AND :reportDate1"),
  @NamedQuery(name = "ELECTIONTURNOUTREPORT.FIND_ELECTION_TURNOUT_REPORT_WITHIN_FOR_ELECTIVE_REGION",
  query = "SELECT r FROM ElectionTurnoutReport r WHERE r.electionTurnout.electiveRegion.regionId = :regionId AND r.reportDate BETWEEN :reportDate0 AND :reportDate1")
})
public class ElectionTurnoutReport implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectionTurnoutReport.class);
  @Id
  private Long reportId = IdGenerator.generateId();
  private long votesCast;
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  private Calendar reportDate;
  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(nullable = false)
  private ElectionTurnout electionTurnout;

  public ElectionTurnoutReport(long votesCast, ElectionTurnout electionTurnout) {
    this();
    this.votesCast = votesCast;
    this.electionTurnout = electionTurnout;
  }

  public ElectionTurnoutReport(long votesCast) {
    this();
    this.votesCast = votesCast;
  }

  public ElectionTurnoutReport() {
    reportDate = Calendar.getInstance();
  }

  public Long getReportId() {
    return reportId;
  }

  public void setElectionTurnout(ElectionTurnout electionTurnout) {
    this.electionTurnout = electionTurnout;
  }

  public ElectionTurnout getElectionTurnout() {
    return electionTurnout;
  }

  public void setReportId(Long reportId) {
    this.reportId = reportId;
  }

  public long getVotesCast() {
    return votesCast;
  }

  public void setVotesCast(long votesCast) {
    this.votesCast = votesCast;
  }

  public Calendar getReportDate() {
    return reportDate;
  }

  public void setReportDate(Calendar reportDate) {
    this.reportDate = reportDate;
  }

  public static long getTotalVotesCast(List<ElectionTurnoutReport> electionTurnoutReports) {
    long votes = 0l;
    for (ElectionTurnoutReport report : electionTurnoutReports) {
      votes += report.votesCast;
    }
    return votes;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 83 * hash + (this.reportId != null ? this.reportId.hashCode() : 0);
    hash = 83 * hash + (int) (this.votesCast ^ (this.votesCast >>> 32));
    hash = 83 * hash + (this.reportDate != null ? this.reportDate.hashCode() : 0);
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
    final ElectionTurnoutReport other = (ElectionTurnoutReport) obj;
    if (this.reportId != other.reportId && (this.reportId == null || !this.reportId.equals(other.reportId))) {
      return false;
    }
    if (this.votesCast != other.votesCast) {
      return false;
    }
    if (this.reportDate != other.reportDate && (this.reportDate == null || !this.reportDate.equals(other.reportDate))) {
      return false;
    }
    return true;
  }
}
