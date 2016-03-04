/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.Tarea;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class TareaFacade extends AbstractFacade<Tarea> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TareaFacade() {
        super(Tarea.class);
    }
    
     public List<Tarea> findByEtapa(Etapa t){
        return em.createQuery("select t from Tarea t where t.etapaid.id= "+t.getId(), Tarea.class).getResultList();
    }
    
    public List<Tarea> buscarTareasEtapa(int etapaId){
        
        try {
            return em.createQuery("select t from Tarea t join t.etapaid e where e.id = :id ", Tarea.class).setParameter("id", etapaId).getResultList();
        } catch (Exception e) { 
            System.out.println("No se pudo realizar la consulta buscarTareasEtapa" + e);
            return new ArrayList<Tarea>();
        }
    }
    
    public List<Tarea> buscarTareasProyecto(int proyectoId){
        try{
            List<Tarea> tareas = em.createQuery("SELECT t FROM Tarea t JOIN t.etapaid e JOIN e.proyectoid p WHERE p.id = :proyectoid", Tarea.class).setParameter("proyectoid", proyectoId).getResultList();

            if(null == tareas){
                tareas = new ArrayList<Tarea>();
            } else{
                System.out.println("TareaFacade - buscarTareasProyecto");
                for(Tarea t : tareas){
                    System.out.println("TAREA: " + t.getTarea());
                }
            }

            return tareas;
        } catch(Exception e){
            System.out.println("No se pudo realizar la consulta TareaFacade buscarTareasProyecto");
            e.printStackTrace();
            return new ArrayList<Tarea>();
        }
    }
    
}
