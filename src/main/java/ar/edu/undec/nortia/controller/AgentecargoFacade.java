/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Agentecargo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author gongono
 */
@Stateless
public class AgentecargoFacade extends AbstractFacade<Agentecargo>  {

     @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AgentecargoFacade() {
        super(Agentecargo.class);
    }
    
    public Agentecargo filtrolegajo(int nro_legajo){
        Agentecargo ac ;
        ac = new Agentecargo();
        try{
           //System.out.println("ffffffffffffffffffffffffffffffff"+em.createNativeQuery("select * from ap.agente where numerodocumento like '%"+documentooapellido+"%' or apellido like '%"+documentooapellido+"%'", Agente.class).getResultList().size()); 
            ac = em.createQuery("select ac from Agentecargo ac where ac.nroLegajo = "+nro_legajo, Agentecargo.class).getSingleResult(); 
            
            return ac;
        }catch(Exception e){
            
            return null;
        }
    }
}
