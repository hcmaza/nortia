package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tinchopy
 */
@Entity
@Table(name = "rendicionexterna", schema = "ap")
@SequenceGenerator(name="rendicionexterna_id_seq", sequenceName="ap.rendicionexterna_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rendicionexterna.findAll", query = "SELECT r FROM Rendicionexterna r"),
    @NamedQuery(name = "Rendicionexterna.findById", query = "SELECT r FROM Rendicionexterna r WHERE r.id = :id"),
    @NamedQuery(name = "Rendicionexterna.findByFecha", query = "SELECT r FROM Rendicionexterna r WHERE r.fecha = :fecha")})
public class Rendicionexterna implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="rendicionexterna_id_seq")
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    
    @OneToMany(mappedBy = "rendicionexternaid", cascade = CascadeType.ALL)
    private List<Archivorendicion> archivorendicionList;

    @Column(name = "informeavance")
    private byte[] informeavance;

    @Column(name = "formulariorendicion")
    private byte[] formulariorendicion;

    public Rendicionexterna() {
    }

    public Rendicionexterna(Integer id) {
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

    public List<Archivorendicion> getArchivorendicionList() {
        if(null == archivorendicionList){
            archivorendicionList = new ArrayList<Archivorendicion>();
        }
        return archivorendicionList;
    }

    public void setArchivorendicionList(List<Archivorendicion> archivorendicionList) {
        this.archivorendicionList = archivorendicionList;
    }

    public byte[] getInformeavance() {
        return informeavance;
    }

    public void setInformeavance(byte[] informeavance) {
        this.informeavance = informeavance;
    }

    public byte[] getFormulariorendicion() {
        return formulariorendicion;
    }

    public void setFormulariorendicion(byte[] formulariorendicion) {
        this.formulariorendicion = formulariorendicion;
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
        if (!(object instanceof Rendicionexterna)) {
            return false;
        }
        Rendicionexterna other = (Rendicionexterna) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Rendicionexterna[ id=" + id + " ]";
    }
    
}
