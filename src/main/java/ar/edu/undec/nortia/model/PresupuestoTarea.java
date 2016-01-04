/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author gongono
 */
@Entity
@Table(name = "presupuesto_tarea", schema = "ap")
@SequenceGenerator(name="presupuesto_tarea_id_seq", sequenceName="ap.presupuesto_tarea_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PresupuestoTarea.findAll", query = "SELECT p FROM PresupuestoTarea p"),
    @NamedQuery(name = "PresupuestoTarea.findByDescripcion", query = "SELECT p FROM PresupuestoTarea p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "PresupuestoTarea.findByCostounitario", query = "SELECT p FROM PresupuestoTarea p WHERE p.costounitario = :costounitario"),
    @NamedQuery(name = "PresupuestoTarea.findByCantidad", query = "SELECT p FROM PresupuestoTarea p WHERE p.cantidad = :cantidad"),
    @NamedQuery(name = "PresupuestoTarea.findByTotal", query = "SELECT p FROM PresupuestoTarea p WHERE p.total = :total"),
    @NamedQuery(name = "PresupuestoTarea.findByAportecomitente", query = "SELECT p FROM PresupuestoTarea p WHERE p.aportecomitente = :aportecomitente"),
    @NamedQuery(name = "PresupuestoTarea.findByAporteuniversidad", query = "SELECT p FROM PresupuestoTarea p WHERE p.aporteuniversidad = :aporteuniversidad"),
    @NamedQuery(name = "PresupuestoTarea.findByAporteorganismo", query = "SELECT p FROM PresupuestoTarea p WHERE p.aporteorganismo = :aporteorganismo"),
    @NamedQuery(name = "PresupuestoTarea.findById", query = "SELECT p FROM PresupuestoTarea p WHERE p.id = :id")})
public class PresupuestoTarea implements Serializable {
    @OneToMany(mappedBy = "presupuestotarea", fetch = FetchType.EAGER)
    private List<Solicitud> solicitud;
    private static final long serialVersionUID = 1L;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "costounitario")
    private BigDecimal costounitario;
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    @Column(name = "total")
    private BigDecimal total;
    @Column(name = "aportecomitente")
    private BigDecimal aportecomitente;
    @Column(name = "aporteuniversidad")
    private BigDecimal aporteuniversidad;
    @Column(name = "aporteorganismo")
    private BigDecimal aporteorganismo;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="presupuesto_tarea_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "rubroid", referencedColumnName = "id")
    @ManyToOne
    private Rubro rubro;
    @JoinColumn(name = "tareaid", referencedColumnName = "id")
    @ManyToOne
    private Tarea tarea;
    
    
    public PresupuestoTarea() {
    }

    public PresupuestoTarea(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCostounitario() {
        return costounitario;
    }

    public void setCostounitario(BigDecimal costounitario) {
        this.costounitario = costounitario;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAportecomitente() {
        return aportecomitente;
    }

    public void setAportecomitente(BigDecimal aportecomitente) {
        this.aportecomitente = aportecomitente;
    }

    public BigDecimal getAporteuniversidad() {
        return aporteuniversidad;
    }

    public void setAporteuniversidad(BigDecimal aporteuniversidad) {
        this.aporteuniversidad = aporteuniversidad;
    }

    public BigDecimal getAporteorganismo() {
        return aporteorganismo;
    }

    public void setAporteorganismo(BigDecimal aporteorganismo) {
        this.aporteorganismo = aporteorganismo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rubro getRubro() {
        if (rubro == null){
            rubro = new Rubro();
        }
        
        return rubro;
    }

    public void setRubro(Rubro rubro) {
        this.rubro = rubro;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public List<Solicitud> getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(List<Solicitud> solicitud) {
        this.solicitud = solicitud;
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
        if (!(object instanceof PresupuestoTarea)) {
            return false;
        }
        PresupuestoTarea other = (PresupuestoTarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        //return "ar.edu.undec.nortia.model.PresupuestoTarea[ id=" + id + " ]";
        return this.descripcion;
    }
    
}
