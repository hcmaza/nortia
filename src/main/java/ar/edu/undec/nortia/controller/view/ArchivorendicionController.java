package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.Archivorendicion;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.ArchivorendicionFacade;
import ar.edu.undec.nortia.controller.RendicionFacade;
import ar.edu.undec.nortia.model.Rendicion;
import ar.edu.undec.nortia.model.Solicitud;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.faces.event.PhaseId;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "archivorendicionController")
@SessionScoped
public class ArchivorendicionController implements Serializable {

    private Archivorendicion current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.ArchivorendicionFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.RendicionFacade ejbFacadeRendicion;
    @EJB
    private ar.edu.undec.nortia.controller.ConfiguracionFacade ejbFacadec;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Archivorendicion> listaArchivos;

    public ArchivorendicionController() {
    }

    public Archivorendicion getSelected() {
        if (current == null) {
            current = new Archivorendicion();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Archivorendicion archivorendicion) {
        current = archivorendicion;
    }

    private ArchivorendicionFacade getFacade() {
        return ejbFacade;
    }

    public RendicionFacade getEjbFacadeRendicion() {
        return ejbFacadeRendicion;
    }

    public List<Archivorendicion> getListaArchivos() {
        if (listaArchivos == null) {
            listaArchivos = new ArrayList<Archivorendicion>();
        }
        return listaArchivos;
    }

    public void setListaArchivos(List<Archivorendicion> listaArchivos) {
        this.listaArchivos = listaArchivos;
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
    
    public String prepareListEjecutados(){
        
        obtenerArchivosRendicionEjecutados();
        
        System.out.println("asdasd");
        
        return "ListComprobantesEjecutados";
    }

    public String prepareView() {
        current = (Archivorendicion) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Archivorendicion();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ArchivorendicionCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Archivorendicion) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ArchivorendicionUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Archivorendicion) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ArchivorendicionDeleted"));
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

    @FacesConverter(forClass = Archivorendicion.class)
    public static class ArchivorendicionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ArchivorendicionController controller = (ArchivorendicionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "archivorendicionController");
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
            if (object instanceof Archivorendicion) {
                Archivorendicion o = (Archivorendicion) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Archivorendicion.class.getName());
            }
        }
    }

    public void nuevoArchivoRendicion() {
        System.out.println("Nuevo Archivo Rendicion Inicio");
        //current = null;
        current = new Archivorendicion();

        // Obtenemos el controlador necesario
        FacesContext context = FacesContext.getCurrentInstance();
        RendicionController rendicioncontroller = (RendicionController) context.getApplication().evaluateExpressionGet(context, "#{rendicionController}", RendicionController.class);

        System.out.println("Rendicion Solicitud: " + rendicioncontroller.getSolicitudSeleccionada().getPresupuestotarea().getDescripcion());
        System.out.println("Rendicion Fecha Solicitud: " + rendicioncontroller.getSolicitudSeleccionada().getFechasolicitud().toString());
//        System.out.println("Rendicion Fecha Aprobacion: " + rendicioncontroller.getSolicitudSeleccionada().getFechaaprobacion().toString());
//        System.out.println("Rendicion Fecha Ejecucion: " + rendicioncontroller.getSolicitudSeleccionada().getFechaejecucion().toString());

        System.out.println("Nuevo Archivo rendicion Fin");
    }

    public void agregarArchivoLista() {

        System.out.println("Inicio Método agregarArchivoLista <<>><<>");

        // Obtenemos el controlador necesario
        FacesContext context = FacesContext.getCurrentInstance();
        RendicionController rendicioncontroller = (RendicionController) context.getApplication().evaluateExpressionGet(context, "#{rendicionController}", RendicionController.class);
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);

        float sumaArchivosRendicion = 0f;

        for (Archivorendicion ar : getListaArchivos()) {
            sumaArchivosRendicion = sumaArchivosRendicion + ar.getMontofactura().floatValue();
        }

        sumaArchivosRendicion = sumaArchivosRendicion + current.getMontofactura().floatValue();
        System.out.println("Suma de Archivos de Rendicion = " + sumaArchivosRendicion);

        if (rendicioncontroller.getSolicitudSeleccionada() == null) {
            System.out.println("rendicioncontroller.getSolicitudSeleccionada() NULLLLLLLLLLLLLLLL asd");
        }

        if (sumaArchivosRendicion > rendicioncontroller.getSolicitudSeleccionada().getImporte().floatValue()) {

            float porcentaje = 20f;

            // Obtenemos el porcentaje maximo el cual no se debe superar por la suma de comprobantes
            try {
                porcentaje = Float.parseFloat(ejbFacadec.findAtributo("maxporcentajerendicion").getValor());
            } catch (Exception e) {
                porcentaje = 20f;
                System.out.println("Error en obtencion de parametro 'maxporcentajerendicion' desde la base de datos [maxporcentajerendicion = 20]");
                e.printStackTrace();
            }

            float porcentajeArchivosRendicion = (rendicioncontroller.getSolicitudSeleccionada().getImporte().floatValue() * (porcentaje / 100.0f));

            System.out.println(porcentaje + "% de la suma de Archivos de Rendicion = " + porcentajeArchivosRendicion);

            //validacion de que el el total de archivos de rendicion, 
            //sea igual o hasta un 20% mayor que el importe de la rendicion
            if (sumaArchivosRendicion > (rendicioncontroller.getSolicitudSeleccionada().getImporte().floatValue() + porcentajeArchivosRendicion)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Creación de la Rendicion", "La suma de comprobantes de pago debe ser igual o mayor hasta un " + porcentaje + "% del total de la solicitud a rendir."));
                return;
            } else {

                float diferencia = sumaArchivosRendicion - rendicioncontroller.getSolicitudSeleccionada().getImporte().floatValue();

                System.out.println("Diferencia: " + diferencia);

                // la diferencia se encuentra dentro del porcentaje permitido
                if (diferencia > 0 && diferencia < porcentajeArchivosRendicion) {
                    // verificar si hay dinero disponible para la diferencia
                    for (Solicitud s : solicitudcontroller.getItemsDisponiblesNuevo()) {
                        if (rendicioncontroller.getSolicitudSeleccionada().getPresupuestotarea().getId() == s.getPresupuestotarea().getId() && s.getDisponible().floatValue() >= diferencia) {

                            // agregar el archivo rendicion a la lista                
                            getListaArchivos().add(current);

                            // a la solicitud encontrada, le cambiamos el importe por la diferencia
                            s.setImporte(new BigDecimal(diferencia));
                            // seteamos la solicitud encontrada como la solicitud para el reintegro
                            rendicioncontroller.setSolicitudReintegroPorDiferencia(s);

                            System.out.println("rendicioncontroller.SolicitudReintegroPorDiferencia: " + rendicioncontroller.getSolicitudReintegroPorDiferencia().getImporte() + " - " + rendicioncontroller.getSolicitudReintegroPorDiferencia().getPresupuestotarea().getDescripcion());

                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rendicion: existe una diferencia.", "La suma de comprobantes supera la solicitud a rendir dentro de lo permitido y hay dinero disponible para el item. La diferencia es de: " + diferencia + " y se asigna a " + s.getPresupuestotarea().getDescripcion()));
                            return;
                        }
                    }

                    // si no hay dinero disponible para la diferencia de rendicion.
                }
            }

        } else {
            // agregar el archivo rendicion a la lista
            getListaArchivos().add(current);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Rendición: comprobante agregado correctamente", "Importe: " + current.getMontofactura().floatValue()));
        }

    }

    public void subirArchivoRendicion(FileUploadEvent event) {
        System.out.println("Subiendo Archivo de Rendición");

        try {
            //current = new Archivorendicion();
            current.setNombrearchivo(event.getFile().getFileName());
            current.setArchivo(event.getFile().getContents());
            //getListaArchivos().add(current);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El archivo" + current.getNombrearchivo() + " fue subido satisfactoriamente!"));
        } catch (Exception e) {
            System.out.println("Excepcion en RendicionController - subirArchivo");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error", "Ocurrió un error al subir el archivo"));
            e.printStackTrace();
        }
    }

    public void removerArchivoLista(Archivorendicion archivo) {

        // se quita de la lista de solicitados
//        Iterator i = this.itemsSolicitados.iterator();
//        while(i.hasNext()){
//            if(((Solicitud)i.next()).getPresupuestotarea().equals(solicitud.getPresupuestotarea())){
//                i.remove();
//            }
//        }
        listaArchivos.remove(archivo);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El comprobante del proveedor: " + current.getProveedor() + " - Nº: " + current.getNrofactura() + " fue borrado"));

    }

    public StreamedContent obtenerImagen() throws IOException {

        StreamedContent imagen = null;

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // Renderizamos el HTML. Devolver un stub de StreamedContent asi se general la URL correcta.

            System.out.println("obtenerImagen: RENDER_RESPONSE");

            return new DefaultStreamedContent();
        } else {
            // Se responde al requerimiento de la imagen, con un StreamContent con los bytes de imagen reales.
            String nombreArchivo = context.getExternalContext().getRequestParameterMap().get("archivo");

            System.out.println("obtenerImagen: <NO> RENDER_RESPONSE");
            System.out.println("Archivo del Comprobante: " + nombreArchivo);

            for (Archivorendicion ar : getListaArchivos()) {
                if (ar.getNombrearchivo().equals(nombreArchivo)) {
                    return new DefaultStreamedContent(new ByteArrayInputStream(ar.getArchivo()));
                }
            }
        }
        return null;
    }

    public StreamedContent getImagenComprobante(){

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // Renderizamos el HTML. Devuelve un stub StreamedContent para generar el URL correcto.
            return new DefaultStreamedContent();
        } else {
            // El browser requiere la imagen. Devuelve el StreamedContent real con los bytes de la imagen.
            String nombreArchivo = context.getExternalContext().getRequestParameterMap().get("archivo");

            
            System.out.println("Nombre de Archivo >> " + nombreArchivo);

            for (Archivorendicion ar : getListaArchivos()) {
                if (ar.getNombrearchivo().equals(nombreArchivo)) {
                    //BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);
                    //ByteArrayOutputStream os = new ByteArrayOutputStream();
                    //ImageIO.write(bufferedImg, "png", os);
                    //imagen = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");
                    return new DefaultStreamedContent(new ByteArrayInputStream(ar.getArchivo()));
                }
            }
        }

        return new DefaultStreamedContent();
    }
    
    public StreamedContent getImagenSelected(){
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // Renderizamos el HTML. Devuelve un stub StreamedContent para generar el URL correcto.
            return new DefaultStreamedContent();
        } else {
            
            // si el current o el archivo que contiene son nulos.
            if(null == current){
                return new DefaultStreamedContent();
            }

            if(null == current.getArchivo()){
                return new DefaultStreamedContent();
            }
            
            // El browser requiere la imagen. Devuelve el StreamedContent real con los bytes de la imagen.
            return new DefaultStreamedContent(new ByteArrayInputStream(current.getArchivo()));
        }

    }

    public float sumarComprobantes() {
        float r = 0;

        if (listaArchivos != null & listaArchivos.size() > 0) {
            for (Archivorendicion a : listaArchivos) {
                r += a.getMontofactura().floatValue();
            }
        }

        return r;
    }

    public float sumarMontoAprobadoComprobantes() {
        float r = 0;

        if (listaArchivos != null & listaArchivos.size() > 0) {
            for (Archivorendicion a : listaArchivos) {
                r += a.getMontoaprobado().floatValue();
            }
        }

        return r;
    }

    public void llenarListaArchivosPorSolicitudSeleccionada() {
        // Obtenemos el controlador necesario
        FacesContext context = FacesContext.getCurrentInstance();
        RendicionController rendicioncontroller = (RendicionController) context.getApplication().evaluateExpressionGet(context, "#{rendicionController}", RendicionController.class);

        // vaciamos la lista de comprobantes
        listaArchivos.clear();

        // si, la solicitud seleccionada no es nula
        if (rendicioncontroller.getSolicitudSeleccionada() != null) {
            System.out.println("rendicioncontroller.getSolicitudSeleccionada NO null");
            listaArchivos.addAll(getFacade().buscarPorRendicion(rendicioncontroller.getSolicitudSeleccionada().getRendicionid().getId()));
        }
    }

    public void llenarListaArchivosPorListaSolicitudes(List<Solicitud> listaSolicitudes) {
        // si la lista no es nula o vacia
        if (listaSolicitudes != null && listaSolicitudes.size() > 0) {
            for (Solicitud s : listaSolicitudes) {
                if (s.getRendicionid() != null) {
                    listaArchivos.addAll(getFacade().buscarPorRendicion(s.getRendicionid().getId()));
                }
            }
        }
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        //String nombreColumna = event.getColumn().getHeaderText();
        //String nombreColumna = event.getColumn().getFacet("header").toString();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cambio.", "Valor Anterior: " + oldValue + ", Nuevo Valor:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
    
    /*
    * Obtenemos la lista de comprobantes de las rendiciones ejecutadas.
    */
    public void obtenerArchivosRendicionEjecutados(){
        
        System.out.println("obtenerArchivosRendicionEjecutados");
        
        List<Rendicion> rendiciones = new ArrayList<Rendicion>();

        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        
        rendiciones = this.ejbFacadeRendicion.obtenerPorProyectoAprobadas(proyectocontroller.getSelected().getId());
        
        // inicializamos la lista de comprobantes
        listaArchivos = new ArrayList<Archivorendicion>();
        
        for(Rendicion rendicion : rendiciones){
            
            try{
                listaArchivos.addAll(getFacade().buscarPorRendicion(rendicion.getId()));
            }catch(Exception e){
                System.out.println("Excepción en obtener los comprobantes de una rendicion");
                e.printStackTrace();
            }
        }
        
        for(Archivorendicion ac : listaArchivos){
            System.out.println("Comprobante >> " + ac.getNrofactura());
        }
        
        // items = a la lista de comprobantes de todas las rendiciones ejecutadas
        //items = new ArrayDataModel<Archivorendicion>(comprobantes.toArray(new Archivorendicion[comprobantes.size()]));
        
        
        
    }
}
