package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.PresupuestoRubroitem;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.PresupuestoRubroitemFacade;
import ar.edu.undec.nortia.controller.RubroFacade;
import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.Presupuesto;
import ar.edu.undec.nortia.model.Proyecto;
import ar.edu.undec.nortia.model.ProyectoAgente;
import ar.edu.undec.nortia.model.Rubro;
import ar.edu.undec.nortia.model.Tarea;
import ar.edu.undec.nortia.model.TareaAgente;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name = "presupuestoRubroitemController")
@ViewScoped
public class PresupuestoRubroitemController implements Serializable {

    private PresupuestoRubroitem current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.PresupuestoRubroitemFacade ejbFacade;
    @EJB
    private RubroFacade ejbFacadeRubro;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<PresupuestoRubroitem> presupuestosrubrositems ;
    private PresupuestoRubroitem current2 = new PresupuestoRubroitem();
    private TreeNode root = new DefaultTreeNode();
    private PieChartModel pieModelAportes;
    private PieChartModel pieModelRubros;
    private BigDecimal sumagastoorganismo;
    private BigDecimal sumagastocomitente;
    private BigDecimal sumagastouniversidad;
    private BigDecimal sumatotal;

    public PresupuestoRubroitemController() {
    }

    public PresupuestoRubroitem getSelected() {
        if (current == null) {
            current = new PresupuestoRubroitem();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PresupuestoRubroitemFacade getFacade() {
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
        current = (PresupuestoRubroitem) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new PresupuestoRubroitem();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoRubroitemCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (PresupuestoRubroitem) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoRubroitemUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (PresupuestoRubroitem) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoRubroitemDeleted"));
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

    @FacesConverter(forClass = PresupuestoRubroitem.class)
    public static class PresupuestoRubroitemControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PresupuestoRubroitemController controller = (PresupuestoRubroitemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "presupuestoRubroitemController");
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
            if (object instanceof PresupuestoRubroitem) {
                PresupuestoRubroitem o = (PresupuestoRubroitem) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PresupuestoRubroitem.class.getName());
            }
        }

    }

    
    public List<PresupuestoRubroitem> getPresupuestosrubrositems()
  {
      if(this.presupuestosrubrositems==null){
          this.presupuestosrubrositems = new ArrayList<PresupuestoRubroitem>();
          FacesContext context = FacesContext.getCurrentInstance();
          ProyectoAgenteController proyectoagentecontroller= (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
           PresupuestoController presupuestocontroller= (PresupuestoController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoController}", PresupuestoController.class);
            for(ProyectoAgente pa:proyectoagentecontroller.getEquipotrabajo()){
           PresupuestoRubroitem pri = new PresupuestoRubroitem();
           
            pri.setPresupuesto(presupuestocontroller.getSelected());
            Rubro r = new Rubro();
            
            if(pa.getConsultorexterno()){
                r = ejbFacadeRubro.findbyId(4); 
            }else{
               r= ejbFacadeRubro.findbyId(5); 
            }
           pri.setRubro(r);
           pri.setDescripcion(pa.getAgente().toString());
           pri.setCostounitario(pa.costoUnitarioCargoLegajo());
           pri.setCantidad( BigDecimal.valueOf(pa.contarDiasTareas()) );
           pri.setAportecomitente(BigDecimal.ZERO);
           pri.setAporteorganismo(BigDecimal.ZERO);
           pri.setAporteuniversidad(BigDecimal.ZERO);
           //pri.setTotal(pri.getCantidad().multiply(pa.costoUnitarioCargoLegajo()));
           this.presupuestosrubrositems.add(pri);
       }
      }
      this.acomodarTablapresupuestorubroitem();
    return this.presupuestosrubrositems;
  }
  
  public void setPresupuestosrubrositems(List<PresupuestoRubroitem> presupuestosrubrositems)
  {
    this.presupuestosrubrositems = presupuestosrubrositems;
  }
  
  public String reinit()
  {
    this.current2 = new PresupuestoRubroitem();
    this.current2.setAportecomitente(BigDecimal.ZERO);
    this.current2.setAporteorganismo(BigDecimal.ZERO);
    this.current2.setAporteuniversidad(BigDecimal.ZERO);
    this.current2.setCantidad(BigDecimal.ONE);
    this.current2.setCostounitario(BigDecimal.ZERO);
    return null;
  }
  
  public PresupuestoRubroitem getCurrent2()
  {
    return this.current2;
  }
  
  public void setCurrent2(PresupuestoRubroitem current2)
  {
    this.current2 = current2;
  }
  
  public void sumarTotalCurrent()
  {
    this.current2.setTotal(this.current2.getCostounitario().multiply(this.current2.getCantidad()));
  }
  
  public boolean verificarAportes()
  {
    int valor = 0;
    BigDecimal sumaaportes = BigDecimal.ZERO;
    
    sumaaportes = this.current2.getAportecomitente().add(this.current2.getAporteorganismo()).add(this.current2.getAporteuniversidad());
    sumaaportes.compareTo(this.current2.getTotal());
    valor = sumaaportes.compareTo(this.current2.getTotal());
    if (valor == 1) {
      return false;
    }
    return true;
  }
  
  @Deprecated
  public void armarPresupuestoNodos()
  {
    PresupuestoRubroitem pri;
    TreeNode treeNode;
    TreeNode treeNodech;
    
      this.root = new DefaultTreeNode("INICIO", null);
      this.root.setExpanded(true);
      this.root.setSelectable(false);
      
      for (Rubro rub : this.ejbFacadeRubro.findAll())
      {
          
        pri = new PresupuestoRubroitem();
        pri.setRubro(rub);
        treeNode = new DefaultTreeNode(pri, this.root);
        treeNode.setExpanded(true);
        for (PresupuestoRubroitem p : getPresupuestosrubrositems()) {
          if (p.getRubro().equals(pri.getRubro())) {
            treeNodech = new DefaultTreeNode(p, treeNode);
             
              
          }
        }
      }
   
  }
  
  public void armarPresupuestosNodos2(Presupuesto presu){
      if(this.getPresupuestosrubrositems().size()>0){
         
      }else{
          
       this.setPresupuestosrubrositems(this.ejbFacade.findByPresupuesto(presu));
      }
        this.root = null;
        this.root = new DefaultTreeNode(new PresupuestoRubroitem(),null);
       PresupuestoRubroitem pri;
        TreeNode treeNode;
        TreeNode treeNodech;
      this.root.setExpanded(true);
      this.root.setSelectable(false);
     
      for (Rubro rub : this.ejbFacadeRubro.findAll())
      {
          
        pri = new PresupuestoRubroitem();
        pri.setRubro(rub);
        treeNode = new DefaultTreeNode(pri, this.root);
        treeNode.setExpanded(true);
        for (PresupuestoRubroitem p : getPresupuestosrubrositems()) {
          if (p.getRubro().equals(pri.getRubro())) {
            treeNodech = new DefaultTreeNode(p, treeNode);
              
          }
        }
      }
   
  }
  
  public TreeNode getRoot()
  {
    return this.root;
  }
  
  public void setRoot(TreeNode root)
  {
    this.root = root;
  }
  
  public void agregarPresupuesto()
  {
      
    if (verificarAportes()){
      this.presupuestosrubrositems.add(this.current2);
      this.sumarGastos();
      this.acomodarTablapresupuestorubroitem();
     //this. armarPresupuestoNodos();
     }
    else
    {
      //reinit();
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total"));
    }
    
    armarGraficosPresupuesto();
  }
  
  public void agregarPresupuestoRRHHCONSULTOR()
  {
      FacesContext context = FacesContext.getCurrentInstance();
      ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
      PresupuestoController presupuestocontroller = (PresupuestoController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoController}", PresupuestoController.class);
      EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
     
      for(ProyectoAgente pa:proyectoagentecontroller.getEquipotrabajo()){
          int suma =  etapacontroller.contarTotalDiasAgente(pa.getAgente());
           PresupuestoRubroitem pri = new PresupuestoRubroitem();
           
            pri.setPresupuesto(presupuestocontroller.getSelected());
            Rubro r = new Rubro();
            
            if(pa.getConsultorexterno()){
              r = ejbFacadeRubro.findbyId(4); 
            }else{
              r = ejbFacadeRubro.findbyId(5); 
            }
           pri.setRubro(r);
           
           int i = 0;
           int lugar = -1;
           for(PresupuestoRubroitem pris:presupuestosrubrositems){
               
               if(pris.getDescripcion().equals(pa.getAgente().toString())){
                   lugar = i;
               }else{
                   
               }
               ++i;
           }
           
           if(lugar==-1){
               if(pa.getConsultorexterno()){
                   pri.setCostounitario(pa.getHonorario());
               }else{
                    pri.setCostounitario(pa.costoUnitarioCargoLegajo());
               } 
                pri.setCantidad( BigDecimal.valueOf(suma) );
                
                pri.setDescripcion(pa.getAgente().toString());
                 pri.setAportecomitente(BigDecimal.ZERO);
                pri.setAporteorganismo(BigDecimal.ZERO);
                pri.setAporteuniversidad(BigDecimal.ZERO);
                if(pri.getCantidad().equals(BigDecimal.ZERO)){
                    
                }else{
                    if(pa.getConsultorexterno()){
                        pri.setTotal(pri.getCantidad().divide(BigDecimal.valueOf(30),0, RoundingMode.HALF_UP));
                    }else{
                       pri.setTotal((pri.getCantidad().divide(BigDecimal.valueOf(7), 2,RoundingMode.HALF_UP)).multiply(pri.getCostounitario())); 
                    }
                }
                
                this.presupuestosrubrositems.add(pri);
           }else{
                PresupuestoRubroitem prin = this.presupuestosrubrositems.get(lugar);

                if(pa.getConsultorexterno()){
                   prin.setCostounitario(pa.getHonorario());
               }else{
                    prin.setCostounitario(pa.costoUnitarioCargoLegajo());
               } 
                
                prin.setCantidad( BigDecimal.valueOf(suma) );
               
               
                if((prin.getCantidad().equals(BigDecimal.ZERO)) | (prin.getCantidad()==null)){
                    
                }else{
                    System.out.println("Cantidad------"+prin.getCantidad());
                    System.out.println("Costo Unitario -----"+ prin.getCostounitario());
                    if(pa.getConsultorexterno()){
                        prin.setTotal((prin.getCantidad().divide(BigDecimal.valueOf(30),0, RoundingMode.HALF_UP)).multiply(prin.getCostounitario()));
                    }else{
                        prin.setTotal((prin.getCantidad().divide(BigDecimal.valueOf(7),1, RoundingMode.HALF_UP)).multiply(prin.getCostounitario()));
                    }
                }
                     this.presupuestosrubrositems.set(lugar, prin);
           }
       }
      
      this.sumarGastos();
      armarGraficosPresupuesto();
  }
  
  public void eliminarPresupuesto(PresupuestoRubroitem pri)
  {
    int contador = 0;
    int posicion = 0;
    for (PresupuestoRubroitem p : getPresupuestosrubrositems())
    {
      if ((p.getRubro().equals(pri.getRubro())) && 
        (p.getDescripcion().equals(pri.getDescripcion())) && 
        (p.getTotal().equals(pri.getTotal()))) {
        posicion = contador;
      }
      
      contador++;
    }
    getPresupuestosrubrositems().remove(posicion);
    //getPresupuestosrubrositems().remove(pri);
     
    armarPresupuestoNodos();
  }
  
  public void eliminarPresupuesto2(PresupuestoRubroitem pri)
  {
      
    getPresupuestosrubrositems().remove(pri);
     
    //ejbFacade.remove(pri);
  }
  
  public void armarGraficosPresupuesto()
  {
    this.pieModelAportes = new PieChartModel();
    this.pieModelRubros = new PieChartModel();
    BigDecimal sumaaportecomitente = BigDecimal.ZERO;
    BigDecimal sumaaporteorganismo = BigDecimal.ZERO;
    BigDecimal sumaaporteuniversidad = BigDecimal.ZERO;
    for (PresupuestoRubroitem pri : getPresupuestosrubrositems())
    {
      sumaaportecomitente = sumaaportecomitente.add(pri.getAportecomitente());
      sumaaporteorganismo = sumaaporteorganismo.add(pri.getAporteorganismo());
      sumaaporteuniversidad = sumaaporteuniversidad.add(pri.getAporteuniversidad());
     
    }
    this.pieModelAportes.set("Aporte Comitente", sumaaportecomitente);
    this.pieModelAportes.set("Aporte Organismo", sumaaporteorganismo);
    this.pieModelAportes.set("Aporte Universidad", sumaaporteuniversidad);
    


    BigDecimal sumarubro = BigDecimal.ZERO;
    for (Rubro rub : this.ejbFacadeRubro.findAll())
    {
      for (PresupuestoRubroitem p : getPresupuestosrubrositems()) {
        if (rub.getRubro().equals(p.getRubro().getRubro())) {
            if(p.getTotal()==null){
                p.setTotal(BigDecimal.ZERO);
            }
            sumarubro = sumarubro.add(p.getTotal());
        }
      }
      this.pieModelRubros.set(rub.getRubro(), sumarubro);
      sumarubro = BigDecimal.ZERO;
    }
  }
  
  public PieChartModel getPieModelAportes()
  {
      
    if (this.pieModelAportes == null)
    {
      this.pieModelAportes = new PieChartModel();
      
      this.pieModelAportes.set("Gasto Organismo", 0);
      this.pieModelAportes.set("Gasto Comitente", 0);
      this.pieModelAportes.set("Gasto Universidad", 0);
     // pieModelAportes.setDiameter(150);
      pieModelAportes.setLegendPosition("e");
      pieModelAportes.setTitle("Aportes");
      pieModelAportes.setSliceMargin(5); 
      pieModelAportes.setShowDataLabels(true);
    }
    return this.pieModelAportes;
  }
  
  public void setPieModelAportes(PieChartModel pieModelAportes)
  {
    this.pieModelAportes = pieModelAportes;
  }
  
  public PieChartModel getPieModelRubros()
  {
    if (this.pieModelRubros == null)
    {
      this.pieModelRubros = new PieChartModel();
      for (Rubro r : this.ejbFacadeRubro.findAll()) {
        this.pieModelRubros.set(r.getRubro(), Integer.valueOf(0));
      }
      //pieModelRubros.setDiameter(150);
        pieModelRubros.setLegendPosition("e");
        pieModelRubros.setTitle("Rubros");
        pieModelRubros.setSliceMargin(5); 
        pieModelRubros.setShowDataLabels(true);
    }
    return this.pieModelRubros;
  }
  
  public void setPieModelRubros(PieChartModel pieModelRubros)
  {
    this.pieModelRubros = pieModelRubros;
  }
  //sumo gasto por rubro agregado 
  public void sumarGastos(){
//
//
//        Iterator it=this.presupuestosrubrositems.iterator();
//     BigDecimal totalcomitente=BigDecimal.ZERO;
//    BigDecimal totaluniversidad=BigDecimal.ZERO;
//    BigDecimal totalorganismo=BigDecimal.ZERO;
      sumagastoorganismo=BigDecimal.ZERO;
        sumagastocomitente=BigDecimal.ZERO;
        sumagastouniversidad=BigDecimal.ZERO;
        sumatotal=BigDecimal.ZERO;
//        int contador=-1;
//     while(it.hasNext()){
//         contador++;
//         PresupuestoRubroitem pri=(PresupuestoRubroitem)it.next();
//        totalcomitente=totalcomitente.add(new BigDecimal(pri.getAportecomitente().setScale(2).toString()));
//       totaluniversidad=totaluniversidad.add(new BigDecimal(pri.getAporteuniversidad().setScale(2).toString()));
//        totalorganismo=totalorganismo.add(new BigDecimal(pri.getAporteorganismo().setScale(2).toString()));
//      
//        pri.setTotal(pri.getAporteorganismo().add(pri.getAportecomitente()).add(pri.getAporteuniversidad()));
//
//        this.presupuestosrubrositems.get(contador).setTotal(pri.getTotal());
//     }
//     sumagastocomitente=totalcomitente;
//     sumagastouniversidad=totaluniversidad;
//     sumagastoorganismo=totalorganismo;
//     sumatotal=sumagastoorganismo.add(sumagastouniversidad).add(sumagastocomitente);
        for(PresupuestoRubroitem pri:this.getPresupuestosrubrositems()){
            
            this.sumagastocomitente=this.sumagastocomitente.add(pri.getAportecomitente());
            this.sumagastoorganismo=this.sumagastoorganismo.add(pri.getAporteorganismo());
            this.sumagastouniversidad=this.sumagastouniversidad.add(pri.getAporteuniversidad());
            System.out.println("--------------suma comitente--x----------------"+this.sumagastocomitente);
      System.out.println("--------------suma organismo--------x----------"+this.sumagastoorganismo);
      System.out.println("--------------suma universidad-----x-------------"+this.sumagastouniversidad);
      System.out.println("--------------suma total----------x--------"+this.sumatotal);
        }
        this.sumatotal = (sumagastoorganismo.add(sumagastocomitente)).add(sumagastouniversidad);
        System.out.println("--------------suma comitente------------------"+this.sumagastocomitente);
      System.out.println("--------------suma organismo------------------"+this.sumagastoorganismo);
      System.out.println("--------------suma universidad------------------"+this.sumagastouniversidad);
      System.out.println("--------------suma total------------------"+this.sumatotal);
    }

    public BigDecimal getSumagastoorganismo() {
        return sumagastoorganismo;
    }

    public void setSumagastoorganismo(BigDecimal sumagastoorganismo) {
        this.sumagastoorganismo = sumagastoorganismo;
    }

    public BigDecimal getSumagastocomitente() {
        return sumagastocomitente;
    }

    public void setSumagastocomitente(BigDecimal sumagastocomitente) {
        this.sumagastocomitente = sumagastocomitente;
    }

    public BigDecimal getSumagastouniversidad() {
        return sumagastouniversidad;
    }

    public void setSumagastouniversidad(BigDecimal sumagastouniversidad) {
        this.sumagastouniversidad = sumagastouniversidad;
    }

    public BigDecimal getSumatotal() {
        return sumatotal;
    }

    public void setSumatotal(BigDecimal sumatotal) {
        this.sumatotal = sumatotal;
    }
  
  public void acomodarTablapresupuestorubroitem(){
      List<PresupuestoRubroitem> prin = new ArrayList<PresupuestoRubroitem>();
      for(Rubro r:this.ejbFacadeRubro.findAll())
        for(PresupuestoRubroitem pri:this.presupuestosrubrositems){
                if(pri.getRubro().equals(r)){
                    prin.add(pri);
                }
        }
      this.presupuestosrubrositems = prin;
  }
  
  public void onCellEdit(CellEditEvent event) {
      current2 = this.presupuestosrubrositems.get(event.getRowIndex());
      if (verificarAportes()){
        this.presupuestosrubrositems.set(event.getRowIndex(),current2);
        this.sumarGastos();
        this.acomodarTablapresupuestorubroitem();
        this.sumarGastos();
        this.armarGraficosPresupuesto();
       //this. armarPresupuestoNodos();
     }
    else
    {
      
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total en el rubro "+ current2.getRubro().getRubro() +" y descripcion " +current2.getDescripcion()));
    }
    
    armarGraficosPresupuesto();
      
  }
  
}
