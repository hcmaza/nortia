/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller.view;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author mpaez
 */
@ManagedBean
@ViewScoped
public class DialogosController implements Serializable{
    public DialogosController(){}
    
    public void dialogo(){
        System.out.println("dialogoCrearAgente");
        //RequestContext.getCurrentInstance().openDialog("dialogo");
        //RequestContext.getCurrentInstance().openDialog("/secure/solicitud/dialogo.xhtml");
        RequestContext.getCurrentInstance().openDialog("/secure/dialogos/dialogo.xhtml");
        System.out.println("dialogoCrearAgente fin");
    }
    
    public void dialogoSolicitudItem(){
        System.out.println("dialogoSolicitudItem");
        RequestContext.getCurrentInstance().openDialog("/secure/dialogos/dialogoSolicitudItem.xhtml");
        System.out.println("dialogoSolicitudItem fin");
    }
    
    public void dialogoVolverLista(){
        RequestContext.getCurrentInstance().openDialog("/secure/dialogos/dialogoVolverLista.xhtml");
    }
    
    public void cerrarYRedireccionar(){
        
        // Cerrar el dialogo
        RequestContext.getCurrentInstance().closeDialog(this);
 
        // Redireccionar
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();

        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, "/secure/agente/List.xhtml"));

        try {
             extContext.redirect(url);
        } catch (IOException ioe) {
            throw new FacesException(ioe);
        }
        
        //return "/secure/agente/List?faces-redirect=true";
    }
    
    public void cerrar(){
        // Cerrar el dialogo
        RequestContext.getCurrentInstance().closeDialog(this);
    }
    
    public void redireccionar(){
        // Redireccionar
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext extContext = ctx.getExternalContext();

        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, "/secure/agente/List.xhtml"));

        try {
             extContext.redirect(url);
        } catch (IOException ioe) {
            throw new FacesException(ioe);
        }
    }
}
