/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Hugo
 */
@Entity
@Table(name = "tarea", schema = "ap")
@SequenceGenerator(name="tarea_id_seq", sequenceName="ap.tarea_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tarea.findAll", query = "SELECT t FROM Tarea t"),
    @NamedQuery(name = "Tarea.findById", query = "SELECT t FROM Tarea t WHERE t.id = :id"),
    @NamedQuery(name = "Tarea.findByTarea", query = "SELECT t FROM Tarea t WHERE t.tarea = :tarea"),
    @NamedQuery(name = "Tarea.findByPrioridad", query = "SELECT t FROM Tarea t WHERE t.prioridad = :prioridad"),
    @NamedQuery(name = "Tarea.findByEstado", query = "SELECT t FROM Tarea t WHERE t.estado = :estado"),
    @NamedQuery(name = "Tarea.findByFechacreacion", query = "SELECT t FROM Tarea t WHERE t.fechacreacion = :fechacreacion"),
    @NamedQuery(name = "Tarea.findByDias", query = "SELECT t FROM Tarea t WHERE t.dias = :dias"),
    @NamedQuery(name = "Tarea.findByDescripcion", query = "SELECT t FROM Tarea t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "Tarea.findByFechamodificacion", query = "SELECT t FROM Tarea t WHERE t.fechamodificacion = :fechamodificacion"),
    @NamedQuery(name = "Tarea.findByFechainicio", query = "SELECT t FROM Tarea t WHERE t.fechainicio = :fechainicio"),
    @NamedQuery(name = "Tarea.findByFechafin", query = "SELECT t FROM Tarea t WHERE t.fechafin = :fechafin")})
public class Tarea implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="tarea_id_seq")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "tarea")
    private String tarea;
    @Size(max = 15)
    @Column(name = "prioridad")
    private String prioridad;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "fechacreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacreacion;
    @Column(name = "dias")
    private Integer dias;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fechamodificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechamodificacion;
    @Column(name = "fechainicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechainicio;
    @Column(name = "fechafin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechafin;
    @JoinColumn(name = "etapaid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Etapa etapaid;
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "tareaid")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Tareaavance> tareaavanceList;
//    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "tareaid" )
//    //@JoinColumn(name="tareaid")    //@LazyCollection(LazyCollectionOption.FALSE)
//    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "tareaid" )
    @Fetch(value = FetchMode.SUBSELECT)
    private List<TareaAgente> tareaAgenteList;
     @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "tarea" )
    @Fetch(value = FetchMode.SUBSELECT)
    private List<PresupuestoTarea> presupuestoTareaList;
    @Column(name = "avance")
    private Integer avance;
//    @Transient
//    private Tareaavance ultimoavance ;

    public Tarea() {
    }

    public Tarea(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechamodificacion() {
        return fechamodificacion;
    }

    public void setFechamodificacion(Date fechamodificacion) {
        this.fechamodificacion = fechamodificacion;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public Etapa getEtapaid() {
        return etapaid;
    }

    public void setEtapaid(Etapa etapaid) {
        this.etapaid = etapaid;
    }

    public List<Tareaavance> getTareaavanceList() {
        return tareaavanceList;
    }

    public void setTareaavanceList(List<Tareaavance> tareaavanceList) {
        this.tareaavanceList = tareaavanceList;
    }
        
    public List<TareaAgente> getTareaAgenteList() {
        return tareaAgenteList;
    }

    public void setTareaAgenteList(List<TareaAgente> tareaAgenteList) {
        this.tareaAgenteList = tareaAgenteList;
    }

    public List<PresupuestoTarea> getPresupuestoTareaList() {
        return presupuestoTareaList;
    }

    public void setPresupuestoTareaList(List<PresupuestoTarea> presupuestoTareaList) {
        this.presupuestoTareaList = presupuestoTareaList;
    }

//    public Tareaavance getUltimoavance() {
//        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy"); 
//        
//        try {
//            if(ultimoavance==null){
//                Tareaavance tareaavanceultimo = new Tareaavance();
//                Date diamaximo = new Date();
//                Date diainicial = formateador.parse("01/01/1920");
//                if(this.tareaavanceList!=null){
//
//                    for(Tareaavance ta : this.tareaavanceList){
//                        if(!ta.getFecha().equals(diainicial)){
//                            if(ta.getFecha().after(diainicial)){
//                                tareaavanceultimo = ta;
//                                diainicial = ta.getFecha();
//                            }
//                        }
//                    }
//                }
//                return tareaavanceultimo;
//            }else{
//                return this.ultimoavance;
//            }
//        } catch (ParseException ex) {
//            
//            Logger.getLogger(Tarea.class.getName()).log(Level.SEVERE, null, ex);
//            return new Tareaavance();
//        }
//        
//        
//        
//        
//    }

    public Integer getAvance() {
        return avance;
    }

    public void setAvance(Integer avance) {
        this.avance = avance;
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
        if (!(object instanceof Tarea)) {
            return false;
        }
        Tarea other = (Tarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Tarea[ id=" + id + " ]";
    }
    
}
