/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Archivorendicion;
import ar.edu.undec.nortia.model.Solicitud;
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
public class ArchivorendicionFacade extends AbstractFacade<Archivorendicion> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArchivorendicionFacade() {
        super(Archivorendicion.class);
    }
    
    public List<Archivorendicion> buscarPorRendicion(int rendicionId){
        
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.rendicionid.id = :rendicionId)", Archivorendicion.class);
        consulta.setParameter("rendicionId", rendicionId);
        return consulta.getResultList();
        
    }
    
    public Archivorendicion buscarUnaPorRendicion(int rendicionId){
        
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.rendicionid.id = :rendicionId)", Archivorendicion.class);
        consulta.setParameter("rendicionId", rendicionId);
        return (Archivorendicion)consulta.getSingleResult();
        
    }
    
}
