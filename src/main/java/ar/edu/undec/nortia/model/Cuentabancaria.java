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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author gongono
 */
@Entity
@Table(name = "cuentabancaria", schema = "ap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cuentabancaria.findAll", query = "SELECT c FROM Cuentabancaria c"),
    @NamedQuery(name = "Cuentabancaria.findByNumerocuenta", query = "SELECT c FROM Cuentabancaria c WHERE c.numerocuenta = :numerocuenta"),
    @NamedQuery(name = "Cuentabancaria.findByCbu", query = "SELECT c FROM Cuentabancaria c WHERE c.cbu = :cbu")})
public class Cuentabancaria implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "numerocuenta")
    private String numerocuenta;
    @Size(max = 50)
    @Column(name = "cbu")
    private String cbu;
    @JoinColumn(name = "bancoid", referencedColumnName = "id")
    @ManyToOne
    private Banco bancoid;
    @OneToMany(mappedBy = "numerocuentabancaria")
    private List<Desembolso> desembolsoList;

    public Cuentabancaria() {
    }

    public Cuentabancaria(String numerocuenta) {
        this.numerocuenta = numerocuenta;
    }

    public String getNumerocuenta() {
        return numerocuenta;
    }

    public void setNumerocuenta(String numerocuenta) {
        this.numerocuenta = numerocuenta;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public Banco getBancoid() {
        return bancoid;
    }

    public void setBancoid(Banco bancoid) {
        this.bancoid = bancoid;
    }

    @XmlTransient
    public List<Desembolso> getDesembolsoList() {
        return desembolsoList;
    }

    public void setDesembolsoList(List<Desembolso> desembolsoList) {
        this.desembolsoList = desembolsoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numerocuenta != null ? numerocuenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cuentabancaria)) {
            return false;
        }
        Cuentabancaria other = (Cuentabancaria) object;
        if ((this.numerocuenta == null && other.numerocuenta != null) || (this.numerocuenta != null && !this.numerocuenta.equals(other.numerocuenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Cuentabancaria[ numerocuenta=" + numerocuenta + " ]";
    }
    
}
