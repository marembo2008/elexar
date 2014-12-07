package com.anosym.elexar;

import com.anosym.utilities.IdGenerator;
import com.anosym.elexar.util.AgentType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author marembo
 */
@Entity
public class Agent implements Serializable {

    private static final long serialVersionUID = IdGenerator.serialVersionUID(Agent.class);
    @Id
    private Long agentId = IdGenerator.generateId();
    private AgentType agentType;
    private String agentName;
    private String phoneNumber;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.agentId != null ? this.agentId.hashCode() : 0);
        hash = 43 * hash + (this.agentType != null ? this.agentType.hashCode() : 0);
        hash = 43 * hash + (this.agentName != null ? this.agentName.hashCode() : 0);
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
        final Agent other = (Agent) obj;
        if (this.agentId != other.agentId && (this.agentId == null || !this.agentId.equals(other.agentId))) {
            return false;
        }
        if (this.agentType != other.agentType) {
            return false;
        }
        if ((this.agentName == null) ? (other.agentName != null) : !this.agentName.equals(other.agentName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Agent{" + "agentId=" + agentId + ", agentType=" + agentType + ", agentName=" + agentName + '}';
    }
}
