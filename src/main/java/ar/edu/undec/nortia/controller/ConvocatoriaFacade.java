/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Convocatoria;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Hugo
 */
@Stateless
public class ConvocatoriaFacade extends AbstractFacade<Convocatoria> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConvocatoriaFacade() {
        super(Convocatoria.class);
    }
    
     public List<Convocatoria> findConvocatoriafinanciamiento(Integer tipoproyectoid,Integer tipofinanciamientoid){
        String queryStr = "Select c from Convocatoria c where c.tipofinanciamientoid.id = :tipofinanciamientoid ";
        TypedQuery<Convocatoria> query = getEntityManager().createQuery(queryStr, Convocatoria.class);
        query.setParameter("tipofinanciamientoid", tipofinanciamientoid);
            
      
        return query.getResultList();
    }
    
    /*
     * Método que busca las convocatorias abiertas (entre fecha de inicio y fecha de fin) en una fecha determinada.
     */
    public List<Convocatoria> findConvocatoriasEnFecha(Date fecha){
         
        TypedQuery query = this.em.createQuery("SELECT c FROM Convocatoria c WHERE :fecha BETWEEN c.fechainicio AND c.fechacierre", Convocatoria.class);
        query.setParameter("fecha", fecha);
        return query.getResultList();
    }
    
    public List<Convocatoria> findConvocatoriasPorTipoProyectoTipoFinanciamientoYFecha(int tipoproyectoid, int tipofinanciamiento, Date fecha){
        
        String query = 
                "SELECT c FROM Convocatoria c "
                + "WHERE (:fecha BETWEEN c.fechainicio AND c.fechacierre) "
                + "AND c.tipofinanciamientoid.id = :tipofinanciamiento "
                + "AND c.tipoproyectoid.id = :tipoproyecto";
        
        TypedQuery consulta = this.em.createQuery(query, Convocatoria.class);
        consulta.setParameter("fecha", fecha);
        consulta.setParameter("tipofinanciamiento", tipofinanciamiento);
        consulta.setParameter("tipoproyecto", tipoproyectoid);
        
        return consulta.getResultList();
    }
    
}
