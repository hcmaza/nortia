/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.model;

import ar.edu.undec.nortia.controller.util.Propiedades;
import ar.edu.undec.nortia.controller.view.util.ConnectJDBCPostgresql;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author mPY
 */
@Entity
@Table(name = "proyecto_agente", schema = "ap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProyectoAgente.findAll", query = "SELECT p FROM ProyectoAgente p"),
    @NamedQuery(name = "ProyectoAgente.findByAgenteid", query = "SELECT p FROM ProyectoAgente p WHERE p.proyectoAgentePK.agenteid = :agenteid"),
    @NamedQuery(name = "ProyectoAgente.findByProyectoid", query = "SELECT p FROM ProyectoAgente p WHERE p.proyectoAgentePK.proyectoid = :proyectoid"),
    @NamedQuery(name = "ProyectoAgente.findByFuncionproyecto", query = "SELECT p FROM ProyectoAgente p WHERE p.funcionproyecto = :funcionproyecto")})
public class ProyectoAgente implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProyectoAgentePK proyectoAgentePK;
    @Size(max = 35)
    @Column(name = "funcionproyecto")
    private String funcionproyecto;
    @JoinColumn(name = "proyectoid", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Proyecto proyecto;
    @JoinColumn(name = "agenteid", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Agente agente;
    @Column(name = "horasdisponibles")
    private Integer horasdisponibles;
    @Column(name = "horasdedicadas")
    private Integer horasdedicadas;
    @Column(name = "consultorexterno")
    private Boolean consultorexterno;
    @Transient
    private List<TareaAgente> tareasagentes = new ArrayList<TareaAgente>();
    @Column(name = "honorario")
    private BigDecimal honorario;
    
    
    public ProyectoAgente() {
    }

    public ProyectoAgente(ProyectoAgentePK proyectoAgentePK) {
        this.proyectoAgentePK = proyectoAgentePK;
    }

    public ProyectoAgente(int agenteid, int proyectoid) {
        this.proyectoAgentePK = new ProyectoAgentePK(agenteid, proyectoid);
    }

    public ProyectoAgentePK getProyectoAgentePK() {
        return proyectoAgentePK;
    }

    public void setProyectoAgentePK(ProyectoAgentePK proyectoAgentePK) {
        this.proyectoAgentePK = proyectoAgentePK;
    }

    public String getFuncionproyecto() {
        return funcionproyecto;
    }

    public void setFuncionproyecto(String funcionproyecto) {
        this.funcionproyecto = funcionproyecto;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Agente getAgente() {
        return agente;
    }

    public void setAgente(Agente agente) {
        this.agente = agente;
    }

    public Integer getHorasdisponibles() {
        return horasdisponibles;
    }

    public void setHorasdisponibles(Integer horasdisponibles) {
        this.horasdisponibles = horasdisponibles;
    }

    public Integer getHorasdedicadas() {
        return horasdedicadas;
    }

    public void setHorasdedicadas(Integer horasdedicadas) {
        this.horasdedicadas = horasdedicadas;
    }

    public Boolean getConsultorexterno() {
        return consultorexterno;
    }

    public void setConsultorexterno(Boolean consultorexterno) {
        this.consultorexterno = consultorexterno;
    }

    public List<TareaAgente> getTareasagentes() {
        return tareasagentes;
    }

    public void setTareasagentes(List<TareaAgente> tareasagentes) {
        this.tareasagentes = tareasagentes;
    }

    public BigDecimal getHonorario() {
        return honorario;
    }

    public void setHonorario(BigDecimal honorario) {
        this.honorario = honorario;
    }
    
    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (proyectoAgentePK != null ? proyectoAgentePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProyectoAgente)) {
            return false;
        }
        ProyectoAgente other = (ProyectoAgente) object;
        if ((this.proyectoAgentePK == null && other.proyectoAgentePK != null) || (this.proyectoAgentePK != null && !this.proyectoAgentePK.equals(other.proyectoAgentePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.ProyectoAgente[ proyectoAgentePK=" + proyectoAgentePK + " ]";
    }
    
    // metodos para hacer el resumen de etapas tareas por agente
    
    public int contarEtapas(){
        
        Integer etapaold= Integer.valueOf(0);
        String setapaold = null;
        int contador =0;
        for(TareaAgente ta:this.tareasagentes){
            
           
            if(!tareasagentes.isEmpty()){
                if(ta.getTareaid().getEtapaid().getEtapa().equals(setapaold)){

                }else{
                    contador++;
                    setapaold = ta.getTareaid().getEtapaid().getEtapa();
                    
                }
            }
        }
        return contador;
    }
    
    public int contarTareas(){
        Integer tareaold= Integer.valueOf(0);
        String stareaold = null;
        int contador =0;
        for(TareaAgente ta:this.tareasagentes){
            if(!tareasagentes.isEmpty()){
                if(ta.getTareaid().getTarea().equals(stareaold)){

                }else{
                    contador++;
                    tareaold = ta.getTareaid().getId();
                }
            }
        }
        return contador;
    }
    
    public int contarDiasTareas(){
        Integer tareaold= Integer.valueOf(0);
        String setapaold = null;
        int contador =0;
        for(TareaAgente ta:this.tareasagentes){
            if(!tareasagentes.isEmpty()){
                contador+= ta.getTareaid().getDias();
            }
        }
        return contador;
    }
    
    public int contarHoras(){
        Integer horas= Integer.valueOf(0);
        Agente sagenteold = null;
        
        for(TareaAgente ta:this.tareasagentes){
            
            if(!tareasagentes.isEmpty()){
                System.out.println("Agente  --"+ta.getAgenteid());
                if(ta.getAgenteid().equals(sagenteold)){
                    System.out.println("Agente horas"+ta.getHorasdedicadas());
                    if(this.consultorexterno==null | !this.consultorexterno){
                        System.out.println("Agente horas 2"+ta.getHorasdedicadas());
                        if(ta.getHorasdedicadas()==null){
                            horas+= 0;
                        }else{
                            horas +=  ta.getHorasdedicadas();
                        }
                    }
                }else{
                     if(ta.getHorasdedicadas()==null){
                            horas+= 0;
                        }else{
                            horas +=  ta.getHorasdedicadas();
                        }
                    sagenteold = ta.getAgenteid();
                }
            }
        }
        return horas;
    }
    
    public int ContarHorasDedicacionCargoLegajo(){
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DATE);
        int resultado = 0; 
        ConnectJDBCPostgresql connectjdbcpostgresql = new ConnectJDBCPostgresql();
        try {
            Propiedades propiedades = new Propiedades();
            connectjdbcpostgresql.connect(propiedades.getHostMapuche(),propiedades.getPortMapuche(), propiedades.getDatabaseMapuche(), propiedades.getUserMapuche(), propiedades.getPasswordMapuche());
            
        } catch (SQLException ex) {
            System.out.println("No se pudo realizar la coneccion "+ex);
            return 0;
        }
        try {
            if(this.getAgente().getLegajo()!=null){
                    PreparedStatement ps= connectjdbcpostgresql.getConn().prepareStatement(" select sum(cant_horas) as cant_horas " +
                            "	from mapuche.dh03 \n" +
                            "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj)\n" +
                            "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ)\n" +
                            "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic)\n" +
                            "	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' and dh01.nro_legaj = "+this.getAgente().getLegajo()+" group by dh01.nro_legaj ");

                    ResultSet rs = ps.executeQuery();

                   while(rs.next()){

                        resultado = rs.getInt("cant_horas");

                   }
            }
         return resultado;
        }catch (SQLException ex) {
            
            System.out.println("No se realizo la consulta en la extraccion de cantidad de horas por cargos "+ ex);
            return 0;
        }
        
    }
    
    public BigDecimal ContarSueldoBasicoCargoLegajo(){
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DATE);
        BigDecimal resultado = BigDecimal.ZERO; 
        ConnectJDBCPostgresql connectjdbcpostgresql = new ConnectJDBCPostgresql();
        try {
            Propiedades propiedades = new Propiedades();
            connectjdbcpostgresql.connect(propiedades.getHostMapuche(),propiedades.getPortMapuche(), propiedades.getDatabaseMapuche(), propiedades.getUserMapuche(), propiedades.getPasswordMapuche());
            
        } catch (SQLException ex) {
            System.out.println("No se pudo realizar la coneccion "+ex);
            return BigDecimal.ZERO;
        }
        try {
            PreparedStatement ps= connectjdbcpostgresql.getConn().prepareStatement(" select sum(impp_basic) as cant_horas " +
                    "	from mapuche.dh03 \n" +
                    "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj)\n" +
                    "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ)\n" +
                    "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic)\n" +
                    "	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' and dh01.nro_legaj = "+this.getAgente().getLegajo()+" group by dh01.nro_legaj ");
           ResultSet rs = ps.executeQuery();
           
           while(rs.next()){
              
                resultado = rs.getBigDecimal("cant_horas");
               
           }
         return resultado;
        }catch (SQLException ex) {
            
            System.out.println("No se realizo la consulta en la extraccion de consulta sueldo basico por cargos "+ ex);
            return BigDecimal.ZERO;
        }
        
    }
    
    public BigDecimal costoUnitarioCargoLegajo(){
         
       
        int meshoras = 0;
        try {
            meshoras = this.ContarHorasDedicacionCargoLegajo()* 4;
            if(meshoras > 0){
              
               return this.ContarSueldoBasicoCargoLegajo().divide(BigDecimal.valueOf(Double.valueOf(""+meshoras)),RoundingMode.HALF_UP);  
            }else{
                return BigDecimal.ZERO;
            }
             
        }catch (Exception ex) {
            
            System.out.println("No se realizo la consulta en la extraccion de costo unitario por cargos "+ ex);
            return BigDecimal.ZERO;
        }
        
    }
}
