/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mpaez
 */
@Entity
@Table(name = "rendicion", schema = "ap")
@SequenceGenerator(name="rendicion_id_seq", sequenceName="ap.rendicion_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rendicion.findAll", query = "SELECT r FROM Rendicion r"),
    @NamedQuery(name = "Rendicion.findById", query = "SELECT r FROM Rendicion r WHERE r.id = :id"),
    @NamedQuery(name = "Rendicion.findByFecha", query = "SELECT r FROM Rendicion r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "Rendicion.findByObservacion", query = "SELECT r FROM Rendicion r WHERE r.observacion = :observacion")})
public class Rendicion implements Serializable {
    @OneToMany(mappedBy = "rendicionid", cascade = CascadeType.ALL)
    private List<Archivorendicion> archivorendicionList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="rendicion_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 250)
    @Column(name = "observacion", length = 250)
    private String observacion;
    
//    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    @JoinColumn(name="rendicionid")
//    private List<Solicitud> solicitudes;

    public Rendicion() {
    }

    public Rendicion(Integer id) {
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
        if (!(object instanceof Rendicion)) {
            return false;
        }
        Rendicion other = (Rendicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Rendicion[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Archivorendicion> getArchivorendicionList() {
        return archivorendicionList;
    }

    public void setArchivorendicionList(List<Archivorendicion> archivorendicionList) {
        this.archivorendicionList = archivorendicionList;
    }
    
}
