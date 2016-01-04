/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Desembolso;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mpaez
 */
@Stateless
public class DesembolsoFacade extends AbstractFacade<Desembolso> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesembolsoFacade() {
        super(Desembolso.class);
    }
    
    public List<Desembolso> obtenerPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT d FROM Desembolso d WHERE d.proyectoid.id = :proyectoid");
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    
}
