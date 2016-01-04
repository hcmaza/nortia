/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * @author mpaez
 */
@Entity
@Table(name = "tiposolicitud", schema = "ap")
@SequenceGenerator(name="tiposolicitud_id_seq", sequenceName="ap.tiposolicitud_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tiposolicitud.findAll", query = "SELECT t FROM Tiposolicitud t"),
    @NamedQuery(name = "Tiposolicitud.findById", query = "SELECT t FROM Tiposolicitud t WHERE t.id = :id"),
    @NamedQuery(name = "Tiposolicitud.findByTiposolicitud", query = "SELECT t FROM Tiposolicitud t WHERE t.tiposolicitud = :tiposolicitud")})
public class Tiposolicitud implements Serializable {
//    @OneToMany(mappedBy = "tiposolicitudid")
//    private List<Solicitud> solicitudList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="tiposolicitud_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 50)
    @Column(name = "tiposolicitud", length = 50)
    private String tiposolicitud;

    public Tiposolicitud() {
    }

    public Tiposolicitud(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTiposolicitud() {
        return tiposolicitud;
    }

    public void setTiposolicitud(String tiposolicitud) {
        this.tiposolicitud = tiposolicitud;
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
        if (!(object instanceof Tiposolicitud)) {
            return false;
        }
        Tiposolicitud other = (Tiposolicitud) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
//        return "ar.edu.undec.nortia.model.Tiposolicitud[ id=" + id + " ]";
        return this.tiposolicitud;
    }

//    @XmlTransient
//    public List<Solicitud> getSolicitudList() {
//        return solicitudList;
//    }
//
//    public void setSolicitudList(List<Solicitud> solicitudList) {
//        this.solicitudList = solicitudList;
//    }
    
}
