package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.EstadosolicitudFacade;
import ar.edu.undec.nortia.controller.SolicitudFacade;
import ar.edu.undec.nortia.model.Solicitudacta;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.SolicitudactaFacade;
import ar.edu.undec.nortia.model.Estadosolicitud;
import ar.edu.undec.nortia.model.Proyecto;
import ar.edu.undec.nortia.model.Solicitud;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "solicitudactaController")
@SessionScoped
public class SolicitudactaController implements Serializable {

    private Solicitudacta current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.SolicitudactaFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.SolicitudFacade ejbFacades;
    @EJB
    EstadosolicitudFacade ejbFacadeestado;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Solicitud> listaSolicitudes;

    private List<Solicitud> listaSolicitudesSeleccionadas;
    
    private List<Solicitud> listaSolicitudesAprobadas = new ArrayList<Solicitud>();

    private Solicitud solicitudActual;

    public SolicitudactaController() {
    }

    public Solicitudacta getSelected() {
        if (current == null) {
            current = new Solicitudacta();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Solicitudacta solicitudacta) {
        current = solicitudacta;
    }

    private SolicitudactaFacade getFacade() {
        return ejbFacade;
    }

    public SolicitudFacade getFacades() {
        return ejbFacades;
    }

    public EstadosolicitudFacade getEjbFacadeestado() {
        return ejbFacadeestado;
    }

    public List<Solicitud> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public List<Solicitud> getListaSolicitudesSeleccionadas() {
        return listaSolicitudesSeleccionadas;
    }

    public void setListaSolicitudesSeleccionadas(List<Solicitud> listaSolicitudesSeleccionadas) {
        this.listaSolicitudesSeleccionadas = listaSolicitudesSeleccionadas;
    }
    
    public List<Solicitud> getListaSolicitudesAprobadas() {
        return listaSolicitudesAprobadas;
    }

    public void setListaSolicitudesAprobadas(List<Solicitud> listaSolicitudesAprobadas) {
        this.listaSolicitudesAprobadas = listaSolicitudesAprobadas;
    }

    public Solicitud getSolicitudActual() {
        return solicitudActual;
    }

    public void setSolicitudActual(Solicitud solicitudActual) {
        this.solicitudActual = solicitudActual;
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
        return "ListPorProyecto";
    }

    public String prepareView() {
        //current = (Solicitudacta) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Solicitudacta();
        selectedItemIndex = -1;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        //SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);
        RendicionController rendicioncontroller = (RendicionController) context.getApplication().evaluateExpressionGet(context, "#{rendicionController}", RendicionController.class);
        PresupuestoController presupuestocontroller = (PresupuestoController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoController}", PresupuestoController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);

        // Seteamos el presupuesto
        presupuestocontroller.findProyecto(proyectocontroller.getSelected().getId());
        presupuestocontroller.sumarGastosView();

        // Seteamos el tree de etapas y tareas para el proyecto actual
        etapacontroller.armarTreeEtapasYTareasPorProyecto();

        // armamos el arbol de nodos de presupuesto tarea
        presupuestotareacontroller.armarPresupuestoGeneral();

        // Seteamos las rendiciones para los indicadores
        rendicioncontroller.prepararRendicion();

        // Llenamos la lista de solicitudes que no fueron aprobadas
        listaSolicitudes = getFacades().obtenerIniciadasPorProyecto(proyectocontroller.getSelected().getId());
        
        // Vaciamos la lista de solicitudes seleccionadas
        listaSolicitudesSeleccionadas = new ArrayList<Solicitud>();      

        // Seteamos la lista de desembolsos para el proyecto actual
        desembolsocontroller.obtenerPorProyecto(proyectocontroller.getSelected().getId());

        for (Solicitud s : listaSolicitudes) {
            System.out.println("SolicitudActaController - prepareCreate");
            System.out.println(s.toString());
        }

        return "CreateSolicitudActa";
    }

    public String create() {
        try {

            // Persistimos el Acta de Solicitud
            current.setFecha(new Date());
            getFacade().createWithPersist(current);

            // Para cada Solicitud seleccionada
            for (Solicitud s : listaSolicitudesSeleccionadas) {
                // dependiendo del estado que se le dio a cada solicitud
                switch (s.getEstadosolicitudid().getId()) {
                    // Solicitud Aprobada >> guardar la fecha de aprobacion, el acta de solicitud y PERSISTIR
                    case 2:
                        s.setSolicitudactaid(current);
                        s.setFechaaprobacion(new Date());
                        getFacades().edit(s);
                        break;
                    // Solicitud Rechazada >> guardar la fecha de "rechazo" y PERSISTIR
                    case 3:
                        s.setFechaaprobacion(new Date());
                        getFacades().edit(s);
                        break;
                    default:
                        s.setFechaaprobacion(new Date());
                        getFacades().edit(s);
                        break;
                }
            }
           /* if(!this.listaSolicitudesAprobadas.isEmpty()){
                System.out.println("##########Entrooo");
                RequestContext.getCurrentInstance().execute("PF('d1final').show()");                
                //JsfUtil.addSuccessMessage("Evaluación de Solicitudes Exitosa!"); 
            }         
            else{
                System.out.println("########## NO Entrooo");
            }*/
            RequestContext.getCurrentInstance().execute("PF('d1final').show()"); 
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void agregarAprobada() {

        // obtenermos el estado de la solicitud "Aprobada" id=2
        Estadosolicitud estadoAprobada;

        try {
            estadoAprobada = this.ejbFacadeestado.find(2);
        } catch (Exception e) {
            estadoAprobada = null;
            e.printStackTrace();
        }
        
        // cambiamos el estado de la solicitud agregada
        this.solicitudActual.setEstadosolicitudid(estadoAprobada);

        // damos la fecha de aprobacion
        this.solicitudActual.setFechaaprobacion(new Date());
        
        //Agregamos para el reporte
        this.listaSolicitudesAprobadas.add(solicitudActual);

        // agregamos a la lista
        this.listaSolicitudesSeleccionadas.add(solicitudActual);

        // quitamos de la lista de disponibles
        this.listaSolicitudes.remove(solicitudActual);        
      
    }

    public void agregarRechazada() {

        // obtenermos el estado de la solicitud "Rechazada" id=3
        Estadosolicitud estadoRechazada;

        try {
            estadoRechazada = this.ejbFacadeestado.find(3);
        } catch (Exception e) {
            estadoRechazada = null;
            e.printStackTrace();
        }

        // cambiamos el estado de la solicitud agregada
        this.solicitudActual.setEstadosolicitudid(estadoRechazada);

        // damos la fecha de rechazo
        this.solicitudActual.setFechaaprobacion(new Date());

        // agregamos a la lista de seleccionadas
        this.listaSolicitudesSeleccionadas.add(solicitudActual);

        // quitamos de la lista de disponibles
        this.listaSolicitudes.remove(solicitudActual);

    }

    public void eliminarDeLista(Solicitud solicitud) {

        // obtenermos el estado de la solicitud "Iniciada" id=1
        Estadosolicitud estadoIniciada;

        try {
            estadoIniciada = this.ejbFacadeestado.find(1);
        } catch (Exception e) {
            estadoIniciada = null;
            e.printStackTrace();
        }

        // cambiamos el estado de la solicitud eliminada
        solicitud.setEstadosolicitudid(estadoIniciada);

        // cambiamos la fecha de aprobacion a null y vaciamos la observacion
        solicitud.setFechaaprobacion(null);
        solicitud.setObsevaluacion("");

        //Qiutamos para el reporte
        this.listaSolicitudesAprobadas.remove(solicitud);
        
        // la quitamos de la lista
        this.listaSolicitudesSeleccionadas.remove(solicitud);

        // la agregamos nuevamente a los disponibles
        this.listaSolicitudes.add(solicitud);
        
    }

    public float sumarAprobadas() {

        float r = 0f;

        if (listaSolicitudesSeleccionadas != null && listaSolicitudesSeleccionadas.size() > 0) {
            for (Solicitud s : listaSolicitudesSeleccionadas) {
                // si esta aprobada, sumamos su valor
                if (s.getEstadosolicitudid().getId().equals(2)) {
                    r = r + s.getImporte().floatValue();
                }
            }
        }

        return r;
    }

    public float sumarRechazadas() {
        float r = 0f;

        if (listaSolicitudesSeleccionadas != null && listaSolicitudesSeleccionadas.size() > 0) {
            for (Solicitud s : listaSolicitudesSeleccionadas) {
                // si esta rechazada, sumamos su valor
                if (s.getEstadosolicitudid().getId().equals(3)) {
                    r = r + s.getImporte().floatValue();
                }
            }
        }

        return r;
    }

    public String prepareEdit() {
        //current = (Solicitudacta) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudactaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Solicitudacta) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudactaDeleted"));
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

    @FacesConverter(forClass = Solicitudacta.class)
    public static class SolicitudactaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SolicitudactaController controller = (SolicitudactaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "solicitudactaController");
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
            if (object instanceof Solicitudacta) {
                Solicitudacta o = (Solicitudacta) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Solicitudacta.class.getName());
            }
        }

    }

    public void obtenerPorProyecto(int proyectoid) {
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoid));
    }
    
    public void pdfSolicitudesEvaluadasAnticipos() throws JRException, IOException {           
        FacesContext context = FacesContext.getCurrentInstance();
        IndicadoresController indicadorescontroller = (IndicadoresController) context.getApplication().evaluateExpressionGet(context, "#{indicadoresController}", IndicadoresController.class);        
         // Obtengo la ruta absoluta del archivo compilado del reporte
        String rutaJasper = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/secure/reportes/solicitudesEvaluadas.jasper");
        
        JRDataSource solicitudes = new JRBeanCollectionDataSource(this.listaSolicitudesAprobadas);
        indicadorescontroller.calcularSaldosPorRubro();
        //HashMap<String, Float> saldosRubro= indicadorescontroller.getListaSaldosRubro();        
        List<String> rubros= new ArrayList<String>(indicadorescontroller.getListaSaldosRubro().keySet());        
        List<Float> saldosRubro = new ArrayList<Float>(indicadorescontroller.getListaSaldosRubro().values()); 
        float totalSolicitado= 0.0F;
        float totalItem= 0.0F;
        float gastado= 0.0F;
        float saldo= 0.0F;
        int index=-1;     
        
        for(Solicitud s: listaSolicitudesAprobadas){  
            for(Solicitud soli: this.ejbFacades.obtenerPorPresupuestoTareas(s.getPresupuestotarea().getId())){
                gastado+=soli.getImporte().floatValue();
            }
            totalItem += s.getPresupuestotarea().getTotal().floatValue();
            totalSolicitado+= s.getImporte().floatValue();
            for(String r: rubros){
                if(s.getPresupuestotarea().getRubro().getAbreviado().equals(r.toString())){
                    index = rubros.indexOf(r);
                    saldo = saldosRubro.get(index);
                }
            }
        }
        
        //Agregando los parametros
        Hashtable<String, Object> parametros = new Hashtable<String, Object>();
        parametros.put("solicitudes", solicitudes);
        parametros.put("total", totalSolicitado);
        parametros.put("saldoItem", totalItem - gastado);
        parametros.put("saldoRubro", saldo);
        
        // Llenamos el reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(rutaJasper, parametros, new JREmptyDataSource());

        // Generamos el archivo a descargar
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=solicitudesEvaluadas.pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        FacesContext.getCurrentInstance().responseComplete();        
    }

}
