package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.RubroFacade;
import ar.edu.undec.nortia.model.TareaAgente;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.TareaAgenteFacade;
import ar.edu.undec.nortia.model.Agente;
import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.PresupuestoTarea;
import ar.edu.undec.nortia.model.ProyectoAgente;
import ar.edu.undec.nortia.model.Rubro;
import ar.edu.undec.nortia.model.Tarea;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.validation.constraints.Null;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

@ManagedBean(name = "tareaAgenteController")
@SessionScoped
public class TareaAgenteController implements Serializable {

    private TareaAgente current;
    private TareaAgente current2;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.TareaAgenteFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.ProyectoAgenteFacade ejbProyectoAgenteFacade;
    @EJB
    private RubroFacade ejbFacadeRubro;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<TareaAgente> tareasagentes;
//    @ManagedProperty(value="#{etapaController}")
//    private EtapaController etapaController;

    public TareaAgenteController() {
    }

    public TareaAgente getSelected() {
        if (current == null) {
            current = new TareaAgente();
            //current.setTareaAgentePK(new ar.edu.undec.nortia.model.TareaAgentePK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private TareaAgenteFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10000000) {

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
        current = (TareaAgente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new TareaAgente();
        // current.setTareaAgentePK(new ar.edu.undec.nortia.model.TareaAgentePK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TareaAgenteCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (TareaAgente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TareaAgenteUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (TareaAgente) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TareaAgenteDeleted"));
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

    @FacesConverter(forClass = TareaAgente.class)
    public static class TareaAgenteControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TareaAgenteController controller = (TareaAgenteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tareaAgenteController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(ar.edu.undec.nortia.model.TareaAgente value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof TareaAgente) {
                TareaAgente o = (TareaAgente) object;
                return getStringKey(o);
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TareaAgente.class.getName());
            }
        }

    }

    public List<TareaAgente> getTareasagentes() {
        return tareasagentes;
    }

    public void setTareasagentes(List<TareaAgente> tareasagentes) {
        this.tareasagentes = tareasagentes;
    }

    public void agregarEquipo() {

        boolean existe = false;
        FacesContext context = FacesContext.getCurrentInstance();
        AgenteViewController agenteviewcontrol = (AgenteViewController) context.getApplication().evaluateExpressionGet(context, "#{agenteViewController}", AgenteViewController.class);
        TareaController tareacontrol = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        EtapaController etapacontrol = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);

        if (tareasagentes == null) {
            tareasagentes = new ArrayList<TareaAgente>();
        }

        for (TareaAgente ta1 : tareasagentes) {
            if (ta1.getAgenteid().getId().equals(this.current2.getAgenteid().getId())) {
                existe = true;
            }
        }



        if (!existe) {

            RequestContext requestContext = RequestContext.getCurrentInstance();

            if (presupuestotareacontroller.agregarPresupuestoRRHHCONSULTOR(this.current2)) {

                tareasagentes.add(this.current2);
                requestContext.addCallbackParam("validation", true);
                // requestContext.execute("dialogotareaagentepresupuesto.hide();");

                context.addMessage("",new FacesMessage("Agregar Persona","Se agregó la persona correctamente a la tarea."));

            } else {
                requestContext.addCallbackParam("validation", false);

                // context.getExternalContext().getContext().

                context.addMessage("",new FacesMessage("Agregar Persona","No se pudo agregar la persona a la tarea."));
            }


        } else{
            System.out.println("Error en Tarea Agente");
            context.addMessage("Agente", new FacesMessage("Error en los datos del agente."));
        }

    }

    public void removerEquipo(Agente a) {

        for (TareaAgente ta1 : tareasagentes) {
            if (ta1.getAgenteid().getId().equals(a.getId())) {
                tareasagentes.remove(ta1);
            }
        }
    }

    public String reinit() {
        current = new TareaAgente();

        return null;
    }

