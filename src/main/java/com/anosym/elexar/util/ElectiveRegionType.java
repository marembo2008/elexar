/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public enum ElectiveRegionType {

  COUNTRY("Country"),
  COUNTY("County"),
  CONSTITUENCY("Constituency"),
  COUNTY_WARD("County Ward"),
  POLLING_STATION("Polling Station");
  private String desc;

  private ElectiveRegionType(String desc) {
    this.desc = desc;
  }

  public static ElectiveRegionType valueOf(int ordinal) {
    for (ElectiveRegionType ert : values()) {
      if (ert.ordinal() == ordinal) {
        return ert;
      }
    }
    return null;
  }

  public String getPersistentTableName() {
    return toString().trim().replaceAll(" ", "").toUpperCase();
  }

  public ElectiveRegionType getChildElectiveRegionType() {
    return valueOf(ordinal() + 1);
  }

  public String getChildPersistentTableName() {
    return getChildElectiveRegionType().getPersistentTableName();
  }

  @Override
  public String toString() {
    return desc;
  }
}
