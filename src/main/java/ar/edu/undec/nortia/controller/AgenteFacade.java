
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller;

import ar.edu.undec.nortia.model.Agente;
import ar.edu.undec.nortia.model.DatosMapuche;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class AgenteFacade extends AbstractFacade<Agente> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AgenteFacade() {
        super(Agente.class);
    }
    
    public Agente agentedocumento(String documento){
        
        try{
           return em.createQuery("select a from Agente a where a.numerodocumento like '"+documento+"%'", Agente.class).getResultList().get(0); 
        }catch(Exception e){
            return null;
        }
        
    }
    
    public List<Agente> filtroDocumentooApellido(String documentooapellido){
        try{
           System.out.println("ffffffffffffffffffffffffffffffff"+em.createNativeQuery("select * from ap.agente where numerodocumento like '%"+documentooapellido+"%' or apellido like '%"+documentooapellido+"%'", Agente.class).getResultList().size()); 
           return em.createQuery("select a from Agente a where a.numerodocumento like '"+documentooapellido+"%' or a.apellido like '%"+documentooapellido+"%'", Agente.class).getResultList(); 
        }catch(Exception e){
            return null;
        }
    }
    public List<Agente> filtroTipoUsuario(int tipo){
        try{
            return em.createQuery("select a from Agente a where a.usuarioid.usuariorol.rolid = '"+tipo, Agente.class).getResultList(); 
        }catch(Exception e){
            return null;
    }
    }
    
//    public List<DatosMapuche> findAllUNDEC(){
//        System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLl");
//        Calendar fecha = new GregorianCalendar();
//        int año = fecha.get(Calendar.YEAR);
//        int mes = fecha.get(Calendar.MONTH);
//        int dia = fecha.get(Calendar.DATE);
//        System.out.println("select dh01.nro_legaj," +
//                 "     desc_appat," +
//                 "     desc_apmat," +
//                 "     desc_nombr," +
//                 "     tipo_docum," +
//                 "     nro_docum," +
//                 "     nro_cuil1," +
//                 "     nro_cuil," +
//                 "     nro_cuil2," +
//                 "      tipo_sexo," +
//                 "     fec_nacim," +
//                 "      pcia_nacim," +
//                 "     pais_nacim," +
//                 "sum(cant_horas)  " +
//                 "	from mapuche.dh03 " +
//                 "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj) " +
//                 "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ) " +
//                 "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic) " +
//"	where dh03.vig_caano > "+año+"and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' group by dh01.nro_legaj");
//        try{
//           return emmapuche.createNativeQuery("select dh01.nro_legaj,\n" +
//                 "     desc_appat,\n" +
//                 "     desc_apmat,\n" +
//                 "     desc_nombr,\n" +
//                 "     tipo_docum,\n" +
//                 "     nro_docum,\n" +
//                 "     nro_cuil1,\n" +
//                 "     nro_cuil,\n" +
//                 "     nro_cuil2,\n" +
//                 "      tipo_sexo,\n" +
//                 "     fec_nacim,\n" +
//                 "      pcia_nacim,\n" +
//                 "     pais_nacim,\n" +
//                 "sum(cant_horas) as cant_horas \n " +
//                 "	from mapuche.dh03 \n" +
//                 "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj)\n" +
//                 "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ)\n" +
//                 "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic)\n" +
//"	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' group by dh01.nro_legaj ",DatosMapuche.class).getResultList();
//        }catch(Exception e){
//           System.err.println(e);
//           return null;
//        }
//        
//        }
    public Agente filtroDocumentooCuil(String documentoocuil){
        Agente a = new Agente();
        try{
           //System.out.println("ffffffffffffffffffffffffffffffff"+em.createNativeQuery("select * from ap.agente where numerodocumento like '%"+documentooapellido+"%' or apellido like '%"+documentooapellido+"%'", Agente.class).getResultList().size()); 
            a = em.createQuery("select a from Agente a where a.numerodocumento like '"+documentoocuil+"%' or a.cuil like '"+documentoocuil+"%'", Agente.class).getSingleResult(); 
            System.out.println("Agente try "+a.getApellido());
            return a;
        }catch(Exception e){
            System.out.println("Agente cacth "+a.getApellido());
            return null;
        }
    }
    
    public List<Agente> agentesSinUsuario(){
        try{
            return em.createQuery("select a from Agente a where a.usuarioid is null", Agente.class).getResultList();
        } catch (Exception e){
            return null;
        }
    }
    
}