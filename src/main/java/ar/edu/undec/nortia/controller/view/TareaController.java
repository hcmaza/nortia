package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.EtapaFacade;
import ar.edu.undec.nortia.controller.RubroFacade;
import ar.edu.undec.nortia.model.Tarea;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.TareaFacade;
import ar.edu.undec.nortia.controller.TareaavanceFacade;
import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.PresupuestoTarea;
import ar.edu.undec.nortia.model.TareaAgente;
import ar.edu.undec.nortia.model.Tareaavance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
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

import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name = "tareaController")
@SessionScoped
public class TareaController implements Serializable {

    private Tarea current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.TareaFacade ejbFacade;
    @EJB
    private RubroFacade ejbFacadeRubro;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Tarea> tareasdeproyecto = new ArrayList<Tarea>() ;
    private Tarea tareanueva=new Tarea();
    
    private Tarea tarea1=new Tarea();
    @ManagedProperty("#{tareaAgenteController}")
    private TareaAgenteController tareaagentecontroller;
    @ManagedProperty("#{presupuestoTareaController}")
    private PresupuestoTareaController presupuestotareacontroller;
        
    private List<Tareaavance> tareasavancesdeproyecto = new ArrayList<Tareaavance>() ;
    
    private Tareaavance avanceold = new Tareaavance();
    
    @EJB
    private TareaavanceFacade ejbtareaavance; 
    
    @EJB
    private EtapaFacade ejbetapa;
    
    
    public TareaController() {
    }

    public Tarea getSelected() {
        if (current == null) {
            current = new Tarea();
            current.setTarea("");
            current.setDescripcion("");
            current.setFechainicio(new Date());
            current.setFechafin(null);
            current.setDias(0);
            current.setEstado(0);
            current.setTareaAgenteList(null);
            current.setPresupuestoTareaList(null);
            selectedItemIndex = -1;
        }
        return current;
    }

