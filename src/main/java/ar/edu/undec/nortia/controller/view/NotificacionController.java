/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller.view;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author gongono
 */
@ManagedBean
@ViewScoped
public class NotificacionController {
    
   private int contadornotifdocente = 0;
   private int contadornotifadministrador = 0;

    /**
     * Creates a new instance of NotificacionController
     */
    public NotificacionController() {
        
    }
    public void contarnotificaciones(){
        FacesContext context = FacesContext.getCurrentInstance();  
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        if(context.getExternalContext().isUserInRole("Administrador") & proyectocontroller.buscarIdeasProyectosEvaluar()>0 ){
           this.setContadornotifadministrador(this.contadornotifadministrador+1);
          
       }
    }
     public void notificar() {  
        FacesContext context = FacesContext.getCurrentInstance();  
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
       if(context.getExternalContext().isUserInRole("Administrador") & proyectocontroller.buscarIdeasProyectosEvaluar()>0 ){
           this.setContadornotifadministrador(this.contadornotifadministrador+1);
           context.addMessage("growlnotificaciones", new FacesMessage(FacesMessage.SEVERITY_INFO, "Ideas proyectos a evaluar " , "Hay "+proyectocontroller.buscarIdeasProyectosEvaluar()+ " proyectos a evaluar !" ));  
       }
       
    
       
      // context.addMessage(null, new FacesMessage(" Bienvenido ",""+ context.getExternalContext().getUserPrincipal()));  
    
     
     }  

    public int getContadornotifdocente() {
        return contadornotifdocente;
    }

    public void setContadornotifdocente(int contadornotifdocente) {
        this.contadornotifdocente = contadornotifdocente;
    }

    public int getContadornotifadministrador() {
        return contadornotifadministrador;
    }

    public void setContadornotifadministrador(int contadornotifadministrador) {
        this.contadornotifadministrador = contadornotifadministrador;
    }
    
     
}
