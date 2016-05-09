package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.Modificacionpresupuesto;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.ModificacionpresupuestoFacade;
import ar.edu.undec.nortia.controller.PresupuestoTareaFacade;
import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.PresupuestoTarea;
import ar.edu.undec.nortia.model.Solicitud;
import ar.edu.undec.nortia.model.Tarea;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "modificacionpresupuestoController")
@SessionScoped
public class ModificacionpresupuestoController implements Serializable {

    private Modificacionpresupuesto current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.ModificacionpresupuestoFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.PresupuestoTareaFacade pstFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private TreeNode root = new DefaultTreeNode();
    
    private List<Modificacionpresupuesto> modificaciones;
    private List<PresupuestoItem> itemsPresupuesto;
    
    private PresupuestoItem piSeleccionado;
    
    private Float presupuestoDisponible = 0f;
    private Float presupuestoDiferencia = 0f;
    
    private String operacion = "";
    
    // Totales
    // Suma Total de Solicitudes
    private Float totalSolicitudes;
    
    // Suma Total de Disponible (SinModificaciones)
    private Float totalSinModificaciones;
    
    // Suma Total de Modificaciones
    private Float totalModificaciones;
    
    // Suma Total de Importes finales
    private Float totalImportesFinales;
    
    

    public Float getTotalSolicitudes() {
        return totalSolicitudes;
    }

    public void setTotalSolicitudes(Float totalSolicitudes) {
        this.totalSolicitudes = totalSolicitudes;
    }

    public Float getTotalSinModificaciones() {
        return totalSinModificaciones;
    }

    public void setTotalSinModificaciones(Float totalSinModificaciones) {
        this.totalSinModificaciones = totalSinModificaciones;
    }

    public Float getTotalModificaciones() {
        return totalModificaciones;
    }

    public void setTotalModificaciones(Float totalModificaciones) {
        this.totalModificaciones = totalModificaciones;
    }

    public Float getTotalImportesFinales() {
        return totalImportesFinales;
    }

