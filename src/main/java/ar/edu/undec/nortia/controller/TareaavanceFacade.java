/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Tarea;
import ar.edu.undec.nortia.model.Tareaavance;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class TareaavanceFacade extends AbstractFacade<Tareaavance> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TareaavanceFacade() {
        super(Tareaavance.class);
    }
    
    public Tareaavance findLastDate(Tarea ta){
        Date fecha = new Date();
        
       try{
           fecha = em.createQuery("select max(t.fecha) from Tareaavance t where t.tareaid.id = " +ta.getId()+" group by tareaid", Date.class).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta findlastdate"+e);
           return null;
       }
        
       try{
           return em.createQuery("select t from Tareaavance t where t.fecha = :fec", Tareaavance.class).setParameter("fec", fecha).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
         
    }
    
}
