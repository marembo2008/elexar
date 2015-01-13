package com.anosym.elexar.mapperdomain.facade;

import com.anosym.elexar.mapperdomain.MappedPolling;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class MappedPollingFacade extends AbstractFacade<MappedPolling> {
    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MappedPollingFacade() {
        super(MappedPolling.class);
    }

}
