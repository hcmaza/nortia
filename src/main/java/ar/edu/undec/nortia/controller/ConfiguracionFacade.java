/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Configuracion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class ConfiguracionFacade extends AbstractFacade<Configuracion> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracionFacade() {
        super(Configuracion.class);
    }
    
    public Configuracion findAtributo(String atributo){
        try{
           return em.createQuery("select c from Configuracion c where c.clave like '"+atributo.trim()+"'", Configuracion.class).getSingleResult();
        }catch(Exception e){
            
            System.out.println("Error en la Consulta de la Tabla Configuracion"+e);  
            return null;
        }
    }
    
}
