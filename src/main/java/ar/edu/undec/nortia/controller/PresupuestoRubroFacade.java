/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.PresupuestoRubro;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class PresupuestoRubroFacade extends AbstractFacade<PresupuestoRubro> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PresupuestoRubroFacade() {
        super(PresupuestoRubro.class);
    }
    
     public List<PresupuestoRubro> findporPresupuesto(int id){
        return em.createQuery("select pr from PresupuestoRubro pr where pr.presupuesto.id=:id", PresupuestoRubro.class).setParameter("id",id).getResultList();
    }
    
}
