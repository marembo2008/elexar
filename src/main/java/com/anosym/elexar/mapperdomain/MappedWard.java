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
@Table(name = "ward")
@NamedQueries({
    @NamedQuery(name = "MappedWard.findConstituencyWards", query = "SELECT w FROM MappedWard w WHERE w.constituency_id = :constituency_id")
})
public class MappedWard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private int id;
    private int constituency_id;
    private String ward_name;

    public int getId() {
        return id;
    }

    public int getConstituency_id() {
        return constituency_id;
    }

    public String getWard_name() {
        return ward_name;
    }

}
