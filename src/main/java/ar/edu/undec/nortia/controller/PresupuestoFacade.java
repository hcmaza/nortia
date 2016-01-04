/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Presupuesto;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class PresupuestoFacade extends AbstractFacade<Presupuesto> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PresupuestoFacade() {
        super(Presupuesto.class);
    }
    public Presupuesto findporProyecto(int id){
        return em.createQuery("select p from Presupuesto p where p.proyectoid.id=:id", Presupuesto.class).setParameter("id",id).getSingleResult();
    }
}
