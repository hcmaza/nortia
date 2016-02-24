package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.ArchivorendicionFacade;
import ar.edu.undec.nortia.controller.EstadosolicitudFacade;
import ar.edu.undec.nortia.model.Rendicion;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.RendicionFacade;
import ar.edu.undec.nortia.controller.SolicitudFacade;
import ar.edu.undec.nortia.controller.TiposolicitudFacade;
import ar.edu.undec.nortia.model.Archivorendicion;
import ar.edu.undec.nortia.model.Estadosolicitud;
import ar.edu.undec.nortia.model.Solicitud;
import ar.edu.undec.nortia.model.Tiposolicitud;

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
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "rendicionController")
@SessionScoped
public class RendicionController implements Serializable {

    private Rendicion current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.RendicionFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.SolicitudFacade ejbFacades;
    @EJB
    private ar.edu.undec.nortia.controller.EstadosolicitudFacade ejbFacadees;
    @EJB
    private ar.edu.undec.nortia.controller.TiposolicitudFacade ejbFacadets;

    @EJB
    private ar.edu.undec.nortia.controller.ArchivorendicionFacade ejbFacadear;
    @EJB
    private ar.edu.undec.nortia.controller.ConfiguracionFacade ejbFacadec;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Solicitud> listaSolicitudes;

    private List<Solicitud> listaSolicitudesSeleccionadas;
    private Solicitud solicitudSeleccionada;

    // creada cuando la suma de los comprobantes de rendicion supera el importe de la solicitud que se está rindiendo
    private Solicitud solicitudReintegroPorDiferencia;

    public RendicionController() {
    }

