/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Tiposolicitud;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


/**
 *
 * @author mpaez
 */
@Stateless
public class TiposolicitudFacade extends AbstractFacade<Tiposolicitud> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TiposolicitudFacade() {
        super(Tiposolicitud.class);
    }
    
    public Tiposolicitud findWithTiposolicitud(String tiposolicitud){
        try{
           Query consulta = em.createQuery("select ts from Tiposolicitud ts where ts.tiposolicitud like :tiposolicitud", Tiposolicitud.class).setParameter("tiposolicitud", tiposolicitud);
           return (Tiposolicitud) consulta.getSingleResult();
        }catch(Exception e){
            System.out.println("problema en la consulta en Tipo de Solicitud "+ e);
            return null;
        }
    }
   

}
