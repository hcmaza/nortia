/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gongono
 */
@Entity
@Table(name = "agentecargo", schema = "ap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Agentecargo.findAll", query = "SELECT a FROM Agentecargo a"),
    @NamedQuery(name = "Agentecargo.findByNroCargo", query = "SELECT a FROM Agentecargo a WHERE a.nroCargo = :nroCargo"),
    @NamedQuery(name = "Agentecargo.findByNroLegajo", query = "SELECT a FROM Agentecargo a WHERE a.nroLegajo = :nroLegajo"),
    @NamedQuery(name = "Agentecargo.findByCodcUacad", query = "SELECT a FROM Agentecargo a WHERE a.codcUacad = :codcUacad"),
    @NamedQuery(name = "Agentecargo.findByImppBasic", query = "SELECT a FROM Agentecargo a WHERE a.imppBasic = :imppBasic")})
public class Agentecargo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "nro_cargo", nullable = false)
    private Integer nroCargo;
    @Column(name = "cant_horas")
    private Integer cant_horas;
    @Column(name = "nro_legajo")
    private Integer nroLegajo;
    @Size(max = 4)
    @Column(name = "codc_uacad", length = 4)
    private String codcUacad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "impp_basic", precision = 9, scale = 2)
    private BigDecimal imppBasic;

    public Agentecargo() {
    }

    public Agentecargo(Integer nroCargo) {
        this.nroCargo = nroCargo;
    }

    public Integer getNroCargo() {
        return nroCargo;
    }

    public void setNroCargo(Integer nroCargo) {
        this.nroCargo = nroCargo;
    }

    public Integer getCant_horas() {
        return cant_horas;
    }

    public void setCant_horas(Integer cant_horas) {
        this.cant_horas = cant_horas;
    }

    
    
    public Integer getNroLegajo() {
        return nroLegajo;
    }

    public void setNroLegajo(Integer nroLegajo) {
        this.nroLegajo = nroLegajo;
    }

    public String getCodcUacad() {
        return codcUacad;
    }

    public void setCodcUacad(String codcUacad) {
        this.codcUacad = codcUacad;
    }

    public BigDecimal getImppBasic() {
        return imppBasic;
    }

    public void setImppBasic(BigDecimal imppBasic) {
        this.imppBasic = imppBasic;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nroCargo != null ? nroCargo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Agentecargo)) {
            return false;
        }
        Agentecargo other = (Agentecargo) object;
        if ((this.nroCargo == null && other.nroCargo != null) || (this.nroCargo != null && !this.nroCargo.equals(other.nroCargo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Agentecargo[ nroCargo=" + nroCargo + " ]";
    }
    
}
