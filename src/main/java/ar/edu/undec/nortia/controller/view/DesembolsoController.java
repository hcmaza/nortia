package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.Desembolso;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.DesembolsoFacade;
import ar.edu.undec.nortia.controller.EstadoproyectoFacade;
import ar.edu.undec.nortia.controller.ProyectoFacade;
import ar.edu.undec.nortia.model.Estadoproyecto;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "desembolsoController")
@SessionScoped
public class DesembolsoController implements Serializable {

    private Desembolso current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.DesembolsoFacade ejbFacade;
    
    @EJB
    private ar.edu.undec.nortia.controller.ProyectoFacade proyectoFacade;
    
    @EJB
    private ar.edu.undec.nortia.controller.EstadoproyectoFacade estadoproyectoFacade;
    
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public DesembolsoController() {
    }

    public ProyectoFacade getProyectoFacade() {
        return proyectoFacade;
    }

    public EstadoproyectoFacade getEstadoproyectoFacade() {
        return estadoproyectoFacade;
    }

    public Desembolso getSelected() {
        if (current == null) {
            current = new Desembolso();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Desembolso desembolso){
        current = desembolso;
        
        System.out.println("DESEMBOLSO setSelected: " + current.getId());
    }

    private DesembolsoFacade getFacade() {
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
        return "ListDesembolsosPorProyecto";
    }

    public String prepareView() {
        //current = (Desembolso) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Desembolso();
        selectedItemIndex = -1;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoController presupuestocontroller = (PresupuestoController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoController}", PresupuestoController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);

        // Buscar presupuesto por proyecto
        presupuestocontroller.findProyecto(proyectocontroller.getSelected().getId());

        // Sumar los gastos del presupuesto
        presupuestocontroller.sumarGastosView();

        // Seteamos el tree de etapas y tareas para el proyecto actual
        etapacontroller.armarTreeEtapasYTareasPorProyecto();

        // Armar presupuesto general
        presupuestotareacontroller.armarPresupuestoGeneral();

        return "CreateDesembolso";
    }

    public String create() {
        try {
            
            FacesContext context = FacesContext.getCurrentInstance();
            ProyectoController proyectocontroller= (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
            
            /* PERSISTENCIA DEL DESEMBOLSO */
            
            // obtenermos el proyecto actual y lo guardamos en el desembolso a guardar
            current.setProyectoid(proyectocontroller.getSelected());
            
            // fecha de carga actual
            current.setFechacarga(new Date());
            
            // persistimos el desembolso
            getFacade().create(current);
            
            /* CAMBIO EN EL ESTADO DE PROYECTO */
            // buscamos el estado de proyecto [Proyecto en Ejecucion = 12]
            Estadoproyecto estado = getEstadoproyectoFacade().buscarPorId(12);
            
            // cambiamos el estado del proyecto a en ejecucion
            proyectocontroller.getSelected().setEstadoproyectoid(estado);
            
            // actualizamos el estado del proyecto
            this.getProyectoFacade().edit(proyectocontroller.getSelected());
            
            JsfUtil.addSuccessMessage("El Desembolso se ha creado con éxito");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        //current = (Desembolso) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DesembolsoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Desembolso) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "ListPorProyecto";
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DesembolsoDeleted"));
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

    @FacesConverter(forClass = Desembolso.class)
    public static class DesembolsoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DesembolsoController controller = (DesembolsoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "desembolsoController");
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
            if (object instanceof Desembolso) {
                Desembolso o = (Desembolso) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Desembolso.class.getName());
            }
        }

    }
    
    /**
     * Llena la coleccion de desembolsos con los desembolsos de un proyecto determinado
     * 
     * @param proyectoId 
     */
    public void obtenerPorProyecto(int proyectoId){
        
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoId));
    }
    
    
    /**
     * Suma y devuelve la suma de todos los desembolsos en la lista
     * 
     * @return 
     */
    public float sumarDesembolsos(){
        float resultado = 0;
        Iterator i = items.iterator();
        
        while(i.hasNext()){
            resultado += ((Desembolso)i.next()).getMonto().floatValue();
        }
        
        return resultado;
        
    }

    public float sumarDesembolsosPorProyecto(int proyectoId){
        float resultado = 0;

        List<Desembolso> listaDesembolsos = this.getFacade().obtenerPorProyecto(proyectoId);
        Iterator<Desembolso> i = listaDesembolsos.iterator();
        while(i.hasNext()){
            resultado += (i.next()).getMonto().floatValue();
        }

        return resultado;

    }

}
