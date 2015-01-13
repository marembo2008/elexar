package com.anosym.elexar.mapperdomain.facade;

import com.anosym.elexar.mapperdomain.MappedCounty;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class MappedCountyFacade extends AbstractFacade<MappedCounty> {
    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MappedCountyFacade() {
        super(MappedCounty.class);
    }

}
