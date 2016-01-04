/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gongono
 */
@Entity
@Table(name = "desembolso", schema = "ap")
@SequenceGenerator(name="desembolso_id_seq", sequenceName="ap.desembolso_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Desembolso.findAll", query = "SELECT d FROM Desembolso d"),
    @NamedQuery(name = "Desembolso.findById", query = "SELECT d FROM Desembolso d WHERE d.id = :id"),
    @NamedQuery(name = "Desembolso.findByFechadesembolso", query = "SELECT d FROM Desembolso d WHERE d.fechadesembolso = :fechadesembolso"),
    @NamedQuery(name = "Desembolso.findByFechacarga", query = "SELECT d FROM Desembolso d WHERE d.fechacarga = :fechacarga"),
    @NamedQuery(name = "Desembolso.findByNumerodesembolso", query = "SELECT d FROM Desembolso d WHERE d.numerodesembolso = :numerodesembolso")})
public class Desembolso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="desembolso_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechadesembolso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadesembolso;
    @Column(name = "fechacarga")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacarga;
    @Size(max = 100)
    @Column(name = "numerodesembolso")
    private String numerodesembolso;
    @JoinColumn(name = "numerocuentabancaria", referencedColumnName = "numerocuenta")
    @ManyToOne
    private Cuentabancaria numerocuentabancaria;
    @Column(name = "monto")
    private BigDecimal monto;
    @JoinColumn(name = "proyectoid", referencedColumnName = "id")
    @ManyToOne
    private Proyecto proyectoid;

    public Desembolso() {
    }

    public Desembolso(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechadesembolso() {
        return fechadesembolso;
    }

    public void setFechadesembolso(Date fechadesembolso) {
        this.fechadesembolso = fechadesembolso;
    }

    public Date getFechacarga() {
        return fechacarga;
    }

    public void setFechacarga(Date fechacarga) {
        this.fechacarga = fechacarga;
    }

    public String getNumerodesembolso() {
        return numerodesembolso;
    }

    public void setNumerodesembolso(String numerodesembolso) {
        this.numerodesembolso = numerodesembolso;
    }

    public Cuentabancaria getNumerocuentabancaria() {
        return numerocuentabancaria;
    }

    public void setNumerocuentabancaria(Cuentabancaria numerocuentabancaria) {
        this.numerocuentabancaria = numerocuentabancaria;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Proyecto getProyectoid() {
        return proyectoid;
    }

    public void setProyectoid(Proyecto proyectoid) {
        this.proyectoid = proyectoid;
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
        if (!(object instanceof Desembolso)) {
            return false;
        }
        Desembolso other = (Desembolso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Desembolso[ id=" + id + " ]";
    }
    
}
