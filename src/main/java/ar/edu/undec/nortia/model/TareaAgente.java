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
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hugo
 */
@Entity
@Table(name = "tarea_agente", schema = "ap")
@SequenceGenerator(name="tarea_agente_id_seq", sequenceName="ap.tarea_agente_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TareaAgente.findAll", query = "SELECT t FROM TareaAgente t"),
    @NamedQuery(name = "TareaAgente.findByFuncion", query = "SELECT t FROM TareaAgente t WHERE t.funcion = :funcion"),
    @NamedQuery(name = "TareaAgente.findById", query = "SELECT t FROM TareaAgente t WHERE t.id = :id")})
public class TareaAgente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Size(max = 50)
    @Column(name = "funcion", length = 50)
    private String funcion;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="tarea_agente_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @JoinColumn(name = "tareaid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tareaid;
    @JoinColumn(name = "agenteid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Agente agenteid;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "costo")
    private BigDecimal costo;
    @Column(name="horasdedicadas")
    private Integer horasdedicadas;
    
    public TareaAgente() {
    }

    public TareaAgente(Integer id) {
        this.id = id;
    }

    public String getFuncion() {
        return funcion;
    }

    public void setFuncion(String funcion) {
        this.funcion = funcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Tarea getTareaid() {
        return tareaid;
    }

    public void setTareaid(Tarea tareaid) {
        this.tareaid = tareaid;
    }

    public Agente getAgenteid() {
        return agenteid;
    }

    public void setAgenteid(Agente agenteid) {
        this.agenteid = agenteid;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public Integer getHorasdedicadas() {
        return horasdedicadas;
    }

    public void setHorasdedicadas(Integer horasdedicadas) {
        this.horasdedicadas = horasdedicadas;
    }
    
    
    
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TareaAgente)) {
            return false;
        }
        TareaAgente other = (TareaAgente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.TareaAgente[ id=" + id + " ]";
    }
    
     public int ContarHorasDedicacionCargoLegajo(){
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH)+1;
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
            if(this.getAgenteid().getLegajo()!=null){
                    PreparedStatement ps= connectjdbcpostgresql.getConn().prepareStatement(" select sum(cant_horas) as cant_horas " +
                            "	from mapuche.dh03 \n" +
                            "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj)\n" +
                            "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ)\n" +
                            "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic)\n" +
                            "	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' and dh01.nro_legaj = "+this.getAgenteid().getLegajo()+" group by dh01.nro_legaj ");
                    System.out.println(" select sum(cant_horas) as cant_horas " +
                                    " from mapuche.dh03 " +
                                    "  left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj) " +
                                    "  left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ) " +
                                    "  left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic) " +
                                    " where dh03.vig_caano > " + (año-1) + " and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' and dh01.nro_legaj = "+this.getAgenteid().getLegajo()+" group by dh01.nro_legaj ");
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
        int mes = fecha.get(Calendar.MONTH)+1;
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
                    "	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' and dh01.nro_legaj = "+this.getAgenteid().getLegajo()+" group by dh01.nro_legaj ");
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
               return this.ContarSueldoBasicoCargoLegajo().divide(BigDecimal.valueOf(Double.valueOf(""+meshoras)),RoundingMode.HALF_UP).setScale(2);
            }else{
                return BigDecimal.ZERO.setScale(2,RoundingMode.CEILING);
            }
             
        }catch (Exception ex) {
            
            System.out.println("No se realizo la consulta en la extraccion de costo unitario por cargos "+ ex);
            return BigDecimal.ZERO.setScale(2);
        }
        
    }
    
    
}
