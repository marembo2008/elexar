/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveSeat;
import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "ELECTIONRESULT.FIND_ELECTION_RESULT_FOR_REGION",
  query = "SELECT r FROM ElectionResult r WHERE r.electiveRegion.regionId = :regionId"),
  @NamedQuery(name = "ELECTIONRESULT.FIND_RESULTS_FOR_REGION_FOR_ELECTIVE_SEAT",
  query = "SELECT r FROM ElectionResult r WHERE r.electiveRegion.regionId = :regionId AND r.electiveSeat = :electiveSeat"),
  @NamedQuery(name = "ELECTIONRESULT.FIND_ELECTION_RESULT_FOR_ELECTIVE_REGION_TYPE",
  query = "SELECT e FROM ElectionResult e JOIN e.electiveRegion er WHERE er.electiveRegionType = :electiveRegionType"),
  @NamedQuery(name = "ELECTIONRESULT.COUNT_ELECTION_RESULT_FOR_ELECTIVE_REGION_TYPE",
  query = "SELECT COUNT(r) FROM ElectionResult r JOIN r.electiveRegion e WHERE e.electiveRegionType = :electiveRegionType")
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ElectionResult implements Serializable {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectionResult.class);
  @Id
  private Long resultId = IdGenerator.generateId();
  @OneToOne
  private ElectiveRegion electiveRegion;
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  private Calendar resultDate;
  private long votesDisputed;
  private long votesRejected;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<CandidateElectionResult> candidateElectionResults;
  @Column(nullable = false)
  private ElectiveSeat electiveSeat;
  @Transient
  public static final Comparator<CandidateElectionResult> resultComparator = new Comparator<CandidateElectionResult>() {
    @Override
    public int compare(CandidateElectionResult o1, CandidateElectionResult o2) {
      return -Integer.valueOf(o1.getVotesGained()).compareTo(o2.getVotesGained());
    }
  };

  public ElectionResult() {
    this.resultDate = Calendar.getInstance();
    this.candidateElectionResults = new ArrayList<CandidateElectionResult>();
    this.electiveSeat = ElectiveSeat.PRESIDENTAIL;
  }

  public ElectionResult(ElectiveRegion electiveRegion, ElectiveSeat electiveSeat, long votesDisputed, long votesRejected) {
    this();
    this.electiveRegion = electiveRegion;
    this.votesDisputed = votesDisputed;
    this.votesRejected = votesRejected;
    this.electiveSeat = electiveSeat;
  }

  public void incrementCandidateResult(Candidate candidate, int votesGained) {
    for (CandidateElectionResult cer : getCandidateElectionResults()) {
      if (cer.getCandidate().equals(candidate)) {
        cer.setVotesGained(cer.getVotesGained() + votesGained);
        break;
      }
    }
  }

  public void incrementVotesDisputed(long votesDisputed) {
    this.votesDisputed += votesDisputed;
  }

  public void incrementVotesRejected(long votesRejected) {
    this.votesRejected += votesRejected;
  }

  public void setElectiveSeat(ElectiveSeat electiveSeat) {
    this.electiveSeat = electiveSeat;
  }

  public ElectiveSeat getElectiveSeat() {
    return electiveSeat;
  }

  public CandidateElectionResult getHighestCandidateElectionResult() {
    return getCandidateElectionResults().isEmpty() ? null : getCandidateElectionResults().get(0);
  }

  public Long getResultId() {
    return resultId;
  }

  public void setResultId(Long resultId) {
    this.resultId = resultId;
  }

  public ElectiveRegion getElectiveRegion() {
    return electiveRegion;
  }

  public void setElectiveRegion(ElectiveRegion electiveRegion) {
    this.electiveRegion = electiveRegion;
  }

  public Calendar getResultDate() {
    return resultDate;
  }

  public void setResultDate(Calendar resultDate) {
    this.resultDate = resultDate;
  }

  public long getVotesDisputed() {
    return votesDisputed;
  }

  public void setVotesDisputed(long votesDisputed) {
    this.votesDisputed = votesDisputed;
  }

  public long getVotesRejected() {
    return votesRejected;
  }

  public void setVotesRejected(long votesRejected) {
    this.votesRejected = votesRejected;
  }

  public void addCandidateElectionResults(CandidateElectionResult candidateElectionResult) {
    if (!getCandidateElectionResults().contains(candidateElectionResult)) {
      getCandidateElectionResults().add(candidateElectionResult);
    }
  }

  public List<CandidateElectionResult> getCandidateElectionResults() {
    if (candidateElectionResults == null) {
      candidateElectionResults = new ArrayList<CandidateElectionResult>();
    } else {
      Collections.sort(candidateElectionResults, resultComparator);
    }
    return candidateElectionResults;
  }

  public void setCandidateElectionResults(List<CandidateElectionResult> candidateElectionResults) {
    this.candidateElectionResults = candidateElectionResults;
  }

  public int totalVotesGained() {
    int total = 0;
    for (CandidateElectionResult cer : getCandidateElectionResults()) {
      total += cer.getVotesGained();
    }
    return total;
  }

  public long getVotesCast() {
    return totalVotesGained() + votesDisputed + votesRejected;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 17 * hash + (this.resultId != null ? this.resultId.hashCode() : 0);
    hash = 17 * hash + (this.electiveRegion != null ? this.electiveRegion.hashCode() : 0);
    hash = 17 * hash + (this.resultDate != null ? this.resultDate.hashCode() : 0);
    hash = 17 * hash + (int) (this.votesDisputed ^ (this.votesDisputed >>> 32));
    hash = 17 * hash + (int) (this.votesRejected ^ (this.votesRejected >>> 32));
    hash = 17 * hash + (this.candidateElectionResults != null ? this.candidateElectionResults.hashCode() : 0);
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
    final ElectionResult other = (ElectionResult) obj;
    if (this.resultId != other.resultId && (this.resultId == null || !this.resultId.equals(other.resultId))) {
      return false;
    }
    if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
      return false;
    }
    if (this.resultDate != other.resultDate && (this.resultDate == null || !this.resultDate.equals(other.resultDate))) {
      return false;
    }
    if (this.votesDisputed != other.votesDisputed) {
      return false;
    }
    if (this.votesRejected != other.votesRejected) {
      return false;
    }
    if (this.candidateElectionResults != other.candidateElectionResults && (this.candidateElectionResults == null || !this.candidateElectionResults.equals(other.candidateElectionResults))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ElectionResult{" + "resultId=" + resultId + ", electiveRegion=" + electiveRegion + ", resultDate=" + resultDate + ", votesDisputed=" + votesDisputed + ", votesRejected=" + votesRejected + ", candidateElectionResults=" + candidateElectionResults + '}';
  }
}
