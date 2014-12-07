/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

import com.anosym.vjax.annotations.v3.Converter;

/**
 *
 * @author marembo
 */
public class VotingMalpractice {

  public static class ElectionMalpracticeTypeConverter implements com.anosym.vjax.converter.v3.Converter<ElectionMalpracticeType, Integer> {

    @Override
    public Integer convertFrom(ElectionMalpracticeType value) {
      return value != null ? value.getCode() : -1;
    }

    @Override
    public ElectionMalpracticeType convertTo(Integer value) {
      return value != null ? ElectionMalpracticeType.valueOf(value) : null;
    }
  }
  private String agent;
  @Converter(ElectionMalpracticeTypeConverter.class)
  private ElectionMalpracticeType code;

  public VotingMalpractice(String agent, ElectionMalpracticeType code) {
    this.agent = agent;
    this.code = code;
  }

  public VotingMalpractice() {
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  public ElectionMalpracticeType getCode() {
    return code;
  }

  public void setCode(ElectionMalpracticeType code) {
    this.code = code;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + (this.agent != null ? this.agent.hashCode() : 0);
    hash = 37 * hash + (this.code != null ? this.code.hashCode() : 0);
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
    final VotingMalpractice other = (VotingMalpractice) obj;
    if ((this.agent == null) ? (other.agent != null) : !this.agent.equals(other.agent)) {
      return false;
    }
    if (this.code != other.code) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "VotingMalpractice{" + "agent=" + agent + ", code=" + code + '}';
  }
}
