/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Evaluacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class EvaluacionFacade extends AbstractFacade<Evaluacion> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EvaluacionFacade() {
        super(Evaluacion.class);
    }
    
    public Evaluacion obtenerEvaluacionPorProyecto(int proyectoId){
        //return em.createQuery("select p from Proyecto p join p.agenteid a where a.id = :id ", Proyecto.class).setParameter("id", agenteid).getResultList();
        return em.createQuery("SELECT e FROM Evaluacion e join e.proyectoid p WHERE p.id = :id", Evaluacion.class).setParameter("id", proyectoId).getSingleResult();
    }
    
    
}
