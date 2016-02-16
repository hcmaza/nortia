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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mpaez
 */
@Entity
@Table(name = "archivorendicion", schema = "ap")
@SequenceGenerator(name="archivorendicion_id_seq", sequenceName="ap.archivorendicion_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Archivorendicion.findAll", query = "SELECT a FROM Archivorendicion a"),
    @NamedQuery(name = "Archivorendicion.findById", query = "SELECT a FROM Archivorendicion a WHERE a.id = :id"),
    @NamedQuery(name = "Archivorendicion.findByNombrearchivo", query = "SELECT a FROM Archivorendicion a WHERE a.nombrearchivo = :nombrearchivo")})
public class Archivorendicion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="archivorendicion_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 150)
    @Column(name = "nombrearchivo", length = 150)
    private String nombrearchivo;
    //@Lob
    @Column(name = "archivo")
    private byte[] archivo;
    
    @JoinColumn(name = "rendicionid", referencedColumnName = "id")
    @ManyToOne
    private Rendicion rendicionid;
    
    @JoinColumn(name = "rendicionexternaid", referencedColumnName = "id")
    @ManyToOne
    private Rendicionexterna rendicionexternaid;
    
    @Size(max = 25)
    @Column(name = "nrofactura")
    private String nrofactura;
    
    @Size(max = 2)
    @Column(name = "tipofactura")
    private String tipofactura;
    
    @Size(max = 200)
    @Column(name = "obsfactura")
    private String obsfactura;
    
    @Column(name = "montofactura")
    private BigDecimal montofactura;
    
    @Column(name = "proveedor")
    private String proveedor;
    
    @Column(name = "fechafactura")
    private Date fechafactura;
    
    @Column(name = "montoaprobado")
    private BigDecimal montoaprobado;
    
    @Column(name = "aporteuniversidad")
    private BigDecimal aporteuniversidad;
    
    @Column(name = "aporteorganismo")
    private BigDecimal aporteorganismo;
    
    @Column(name = "aportecomitente")
    private BigDecimal aportecomitente;
    
    @Column(name = "estado")
    private Integer estado;
    
    @Column(name = "obsevaluacion")
    private String obsevaluacion;

    public Archivorendicion() {
    }

    public Archivorendicion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombrearchivo() {
        return nombrearchivo;
    }

    public void setNombrearchivo(String nombrearchivo) {
        this.nombrearchivo = nombrearchivo;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public Rendicion getRendicionid() {
        return rendicionid;
    }

    public void setRendicionid(Rendicion rendicionid) {
        this.rendicionid = rendicionid;
    }

    public Rendicionexterna getRendicionexternaid() {
        return rendicionexternaid;
    }

    public void setRendicionexternaid(Rendicionexterna rendicionexternaid) {
        this.rendicionexternaid = rendicionexternaid;
    }

    public String getNrofactura() {
        return nrofactura;
    }

    public void setNrofactura(String nrofactura) {
        this.nrofactura = nrofactura;
    }

    public String getTipofactura() {
        return tipofactura;
    }

    public void setTipofactura(String tipofactura) {
        this.tipofactura = tipofactura;
    }

    public String getObsfactura() {
        return obsfactura;
    }

    public void setObsfactura(String obsfactura) {
        this.obsfactura = obsfactura;
    }

    public BigDecimal getMontofactura() {
        return montofactura;
    }

    public void setMontofactura(BigDecimal montofactura) {
        this.montofactura = montofactura;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public Date getFechafactura() {
        return fechafactura;
    }

    public void setFechafactura(Date fechafactura) {
        this.fechafactura = fechafactura;
    }

    public BigDecimal getMontoaprobado() {
        return montoaprobado;
    }

    public void setMontoaprobado(BigDecimal montoaprobado) {
        this.montoaprobado = montoaprobado;
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

    public BigDecimal getAportecomitente() {
        return aportecomitente;
    }

    public void setAportecomitente(BigDecimal aportecomitente) {
        this.aportecomitente = aportecomitente;
    }
 

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getObsevaluacion() {
        return obsevaluacion;
    }

    public void setObsevaluacion(String obsevaluacion) {
        this.obsevaluacion = obsevaluacion;
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
        if (!(object instanceof Archivorendicion)) {
            return false;
        }
        Archivorendicion other = (Archivorendicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Archivorendicion[ id=" + id + " ]";
    }
    
}
