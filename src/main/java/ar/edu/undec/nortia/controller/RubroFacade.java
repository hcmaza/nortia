/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Rubro;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class RubroFacade extends AbstractFacade<Rubro> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RubroFacade() {
        super(Rubro.class);
    }
    
    public Rubro findbyId(int rubroid){
        
        
       try{
           return em.createQuery("select r from Rubro r where r.id = :id ", Rubro.class).setParameter("id", rubroid).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    }
    
    public List<Rubro> findwithoutRRHHCONSULTOR(){
        
        
       try{
           return em.createQuery("select r from Rubro r where r.id != 4 and r.id != 5", Rubro.class).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    }
}