    private TareaFacade getFacade() {
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
        current = (Tarea) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Tarea();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TareaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Tarea) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TareaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Tarea) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TareaDeleted"));
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

    @FacesConverter(forClass = Tarea.class)
    public static class TareaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TareaController controller = (TareaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tareaController");
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
            if (object instanceof Tarea) {
                Tarea o = (Tarea) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Tarea.class.getName());
            }
        }

    }

    public List<Tarea> getTareasdeproyecto() {
        
        return tareasdeproyecto;
    }

    public void setTareasdeproyecto(List<Tarea> tareasdeproyecto) {
        this.tareasdeproyecto = tareasdeproyecto;
    }
    
     public void rearmarTareasProyecto(){
         
         tareaagentecontroller.setTareasagentes(new ArrayList<TareaAgente>() );
         presupuestotareacontroller.setPresupuestostareasitems(new ArrayList<PresupuestoTarea>() );
         current=null;
         tarea1=null;
        
         tareanueva=null;
         
     }
    
    public void armarTareasProyecto(){
        if(tareasdeproyecto==null){
            tareasdeproyecto = new ArrayList();
        }
        
        if(current.getId()==null){
            current.setId(tareasdeproyecto.size()+1);
            //current.setEstado("0");
           
           current.setTareaAgenteList(tareaagentecontroller.getTareasagentes());
           current.setPresupuestoTareaList(this.presupuestotareacontroller.getPresupuestostareasitems());
           
           tareasdeproyecto.add(current);
           presupuestotareacontroller.armarPresupuestoGeneral();
        }
        
        current=null;
        
       //crearChart();
       
    }
    
       
    public void calcularDiferenciaDias(){
        // tomamos la instancia del tipo de calendario
        Calendar calendarInicio = Calendar.getInstance();
        Calendar calendarFinal = Calendar.getInstance();

        System.out.println("calcularDiferenciaDias 0");

        // Configramos la fecha del calendatio, tomando los valores del date que
        // generamos en el parse
        calendarInicio.setTime(current.getFechainicio());
        calendarFinal.setTime(current.getFechafin());

        System.out.println("calcularDiferenciaDias 1");

        // obtenemos el valor de las fechas en milisegundos
        long milisegundos1 = calendarInicio.getTimeInMillis();
        long milisegundos2 = calendarFinal.getTimeInMillis();

        System.out.println("calcularDiferenciaDias 2");

        // tomamos la diferencia
        long diferenciaMilisegundos = milisegundos2 - milisegundos1;

        // Despues va a depender en que formato queremos  mostrar esa
        // diferencia, minutos, segundo horas, dias, etc, aca van algunos 
        // ejemplos de conversion
        // calcular la diferencia en segundos
        long diffSegundos =  Math.abs (diferenciaMilisegundos / 1000);

        // calcular la diferencia en minutos
        long diffMinutos =  Math.abs (diferenciaMilisegundos / (60 * 1000));
        long restominutos = diffMinutos%60;

        // calcular la diferencia en horas
        long diffHoras =   (diferenciaMilisegundos / (60 * 60 * 1000));

        // calcular la diferencia en dias
        long diffdias = Math.abs( diferenciaMilisegundos / (24 * 60 * 60 * 1000) );
        int entero=(int) diffdias;

        System.out.println("calcularDiferenciaDias 3");

        getSelected().setDias(entero+1);

        System.out.println("calcularDiferenciaDias 4");

    }

    public Tarea getTareanueva() {
        return tareanueva;
    }

    public void setTareanueva(Tarea tareanueva) {
        this.tareanueva = tareanueva;
    }
    
    
    
    public void removerTareadeProyecto(){
       
       
        this.tareasdeproyecto.remove(current);
      //  crearChart();
        
        
    }
    
   

        
    public void sumarDias(){
        Calendar cal=Calendar.getInstance();
        cal.setTime(current.getFechainicio());
        cal.add(Calendar.DAY_OF_YEAR, current.getDias());
        current.setFechafin(cal.getTime());

                
    }
    
    public void setSelected(Tarea tarea){
        
        current= tarea;
    }

    
    public Tarea getTarea1() {
        return tarea1;
    }

    public void setTarea1(Tarea tarea1) {
        this.tarea1 = tarea1;
    }
    
    public void grabarTareas(){
        for(Tarea t:tareasdeproyecto){
            this.ejbFacade.createWithPersist(t);
            
        }
    }
    
    public void editarTarea(Tarea t){
        current = t;
       tareaagentecontroller.setTareasagentes(current.getTareaAgenteList());
        presupuestotareacontroller.setPresupuestostareasitems(current.getPresupuestoTareaList());
    }

    public TareaAgenteController getTareaagentecontroller() {
        return tareaagentecontroller;
    }

    public void setTareaagentecontroller(TareaAgenteController tareaagentecontroller) {
        this.tareaagentecontroller = tareaagentecontroller;
    }

    public PresupuestoTareaController getPresupuestotareacontroller() {
        return presupuestotareacontroller;
    }

    public void setPresupuestotareacontroller(PresupuestoTareaController presupuestotareacontroller) {
        this.presupuestotareacontroller = presupuestotareacontroller;
    }
    
    /**
     * Llenar la lista de tareas con las tareas de una etapa
     * 
     * @param etapaId
     */
    public void establecerTareasPorEtapa(int etapaId){
        tareasdeproyecto = this.getFacade().buscarTareasEtapa(etapaId);
        
        
    }
    
    /**
     * Llenar la lista de tareas con las tareas de un proyecto
     * 
     * @param proyectoId 
     */
    public void establecerTareasPorProyecto(int proyectoId){
        try{
            tareasdeproyecto = this.getFacade().buscarTareasProyecto(proyectoId);
        }catch(Exception e){
            System.out.println("Excepcion en TareaController: establecerTareasPorProyecto(int proyectoId)");
        }
    }
    
    public void editarAvanceTarea(Tarea t){
        current = t;
       
    }

    public List<Tareaavance> getTareasavancesdeproyecto() {
        return tareasavancesdeproyecto;
    }

    public void setTareasavancesdeproyecto(List<Tareaavance> tareasavancesdeproyecto) {
        this.tareasavancesdeproyecto = tareasavancesdeproyecto;
    }
    
    
    //armo los avances de las tareas
    public void armarTareasAvancesProyecto(){
        
        FacesContext context = FacesContext.getCurrentInstance();
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        TareaavanceController tareaavancecontroller = (TareaavanceController) context.getApplication().evaluateExpressionGet(context, "#{tareaavanceController}", TareaavanceController.class);
        if(tareasavancesdeproyecto==null){
            tareasavancesdeproyecto = new ArrayList();
        }
        tareaavancecontroller.getSelected().setTareaid(current);
       List<Etapa> listadoetapas = new ArrayList<Etapa>();
        for(Etapa e : etapacontroller.getEtapas()){
            List<Tarea> listadotareas = new ArrayList<Tarea>();
            for(Tarea t: e.getTareaList()){
                
                if(t.getId().equals(tareaavancecontroller.getSelected().getTareaid().getId())){
                    tareaavancecontroller.getSelected().setFecha(new Date());
                    
                    t.getTareaavanceList().add(tareaavancecontroller.getSelected());
                     System.out.println("Tarea "+ t.getTarea());
                     System.out.println("tarea avance "+t.getTareaavanceList().get(0).getAvance());
             
                }
                
                   listadotareas.add(t);
                
                
            }
            
            
            e.setTareaList(listadotareas);
            listadoetapas.add(e);
        }
         etapacontroller.setEtapas(listadoetapas);
         
         //grabo en la base de datos
         try{
           
              this.ejbtareaavance.edit(tareaavancecontroller.getSelected());
              // Pregunto si la etapa de esa tarea esta completa si lo esta
              // realizo el infome de avance de esa etapa
              int contadortotaltareas = 0;
              int contadortareascompletas = 0;
              for(Tarea tarea : ejbetapa.find(tareaavancecontroller.getSelected().getTareaid().getEtapaid().getId()).getTareaList()){
                  if(ejbtareaavance.findLastDate(tarea).getAvance()==100){
                      ++contadortareascompletas;
                      System.out.println("contadortareascompletas "+contadortareascompletas);
                  
                  }
                  ++contadortotaltareas;
                  System.out.println("contadortotaltareas "+contadortotaltareas);
                  
              }
              if(contadortotaltareas==contadortareascompletas){
                  System.out.println("igualessssssssssssssss");
                  RequestContext.getCurrentInstance().execute("PF('dinformeavance').show()");
                
              }
              //-----------------------------------
         }catch(Exception e){
             System.out.println("Error en armar tarea avance ="+e);
         }
                
        etapacontroller.editarListadoEtapas();
       //crearChart();
       
    }

    public Tareaavance getAvanceold() {
        return avanceold;
    }

    public void setAvanceold(Tareaavance avanceold) {
        this.avanceold = avanceold;
    }


    /**
     *  TabView en CrearConEtapa >> Dialogo Crear Etapa >> Dialogo Nueva Tarea en Etapa
     */
/*    private TabView nuevaTareaTab = new TabView();

    public TabView getNuevaTareaTab() {
        return nuevaTareaTab;
    }

    public void setNuevaTareaTab(TabView nuevaTareaTab) {
        this.nuevaTareaTab = nuevaTareaTab;
    }

    public void onNuevaTareaTabChange(TabChangeEvent event) {
        TabView tabView = (TabView) event.getComponent();
        int activeIndex = tabView.getChildren().indexOf(event.getTab());
        this.nuevaTareaTab.setActiveIndex(activeIndex);
    }*/




}
