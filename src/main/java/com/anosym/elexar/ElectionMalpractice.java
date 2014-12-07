package com.anosym.elexar;

import com.anosym.utilities.FormattedCalendar;
import com.anosym.utilities.IdGenerator;
import com.anosym.elexar.util.ElectionMalpracticeType;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ELECTIONMALPRACTICE.COUNT_ELECTION_MALPRACTICE_BY_TYPE_AND_AGENT",
                query = "SELECT COUNT(m) FROM ElectionMalpractice m WHERE m.malpracticeType = :malpracticeType AND m.politicalAgent.agentId = :agentId"),
    @NamedQuery(name = "ELECTIONMALPRACTICE.COUNT_ELECTION_MALPRACTICE_BY_ELECTIVE_REGION",
                query = "SELECT COUNT(m) FROM ElectionMalpractice m WHERE  m.politicalAgent.electiveRegion.regionId = :regionId"),
    @NamedQuery(name = "ELECTIONMALPRACTICE.GET_ELECTION_MALPRACTICE_BY_ELECTIVE_REGION",
                query = "SELECT m FROM ElectionMalpractice m WHERE  m.politicalAgent.electiveRegion.regionId = :regionId")
})
public class ElectionMalpractice implements Serializable {

    private static final long serialVersionUID = IdGenerator.serialVersionUID(ElectionMalpractice.class);
    @Id
    private Long malpracticeId = IdGenerator.generateId();
    @ManyToOne
    private PoliticalAgent politicalAgent;
    private ElectionMalpracticeType malpracticeType;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar malpracticeTime;

    public ElectionMalpractice() {
        malpracticeTime = Calendar.getInstance();
    }

    public ElectionMalpractice(PoliticalAgent politicalAgent, ElectionMalpracticeType malpracticeType) {
        this();
        this.politicalAgent = politicalAgent;
        this.malpracticeType = malpracticeType;
    }

    public Long getMalpracticeId() {
        return malpracticeId;
    }

    public void setMalpracticeId(Long malpracticeId) {
        this.malpracticeId = malpracticeId;
    }

    public PoliticalAgent getPoliticalAgent() {
        return politicalAgent;
    }

    public void setPoliticalAgent(PoliticalAgent politicalAgent) {
        this.politicalAgent = politicalAgent;
    }

    public ElectionMalpracticeType getMalpracticeType() {
        return malpracticeType;
    }

    public void setMalpracticeType(ElectionMalpracticeType malpracticeType) {
        this.malpracticeType = malpracticeType;
    }

    public Calendar getMalpracticeTime() {
        return malpracticeTime;
    }

    public void setMalpracticeTime(Calendar malpracticeTime) {
        this.malpracticeTime = malpracticeTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.malpracticeId != null ? this.malpracticeId.hashCode() : 0);
        hash = 37 * hash + (this.politicalAgent != null ? this.politicalAgent.hashCode() : 0);
        hash = 37 * hash + (this.malpracticeType != null ? this.malpracticeType.hashCode() : 0);
        hash = 37 * hash + (this.malpracticeTime != null ? this.malpracticeTime.hashCode() : 0);
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
        final ElectionMalpractice other = (ElectionMalpractice) obj;
        if (this.malpracticeId != other.malpracticeId && (this.malpracticeId == null || !this.malpracticeId.equals(other.malpracticeId))) {
            return false;
        }
        if (this.politicalAgent != other.politicalAgent && (this.politicalAgent == null || !this.politicalAgent.equals(other.politicalAgent))) {
            return false;
        }
        if (this.malpracticeType != other.malpracticeType) {
            return false;
        }
        if (this.malpracticeTime != other.malpracticeTime && (this.malpracticeTime == null || !this.malpracticeTime.equals(other.malpracticeTime))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ElectionMalpractice{" + "politicalAgent=" + politicalAgent + ", "
                + "pollingStation=" + (politicalAgent != null ? politicalAgent.getElectiveRegion() : "") + ", malpracticeType=" + malpracticeType + ", "
                + "malpracticeTime=" + FormattedCalendar.toISOString(malpracticeTime) + '}';
    }
}
