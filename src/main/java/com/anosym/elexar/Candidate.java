package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import com.anosym.elexar.util.ElectiveSeat;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author marembo
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "CANDIDATE.FIND_CANDIDATES_FOR_ELECTIVE_SEATS", query = "SELECT c FROM Candidate c WHERE c.electiveSeat = :electiveSeat"),
    @NamedQuery(name = "CANDIDATE.FIND_CANDIDATE_WITH_CODE", query = "SELECT c FROM Candidate c WHERE LOWER(c.candidateCode) = LOWER(:code)")
})
public class Candidate implements Serializable {

    private static final long serialVersionUID = IdGenerator.serialVersionUID(Candidate.class);
    @Id
    private Long candidateId = IdGenerator.generateId();
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String middleName;
    @Column(nullable = false)
    private ElectiveSeat electiveSeat;
    @OneToOne
    private ElectiveRegion electiveRegion;
    /**
     * Url for the candidate photo;
     */
    private String candidatePhoto;
    private String politicalLogo;
    private String partyColor;
    @Column(unique = true, nullable = false)
    private String candidateCode;

    public Candidate() {
    }

    public Candidate(String firstName, String lastName, String middleName, ElectiveSeat electiveSeat, ElectiveRegion electiveRegion) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.electiveSeat = electiveSeat;
        this.electiveRegion = electiveRegion;
    }

    public Candidate(String firstName, String lastName, String middleName, ElectiveSeat electiveSeat, ElectiveRegion electiveRegion,
                     String candidatePhoto) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.electiveSeat = electiveSeat;
        this.electiveRegion = electiveRegion;
        this.candidatePhoto = candidatePhoto;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setPartyColor(String partyColor) {
        this.partyColor = partyColor;
    }

    public String getPartyColor() {
        return partyColor;
    }

    public String getPoliticalLogo() {
        return politicalLogo;
    }

    public void setPoliticalLogo(String politicalLogo) {
        this.politicalLogo = politicalLogo;
    }

    public String getCandidatePhoto() {
        return candidatePhoto;
    }

    public void setCandidatePhoto(String candidatePhoto) {
        this.candidatePhoto = candidatePhoto;
    }

    public void setElectiveRegion(ElectiveRegion electiveRegion) {
        this.electiveRegion = electiveRegion;
    }

    public ElectiveRegion getElectiveRegion() {
        return electiveRegion;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setElectiveSeat(ElectiveSeat electiveSeat) {
        this.electiveSeat = electiveSeat;
    }

    public ElectiveSeat getElectiveSeat() {
        return electiveSeat;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.candidateId != null ? this.candidateId.hashCode() : 0);
        hash = 53 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 53 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 53 * hash + (this.middleName != null ? this.middleName.hashCode() : 0);
        hash = 53 * hash + (this.electiveSeat != null ? this.electiveSeat.hashCode() : 0);
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
        final Candidate other = (Candidate) obj;
        if (this.candidateId != other.candidateId && (this.candidateId == null || !this.candidateId.equals(other.candidateId))) {
            return false;
        }
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.middleName == null) ? (other.middleName != null) : !this.middleName.equals(other.middleName)) {
            return false;
        }
        if (this.electiveSeat != other.electiveSeat) {
            return false;
        }
        if (this.electiveRegion != other.electiveRegion && (this.electiveRegion == null || !this.electiveRegion.equals(other.electiveRegion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String name = "";
        if (firstName != null) {
            name += firstName + " ";
        }
        if (middleName != null) {
            name += middleName + " ";
        }
        if (lastName != null) {
            name += lastName + " ";
        }
        return name;
    }
}
