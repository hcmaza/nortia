package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.PasajeroFacade;
import ar.edu.undec.nortia.model.Traslado;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.TrasladoFacade;
import ar.edu.undec.nortia.model.Pasajero;
import ar.edu.undec.nortia.model.ProyectoAgente;
import ar.edu.undec.nortia.model.Solicitud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "trasladoController")
@SessionScoped
public class TrasladoController implements Serializable {

    private Traslado current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.TrasladoFacade ejbFacade;
    
    @EJB
    private ar.edu.undec.nortia.controller.PasajeroFacade ejbPasajeroFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private List<ProyectoAgente> listaPasajerosSeleccionados;
    
    private int capacidadCalculada=0;

    public int getCapacidadCalculada() {
        return capacidadCalculada;
    }

    public void setCapacidadCalculada(int capacidadCalculada) {
        this.capacidadCalculada = capacidadCalculada;
    }    
    
    public List<ProyectoAgente> getListaPasajerosSeleccionados() {
        if(listaPasajerosSeleccionados == null){
            listaPasajerosSeleccionados = new ArrayList<ProyectoAgente>();
        }
        
        return listaPasajerosSeleccionados;
    }

    public void setListaPasajerosSeleccionados(List<ProyectoAgente> listaPasajerosSeleccionados) {
        this.listaPasajerosSeleccionados = listaPasajerosSeleccionados;
    }

    public PasajeroFacade getEjbPasajeroFacade() {
        return ejbPasajeroFacade;
    }

    public TrasladoController() {
    }

    public Traslado getSelected() {
        if (current == null) {
            current = new Traslado();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Traslado current) {
        this.current = current;
    }

    private TrasladoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Traslado) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Traslado();
        selectedItemIndex = -1;
        return "Create";
    }
    
    public void prepararTraslado(){
        current = new Traslado();
        selectedItemIndex = -1;                
    }

    public String create() {
                
        try {
            
            /*FacesContext context = FacesContext.getCurrentInstance();
            ProyectoController proyectoController = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
            VehiculoController vehiculoController = (VehiculoController) context.getApplication().evaluateExpressionGet(context, "#{vehiculoController}", VehiculoController.class);
            SolicitudController solicitudController=(SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
            */
                                 
            // persistir
            //getFacade().create(current);
            //getFacade().createWithPersist(current);
            
           /* // persistir los pasajeros
            for(Pasajero p : current.getPasajeroList()){
                p.setTrasladoid(current);
                
                this.getEjbPasajeroFacade().create(p);
            }*/
            
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TrasladoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Traslado) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TrasladoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Traslado) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TrasladoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Traslado.class)
    public static class TrasladoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrasladoController controller = (TrasladoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trasladoController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Traslado) {
                Traslado o = (Traslado) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Traslado.class.getName());
            }
        }

    }
    
//    public void agregarPasajero(){
//        
//        FacesContext context = FacesContext.getCurrentInstance();
//        PasajeroController pasajeroController = (PasajeroController) context.getApplication().evaluateExpressionGet(context, "#{pasajeroController}", PasajeroController.class);
//        
//        this.current.getPasajeroList().add(pasajeroController.getSelected());
//    }
//    
//    public void quitarPasajero(Pasajero pasajero){
//
//        // se quita de la lista de pasajeros
//        Iterator i = this.getSelected().getPasajeroList().iterator();
//
//        while(i.hasNext()){
//            //if(((Pasajero)i.next()).getDni().equals(pasajero.getDni())){
//            if(((Pasajero)i.next()).get .equals(pasajero.getAgenteid().getId){
//                
//                i.remove();
//            }
//        }
//    }
    
    public void asdasd(){
        System.out.println("Tamaño de lista de pasajeros seleccionados = " + this.getListaPasajerosSeleccionados().size());
    }

}
