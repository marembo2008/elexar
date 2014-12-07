/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.converter;

import com.anosym.utilities.FormattedCalendar;
import java.util.Calendar;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author marembo
 */
@FacesConverter(value = "calendarConverter", forClass = Calendar.class)
public class CalendarConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }
    return FormattedCalendar.parseISODate(value);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || !(value instanceof Calendar)) {
      return null;
    }
    return FormattedCalendar.toISOString((Calendar) value);
  }
}
