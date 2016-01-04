package ar.edu.undec.nortia.controller.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author gongono
 */
@ManagedBean
@RequestScoped
public class Utilidades {

    /**
     * Creates a new instance of Utilidades
     */
    public Utilidades() {
        
        
    }
    
    public String getAnoActual(){
        String formato="yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
        return dateFormat.format(new Date());
       
    }
    
}
