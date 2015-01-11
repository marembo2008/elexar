package com.anosym.elexar.facade;

import com.anosym.elexar.ElectiveRegion;
import com.anosym.elexar.ElectiveRegionBoundary;
import com.anosym.elexar.controller.ElexarController;
import com.anosym.jflemax.geometry.Coordinate;
import com.anosym.jflemax.geometry.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class ElectiveRegionBoundaryFacade extends AbstractFacade<ElectiveRegionBoundary> {

    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;
    @EJB
    private ElectiveRegionFacade electiveRegionFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ElectiveRegionBoundaryFacade() {
        super(ElectiveRegionBoundary.class);
    }

    /**
     * Loads the elective region with the coordinates data decoded.
     *
     * @param electiveRegion
     * @return
     */
    public ElectiveRegionBoundary findElectiveRegionBoundary(ElectiveRegion electiveRegion) {
        //we use native sql
        String sql = "SELECT "
                + "boundaryId, AsText(centreCoordinate), AsText(regionBoundaries) "
                + "FROM ElectiveRegionBoundary WHERE electiveRegion_regionId = " + electiveRegion.getRegionId();
        try {
            Object[] data = (Object[]) getEntityManager().createNativeQuery(sql).getSingleResult();
            if (data != null) {
                return decodeElectiveRegionBoundary(data, electiveRegion);
            }
        } catch (Exception e) {
            ElexarController.logError(e);
        }
        return null;
    }

    public List<ElectiveRegionBoundary> getElectiveRegionBoundaries(ElectiveRegion electiveRegion) {
        try {
            String tableName = electiveRegion.getElectiveRegionType().getChildPersistentEntityName();
            String sql = "SELECT "
                    + "boundaryId, AsText(centreCoordinate),AsText(regionBoundaries), electiveRegion_regionId "
                    + "FROM ElectiveRegionBoundary AS B JOIN " + tableName + " AS E ON B.electiveRegion_regionId = E.regionId WHERE E.parentElectiveRegion_regionId = " + electiveRegion
                    .getRegionId();
            List<Object[]> list = getEntityManager()
                    .createNativeQuery(sql)
                    .getResultList();
            if (list != null && !list.isEmpty()) {
                List<ElectiveRegionBoundary> electiveRegionBoundaries = new ArrayList<>();
                for (Object[] data : list) {
                    Object id = data[3];
                    ElectiveRegion region = electiveRegionFacade.find(id);
                    ElectiveRegionBoundary er = decodeElectiveRegionBoundary(data, region);
                    electiveRegionBoundaries.add(er);
                }
                return electiveRegionBoundaries;
            }
        } catch (Exception e) {
            ElexarController.logError(e);
        }
        return Collections.EMPTY_LIST;
    }

    private ElectiveRegionBoundary decodeElectiveRegionBoundary(Object[] data, ElectiveRegion electiveRegion) {
        ElectiveRegionBoundary electiveRegionBoundary = new ElectiveRegionBoundary(electiveRegion);
        if (data[0] != null) {
            electiveRegionBoundary.setBoundaryId(Long.parseLong(data[0].toString()));
        }
        if (data[1] != null) {
            electiveRegionBoundary.setCentreCoordinate(Coordinate.fromPointString(data[1].toString()));
        }
        if (data[2] != null) {
            electiveRegionBoundary.setRegionBoundaries(Polygon.fromPolygonString(data[2].toString()));
        }
        return electiveRegionBoundary;
    }
}
