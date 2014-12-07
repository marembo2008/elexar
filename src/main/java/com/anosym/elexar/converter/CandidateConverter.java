/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.converter;

import com.anosym.elexar.Candidate;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author marembo
 */
@FacesConverter("candidateConverter")
public class CandidateConverter implements Converter {

  private static final Map<String, Candidate> map = new HashMap<String, Candidate>();

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return map.get(value);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value instanceof Candidate) {
      Candidate c = (Candidate) value;
      map.put(c.toString(), c);
      return c.toString();
    }
    return null;
  }
}
