/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar;

import com.anosym.elexar.util.ElectiveSeat;
import com.anosym.utilities.IdGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author marembo
 */
@Entity
public class ManualEntryElectionResult extends ElectionResult {

  private static final long serialVersionUID = IdGenerator.serialVersionUID(ManualEntryElectionResult.class);
  @Column(length = 7000, nullable = false)
  private String description;

  public ManualEntryElectionResult(ElectiveRegion electiveRegion, long votesDisputed, long votesRejected) {
    super(electiveRegion, ElectiveSeat.PRESIDENTAIL, votesDisputed, votesRejected);
  }

  public ManualEntryElectionResult(ElectiveRegion electiveRegion, ElectiveSeat electiveSeat, long votesDisputed, long votesRejected) {
    super(electiveRegion, electiveSeat, votesDisputed, votesRejected);
  }

  public ManualEntryElectionResult() {
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
