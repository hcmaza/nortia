/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Estadosolicitud;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mpaez
 */
@Stateless
public class EstadosolicitudFacade extends AbstractFacade<Estadosolicitud> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstadosolicitudFacade() {
        super(Estadosolicitud.class);
    }
    
    public Estadosolicitud buscarPorNombre(String nombre){
        try{
            Query consulta = em.createQuery("SELECT e FROM Estadosolicitud e WHERE e.estadosolicitud = :nombre", Estadosolicitud.class);
            consulta.setParameter("nombre", nombre);
            return (Estadosolicitud)consulta.getSingleResult();
        } catch(Exception e){
            e.printStackTrace();
            return new Estadosolicitud();
        }
        
        
    }
    
}
