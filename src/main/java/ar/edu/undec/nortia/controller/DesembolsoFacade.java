/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Desembolso;

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
public class DesembolsoFacade extends AbstractFacade<Desembolso> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesembolsoFacade() {
        super(Desembolso.class);
    }
    
    public List<Desembolso> obtenerPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT d FROM Desembolso d WHERE d.proyectoid.id = :proyectoid");
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }

    public float obtenerImporteUltimoDesembolsoProyecto(int proyectoId){

        List<Desembolso> listaDesembolsos;

        try{
            Query consulta = em.createQuery("SELECT d FROM Desembolso d WHERE d.proyectoid.id = :proyectoid ORDER BY d.fechadesembolso DESC");
            consulta.setParameter("proyectoid", proyectoId);
            listaDesembolsos = consulta.getResultList();

            // si la lista de desembolsos está vacía o nula, se devuelve cero
            if(null == listaDesembolsos || listaDesembolsos.isEmpty()){
                return 0.0f;
            } else{
                return listaDesembolsos.get(0).getMonto().floatValue();
            }
        } catch(Exception e){
            System.out.println("Error en la consulta >> DesembolsoFacade >> obtenerImporteUltimoDesembolsoProyecto");
            return 0.0f;
        }


    }
    
    
}
