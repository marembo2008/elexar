package com.anosym.elexar.mapperdomain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author marembo
 */
@Entity
@Table(name = "constituency")
public class MappedConstituency implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    private String constituency_name;
    private String const_code;
    private String county_code;
    private String geometry;
    private float longitude;
    private float latitude;

    public Long getId() {
        return id;
    }

    public String getConstituency_name() {
        return constituency_name;
    }

    public String getConst_code() {
        return const_code;
    }

    public String getCounty_code() {
        return county_code;
    }

    public String getGeometry() {
        return geometry;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

}
