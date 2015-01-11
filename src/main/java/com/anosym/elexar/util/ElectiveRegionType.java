package com.anosym.elexar.util;

/**
 *
 * @author marembo
 */
public enum ElectiveRegionType {

    COUNTRY("Country", "Country"),
    COUNTY("County", "County"),
    CONSTITUENCY("Constituency", "Constituency"),
    COUNTY_WARD("County Ward", "CountyWard"),
    POLLING_STATION("Polling Station", "PollingStation");

    private final String desc;
    private final String entityName;

    private ElectiveRegionType(String desc, String entityName) {
        this.desc = desc;
        this.entityName = entityName;
    }

    public static ElectiveRegionType valueOf(final int ordinal) {
        for (ElectiveRegionType ert : values()) {
            if (ert.ordinal() == ordinal) {
                return ert;
            }
        }
        return null;
    }

    public String getEntityName() {
        return entityName;
    }

    public ElectiveRegionType getChildElectiveRegionType() {
        return valueOf(ordinal() + 1);
    }

    public String getChildPersistentEntityName() {
        return getChildElectiveRegionType().getEntityName();
    }

    @Override
    public String toString() {
        return desc;
    }
}
