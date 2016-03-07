/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Archivorendicion;
import java.util.Date;
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
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArchivorendicionFacade() {
        super(Archivorendicion.class);
    }
    
    public List<Archivorendicion> buscarPorRendicion(int rendicionId){
        
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.rendicionid.id = :rendicionId", Archivorendicion.class);
        consulta.setParameter("rendicionId", rendicionId);
        return consulta.getResultList();
        
    }
    
        public List<Archivorendicion> buscarPorRendicionYEjecutados(int rendicionId){
        
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.rendicionid.id = :rendicionId", Archivorendicion.class);
        consulta.setParameter("rendicionId", rendicionId);
        return consulta.getResultList();
        
    }
    
    public Archivorendicion buscarUnaPorRendicion(int rendicionId){
        
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.rendicionid.id = :rendicionId", Archivorendicion.class);
        consulta.setParameter("rendicionId", rendicionId);
        return (Archivorendicion)consulta.getSingleResult();
    }
    
    public List<Archivorendicion> buscarPorFechaDesdeHasta(Date desde, Date hasta){
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.fechafactura BETWEEN :desde AND :hasta)", Archivorendicion.class);
        consulta.setParameter("desde", desde);
        consulta.setParameter("hasta", hasta);
        return consulta.getResultList();
    }

    public List<Archivorendicion> buscarEntreFechasConEstado(Date desde, Date hasta){
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.estado IS NOT NULL AND ar.fechafactura BETWEEN :desde AND :hasta", Archivorendicion.class);
        consulta.setParameter("desde", desde);
        consulta.setParameter("hasta", hasta);
        return consulta.getResultList();
    }

    public List<Archivorendicion> buscarEntreFechasConEstadoSinRendicionExterna(Date desde, Date hasta){
        Query consulta = em.createQuery("SELECT ar FROM Archivorendicion ar WHERE ar.estado IS NOT NULL AND ar.rendicionexternaid IS NULL AND ar.fechafactura BETWEEN :desde AND :hasta", Archivorendicion.class);
        consulta.setParameter("desde", desde);
        consulta.setParameter("hasta", hasta);
        return consulta.getResultList();
    }

}
