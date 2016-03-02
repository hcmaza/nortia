/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Proyecto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Hugo
 */
@Stateless
public class ProyectoFacade extends AbstractFacade<Proyecto> {
    @PersistenceContext(unitName = "NORTIAPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProyectoFacade() {
        super(Proyecto.class);
    }
    
    public List<Proyecto> buscarProyectoAgente(int agenteid){
        
        List<Proyecto> proyecto;
       try{
           return em.createQuery("select p from Proyecto p join p.agenteid a where a.id = :id ", Proyecto.class).setParameter("id", agenteid).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
    }
    
    public List<Proyecto> buscarProyectoEstado(int estado){
        
        List<Proyecto> proyecto;
       try{
           return em.createQuery("select p from Proyecto p join p.agenteid a where p.estadoproyectoid.id = :estado", Proyecto.class).setParameter("estado", estado).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    }
    
    public List<Proyecto> buscarEvaluarIdeaProyectoEstado(){
        
        List<Proyecto> proyectosaevaluar;
       try{
           proyectosaevaluar = em.createQuery("select p from Proyecto p join p.agenteid a where p.estadoproyectoid.id = 1 or p.estadoproyectoid.id= 7", Proyecto.class).getResultList();
           for(Proyecto p : proyectosaevaluar){
               System.out.println("-----------------"+p.getEstadoproyectoid().getEstado());
           }
           return proyectosaevaluar;
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    }
    
    /**
     * Obtiene proyectos por condicion de que sean proyectos (no IP) y por agente.
     * 
     * @param isproyecto
     * @param agenteid
     * @return 
     */
    public List<Proyecto> buscarProyectosAgente(boolean isproyecto,int agenteid){
        
        String consulta="select p from Proyecto p join p.agenteid a where p.agenteid.id= "+agenteid;
        if(isproyecto){
             consulta+=" and p.estadoproyectoid.id in (2,5,6,8,12) ";
         }else{
            consulta+=" and p.estadoproyectoid.id in (1,3,4,7) ";
        }
        List<Proyecto> proyecto;
        
       try{
           System.out.println("agente "+agenteid+"----------------------"+em.createQuery(consulta , Proyecto.class).getResultList().size());
           return em.createQuery(consulta , Proyecto.class).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
    } 
    
    public long buscarProyectosEvaluar(){
        
        List<Proyecto> proyecto;
       try{
           return (long)em.createQuery("select count(p) from Proyecto p where p.estadoproyectoid.id = 1 and p.estadoproyectoid.id = 7 ", Long.class).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return 0;
       }
    } 
    
    public long buscarIdeasProyectosEvaluar(){
        
        List<Proyecto> proyecto;
       try{
           System.out.println("cantidad evaluar ------");
           return (long)em.createQuery("select count(p) from Proyecto p where p.estadoproyectoid.id = 1 or p.estadoproyectoid.id = 7 ", Long.class).getSingleResult();
           
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return 0;
       }
    }
    
    public List<Proyecto> buscarProyectosaEvaluar(){
       try{
           return em.createQuery("select p from Proyecto p where p.estadoproyectoid.id = 6 and p.estadoproyectoid.id = 11 ", Proyecto.class).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
         
    }
    
    /**
     * Obtiene una lista de todos los proyectos
     * 
     * @return Todos los Proyectos
     */
    public List<Proyecto> buscarTodos(){
        try{
            return em.createQuery("SELECT p FROM Proyecto p").getResultList();
        }catch(Exception e){
            System.out.println("No se pudo realizar la consulta");
            e.printStackTrace();
            return new ArrayList<Proyecto>();
        }
    }
    

    public List<Proyecto> buscarProyectoPorConvocatoria(int idConvocatoria){        
       try{
           return em.createQuery("select p from Proyecto p where p.convocatoriaid.id = :idConvocatoria and p.estadoproyectoid.id=8", Proyecto.class).setParameter("idConvocatoria", idConvocatoria).getResultList();
       }
       catch(Exception e){
           System.out.println("No se pudo realizar la consulta "+e);
           return null;
       }  
    }

    
    public void acumularCantidadReintegrosPorProyecto(int proyectoId){
        Query consulta = em.createQuery("UPDATE Proyecto SET cantidadreintegros = cantidadreintegros + 1 WHERE id = :proyectoId" );
        consulta.setParameter( "proyectoId", proyectoId );
        consulta.executeUpdate();
    }
    
    public int obtenerCantidadReintegrosPorProyecto(int proyectoId){
        return ((Number)em.createQuery("SELECT p.cantidadreintegros FROM Proyecto p WHERE p.id = :proyectoId")
                .setParameter("proyectoId", proyectoId)
                .getSingleResult())
                .intValue();
    }
    
}
