/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public enum ElectionMalpracticeType {

  MISSING_NAME_FROM_REGISTER(3, "Name missing from register"),
  BVR_SYSTEM_NOT_WORKING(4, "BVR System not working"),
  COMPUTER_FAILURE(9, "Computer Failure"),
  TAMPERING_WITH_RESULTS(43, "Tampering with results"),
  STATION_CLOSED_LATE(1, "Station Closed Late"),
  BALLOT_BOXES_NOT_SEALED(28, "Ballot boxes not sealed"),
  VOTERS_VOTING_MORE_THAN_ONCE(22, "Voters voting more than once"),
  INELLIGIBALE_VOTERS_ALLOWED_TO_VOTE(25, "Inelligible voters allowed to vote");
  private int code;
  private String description;

  private ElectionMalpracticeType(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public int getCode() {
    return code;
  }

  public static ElectionMalpracticeType valueOf(int code) {
    for (ElectionMalpracticeType emt : values()) {
      if (emt.code == code) {
        return emt;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return description;
  }
}
