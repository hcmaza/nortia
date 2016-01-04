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
@Table(name = "estadosolicitud", schema = "ap")
@SequenceGenerator(name="estadosolicitud_id_seq", sequenceName="ap.estadosolicitud_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estadosolicitud.findAll", query = "SELECT e FROM Estadosolicitud e"),
    @NamedQuery(name = "Estadosolicitud.findById", query = "SELECT e FROM Estadosolicitud e WHERE e.id = :id"),
    @NamedQuery(name = "Estadosolicitud.findByEstadosolicitud", query = "SELECT e FROM Estadosolicitud e WHERE e.estadosolicitud = :estadosolicitud")})
public class Estadosolicitud implements Serializable {
//    @OneToMany(mappedBy = "estadosolicitudid")
//    private List<Solicitud> solicitudList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="estadosolicitud_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "estadosolicitud")
    private String estadosolicitud;

    public Estadosolicitud() {
    }

    public Estadosolicitud(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstadosolicitud() {
        return estadosolicitud;
    }

    public void setEstadosolicitud(String estadosolicitud) {
        this.estadosolicitud = estadosolicitud;
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
        if (!(object instanceof Estadosolicitud)) {
            return false;
        }
        Estadosolicitud other = (Estadosolicitud) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
//        return "ar.edu.undec.nortia.model.Estadosolicitud[ id=" + id + " ]";
        return this.estadosolicitud;
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
