package com.anosym.elexar.mapperdomain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author marembo
 */
@Entity
@Table(name = "polling")
@NamedQueries({
    @NamedQuery(name = "MappedPolling.findWardPollingStations", query = "SELECT p FROM MappedPolling p WHERE p.ward_id = :ward_id")
})
public class MappedPolling implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private int id;
    private int ward_id;
    private String polling_name;
    private float longitude;
    private float latitude;

    public int getId() {
        return id;
    }

    public int getWard_id() {
        return ward_id;
    }

    public String getPolling_name() {
        return polling_name;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

}
