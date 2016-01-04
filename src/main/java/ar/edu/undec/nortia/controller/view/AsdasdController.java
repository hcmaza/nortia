
package ar.edu.undec.nortia.controller.view;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author tinchopy
 */
@ManagedBean
@RequestScoped
public class AsdasdController {

    /**
     * Creates a new instance of AsdasdController
     */
    public AsdasdController() {
    }
    
    public void asdasd(){
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        
        System.out.println("Método 1");
        
        System.out.println("Método B");
        
        System.out.println("Método III");
        
        try{
            context.redirect(context.getRequestContextPath() + "/secure/asdasd/qweqwe.xhtml");
        } catch(IOException ioe){
            System.out.println("Exception de IO");
        } catch(Exception e){
            System.out.println("Exception Comun");
        }
        
        
    }
    
}
