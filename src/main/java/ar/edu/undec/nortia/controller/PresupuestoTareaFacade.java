/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.PresupuestoTarea;
import ar.edu.undec.nortia.model.Tarea;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author gongono
 */
@Stateless
public class PresupuestoTareaFacade extends AbstractFacade<PresupuestoTarea> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PresupuestoTareaFacade() {
        super(PresupuestoTarea.class);
    }
    
     public List<PresupuestoTarea> findByTarea(Tarea t){
        //System.out.println("iiiiiiiiiiiiiiiiiiii"+pr.getId());
        return em.createQuery("select pt from PresupuestoTarea pt where pt.tarea.id="+t.getId(), PresupuestoTarea.class).getResultList();
    }
     
    public List<PresupuestoTarea> obtenerPorProyecto(int proyectoid){
        try{
            return em.createQuery("select pt from PresupuestoTarea pt where pt.tarea.etapaid.proyectoid.id =" + proyectoid, PresupuestoTarea.class).getResultList();
        }catch(Exception e){
            e.printStackTrace();
            return new ArrayList<PresupuestoTarea>();
        }
    }
    
    public float obtenerTotalPorProyecto(int proyectoid){
        
        BigDecimal bigDecimal;
        Float total = 0.0f;
        
        try{
            bigDecimal = em.createQuery("select SUM(pt.total) from PresupuestoTarea pt where pt.tarea.etapaid.proyectoid.id =" + proyectoid, BigDecimal.class).getSingleResult();
            total = bigDecimal.floatValue();
            
        }catch(Exception e){
            /*e.printStackTrace();*/
            return 0f;
        }
        
        return total;
    }
    
    public void removeWithId(int i){
        try{
            em.createQuery("delete from PresupuestoTarea pt where pt.id="+i).executeUpdate();
        }catch(Exception e){
            System.out.println("Error en presupuestotarea "+e);
        }
    }
    
}
