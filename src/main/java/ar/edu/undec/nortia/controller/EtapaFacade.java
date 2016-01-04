/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.Proyecto;
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
public class EtapaFacade extends AbstractFacade<Etapa> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EtapaFacade() {
        super(Etapa.class);
    }
    
    public List<Etapa> findByProyecto(Proyecto p){
        return em.createQuery("select e from Etapa e where e.proyectoid.id= "+p.getId()+" order by e.id", Etapa.class).getResultList();
    }
    
    public List<Etapa> buscarEtapasProyecto(int proyectoid){
        
        try {
            return em.createQuery("select e from Etapa e join e.proyectoid p where p.id = :id ", Etapa.class).setParameter("id", proyectoid).getResultList();
        } catch (Exception e) {
            System.out.println("No se pudo realizar la consulta" + e);
            return new ArrayList<Etapa>();
        }
    }
    
        
}
