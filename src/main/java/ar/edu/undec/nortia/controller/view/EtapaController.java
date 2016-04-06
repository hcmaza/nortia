package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.EtapaFacade;
import ar.edu.undec.nortia.model.Agente;
import ar.edu.undec.nortia.model.ProyectoAgente;
import ar.edu.undec.nortia.model.Tarea;
import ar.edu.undec.nortia.model.TareaAgente;
import ar.edu.undec.nortia.model.Tareaavance;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
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
import org.primefaces.event.FlowEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "etapaController")
@SessionScoped
public class EtapaController implements Serializable {

    private Etapa current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.EtapaFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    //private TareaController tareacontroller;
    private TreeNode root = new DefaultTreeNode();
    private TreeNode nodoSeleccionado;
    private List<Etapa> etapas = new ArrayList<Etapa>();
    private String gsoncategoria = "[]";
    private String data = "[0]";
    private String dataactual = "[0]";
    private long mindate;
    private Tarea tareaseleccionada;
    private boolean paraeditar = false;

    //Lista para el resumen del equipo
    List<ProyectoAgente> paprincipal = new ArrayList<ProyectoAgente>();

    public EtapaController() {
    }

    public Etapa getSelected() {
        if (current == null) {
            current = new Etapa();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EtapaFacade getFacade() {
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
        current = (Etapa) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Etapa();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EtapaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Etapa) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EtapaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Etapa) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EtapaDeleted"));
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

    @FacesConverter(forClass = Etapa.class)
    public static class EtapaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EtapaController controller = (EtapaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "etapaController");
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
            if (object instanceof Etapa) {
                Etapa o = (Etapa) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Etapa.class.getName());
            }
        }

    }

    public String onFlowProcess(FlowEvent event) {

        return event.getNewStep();

    }

    @PostConstruct
    public void init() {

    }

    public TreeNode getRoot() {

        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void setNodoSeleccionado(TreeNode nodoSeleccionado) {
        this.nodoSeleccionado = nodoSeleccionado;
    }
    
    public List<Etapa> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<Etapa> etapas) {
        this.etapas = etapas;
    }

    public void agregaralListadoEtapas() {

        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        if (this.paraeditar) {
            editarListadoEtapas();
        } else {
            if (this.current.getEtapa() != null) {
                boolean encontro = false;
                for (Etapa etapa : etapas) {

                    if (etapa.getEtapa().equals(current.getEtapa())) {

                        encontro = true;
                    }
                }
                if (!encontro) {

                    int contardias = 0;
                    Date mindia = new Date("2999/12/12");
                    Date maxdia = new Date("2001/01/01");
                    root = new DefaultTreeNode(new Tarea(), null);
                    root.setExpanded(true);
                    current.setTareaList(tareacontroller.getTareasdeproyecto());

                    for (Tarea t : tareacontroller.getTareasdeproyecto()) {
                        contardias = contardias + t.getDias();
                        if (t.getFechainicio().before(mindia)) {
                            mindia = t.getFechainicio();
                        }
                        if (t.getFechafin().after(maxdia)) {
                            maxdia = t.getFechafin();
                        }
                    }
                    current.setFechainicio(mindia);
                    current.setFechafin(maxdia);
                    current.setDias(contardias);
                    etapas.add(current);

                    for (Etapa etapa : etapas) {

                        Tarea t = new Tarea();
                        t.setTarea(etapa.getEtapa());
                        t.setFechainicio(etapa.getFechainicio());
                        t.setFechafin(etapa.getFechafin());
                        t.setDias(etapa.getDias());
                        TreeNode et = new DefaultTreeNode(t, root);
                        et.setExpanded(true);
                        for (Tarea tarea : etapa.getTareaList()) {
                            TreeNode tar = new DefaultTreeNode(tarea, et);
                        }

                    }
                }

                this.agentesProyecto();
                presupuestotareacontroller.armarPresupuestoGeneral();
                crearChart();
                current = null;
                tareacontroller.setTareasdeproyecto(null);
            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                message.setSummary("ERROR");
                message.setDetail("Ingrese el nombre de la Etapa ");
                FacesContext.getCurrentInstance().addMessage("growlprincipal", message);
            }
        }
    }

    public void eliminaralListadoEtapas() {
        etapas.remove(current);
    }

    public String getGsoncategoria() {
        return gsoncategoria;
    }

    public void setGsoncategoria(String gsoncategoria) {
        this.gsoncategoria = gsoncategoria;
    }

    public void crearChart() {
        Gson gson = new Gson();
        List<String> categoria = new ArrayList<String>();
        data = "[";
        dataactual = "[";
        mindate = Long.MAX_VALUE;
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
         
        TareaavanceController tareaavancecontroller = (TareaavanceController) context.getApplication().evaluateExpressionGet(context, "#{tareaavanceController}", TareaavanceController.class);

        if (tareacontroller.getTareasdeproyecto() == null) {
            int cant = etapas.size();
        } else {
            int cant = tareacontroller.getTareasdeproyecto().size() + etapas.size();
        }
        int contaretapas = 0;
        int contador = 0;
        for (Etapa e : etapas) {
            contador++;

            categoria.add(e.getEtapa());

            //data+="["+e.getFechainicio().getTime()+","+e.getFechafin().getTime()+"],";
            data += "{low:" + e.getFechainicio().getTime() + ",high:" + e.getFechafin().getTime() + ",color:'#66BB6A'},";
            dataactual += "[" + e.getFechainicio().getTime() + "," + e.getFechainicio().getTime() + "],";
            contaretapas++;
            for (Tarea t : this.etapas.get(contaretapas - 1).getTareaList()) {
                if (t.getFechainicio().getTime() < mindate) {
                    mindate = t.getFechainicio().getTime();
                }
                contador++;
                categoria.add(t.getTarea());
                data += "[" + t.getFechainicio().getTime() + "," + t.getFechafin().getTime() + "],";
                
               
                
                Tareaavance avance = new Tareaavance();
                avance.setAvance(0);
                Calendar cal = new GregorianCalendar(2001,0,1);
                Date fechainicial = cal.getTime();
                if(t.getTareaavanceList()!=null){
                for(Tareaavance tav:t.getTareaavanceList()){

                        if(tav.getFecha().after(fechainicial)) {

                           avance = tav;
                           fechainicial = tav.getFecha();
                       }

                    }
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(t.getFechainicio());
                calendar.add(Calendar.DAY_OF_YEAR, (t.getDias() * avance.getAvance()) / 100);
                dataactual += "[" + t.getFechainicio().getTime() + "," + calendar.getTime().getTime() + "],";
            }
        }
        gsoncategoria = "" + gson.toJson(categoria).replace('\"', '\'');
        data += "]";
        dataactual += "]";

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getMindate() {
        return mindate;
    }

    public void setMindate(long mindate) {
        this.mindate = mindate;
    }

    public void rearmarEtapasProyecto() {

        current = null;
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontrol = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);

        tareacontrol.setTareasdeproyecto(null);
        this.paraeditar = false;

    }

    public String getDataactual() {
        return dataactual;
    }

    public void setDataactual(String dataactual) {
        this.dataactual = dataactual;
    }

    public Tarea getTareaseleccionada() {
        return tareaseleccionada;
    }

    public void setTareaseleccionada(Tarea tareaseleccionada) {
        this.tareaseleccionada = tareaseleccionada;
    }

    public void eliminarEtapaTreeTable(Tarea tar) {
        int contador = 0;
        int lugar = 0;
        for (TreeNode tn : root.getChildren()) {

            if (((Tarea) tn.getData()).getTarea().equals(tar.getTarea())) {
                lugar = contador;
            }
            contador++;
        }
        root.getChildren().remove(lugar);
        Etapa etapaguardada = new Etapa();
        contador = 0;
        lugar = 0;
        for (Etapa et : etapas) {

            if (tar.getTarea().equals(et.getEtapa())) {
                lugar = contador;
            }
            contador++;
        }
        etapas.remove(lugar);

        crearChart();
    }

    public void prepararEditarEtapaTreeTable(Tarea tar) {
        this.paraeditar = true;

        int contador = 0;
        int posicion = 0;
        for (Etapa e : etapas) {
            if (e.getEtapa().equals(tar.getTarea())) {
                if (e.getFechainicio().equals(tar.getFechainicio())) {
                    posicion = contador;
                }

            }
            contador++;
        }
        current = etapas.get(posicion);
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontrol = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);

        tareacontrol.setTareasdeproyecto(current.getTareaList());

    }

    public void editarListadoEtapas() {
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);

        Date mindia = new Date("2999/12/12");
        Date maxdia = new Date("2001/01/01");
        int contardias = 0;
        root = new DefaultTreeNode(new Tarea(), null);
        root.setExpanded(true);
        //current.setTareaList(tareacontroller.getTareasdeproyecto());
        if(tareacontroller.getTareasdeproyecto() != null){
            for (Tarea t : tareacontroller.getTareasdeproyecto()) {
                contardias = contardias + t.getDias();
                if (t.getFechainicio().before(mindia)) {
                    mindia = t.getFechainicio();
                }
                if (t.getFechafin().after(maxdia)) {
                    maxdia = t.getFechafin();
                }
            }
            current.setFechainicio(mindia);
            current.setFechafin(maxdia);
            current.setDias(contardias);
        }
        //etapas.add(current);
        for (Etapa etapa : etapas) {

            Tarea t = new Tarea();
            t.setTarea(etapa.getEtapa());
            t.setFechainicio(etapa.getFechainicio());
            t.setFechafin(etapa.getFechafin());
            t.setDias(etapa.getDias());
            TreeNode et = new DefaultTreeNode(t, root);
            et.setExpanded(true);
            for (Tarea tarea : etapa.getTareaList()) {
                TreeNode tar = new DefaultTreeNode(tarea, et);
            }

        }

        crearChart();
        presupuestotareacontroller.armarPresupuestoGeneral();
        this.agentesProyecto();
        current = null;
        tareacontroller.setTareasdeproyecto(null);

    }

    public boolean isParaeditar() {
        return paraeditar;
    }

    public void setParaeditar(boolean paraeditar) {
        this.paraeditar = paraeditar;
    }

    public void prepareEditarListadoEtapas() {
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);

        int contardias = 0;
        Date mindia = new Date("2999/12/12");
        Date maxdia = new Date("2001/01/01");
        root = new DefaultTreeNode(new Tarea(), null);
        root.setExpanded(true);

        for (Etapa etapa : etapas) {
            Tarea t = new Tarea();
            t.setTarea(etapa.getEtapa());
            t.setFechainicio(etapa.getFechainicio());
            t.setFechafin(etapa.getFechafin());
            t.setDias(etapa.getDias());
            
            if(etapa.getEstado()==null){
            }else{
                if(etapa.getEstado().equals("Terminada")){
                    t.setEstado(-1);
                }
            }
            TreeNode et = new DefaultTreeNode(t, root);
            et.setExpanded(true);
            for (Tarea tarea : etapa.getTareaList()) {
                TreeNode tar = new DefaultTreeNode(tarea, et);
            }
            crearChart();
            current = null;
            tareacontroller.setTareasdeproyecto(null);
        }

    }

    //Obtengo el resumen de etapas tareas en el proyecto(Resumen de Equipo)
    public void agentesProyecto() {
        this.paprincipal = new ArrayList<ProyectoAgente>();
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);

        for (ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()) {

            List<TareaAgente> tareaagentelista = new ArrayList<TareaAgente>();

            for (Etapa e : this.getEtapas()) {

                for (Tarea t : e.getTareaList()) {

                    for (TareaAgente ta : t.getTareaAgenteList()) {

                        if (pa.getAgente().getId().equals(ta.getAgenteid().getId())) {
                            if (ta.getTareaid() != null) {

                            }

                            tareaagentelista.add(ta);

                        }
                    }

                }
            }
            pa.setTareasagentes(tareaagentelista);

            paprincipal.add(pa);
        }

