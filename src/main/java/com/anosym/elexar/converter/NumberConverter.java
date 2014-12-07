/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.converter;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author marembo
 */
@FacesConverter("numberConverter")
public class NumberConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    if (value != null) {
      return Long.parseLong(value.replaceAll(",", ""));
    }
    return null;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    List<Character> ch = new ArrayList<Character>();
    for (char c : value.toString().trim().toCharArray()) {
      ch.add(c);
    }
    for (int i = ch.size() - 1, j = 1; i >= 0; i--, j++) {
      if (j % 3 == 0 && j != 1 && i != 0) {
        ch.add(i, ',');
      }
    }
    char[] cc = new char[ch.size()];
    for (int i = 0; i < ch.size(); i++) {
      cc[i] = ch.get(i);
    }
    return new String(cc);
  }
}
