/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public enum FlaggedIssueType {

  /**
   * When the turnout is more than the registered voters.
   */
  OVER_VOTER_TURNOUT,
  /**
   * When there is less than half percent of total voters turnout by midday.
   */
  LESS_THAN_HALF_TURNOUT_BY_MIDDAY,
  /**
   * When there is non-linear form of voting on the turnout graph.
   */
  NON_LINEAR_TURNOUT
}
