/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public class VotingTurnout {

  private int votesCast;
  private String agent;

  public VotingTurnout() {
  }

  public VotingTurnout(int votesCast, String agent) {
    this.votesCast = votesCast;
    this.agent = agent;
  }

  public int getVotesCast() {
    return votesCast;
  }

  public void setVotesCast(int votesCast) {
    this.votesCast = votesCast;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + this.votesCast;
    hash = 11 * hash + (this.agent != null ? this.agent.hashCode() : 0);
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
    final VotingTurnout other = (VotingTurnout) obj;
    if (this.votesCast != other.votesCast) {
      return false;
    }
    if ((this.agent == null) ? (other.agent != null) : !this.agent.equals(other.agent)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "VotingTurnout{" + "votesCast=" + votesCast + ", agent=" + agent + '}';
  }
}
