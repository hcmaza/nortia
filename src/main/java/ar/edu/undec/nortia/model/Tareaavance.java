/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hugo
 */
@Entity
@Table(name = "tareaavance", schema = "ap")
@SequenceGenerator(name="tareaavance_id_seq", sequenceName="ap.tareaavance_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tareaavance.findAll", query = "SELECT t FROM Tareaavance t"),
    @NamedQuery(name = "Tareaavance.findById", query = "SELECT t FROM Tareaavance t WHERE t.id = :id"),
    @NamedQuery(name = "Tareaavance.findByFecha", query = "SELECT t FROM Tareaavance t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Tareaavance.findByAvance", query = "SELECT t FROM Tareaavance t WHERE t.avance = :avance"),
    @NamedQuery(name = "Tareaavance.findByEstado", query = "SELECT t FROM Tareaavance t WHERE t.estado = :estado")})
public class Tareaavance implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="tareaavance_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "avance")
    private Integer avance;
    @Column(name = "estado")
    private Character estado;
    @JoinColumn(name = "tareaid", referencedColumnName = "id")
    @ManyToOne
    private Tarea tareaid;
    @Column(name = "fechainicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechainicio;
    @Column(name = "fechafin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechafin;
    

    public Tareaavance() {
    }

    public Tareaavance(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getAvance() {
        return avance;
    }

    public void setAvance(Integer avance) {
        this.avance = avance;
    }

    public Character getEstado() {
        return estado;
    }

    public void setEstado(Character estado) {
        this.estado = estado;
    }

    public Tarea getTareaid() {
        return tareaid;
    }

    public void setTareaid(Tarea tareaid) {
        this.tareaid = tareaid;
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

    public void setFechafinal(Date fechafin) {
        this.fechafin = fechafin;
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
        if (!(object instanceof Tareaavance)) {
            return false;
        }
        Tareaavance other = (Tareaavance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Tareaavance[ id=" + id + " ]";
    }
    
}
