/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public enum ElectiveSeat {

  PRESIDENTAIL("Presidential"),
  SENATORIAL("Senatorial"),
  GUBERNATORIAL("Gubernatorial"),
  WOMEN_REPRESENTATIVE("Women, Representative"),
  MP("Member of National Assemby"),
  COUNTY_REPRESENTATIVE("County Assembly Representative");
  private String desc;

  private ElectiveSeat(String desc) {
    this.desc = desc;
  }

  @Override
  public String toString() {
    return desc;
  }
}
