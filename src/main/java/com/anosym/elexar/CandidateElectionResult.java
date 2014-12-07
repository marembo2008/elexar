package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author marembo
 */
@Entity
public class CandidateElectionResult implements Serializable {

    private static final long serialVersionUID = IdGenerator.serialVersionUID(CandidateElectionResult.class);
    @Id
    private Long resultId = IdGenerator.generateId();
    @OneToOne
    @JoinColumn(nullable = false)
    private Candidate candidate;
    private int votesGained;

    public CandidateElectionResult(Candidate candidate, int votesGained) {
        this.candidate = candidate;
        this.votesGained = votesGained;
    }

    public CandidateElectionResult() {
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public int getVotesGained() {
        return votesGained;
    }

    public void setVotesGained(int votesGained) {
        this.votesGained = votesGained;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.candidate != null ? this.candidate.hashCode() : 0);
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
        final CandidateElectionResult other = (CandidateElectionResult) obj;
        if (this.candidate != other.candidate && (this.candidate == null || !this.candidate.equals(other.candidate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CandidateElectionResult{" + "resultId=" + resultId + ", candidate=" + candidate + ", votesGained=" + votesGained + '}';
    }
}