//        DefaultTreeNode root = new DefaultTreeNode();
//       root.setExpanded(true);
//         FacesContext context = FacesContext.getCurrentInstance();
//         ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController)context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
//          EtapaController etapacontroller = (EtapaController)context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
//                         
//          DefaultTreeNode[] x= new DefaultTreeNode[proyectoagentecontroller.getEquipotrabajo().size()+1];
//          int i=0;
//          for(ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()){
//              ++i;
//              TareaAgente tasimple = new TareaAgente();
//              tasimple.setAgenteid(pa.getAgente());
//              
//              x[i] = new DefaultTreeNode(tasimple,root);
//              x[i].setExpanded(true);
//                for(Etapa e:etapacontroller.getEtapas()){
//                    for(Tarea t:e.getTareaList()){
//                        for(TareaAgente ta:t.getTareaAgenteList()){
//                            if(pa.getAgente().getId().equals(ta.getAgenteid().getId())){
//                                
//                               DefaultTreeNode u = new DefaultTreeNode("",ta,x[i]);
//                               u.setExpanded(true);
//                               
//                                //(ta.getTareaid().getDias()/7)*pa.getHorasdedicadas()
//                                this.getTareaagentesproyecto().add(ta);
//                                
//                            }
//                        }
//                        
//                    }
//                }
//          }
//          
//          
        for (ProyectoAgente pa : paprincipal) {

            for (TareaAgente ta : pa.getTareasagentes()) {

            }
        }

    }

    public List<ProyectoAgente> getPaprincipal() {
        return paprincipal;
    }

    public void setPaprincipal(List<ProyectoAgente> paprincipal) {
        this.paprincipal = paprincipal;
    }

    public int contarTotalDiasAgente(Agente a) {
        int suma = 0;
        for (Etapa e : this.etapas) {
            for (Tarea t : e.getTareaList()) {
                for (TareaAgente ta : t.getTareaAgenteList()) {
                    if (ta.getAgenteid().equals(a)) {
                        System.out.println("-------------------------" + suma);
                        suma += t.getDias();
                    }
                }
            }
        }
        return suma;
    }

    /**
     * Obtener las etapas de un proyecto y setearlas en el controller
     *
     * @param proyectoId
     */
    public void establecerEtapasPorProyecto(int proyectoId) {
        etapas = this.getFacade().buscarEtapasProyecto(proyectoId);
    }

    /**
     * Armar Tree de Etapas y Tareas
     */
    public void armarTreeEtapasYTareasPorProyecto() {
        
        System.out.println("armarTreeEtapasYTareasPorProyecto inicio");
        
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // setar la lista de etapas para el proyecto actual
        establecerEtapasPorProyecto(proyectocontroller.getSelected().getId());
        
        // Vaciar la raiz
        root = new DefaultTreeNode(new Tarea(), null);
        
        for (Etapa etapa : etapas) {

            Tarea t = new Tarea();
            t.setId(etapa.getId());
            t.setTarea(etapa.getEtapa());
            t.setFechainicio(etapa.getFechainicio());
            t.setFechafin(etapa.getFechafin());
            t.setDias(etapa.getDias());
            TreeNode et = new DefaultTreeNode(t, root);
            et.setExpanded(true);
            for (Tarea tarea : etapa.getTareaList()) {
                TreeNode tar = new DefaultTreeNode(tarea, et);
            }
        }
    }
    
    // Eventos de Tree de Etapas y Tareas
    public void onTreeNodeSelect(NodeSelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
        
        Tarea t = (Tarea) event.getTreeNode().getData();
        Boolean a = event.getTreeNode().isLeaf();
        int c = event.getTreeNode().getChildCount();
        
        System.out.println("onTreeNodeSelect: " + t.getTarea() + " Cantidad: " + c + " es Hoja? " + a.toString());
        
        // Si es etapa
        if (!event.getTreeNode().isLeaf()){
            Etapa e = this.getFacade().find(t.getId());
        }//Si es tarea
        else{
            
        }
                
    }
 
    public void onTreeNodeUnselect(NodeUnselectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    //para avance
    public void prepareAvanceListadoEtapas() {
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);

        int contardias = 0;
        Date mindia = new Date("2999/12/12");
        Date maxdia = new Date("2001/01/01");
        root = new DefaultTreeNode(new Tarea(), null);
        root.setExpanded(true);

        for (Etapa etapa : etapas) {
            Tarea t = new Tarea();
            t.setTarea(etapa.getEtapa());
            t.setFechainicio(etapa.getFechainicio());
            t.setFechafin(etapa.getFechafin());
            t.setDias(etapa.getDias());
            
            if(etapa.getEstado()==null){
            }else{
                if(etapa.getEstado().equals("Terminada")){
                    t.setEstado(-1);
                }
            }
            TreeNode et = new DefaultTreeNode(t, root);
            et.setExpanded(true);
            for (Tarea tarea : etapa.getTareaList()) {
                if(etapa.getEstado()==null){
                }else{
                    if(tarea.getEtapaid().getEstado().equals("Terminada")){
                        tarea.setEstado(-1);
                    }
                }
                TreeNode tar = new DefaultTreeNode(tarea, et);
            }
            crearChart();
            current = null;
            tareacontroller.setTareasdeproyecto(null);
        }

    }
}
