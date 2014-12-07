/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anosym.elexar.converter;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.utilities.Utility;
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
@FacesConverter("specialElectiveRegionConverter")
public class ElectiveRegionConverter implements Converter {

  private static final Map<String, ElectiveRegion> MAP = new HashMap<String, ElectiveRegion>();

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    if (Utility.isNullOrEmpty(value)) {
      return null;
    }
    return MAP.get(value.trim());
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value instanceof ElectiveRegion) {
      ElectiveRegion electiveRegion = (ElectiveRegion) value;
      String name = electiveRegion.getRegionName() + "(" + electiveRegion.getElectiveRegionType() + ")";
      MAP.put(name, electiveRegion);
      return name;
    }
    return null;
  }
}
