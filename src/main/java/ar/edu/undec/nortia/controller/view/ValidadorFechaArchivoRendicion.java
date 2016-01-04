
package ar.edu.undec.nortia.controller.view;

import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * Validador de la fecha del archivo de comprobante (factura), dicha fecha debe ser posterior
 * a la fecha de la solicitud.
 * 
 * @author mpaez
 */
@FacesValidator("validadorFechaArchivoRendicion")
public class ValidadorFechaArchivoRendicion implements Validator{
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        
        if (value == null) {
            return;
        }
         
        // Dejar el manejo de null de fechaSolicitud a required="true"
        Object componenteFechaSolicitud = component.getAttributes().get("fechaSolicitud");
        if (componenteFechaSolicitud==null) {
            return;
        }
         
        Date fechaSolicitud = (Date)componenteFechaSolicitud;
        Date fechaArchivoRendicion = (Date)value; 
        
        if (fechaArchivoRendicion.before(fechaSolicitud)) {
            throw new ValidatorException(new FacesMessage(
                FacesMessage.SEVERITY_ERROR, "Fecha Incorrecta", "La fecha del comprobante debe ser posterior a la fecha de la solicitud."));
        }
    }
}
