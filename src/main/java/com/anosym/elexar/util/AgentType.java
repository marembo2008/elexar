/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public enum AgentType {

  POLITICAL_AGENT("Political Agent"),
  ELECTION_OBSERVER("Election Observer");
  private String description;

  private AgentType(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return description;
  }
}
