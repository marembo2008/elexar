/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveRegionType;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author marembo
 */
@Entity
public class Constituency extends ElectiveRegion implements Serializable {

  public Constituency() {
    super(ElectiveRegionType.CONSTITUENCY);
  }

  public Constituency(String regionName, long registeredVoters) {
    super(regionName, registeredVoters, ElectiveRegionType.CONSTITUENCY);
  }
}
