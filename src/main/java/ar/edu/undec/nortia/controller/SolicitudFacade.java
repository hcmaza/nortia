/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

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
public class SolicitudFacade extends AbstractFacade<Solicitud> {

    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudFacade() {
        super(Solicitud.class);
    }

    /**
     * Obtiene una lista de solicitud por proyecto (solicitud no tiene relacion con proyecto, solo a traves de PresupuestoTarea)
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerPorProyecto(int proyectoid) {
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    /**
     * Obtiene una lista de solicitudes con estado "Iniciada" para un proyecto determinado
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerIniciadasPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.estadosolicitudid.id = 1 AND s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    /**
     * Obtiene una lista de solicitudes con estado "Aprobada" para un proyecto determinado
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerAprobadasPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.estadosolicitudid.id = 2 AND s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    /**
     * Obtiene una lista de solicitudes con estado "Ejecucion" para un proyecto determinado
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerEjecucionPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.estadosolicitudid.id = 4 AND s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    /**
     * Obtiene una lista de solicitudes con estado "Rendida" para un proyecto determinado
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerRendidasPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.estadosolicitudid.id = 5 AND s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    /**
     * Obtiene una lista de solicitudes con estado "Ejecucion" para un proyecto determinado
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerRendicionAEvaluarPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.estadosolicitudid.id = 6 AND s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    /**
     * Obtiene una solicitud de un presupuestotarea determinado
     * 
     * @param presupuestotareaid
     * @return 
     */
    public Solicitud obtenerPorPresupuestoTarea(int presupuestotareaid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.presupuestotarea.id = :presupuestotareaid", Solicitud.class);
        consulta.setParameter("presupuestotareaid", presupuestotareaid);
        return (Solicitud) consulta.getSingleResult();
    }
    
    /**
    * Obtiene una lista de Solicitudes de un item
    *
     * @param presupuestotareaid
     * @return 
    */
    public List<Solicitud> obtenerPorPresupuestoTareas(int presupuestotareaid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.presupuestotarea.id = :presupuestotareaid", Solicitud.class);
        consulta.setParameter("presupuestotareaid", presupuestotareaid);
        return consulta.getResultList();
    }
    
    /**
     * Ontiene una solicitud de una rendicion determinada
     * 
     * @param rendicionid
     * @return 
     */
    public Solicitud obtenerPorRendicion(int rendicionid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.rendicionid.id = :rendicionid", Solicitud.class);
        consulta.setParameter("rendicionid", rendicionid);
        return (Solicitud) consulta.getSingleResult();
    }
}
