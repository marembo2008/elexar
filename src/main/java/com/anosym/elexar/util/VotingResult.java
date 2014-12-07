/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

import com.anosym.vjax.annotations.v3.GenericMapType;
import java.util.Map;

/**
 *
 * @author marembo
 */
public class VotingResult {

  private int disputed;
  private int rejected;
  private String agent;
  @GenericMapType(key = String.class,
  value = Integer.class,
  keyMarkup = "code",
  valueMarkup = "votes",
  entryMarkup = "candidate")
  private Map<String, Integer> result;

  public VotingResult() {
  }

  public int getDisputed() {
    return disputed;
  }

  public void setDisputed(int disputed) {
    this.disputed = disputed;
  }

  public int getRejected() {
    return rejected;
  }

  public void setRejected(int rejected) {
    this.rejected = rejected;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  public void setResult(Map<String, Integer> result) {
    this.result = result;
  }

  public Map<String, Integer> getResult() {
    return result;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 47 * hash + this.disputed;
    hash = 47 * hash + this.rejected;
    hash = 47 * hash + (this.agent != null ? this.agent.hashCode() : 0);
    hash = 47 * hash + (this.result != null ? this.result.hashCode() : 0);
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
    final VotingResult other = (VotingResult) obj;
    if (this.disputed != other.disputed) {
      return false;
    }
    if (this.rejected != other.rejected) {
      return false;
    }
    if ((this.agent == null) ? (other.agent != null) : !this.agent.equals(other.agent)) {
      return false;
    }
    if (this.result != other.result && (this.result == null || !this.result.equals(other.result))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "VotingResult{" + "disputed=" + disputed + ", rejected=" + rejected + ", agent=" + agent + ", votes=" + result + '}';
  }
}