    public String controlarHorasAgente() {

        return null;
    }

    public Double sumarHorasAgenteProyectos() {

        try{
            return this.ejbProyectoAgenteFacade.sumarHorasAgenteProyectos(current.getAgenteid().getId());
        } catch (NullPointerException npe){
            System.out.println("Error Número Nulo >> Retornamos 0");
            npe.printStackTrace();
            return 0d;
        }
    }

    public Double sumarHorasAgenteProyectoActual() {
//         for(Etapa e : etapaController.getEtapas()){
//             for(Tarea t : e.getTareaList()){
//                 for(TareaAgente ta : t.getTareaAgenteList()){
//                    // t.getDias()/7
//                 }
//             }
//         }

        try{
            return this.ejbProyectoAgenteFacade.sumarHorasAgenteProyectos(current.getAgenteid().getId());
        } catch(NullPointerException npe){
            System.out.println("Error Número NULO");
            npe.printStackTrace();
            return 0d;
        }
    }

    public void onCellEdit() {
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);

        if (verificarAgentesHoras()) {
            System.out.println("onCellEdit >> verificarAgentesHoras >> true");
        } else {
            System.out.println("onCellEdit >> verificarAgentesHoras >> false");
            try{
                presupuestotareacontroller.getCurrent2().setTotal((presupuestotareacontroller.getCurrent2().getCantidad().divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP)).multiply(presupuestotareacontroller.getCurrent2().getCostounitario()));
                presupuestotareacontroller.getCurrent2().setTotal((presupuestotareacontroller.getCurrent2().getTotal().multiply(BigDecimal.valueOf(this.current2.getHorasdedicadas()))));
                presupuestotareacontroller.getCurrent2().setTotal(presupuestotareacontroller.getCurrent2().getTotal().setScale(2, RoundingMode.HALF_UP));

                if(!agenteEsConsultorExterno()){
                    System.out.println("onCellEdit >> es Docente");
                    System.out.println("onCellEdit >> current2.total = " + presupuestotareacontroller.getCurrent2().getTotal());

                    presupuestotareacontroller.getCurrent2().setAporteuniversidad(presupuestotareacontroller.getCurrent2().getTotal());
                    presupuestotareacontroller.getCurrent2().setAporteorganismo(BigDecimal.ZERO);
                    presupuestotareacontroller.getCurrent2().setAportecomitente(BigDecimal.ZERO);

                    System.out.println("onCellEdit >> current2.aporteUniversidad = " + presupuestotareacontroller.getCurrent2().getAporteuniversidad());
                    System.out.println("onCellEdit >> current2.aporteOrganismo = " + presupuestotareacontroller.getCurrent2().getAporteorganismo());
                    System.out.println("onCellEdit >> current2.aporteComitente = " + presupuestotareacontroller.getCurrent2().getAportecomitente());
                }

            } catch(NullPointerException npe){
                System.out.println("Error >> Costo unitario u horas dedicadas son NULOS.");
                presupuestotareacontroller.getCurrent2().setTotal(BigDecimal.ZERO);
            }
        }
    }

    public boolean agenteEsConsultorExterno(){
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);

        try{
            for (ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()) {
                if (pa.getAgente().getId().equals(current2.getAgenteid().getId()) && pa.getConsultorexterno()) {
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            System.out.println("Excepcion en TareaAgenteController.agenteEsConsultorExterno()");
            return true;
        }
    }

    public boolean verificarAgentesHoras() {

        boolean resultado = false;
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);

        for (ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()) {
            if (pa.getAgente().getId().equals(current2.getAgenteid().getId())) {

                if (current2.getHorasdedicadas() > pa.getHorasdisponibles()) {

                    resultado = false;
                    //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El agente " + current2.getAgenteid() + " sobrepasa \n las " + pa.getHorasdisponibles() + " hs que se han establecido en el proyecto. \n"));
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_WARN, "Corregir!" , current2.getAgenteid().getApellido() + ", " + current2.getAgenteid().getNombres() + "<br />" + "Sobrepasa las " + pa.getHorasdisponibles() + " horas disponibles en el proyecto."));
                } else {

                    System.out.println("verificarAgentesHoras >> presupuestotareacontroller.getCurrent2().getCantidad() = " + presupuestotareacontroller.getCurrent2().getCantidad());
                    System.out.println("verificarAgentesHoras >> presupuestotareacontroller.getCurrent2().getCostounitario() = " + presupuestotareacontroller.getCurrent2().getCostounitario());

                    presupuestotareacontroller.getCurrent2().setTotal((presupuestotareacontroller.getCurrent2().getCantidad().divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP)).multiply(presupuestotareacontroller.getCurrent2().getCostounitario()));
                    presupuestotareacontroller.getCurrent2().setTotal(presupuestotareacontroller.getCurrent2().getTotal().setScale(2, RoundingMode.HALF_UP));

                    // si es docente, el aporte de la universidad es igual al total
                    if(!pa.getConsultorexterno()){

                        System.out.println("verificarAgentesHoras >> es Docente");
                        System.out.println("verificarAgentesHoras >> current2.total = " + presupuestotareacontroller.getCurrent2().getTotal());

                        presupuestotareacontroller.getCurrent2().setAporteuniversidad(presupuestotareacontroller.getCurrent2().getTotal());
                        presupuestotareacontroller.getCurrent2().setAporteorganismo(BigDecimal.ZERO);
                        presupuestotareacontroller.getCurrent2().setAportecomitente(BigDecimal.ZERO);

                        System.out.println("verificarAgentesHoras >> current2.aporteUniversidad = " + presupuestotareacontroller.getCurrent2().getAporteuniversidad());
                        System.out.println("verificarAgentesHoras >> current2.aporteOrganismo = " + presupuestotareacontroller.getCurrent2().getAporteorganismo());
                        System.out.println("verificarAgentesHoras >> current2.aporteComitente = " + presupuestotareacontroller.getCurrent2().getAportecomitente());
                    }
                }
            }
        }

        return resultado;
    }

    public void armarTareaAgentePresupuesto() {

        FacesContext context = FacesContext.getCurrentInstance();
        AgenteViewController agenteviewcontroller = (AgenteViewController) context.getApplication().evaluateExpressionGet(context, "#{agenteViewController}", AgenteViewController.class);
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
        EtapaController etapacontrol = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);

        this.current2 = new TareaAgente();
        this.current2.setAgenteid(agenteviewcontroller.getSelected());

        tareacontroller.getSelected().setEtapaid(etapacontrol.getSelected());
        this.current2.setTareaid(tareacontroller.getSelected());

        for (ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()) {
            if (agenteviewcontroller.getSelected().getId().equals(pa.getAgente().getId())) {
                if (pa.getConsultorexterno()) {
                    this.current2.setHorasdedicadas(null);
                } else {
                    this.current2.setHorasdedicadas(0);
                }
            }
        }

        // nuevo proyecto agente, establecer la tarea
        ProyectoAgente proyectoAgente = new ProyectoAgente();
        presupuestotareacontroller.setCurrent2(new PresupuestoTarea());
        presupuestotareacontroller.getCurrent2().setTarea(this.current2.getTareaid());

        // establecer el rubro de current 2
        Rubro rubro = new Rubro();

        for (ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()) {
            if (pa.getAgente().equals(current2.getAgenteid())) {

                if (pa.getConsultorexterno()) {
                    rubro = ejbFacadeRubro.findbyId(4);
                } else {
                    rubro = ejbFacadeRubro.findbyId(5);
                }
                proyectoAgente = pa;
            }
        }
        presupuestotareacontroller.getCurrent2().setRubro(rubro);


        // costo unitario | consultor externo = honorarios | docente = costo de cargo
        if (proyectoAgente.getConsultorexterno()) {
            presupuestotareacontroller.getCurrent2().setCostounitario(proyectoAgente.getHonorario());
            System.out.println("CONSULTOR EXTERNO COSTO UNITARIO = " + presupuestotareacontroller.getCurrent2().getCostounitario());
        } else {
            presupuestotareacontroller.getCurrent2().setCostounitario(current2.costoUnitarioCargoLegajo());
            System.out.println("DOCENTE COSTO UNITARIO = " + presupuestotareacontroller.getCurrent2().getCostounitario());
        }

        // setamos los dias
        presupuestotareacontroller.getCurrent2().setCantidad(BigDecimal.valueOf(tareacontroller.getSelected().getDias()));

        // seteamos la descripcion
        presupuestotareacontroller.getCurrent2().setDescripcion(current2.getAgenteid().toString());

        try{
            if (presupuestotareacontroller.getCurrent2().getCantidad().equals(BigDecimal.ZERO)) {

            } else {
                if (proyectoAgente.getConsultorexterno()) {

                    // CONSULTOR EXTERNO
                    presupuestotareacontroller.getCurrent2().setTotal((presupuestotareacontroller.getCurrent2().getCantidad().divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP)).multiply(presupuestotareacontroller.getCurrent2().getCostounitario()));
                    presupuestotareacontroller.getCurrent2().setTotal(presupuestotareacontroller.getCurrent2().getTotal().setScale(2, RoundingMode.HALF_UP));

                    // seteamos los aportes
                    presupuestotareacontroller.getCurrent2().setAporteuniversidad(BigDecimal.ZERO);
                    presupuestotareacontroller.getCurrent2().setAporteorganismo(BigDecimal.ZERO);
                    presupuestotareacontroller.getCurrent2().setAportecomitente(BigDecimal.ZERO);
                } else {

                    // DOCENTE
                    presupuestotareacontroller.getCurrent2().setTotal((presupuestotareacontroller.getCurrent2().getCantidad().divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP)).multiply(presupuestotareacontroller.getCurrent2().getCostounitario()));
                    presupuestotareacontroller.getCurrent2().setTotal(presupuestotareacontroller.getCurrent2().getTotal().setScale(2, RoundingMode.HALF_UP));

                    // seteamos los aportes, al ser docente, el total es pagado por la universidad
                    presupuestotareacontroller.getCurrent2().setAporteuniversidad(presupuestotareacontroller.getCurrent2().getTotal());
                    presupuestotareacontroller.getCurrent2().setAporteorganismo(BigDecimal.ZERO);
                    presupuestotareacontroller.getCurrent2().setAportecomitente(BigDecimal.ZERO);


                }
            }
        }catch (NullPointerException npe){
            context.addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Honorarios de Integrante","Se debe extablecer los honorarios del integrante en la configuración del equipo de trabajo."));
            npe.printStackTrace();
            return;
        }
    }

    public TareaAgente getCurrent2() {
        if (current2 == null) {
            current2 = new TareaAgente();
        }
        return current2;
    }

    public void setCurrent2(TareaAgente current2) {
        this.current2 = current2;
    }

    public void eliminarTareaAgente(TareaAgente ta) {
        int lugar = -1;
        int contador = 0;
        for (TareaAgente tan : this.tareasagentes) {

            if (tan.getAgenteid().equals(ta.getAgenteid())) {
                lugar = contador;
            }
            contador++;
        }
        this.tareasagentes.remove(lugar);
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        contador = 0;
        lugar = 0;
        for (PresupuestoTarea pt : presupuestotareacontroller.getPresupuestostareasitems()) {
            if (pt.getDescripcion().equals(ta.getAgenteid().toString())) {
                lugar = contador;
            }

            ++contador;
        }
        presupuestotareacontroller.getPresupuestostareasitems().remove(lugar);
    }


}
