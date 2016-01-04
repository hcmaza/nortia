/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Rendicion;
import ar.edu.undec.nortia.model.Solicitudacta;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mpaez
 */
@Stateless
public class RendicionFacade extends AbstractFacade<Rendicion> {

    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RendicionFacade() {
        super(Rendicion.class);
    }

    public List<Rendicion> obtenerPorProyecto(int proyectoid) {
        try {
            Query consulta = em.createQuery("SELECT r FROM Rendicion r WHERE r.id IN (SELECT s.rendicionid.id FROM Solicitud s WHERE s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid)", Rendicion.class);
            consulta.setParameter("proyectoid", proyectoid);
            return consulta.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RendicionFacade - obtenerPorProyecto");
            return new ArrayList<Rendicion>();
        }
    }

}
