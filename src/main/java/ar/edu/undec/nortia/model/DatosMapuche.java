/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hugo
 */
@Entity
public class DatosMapuche implements Serializable {
    @Id
     private Integer nro_legaj;
     private String desc_appat;
     private String desc_apmat;
     private String desc_nombr;
     private String tipo_docum;
     private Integer nro_docum;
     private Integer nro_cuil1;
     private Integer nro_cuil;
     private Integer nro_cuil2;
     private Character tipo_sexo;
     @Temporal(TemporalType.TIMESTAMP)
     private Date fec_nacim;
     private Character pcia_nacim;
     private String pais_nacim;
     private Integer cant_horas;

    public DatosMapuche() {
    }

    public Integer getNro_legaj() {
        return nro_legaj;
    }

    public void setNro_legaj(Integer nro_legaj) {
        this.nro_legaj = nro_legaj;
    }

    public String getDesc_appat() {
        return desc_appat;
    }

    public void setDesc_appat(String desc_appat) {
        this.desc_appat = desc_appat;
    }

    public String getDesc_apmat() {
        return desc_apmat;
    }

    public void setDesc_apmat(String desc_apmat) {
        this.desc_apmat = desc_apmat;
    }

    public String getDesc_nombr() {
        return desc_nombr;
    }

    public void setDesc_nombr(String desc_nombr) {
        this.desc_nombr = desc_nombr;
    }

    public String getTipo_docum() {
        return tipo_docum;
    }

    public void setTipo_docum(String tipo_docum) {
        this.tipo_docum = tipo_docum;
    }

    public Integer getNro_docum() {
        return nro_docum;
    }

    public void setNro_docum(Integer nro_docum) {
        this.nro_docum = nro_docum;
    }

    public Integer getNro_cuil1() {
        return nro_cuil1;
    }

    public void setNro_cuil1(Integer nro_cuil1) {
        this.nro_cuil1 = nro_cuil1;
    }

    public Integer getNro_cuil() {
        return nro_cuil;
    }

    public void setNro_cuil(Integer nro_cuil) {
        this.nro_cuil = nro_cuil;
    }

    public Integer getNro_cuil2() {
        return nro_cuil2;
    }

    public void setNro_cuil2(Integer nro_cuil2) {
        this.nro_cuil2 = nro_cuil2;
    }

    public Character getTipo_sexo() {
        return tipo_sexo;
    }

    public void setTipo_sexo(Character tipo_sexo) {
        this.tipo_sexo = tipo_sexo;
    }

    public Date getFec_nacim() {
        return fec_nacim;
    }

    public void setFec_nacim(Date fec_nacim) {
        this.fec_nacim = fec_nacim;
    }

    public Character getPcia_nacim() {
        return pcia_nacim;
    }

    public void setPcia_nacim(Character pcia_nacim) {
        this.pcia_nacim = pcia_nacim;
    }

    public String getPais_nacim() {
        return pais_nacim;
    }

    public void setPais_nacim(String pais_nacim) {
        this.pais_nacim = pais_nacim;
    }

    

    public Integer getCant_horas() {
        return cant_horas;
    }

    public void setCant_horas(Integer cant_horas) {
        this.cant_horas = cant_horas;
    }
     
     
    
}