    public Rendicion getSelected() {
        if (current == null) {
            current = new Rendicion();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Rendicion rendicion) {
        current = rendicion;
    }

    private RendicionFacade getFacade() {
        return ejbFacade;
    }

    public SolicitudFacade getFacades() {
        return ejbFacades;
    }

    public EstadosolicitudFacade getFacadees() {
        return ejbFacadees;
    }

    public TiposolicitudFacade getEjbFacadets() {
        return ejbFacadets;
    }

    public ArchivorendicionFacade getFacadear() {
        return ejbFacadear;
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

    public Solicitud getSolicitudSeleccionada() {

        if (solicitudSeleccionada == null) {
            solicitudSeleccionada = new Solicitud();
        }
        return solicitudSeleccionada;
    }

    public void setSolicitudSeleccionada(Solicitud solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    public Solicitud getSolicitudReintegroPorDiferencia() {
        return solicitudReintegroPorDiferencia;
    }

    public void setSolicitudReintegroPorDiferencia(Solicitud solicitudReintegroPorDiferencia) {
        this.solicitudReintegroPorDiferencia = solicitudReintegroPorDiferencia;
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
        return "ListRendicionesPorProyecto";
    }

    public String prepareView() {
        //current = (Rendicion) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public void iniciarRendicion() {

        // Evitar que salte nuevamente el prerender
        if (!FacesContext.getCurrentInstance().isPostback()) {
            prepararRendicion();
            System.out.println("RendicionController - iniciarRendicion SIN POSTBACK");
        }
    }

    public void prepararRendicion() {

        System.out.println("RendicionController - prepararRendicion()");

        current = new Rendicion();
        selectedItemIndex = -1;

        current.setFecha(new Date());

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        ArchivorendicionController archivorendicioncontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);

        // Vaciamos la lista de archivos de rendicion
        archivorendicioncontroller.setListaArchivos(new ArrayList<Archivorendicion>());

        boolean admin = false;
        admin = context.getExternalContext().isUserInRole("Administrador");

        System.out.println("USUARIO admin? " + admin);

        // dependiendo del usuario llenamos la lista de solicitudes a rendir
        // si es un "DOCENTE" las solicitudes a rendir, seran las que tienen Estado = APROBADA
        // si es un "ADMINISTRADOR" las solicitudes a rendir, seran las que tienen Estado = APROBADA y EJECUCION
        listaSolicitudes = getFacades().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());

        if (admin) {
            // preparar solicitudes
            solicitudcontroller.armarSolicitudesDesembolsosYRendicion();

            listaSolicitudes.addAll(getFacades().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        }

        System.out.println("TAMAÑO listaSolicitudes: " + listaSolicitudes.size());

        // Vaciamos la lista de solicitudes seleccionadas [Seleccion de Multiples Solicitudes]
        listaSolicitudesSeleccionadas = new ArrayList<Solicitud>();

        // Vaciamos la solicitud seleccionada [Seleccion de Solicitud Unica]
        solicitudSeleccionada = new Solicitud();

    }

    public String prepareCreate() {

        prepararRendicion();

        return "CreateRendicion";
    }

    public String create() {
        try {

            // si la solicitud, tiene ID y la referencia a un presupuesto_tarea...
            if (solicitudSeleccionada.getId() != null && solicitudSeleccionada.getId() != 0 && solicitudSeleccionada.getPresupuestotarea() != null) {

                // Obtenemos el controlador necesario
                FacesContext context = FacesContext.getCurrentInstance();
                ArchivorendicionController arcontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);
                SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);

                float sumaArchivosRendicion = 0f;

                for (Archivorendicion ar : arcontroller.getListaArchivos()) {
                    sumaArchivosRendicion = sumaArchivosRendicion + ar.getMontofactura().floatValue();
                }

                System.out.println("Suma de Archivos de Rendicion = " + sumaArchivosRendicion);

                float porcentaje = 20f;

                // Obtenemos el porcentaje maximo el cual no se debe superar por la suma de comprobantes
                try {
                    porcentaje = Float.parseFloat(ejbFacadec.findAtributo("maxporcentajerendicion").getValor());
                    // System.out.println("maxanticipo=" + String.valueOf(maxanticipo));
                } catch (Exception e) {
                    porcentaje = 20f;
                    System.out.println("Error en obtencion de parametro 'maxanticipo' desde la base de datos [maxanticipo = 0]");
                    e.printStackTrace();
                }

                float porcentajeArchivosRendicion = (solicitudSeleccionada.getImporte().floatValue() * (porcentaje / 100.0f));

                System.out.println(porcentaje + "% de la suma de Archivos de Rendicion = " + porcentajeArchivosRendicion);

                //validacion de que el el total de archivos de rendicion, 
                //sea igual o hasta un 20% mayor que el importe de la rendicion
                if (sumaArchivosRendicion >= solicitudSeleccionada.getImporte().floatValue() && sumaArchivosRendicion <= (solicitudSeleccionada.getImporte().floatValue() + porcentajeArchivosRendicion)) {

                    // rendicion con fecha actual del sistema
                    current.setFecha(new Date());

                    // se guarda la rendicion
                    getFacade().createWithPersist(current);

                    // Estado de la solicitud que se rinde
                    Estadosolicitud estadoRendicionAEvaluar;
                    // Estado de la solicitud que se crea por diferencia
                    Estadosolicitud estadoAprobada;

                    // obtenemos los estados
                    try {
                        // Estado de la Solicitud = "Rendicion a Evaluar"
                        estadoRendicionAEvaluar = getFacadees().find(6);
                        // Estado de la Solicitud = "Aprobada"
                        estadoAprobada = getFacadees().find(2);
                    } catch (Exception e) {
                        estadoRendicionAEvaluar = null;
                        estadoAprobada = null;
                        System.out.println("EstadosolicitudFacade: problema de recuperacion");
                        e.printStackTrace();
                    }

                    // Para cada Solicitud seleccionada, actualizar con el nuevo estado y rendicion correspondiente
                    solicitudSeleccionada.setRendicionid(current);
                    solicitudSeleccionada.setEstadosolicitudid(estadoRendicionAEvaluar);
                    solicitudSeleccionada.setFechaejecucion(new Date());
                    getFacades().edit(solicitudSeleccionada);

                    // Caso Alternativo: la suma de todos los comprobantes cargados es superior (dentro de lo permitido)
                    // al importe de la solicitud que se rinde
                    if (sumaArchivosRendicion > solicitudSeleccionada.getImporte().floatValue()) {

                        // tipo reintegro
                        Tiposolicitud tipoReintegro;

                        // obtenemos el tipo
                        try {
                            // Tipo de Solicitud = "Reintegro"
                            tipoReintegro = getEjbFacadets().find(4);
                        } catch (Exception e) {
                            tipoReintegro = null;
                            System.out.println("TipoSolicitudFacade: problema de recuperacion");
                            e.printStackTrace();
                        }

                        // seteamos la solicitud por diferencia
                        solicitudReintegroPorDiferencia.setFechasolicitud(new Date());
                        solicitudReintegroPorDiferencia.setFechaaprobacion(new Date());
                        solicitudReintegroPorDiferencia.setRendicionid(current);
                        solicitudReintegroPorDiferencia.setTiposolicitudid(tipoReintegro);
                        solicitudReintegroPorDiferencia.setEstadosolicitudid(estadoAprobada);
                        solicitudReintegroPorDiferencia.setObservacion("Diferencia por: $" + solicitudReintegroPorDiferencia.getImporte().floatValue() + " - " + solicitudSeleccionada.getPresupuestotarea().getDescripcion());

                        // Guardamos la solicitud de reintegro por diferencia con Estado = Aprobada
                        getFacades().createWithPersist(solicitudReintegroPorDiferencia);

                    }

                    // Para cada archivo de rendicion subido
                    for (Archivorendicion ar : arcontroller.getListaArchivos()) {
                        // le damos la referencia a la rendicion actual y persistimos el archivo
                        ar.setRendicionid(current);
                        getFacadear().create(ar);
                    }

                    //JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionCreated"));
                    JsfUtil.addSuccessMessage("Rendición Creada Correctamente!");

                } else {
                    FacesContext.getCurrentInstance().addMessage("mensajeRendicion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Creación de la Rendicion", "La suma de comprobantes de pago debe ser igual o mayor hasta un " + porcentaje + "% del total de la solicitud a rendir."));
                    return null;
                }
            }
            //return prepareList();
            return null;

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }

