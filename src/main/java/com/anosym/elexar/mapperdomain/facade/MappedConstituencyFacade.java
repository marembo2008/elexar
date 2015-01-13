package com.anosym.elexar.mapperdomain.facade;

import com.anosym.elexar.mapperdomain.MappedConstituency;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author marembo
 */
@Stateless
public class MappedConstituencyFacade extends AbstractFacade<MappedConstituency> {

    @PersistenceContext(unitName = "ElexarPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MappedConstituencyFacade() {
        super(MappedConstituency.class);
    }

}
