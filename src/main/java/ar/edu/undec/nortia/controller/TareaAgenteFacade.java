/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Agente;
import ar.edu.undec.nortia.model.Tarea;
import ar.edu.undec.nortia.model.TareaAgente;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class TareaAgenteFacade extends AbstractFacade<TareaAgente> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TareaAgenteFacade() {
        super(TareaAgente.class);
    }
    
     public List<TareaAgente> findByTareaAgente(Tarea t, Agente a){
        return em.createQuery("select ta from TareaAgente ta where ta.tareaid.id = "+t.getId()+" and ta.agenteid.id = "+a.getId(), TareaAgente.class).getResultList();
    }
    
      public List<TareaAgente> findByTareaAgente(Agente a){
        return em.createQuery("select ta from TareaAgente ta where ta.agenteid.id = "+a.getId(), TareaAgente.class).getResultList();
    }
     
    public void removeWithId(int i){
        try{
            em.createQuery("delete from TareaAgente ta where ta.id="+i).executeUpdate();
        }catch(Exception e){
            System.out.println("Error en tareaagente "+e);
        }
    }
}
