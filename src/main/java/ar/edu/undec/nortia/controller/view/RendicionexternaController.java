package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.ArchivorendicionFacade;
import ar.edu.undec.nortia.model.Rendicionexterna;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.RendicionexternaFacade;
import ar.edu.undec.nortia.model.Archivorendicion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

@ManagedBean(name = "rendicionexternaController")
@SessionScoped
public class RendicionexternaController implements Serializable {

    private Rendicionexterna current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.RendicionexternaFacade ejbFacade;
    
    @EJB
    private ar.edu.undec.nortia.controller.ArchivorendicionFacade ejbFacadeComprobantes;
    
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private List<Archivorendicion> listaComprobantes;
    private Archivorendicion comprobanteSeleccionado;
    
    private Date desde;
    private Date hasta;

    public Archivorendicion getComprobanteSeleccionado() {
        return comprobanteSeleccionado;
    }

    public void setComprobanteSeleccionado(Archivorendicion comprobanteSeleccionado) {
        this.comprobanteSeleccionado = comprobanteSeleccionado;
    }

    public List<Archivorendicion> getListaComprobantes() {
        if(null == listaComprobantes){
            listaComprobantes = new ArrayList<Archivorendicion>();
        }
        return listaComprobantes;
    }

    public void setListaComprobantes(List<Archivorendicion> listaComprobantes) {
        this.listaComprobantes = listaComprobantes;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public RendicionexternaController() {
    }

    public Rendicionexterna getSelected() {
        if (current == null) {
            current = new Rendicionexterna();
            selectedItemIndex = -1;
        }
        return current;
    }

    private RendicionexternaFacade getFacade() {
        return ejbFacade;
    }

    public ArchivorendicionFacade getEjbFacadeComprobantes() {
        return ejbFacadeComprobantes;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(1000000) {

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
        current = (Rendicionexterna) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Rendicionexterna();
        selectedItemIndex = -1;
        return "CrearRendicionExterna";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionexternaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Rendicionexterna) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionexternaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Rendicionexterna) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionexternaDeleted"));
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

    @FacesConverter(forClass = Rendicionexterna.class)
    public static class RendicionexternaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RendicionexternaController controller = (RendicionexternaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rendicionexternaController");
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
            if (object instanceof Rendicionexterna) {
                Rendicionexterna o = (Rendicionexterna) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Rendicionexterna.class.getName());
            }
        }

    }
    
    public void agregarComprobanteARendicionExterna(){
        
        if(null == comprobanteSeleccionado){
            System.out.println("comprobanteSeleccionado nulo");
        }
        
        // agregamos el comprobante seleccionado a la lista de comprobantes en la rendicion externa
        this.getSelected().getArchivorendicionList().add(comprobanteSeleccionado);
    }
    
    public void removerComprobanteDeRendicionExterna(Archivorendicion archivo) {

        // se quita de la lista de solicitados
//        Iterator i = this.itemsSolicitados.iterator();
//        while(i.hasNext()){
//            if(((Solicitud)i.next()).getPresupuestotarea().equals(solicitud.getPresupuestotarea())){
//                i.remove();
//            }
//        }
        this.getSelected().getArchivorendicionList().remove(archivo);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El comprobante del proveedor: " + comprobanteSeleccionado.getProveedor() + " - Nº: " + comprobanteSeleccionado.getNrofactura() + " fue borrado"));

    }

}
