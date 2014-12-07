/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.jpaconverter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author marembo
 */
public class CoordinateConverter implements Converter {

  @Override
  public Object convertObjectValueToDataValue(Object o, Session sn) {
    return null;
  }

  @Override
  public Object convertDataValueToObjectValue(Object o, Session sn) {
    return null;
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public void initialize(DatabaseMapping dm, Session sn) {
  }
}
