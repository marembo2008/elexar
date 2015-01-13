package com.anosym.elexar.mapperdomain.facade;

import com.anosym.elexar.mapperdomain.MappedWard;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class MappedWardFacade extends AbstractFacade<MappedWard> {
    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MappedWardFacade() {
        super(MappedWard.class);
    }

}
