/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package ar.edu.undec.nortia.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
 * @author tinchopy
 */
@Entity
@Table(name = "traslado", schema = "ap")
@SequenceGenerator(name="traslado_id_seq", sequenceName="ap.traslado_id_seq", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Traslado.findAll", query = "SELECT t FROM Traslado t"),
    @NamedQuery(name = "Traslado.findById", query = "SELECT t FROM Traslado t WHERE t.id = :id"),
    @NamedQuery(name = "Traslado.findByFechahoraviaje", query = "SELECT t FROM Traslado t WHERE t.fechahoraviaje = :fechahoraviaje"),
    @NamedQuery(name = "Traslado.findByFechahoraregreso", query = "SELECT t FROM Traslado t WHERE t.fechahoraregreso = :fechahoraregreso"),
    @NamedQuery(name = "Traslado.findByDestino", query = "SELECT t FROM Traslado t WHERE t.destino = :destino")})
public class Traslado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="traslado_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "fechahoraviaje")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechahoraviaje;
    
    @Column(name = "fechahoraregreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechahoraregreso;
    
    @Size(max = 100)
    @Column(name = "destino", length = 100)
    private String destino;
    
    @JoinColumn(name = "responsableid", referencedColumnName = "id")
    @ManyToOne
    private Agente responsableid;
       
    @JoinColumn(name = "proyectoid", referencedColumnName = "id")
    @ManyToOne
    private Proyecto proyectoid;   
    
    @JoinColumn(name = "vehiculoid", referencedColumnName = "id")
    @ManyToOne
    private Vehiculo vehiculoid;
    
    @JoinColumn(name = "solicitudid", referencedColumnName = "id")
    @ManyToOne
    private Solicitud solicitudid;

    public Solicitud getSolicitudid() {
        return solicitudid;
    }

    public void setSolicitudid(Solicitud solicitudid) {
        this.solicitudid = solicitudid;
    }

    public Vehiculo getVehiculoid() {
        return vehiculoid;
    }

    public void setVehiculoid(Vehiculo vehiculoid) {
        this.vehiculoid = vehiculoid;
    }

    public Traslado() {
    }

    public Traslado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechahoraviaje() {
        return fechahoraviaje;
    }

    public void setFechahoraviaje(Date fechahoraviaje) {
        this.fechahoraviaje = fechahoraviaje;
    }

    public Date getFechahoraregreso() {
        return fechahoraregreso;
    }

    public void setFechahoraregreso(Date fechahoraregreso) {
        this.fechahoraregreso = fechahoraregreso;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    public Proyecto getProyectoid() {
        return proyectoid;
    }

    public void setProyectoid(Proyecto proyectoid) {
        this.proyectoid = proyectoid;
    }

    public Agente getResponsableid() {
        return responsableid;
    }

    public void setResponsableid(Agente responsableid) {
        this.responsableid = responsableid;
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
        if (!(object instanceof Traslado)) {
            return false;
        }
        Traslado other = (Traslado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.nortia.model.Traslado[ id=" + id + " ]";
    }
    
}