    public void setTotalImportesFinales(Float totalImportesFinales) {
        this.totalImportesFinales = totalImportesFinales;
    }
    

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }
    
    public Float getPresupuestoDisponible() {
        return presupuestoDisponible;
    }

    public void setPresupuestoDisponible(Float presupuestoDisponible) {
        this.presupuestoDisponible = presupuestoDisponible;
    }

    public Float getPresupuestoDiferencia() {
        return presupuestoDiferencia;
    }

    public void setPresupuestoDiferencia(Float presupuestoDiferencia) {
        this.presupuestoDiferencia = presupuestoDiferencia;
    }
    public String obtenerEstiloDiferencia() {
        
        // Si el valor es 0 > color gris
        if(presupuestoDiferencia.equals(0f)){
            return "colorGris";
        }
        
        // si el valor es positivo o negativo, cambiar el color
        if(presupuestoDiferencia > 0){
            return "colorVerde";
        } else {
            return "colorRojo";
        }
    }

    public PresupuestoItem getPiSeleccionado() {
        return piSeleccionado;
    }

    public void setPiSeleccionado(PresupuestoItem piSeleccionado) {
        this.piSeleccionado = piSeleccionado;
    }

    public List<PresupuestoItem> getItemsPresupuesto() {
        
        if(itemsPresupuesto == null){
            itemsPresupuesto = new ArrayList<PresupuestoItem>();
        }
        
        return itemsPresupuesto;
    }

    public TreeNode getRoot() {
        return root;
    }

    public List<Modificacionpresupuesto> getModificaciones() {
        
        if(modificaciones == null){
            modificaciones = new ArrayList<Modificacionpresupuesto>();
        }
        
        return modificaciones;
    }

    public void setModificaciones(List<Modificacionpresupuesto> modificaciones) {
        this.modificaciones = modificaciones;
    }

    public ModificacionpresupuestoController() {
    }

    public Modificacionpresupuesto getSelected() {
        if (current == null) {
            current = new Modificacionpresupuesto();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ModificacionpresupuestoFacade getFacade() {
        return ejbFacade;
    }
    
    private PresupuestoTareaFacade getPstFacade() {
        return pstFacade;
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
        return "ListModificacionesPorProyecto";
    }
    
    public void obtenerPorProyecto(int proyectoid) {
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoid));
    }

    public String prepareView() {
        current = (Modificacionpresupuesto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Modificacionpresupuesto();
        selectedItemIndex = -1;
        
        // importe del presupuesto diponible en 0
        presupuestoDisponible = 0f;
        
        // importe de la diferencia de presupuesto por modificaciones en 0
        presupuestoDiferencia = 0f;

        FacesContext context = FacesContext.getCurrentInstance();
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        
        // obtener y crear las listas de todas las solicitudes, desembolsos y rendiciones del proyecto
        solicitudcontroller.armarSolicitudesDesembolsosYRendicion();
        
        // armar el arbol de presupuesto
        armarModificacionesPresupuestoNodos();
        
        // operacion inicial
        this.setOperacion("suma");

        // preparar indicadores TEMPORALES para ejemplo
        IndicadoresController indicadorescontroller = (IndicadoresController) context.getApplication().evaluateExpressionGet(context, "#{indicadoresController}", IndicadoresController.class);
        indicadorescontroller.obtenerCalculos();
        //indicadorescontroller.crearModeloLinealEvolucionDesembolsosEjecuciones();
        
        return "CreateModificacionPresupuesto";
    }

    public String create() {
        try {
            
            // Modificar las propiedades de los objetos PresupuestoTarea correspondientes y Persistir
            for(Modificacionpresupuesto modificacion : this.getModificaciones()){
                
                // modificar el valor de presupuesto del presupuesto tarea de acuerdo al valor de la modificacion
                PresupuestoTarea pst = modificacion.getPresupuestotareaid();
                pst.setTotal(pst.getTotal().add(modificacion.getModificacion()));
                
                // actualizamos la bd
                getPstFacade().edit(pst);
                
                // persistimos la modificacion
                getFacade().create(modificacion);
                
            }
            
            // Persistir las ModificacionPresupuesto
            
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModificacionpresupuestoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Modificacionpresupuesto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModificacionpresupuestoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Modificacionpresupuesto) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModificacionpresupuestoDeleted"));
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

    @FacesConverter(forClass = Modificacionpresupuesto.class)
    public static class ModificacionpresupuestoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ModificacionpresupuestoController controller = (ModificacionpresupuestoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "modificacionpresupuestoController");
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
            if (object instanceof Modificacionpresupuesto) {
                Modificacionpresupuesto o = (Modificacionpresupuesto) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Modificacionpresupuesto.class.getName());
            }
        }

    }
    
    /**
     * Arma el arbol de nodos del presupuesto que se muestra en la treetable, 
     * se wrappean objetos PresupuestoTarea en objetos PresupuestoItem donde
     * se calculan los importe y totales correspondientes a las solicitudes y modificaciones
     * que sufrio el presupuesto de cada item determinado.
     * 
     */
    public void armarModificacionesPresupuestoNodos() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        EtapaController etapaController = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        
        SolicitudController solicitudController = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        
        
        
        BigDecimal sumaTotalSolicitudesPst = BigDecimal.ZERO;
        BigDecimal sumaTotalModificacionesPst = BigDecimal.ZERO;
        
        BigDecimal sumaTotalTarea = BigDecimal.ZERO;
        BigDecimal sumaTotalSolicitudesTarea = BigDecimal.ZERO;
        BigDecimal sumaTotalModificacionesTarea = BigDecimal.ZERO;

        BigDecimal sumaTotalEtapa = BigDecimal.ZERO;
        BigDecimal sumaTotalSolicitudesEtapa = BigDecimal.ZERO;
        BigDecimal sumaTotalModificacionesEtapa = BigDecimal.ZERO;
        
        BigDecimal sumaTotalSolicitudes = BigDecimal.ZERO;
        BigDecimal sumaTotalDisponible = BigDecimal.ZERO;
        BigDecimal sumaTotalModificaciones = BigDecimal.ZERO;
        BigDecimal sumaTotalImportesFinales = BigDecimal.ZERO;

        List<PresupuestoItem> sumatoriaPorEtapa = new ArrayList<PresupuestoItem>();
        List<PresupuestoItem> sumatoriaPorTarea = new ArrayList<PresupuestoItem>();
        List<PresupuestoItem> sumatoriaPorPst = new ArrayList<PresupuestoItem>();

        int ie = 0;
        int it = 0;
        int ip = 0;
        
        for (Etapa etapa : etapaController.getEtapas()) {

            sumaTotalEtapa = BigDecimal.ZERO;
            sumaTotalSolicitudesEtapa = BigDecimal.ZERO;
            sumaTotalModificacionesEtapa = BigDecimal.ZERO;

            for (Tarea tarea : etapa.getTareaList()) {

                sumaTotalTarea = BigDecimal.ZERO;
                sumaTotalSolicitudesTarea = BigDecimal.ZERO;
                sumaTotalModificacionesTarea = BigDecimal.ZERO;
                
                for (PresupuestoTarea psTarea : tarea.getPresupuestoTareaList()) {

                    // acumulador de solicitudes por presupuesto_tarea = 0
                    sumaTotalSolicitudesPst = BigDecimal.ZERO;

                    // Verificar si hay solicitudes anteriores para el presupuesto_tarea
                    for(Solicitud s : solicitudController.getItemsAprobados() ){
                        
                        if(psTarea.getId().equals(s.getPresupuestotarea().getId())){
                            sumaTotalSolicitudesPst = sumaTotalSolicitudesPst.add(s.getImporte());
                        }
                    }
                    
                    // acumulador de modificaciones por presupuesto_tarea = 0
                    sumaTotalModificacionesPst = BigDecimal.ZERO;
                   
                    //  Verificar si hay modificaciones para el presupuesto_tarea
                    for(Modificacionpresupuesto mp : this.getModificaciones()){
                        
                        if(mp.getPresupuestotareaid().getId().equals(psTarea.getId())){
                            sumaTotalModificacionesPst = sumaTotalModificacionesPst.add(mp.getModificacion());
                        }
                    }
                    
                    PresupuestoItem pi3 = this.new PresupuestoItem();
                    pi3.setPresupuestoTarea(psTarea);
                    pi3.setDescripcion(psTarea.getDescripcion());
                    pi3.setImporteOriginal(psTarea.getTotal().floatValue());
                    pi3.setImporteSolicitudes(sumaTotalSolicitudesPst.floatValue());
                    pi3.setImporteSinModificacion(pi3.getImporteOriginal() - pi3.getImporteSolicitudes());
                    pi3.setImporteModificacion(sumaTotalModificacionesPst.floatValue());
                    pi3.setImporteFinal(pi3.getImporteSinModificacion() + pi3.getImporteModificacion());

                    sumaTotalTarea = sumaTotalTarea.add(psTarea.getTotal());
                    sumaTotalSolicitudesTarea = sumaTotalSolicitudesTarea.add(sumaTotalSolicitudesPst);
                    sumaTotalModificacionesTarea = sumaTotalModificacionesTarea.add(sumaTotalModificacionesPst);
                    
                    sumatoriaPorPst.add(ip, pi3);
                    
                    ip++;
                
                }
                
                sumaTotalEtapa = sumaTotalEtapa.add(sumaTotalTarea);
                sumaTotalSolicitudesEtapa = sumaTotalSolicitudesEtapa.add(sumaTotalSolicitudesTarea);
                sumaTotalModificacionesEtapa = sumaTotalModificacionesEtapa.add(sumaTotalModificacionesTarea);
                
                PresupuestoItem pi2 = this.new PresupuestoItem();
                
                PresupuestoTarea pst2 = new PresupuestoTarea();
                pst2.setTarea(tarea);
                pst2.setDescripcion("Tarea: " + tarea.getTarea());
                pi2.setPresupuestoTarea(pst2);
                
                //pi2.setDescripcion("Tarea: " + tarea.getTarea());
                pi2.setImporteOriginal(sumaTotalTarea.floatValue());
                pi2.setImporteSolicitudes(sumaTotalSolicitudesTarea.floatValue());
                pi2.setImporteSinModificacion(pi2.getImporteOriginal() - pi2.getImporteSolicitudes());
                pi2.setImporteModificacion(sumaTotalModificacionesTarea.floatValue());
                pi2.setImporteFinal(pi2.getImporteSinModificacion() + sumaTotalModificacionesTarea.floatValue());
                
                sumatoriaPorTarea.add(it, pi2);
                it++;

            }
            
            PresupuestoItem pi = this.new PresupuestoItem();
            
            PresupuestoTarea pst = new PresupuestoTarea();
            Tarea t = new Tarea();
            t.setEtapaid(etapa);
            pst.setTarea(t);
            pst.setDescripcion("Etapa: " + etapa.getEtapa());
            pi.setPresupuestoTarea(pst);
            
            pi.setImporteOriginal(sumaTotalEtapa.floatValue());
            pi.setImporteSolicitudes(sumaTotalSolicitudesEtapa.floatValue());
            pi.setImporteSinModificacion(pi.getImporteOriginal() - pi.getImporteSolicitudes());
            pi.setImporteModificacion(sumaTotalModificacionesEtapa.floatValue());
            pi.setImporteFinal(pi.getImporteSinModificacion() + sumaTotalModificacionesEtapa.floatValue());

            // Totales Generales
            sumaTotalSolicitudes = sumaTotalSolicitudes.add(sumaTotalSolicitudesEtapa);
            sumaTotalDisponible = sumaTotalDisponible.add(new BigDecimal(pi.getImporteSinModificacion()));
            sumaTotalModificaciones = sumaTotalModificaciones.add(sumaTotalModificacionesEtapa);
            sumaTotalImportesFinales = sumaTotalImportesFinales.add(new BigDecimal(pi.getImporteFinal()));
            
            sumatoriaPorEtapa.add(ie, pi);

            ie++;

        }
        
        // Totales Generales a Propiedades del Managed Bean
        this.setPresupuestoDisponible(sumaTotalDisponible.floatValue());
        this.setTotalSolicitudes(sumaTotalSolicitudes.floatValue());
        this.setTotalSinModificaciones(sumaTotalDisponible.floatValue());
        this.setTotalModificaciones(sumaTotalModificaciones.floatValue());
        this.setTotalImportesFinales(sumaTotalImportesFinales.floatValue());
        
        root = new DefaultTreeNode(new PresupuestoItem(), null);
        root.setExpanded(false);

        ie = 0;
        it = 0;
        ip = 0;
        
        for (Etapa etapa : etapaController.getEtapas()) {

            TreeNode et = new DefaultTreeNode(sumatoriaPorEtapa.get(ie), root);
            et.setExpanded(false);

            for (Tarea tarea : etapa.getTareaList()) {
                
                TreeNode tar = new DefaultTreeNode(sumatoriaPorTarea.get(it), et);
                tar.setExpanded(false);
                
                for (PresupuestoTarea p : tarea.getPresupuestoTareaList()) {
                    //TreeNode tp = new DefaultTreeNode(p, tar);
                    
                    TreeNode tp = new DefaultTreeNode(sumatoriaPorPst.get(ip), tar);
                    
                    ip++;
                }
                it++;
            }
            ie++;
        }
        
        itemsPresupuesto = sumatoriaPorPst;

        System.out.println("FIN ARMAR ARBOL");

    }
    
    /**
     * Clase Wrapper usada para la generación del arbol de items del presupuesto. 
     * 
     * La clase interna es PresupuestoTarea. 
     * Aqui se generan la suma de importes del presupuesto original, 
     * las solicitudes, los desembolsos, las rendiciones y modificaciones anteriores.
     * 
     */
    public class PresupuestoItem{
        
        private PresupuestoTarea presupuestoTarea;
        private String descripcion;
        private Float importeOriginal;
        private Float importeSolicitudes;
        private Float importeSinModificacion;
        private Float importeModificacion;
        private Float importeFinal;

        public PresupuestoTarea getPresupuestoTarea() {
            return presupuestoTarea;
        }

        public void setPresupuestoTarea(PresupuestoTarea presupuestoTarea) {
            this.presupuestoTarea = presupuestoTarea;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Float getImporteOriginal() {
            return importeOriginal;
        }

        public void setImporteOriginal(Float importeOriginal) {
            this.importeOriginal = importeOriginal;
        }

        public Float getImporteSolicitudes() {
            return importeSolicitudes;
        }

        public void setImporteSolicitudes(Float importeSolicitudes) {
            this.importeSolicitudes = importeSolicitudes;
        }

        public Float getImporteSinModificacion() {
            return importeSinModificacion;
        }

        public void setImporteSinModificacion(Float importeSinModificacion) {
            this.importeSinModificacion = importeSinModificacion;
        }

        public Float getImporteModificacion() {
            return importeModificacion;
        }

        public void setImporteModificacion(Float importeModificacion) {
            System.out.println("setImporteModificacion 1 >> " + importeModificacion);
            this.importeModificacion = importeModificacion;
            System.out.println("setImporteModificacion 2 >> " + this.getImporteModificacion());
        }

        public Float getImporteFinal() {
            return importeFinal;
        }

        public void setImporteFinal(Float importeFinal) {
            this.importeFinal = importeFinal;
        }
    }
    
    public void agregarModificacion(){
   
        Modificacionpresupuesto modificacion = new Modificacionpresupuesto();
        modificacion.setPresupuestotareaid(piSeleccionado.getPresupuestoTarea());
        modificacion.setFecha(new Date());
        
        // obtenemos el importe de la modificacion de PresupuestoItem seleccionado y lo negamos
        System.out.println("agregarModificacionSumar >> piSeleccionado.getImporteModificacion = " + piSeleccionado.getImporteModificacion());
        BigDecimal importeModificacion = new BigDecimal(piSeleccionado.getImporteModificacion());
        System.out.println("agregarModificacionSumar >> importeModificacion = " + importeModificacion.floatValue());
        
        if(this.getOperacion().equals("resta")){
            importeModificacion = importeModificacion.negate();
        }
        
        modificacion.setModificacion(importeModificacion);

        this.getModificaciones().add(modificacion);
        
        // calculamos la diferencia en el presupuesto
        presupuestoDiferencia = presupuestoDiferencia - importeModificacion.floatValue();
        
        // llamamos al método para rearmar el arbol de nodos de presupuesto
        armarModificacionesPresupuestoNodos();
    }
    
    // Agregamos una modificacion que suma presupuesto a un item
    public void agregarModificacionSumar(){
   
        Modificacionpresupuesto modificacion = new Modificacionpresupuesto();
        modificacion.setPresupuestotareaid(piSeleccionado.getPresupuestoTarea());
        modificacion.setFecha(new Date());
        
        // obtenemos el importe de la modificacion de PresupuestoItem seleccionado y lo negamos
        System.out.println("agregarModificacionSumar >> piSeleccionado.getImporteModificacion = " + piSeleccionado.getImporteModificacion());
        BigDecimal importeModificacion = new BigDecimal(piSeleccionado.getImporteModificacion());
        System.out.println("agregarModificacionSumar >> importeModificacion = " + importeModificacion.floatValue());
        modificacion.setModificacion(importeModificacion);

        this.getModificaciones().add(modificacion);
        
        // calculamos la diferencia en el presupuesto
        presupuestoDiferencia = presupuestoDiferencia - piSeleccionado.importeModificacion;
        
        // llamamos al método para rearmar el arbol de nodos de presupuesto
        armarModificacionesPresupuestoNodos();
    }
    
    // Agregamos una modificacion que resta presupuesto a un item
    public void agregarModificacionQuitar(){
        
        Modificacionpresupuesto modificacion = new Modificacionpresupuesto();
        modificacion.setPresupuestotareaid(piSeleccionado.getPresupuestoTarea());
        modificacion.setFecha(new Date());

        // obtenemos el importe de la modificacion de PresupuestoItem seleccionado y lo negamos
        BigDecimal importeModificacion = new BigDecimal(piSeleccionado.getImporteModificacion());
        importeModificacion = importeModificacion.negate();
        System.out.println("agregarModificacionQuitar >> importeModificacion = " + importeModificacion.floatValue());
        modificacion.setModificacion(importeModificacion);

        this.getModificaciones().add(modificacion);
        
        // calculamos la diferencia en el presupuesto
        presupuestoDiferencia = presupuestoDiferencia + piSeleccionado.importeModificacion;
        
        // llamamos al método para rearmar el arbol de nodos de presupuesto
        armarModificacionesPresupuestoNodos();
    }
    
    // Quitamos una modificacion del presupuesto
    public void quitarModificacion(Modificacionpresupuesto modificacion) {
        
        // calculamos la diferencia en el presupuesto
        presupuestoDiferencia = presupuestoDiferencia + modificacion.getModificacion().floatValue();

        // se quita de la lista de modificaciones
        Iterator i = this.modificaciones.iterator();
        
        while(i.hasNext()){
            if(((Modificacionpresupuesto) i.next()).getPresupuestotareaid().equals(modificacion.getPresupuestotareaid())) {
                i.remove();
            }
        }
                
        // llamamos al método para rearmar el arbol de nodos de presupuesto
        armarModificacionesPresupuestoNodos();
    }
    
    // Suma de las modificaciones agregadas
    public Float sumarModificaciones() {
        float r = 0;

        for (Modificacionpresupuesto p : modificaciones) {
            r += p.getModificacion().floatValue();
        }

        return r;
    }

    // método que calcula el importe final de un item de presupuesto, resta o suma segun corresponda
    public void calcularImporteFinal(){
        
        if(this.getOperacion().equals("resta")){
            this.piSeleccionado.setImporteModificacion(- this.piSeleccionado.getImporteModificacion());
        }
        
        this.piSeleccionado.setImporteFinal(this.piSeleccionado.getImporteSinModificacion() + this.piSeleccionado.getImporteModificacion());
        
        
    }

}
