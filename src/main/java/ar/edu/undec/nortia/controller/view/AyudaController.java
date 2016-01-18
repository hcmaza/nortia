
package ar.edu.undec.nortia.controller.view;

import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author tinchopy
 */
@ManagedBean
@SessionScoped
public class AyudaController {
    
    private String textoAyuda = "";

    public AyudaController() {
    }

    public String getTextoAyuda() {
        return textoAyuda;
    }

    public void setTextoAyuda(String textoAyuda) {
        this.textoAyuda = textoAyuda;
    }
    
    public void setearTextoAyudaPorClave(String clave){
        
        textoAyuda = ResourceBundle.getBundle("/Bundle").getString(clave);
    }
    
    public String mostrarTextoAyuda(){
        return textoAyuda;
    } 
    
    public String mostrarTextoAyudaPorClave(String clave){
        return ResourceBundle.getBundle("/Bundle").getString(clave);
    } 
}
