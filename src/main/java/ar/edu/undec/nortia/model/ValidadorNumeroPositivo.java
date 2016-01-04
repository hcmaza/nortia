/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.model;

import java.math.BigDecimal;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author mpy
 */
@FacesValidator("validadorNumeroPositivo")
public class ValidadorNumeroPositivo implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        try {
        if (new BigDecimal(value.toString()).signum() < 1) {
            FacesMessage msg = new FacesMessage("Valor Inválido.", 
                    "El valor debe ser un número positivo.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg); 
        } 
    } catch (NumberFormatException ex) {
        FacesMessage msg = new FacesMessage("Valor Inválido.", "No es un número");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ValidatorException(msg); 
    }
    }
    
}