        //            // Usado para la rendicion de multiples solicitudes
//            if (!listaSolicitudesSeleccionadas.isEmpty()) {
//                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El archivo" + current.getNombrearchivo() +  " fue subido satisfactoriamente!"));
//
//                current.setFecha(new Date());
//
//                // se guarda la rendicion
//                getFacade().createWithPersist(current);
//
//                // Estado de la solicitud
//                Estadosolicitud estado;
//
//                try {
//                    // Estado de la Solicitud = "Rendida"
//                    estado = getFacadees().find(5);
//                } catch (Exception e) {
//                    estado = null;
//                    System.out.println("EstadosolicitudFacade: problema de recuperacion");
//                    e.printStackTrace();
//                }
//
//                // Para cada Solicitud seleccionada, actualizar con el nuevo estado y rendicion correspondiente
//                for (Solicitud s : listaSolicitudesSeleccionadas) {
//                    s.setRendicionid(current);
//                    //s.setFechaejecucion(new Date());
//                    s.setEstadosolicitudid(estado);
//                    getFacades().edit(s);
//                }
//
//                // Para cada archivo de rendicion subido
//                // Obtenemos el controladores necesario
//                FacesContext context = FacesContext.getCurrentInstance();
//                ArchivorendicionController arcontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);
//
//                for (Archivorendicion ar : arcontroller.getListaArchivos()) {
//                    ar.setRendicionid(current);
//                    getFacadear().create(ar);
//                }
//
//                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionCreated"));
//            }
    }

    public String prepareEdit() {
        //current = (Rendicion) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Rendicion) getItems().getRowData();
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
            // buscar solicitud para actualizarla
            Solicitud s;

            try {
                s = getFacades().obtenerPorRendicion(current.getId());

                // Sin Rendicion
                s.setRendicionid(null);
            } catch (Exception e) {
                s = null;
                e.printStackTrace();
            }

            // buscar estado de solicitud anterior: "Aprobada"
            Estadosolicitud es;

            try {

                es = getFacadees().find(2);

            } catch (Exception e) {
                es = null;
                e.printStackTrace();
            }

            s.setEstadosolicitudid(es);

            //Actualizamos la Solicitud
            getFacades().edit(s);

            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionDeleted"));
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

    @FacesConverter(forClass = Rendicion.class)
    public static class RendicionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RendicionController controller = (RendicionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rendicionController");
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
            if (object instanceof Rendicion) {
                Rendicion o = (Rendicion) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Rendicion.class.getName());
            }
        }
    }

    public void obtenerPorProyecto(int proyectoid) {
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoid));
    }

    public float sumarSolicitudesARendir() {

        float resultado = 0f;

        if (listaSolicitudes != null && listaSolicitudes.size() > 0) {
            for (Solicitud s : listaSolicitudes) {
                resultado = resultado + s.getImporte().floatValue();
            }
        }

        return resultado;
    }

    public String prepareEvaluacion() {

        prepararEvaluacion();

        return "CreateEvaluacionRendicion";
    }

    public void prepararEvaluacion() {

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        //SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ArchivorendicionController archivorendicioncontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);

        // llamamos al metodo que arma el presupuesto, solicitudes y desembolsos del proyecto
        solicitudcontroller.armarSolicitudesDesembolsosYRendicion();

        // Vaciamos la lista de archivos de rendicion
        //archivorendicioncontroller.setListaArchivos(new ArrayList<Archivorendicion>());
        // Llenamos la lista de solicitudes con "Rendicion a Evaluar", es decir que ya pueden ser evaluadas por el administrador.
        listaSolicitudes = getFacades().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId());

        // Llenamos la lista de comprobantes de rendicion
        //archivorendicioncontroller.setListaArchivos(new ArrayList<Archivorendicion>());
        //archivorendicioncontroller.llenarListaArchivosPorListaSolicitudes(listaSolicitudes);
        // Vaciamos la lista de solicitudes seleccionadas [Seleccion de Multiples Solicitudes]
        listaSolicitudesSeleccionadas = new ArrayList<Solicitud>();

        // Vaciamos la solicitud seleccionada [Seleccion de Solicitud Unica]
        solicitudSeleccionada = new Solicitud();

    }

    public String createEvaluacion() {

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        //SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ArchivorendicionController archivorendicioncontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);

        Estadosolicitud estadoRendida = null;

        try {
            // EstadoSolicitud "Rendida" id=5
            estadoRendida = this.ejbFacadees.find(5);
        } catch (Exception e) {
            System.out.println("ERROR: Recuperacion de EstadoSolicitud = estadoRendida = this.ejbFacadees.find(5);");
            e.printStackTrace();
        }

        // bandera, true >> SE EVALUA, false >> NO SE EVALUA
        boolean bandera = true;

        // verificar que todos los comprobantes, se le asigno un estado y el monto aprobado.
        for(Archivorendicion ar : archivorendicioncontroller.getListaArchivos()){
            if(ar.getMontoaprobado() == null || ar.getEstado() == null){
                bandera = false;
            }
        }

        // todos los comprobantes, tienen un estado definido y monto aprobado.
        if (bandera){

            System.out.println("SE EVALUA");

            // damos el estado "Rendida" a la solicitud
            solicitudSeleccionada.setEstadosolicitudid(estadoRendida);

            // guardamos el cambio de estado en la solicitud
            this.getFacades().edit(solicitudSeleccionada);

            // guardamos los cambios en los comprobantes de rendicion
            for (Archivorendicion ar : archivorendicioncontroller.getListaArchivos()) {
                this.getFacadear().edit(ar);
            }

            return "/index.xhtml?faces-redirect=true";
        } else{

            System.out.println("NO se Evalua");

            String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            return viewId + "?faces-redirect=true";
        }

    }
    
    /*
    * Lista de Gastos Ejecutados
    */

    public String prepareListGastosEjecutados() {
        
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        
        List<Rendicion> rendiciones = this.getFacade().obtenerPorProyectoAprobadas(proyectocontroller.getSelected().getId());
        
        items = new ArrayDataModel<Rendicion>(rendiciones.toArray(new Rendicion[rendiciones.size()]));

        return "ListRendicionesPorProyecto";
    }
    
    

}
