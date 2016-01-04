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
import javax.persistence.ManyToOne;
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
 * @author Hugo
 */
@Entity
@Table(name = "evaluacion", schema = "ap")
@SequenceGenerator(name="evaluacion_id_seq", sequenceName="ap.evaluacion_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Evaluacion.findAll", query = "SELECT e FROM Evaluacion e"),
    @NamedQuery(name = "Evaluacion.findById", query = "SELECT e FROM Evaluacion e WHERE e.id = :id"),
    @NamedQuery(name = "Evaluacion.findByFecha", query = "SELECT e FROM Evaluacion e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Evaluacion.findByObservacion", query = "SELECT e FROM Evaluacion e WHERE e.observacion = :observacion")})
public class Evaluacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="evaluacion_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "evaluacion")
    private List<EvaluacionPregunta> evaluacionPreguntaList;
    @JoinColumn(name = "proyectoid", referencedColumnName = "id")
    @ManyToOne
    private Proyecto proyectoid;
    @JoinColumn(name = "usuarioid", referencedColumnName = "usuarioid")
    @ManyToOne
    private Usuario usuarioid; 
    
    public Evaluacion() {
    }

    public Evaluacion(Integer id) {
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
    
    
    @XmlTransient
    public List<EvaluacionPregunta> getEvaluacionPreguntaList() {
        return evaluacionPreguntaList;
    }

    public void setEvaluacionPreguntaList(List<EvaluacionPregunta> evaluacionPreguntaList) {
        this.evaluacionPreguntaList = evaluacionPreguntaList;
    }

    public Proyecto getProyectoid() {
        return proyectoid;
    }

    public void setProyectoid(Proyecto proyectoid) {
        this.proyectoid = proyectoid;
    }

    public Usuario getUsuarioid() {
        return usuarioid;
    }

    public void setUsuarioid(Usuario usuarioid) {
        this.usuarioid = usuarioid;
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
        if (!(object instanceof Evaluacion)) {
            return false;
        }
        Evaluacion other = (Evaluacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Evaluacion[ id=" + id + " ]";
    }
    
}
