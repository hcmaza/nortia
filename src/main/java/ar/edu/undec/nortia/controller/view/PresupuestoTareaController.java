package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.PresupuestoTarea;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.PresupuestoTareaFacade;
import ar.edu.undec.nortia.controller.RubroFacade;
import ar.edu.undec.nortia.model.Etapa;
import ar.edu.undec.nortia.model.ProyectoAgente;
import ar.edu.undec.nortia.model.Rubro;
import ar.edu.undec.nortia.model.Tarea;
import ar.edu.undec.nortia.model.TareaAgente;

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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name = "presupuestoTareaController")
@SessionScoped
public class PresupuestoTareaController implements Serializable {

    private PresupuestoTarea current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.PresupuestoTareaFacade ejbFacade;
    @EJB
    private RubroFacade ejbFacadeRubro;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<PresupuestoTarea> presupuestostareasitems;
    private List<PresupuestoTarea> presupuestostareas;
    private PresupuestoTarea current2 = new PresupuestoTarea();
    private TreeNode root = new DefaultTreeNode();
    private PieChartModel pieModelAportes;
    private PieChartModel pieModelRubros;
    private BigDecimal sumagastoorganismo;
    private BigDecimal sumagastocomitente;
    private BigDecimal sumagastouniversidad;
    private BigDecimal sumatotal;

    // Para el pick list de solicitudes
    private DualListModel<PresupuestoTarea> plPresupuestoTarea;

    public DualListModel<PresupuestoTarea> getPlPresupuestoTarea() {
        if (plPresupuestoTarea == null) {
            plPresupuestoTarea = new DualListModel<PresupuestoTarea>(new ArrayList<PresupuestoTarea>(), new ArrayList<PresupuestoTarea>());
            //plPresupuestoTarea =  new DualListModel<PresupuestoTarea>(this.getPresupuestostareas(), new ArrayList<PresupuestoTarea>());
        }
        return plPresupuestoTarea;
    }

    public void setPlPresupuestoTarea(DualListModel<PresupuestoTarea> plPresupuestoTarea) {
        this.plPresupuestoTarea = plPresupuestoTarea;
    }

    public PresupuestoTareaController() {
    }

    public PresupuestoTarea getSelected() {
        if (current == null) {
            current = new PresupuestoTarea();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(PresupuestoTarea nuevoCurrent) {
        current = nuevoCurrent;
    }

    private PresupuestoTareaFacade getFacade() {
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
        return "List";
    }

    public String prepareView() {
        current = (PresupuestoTarea) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new PresupuestoTarea();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoTareaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (PresupuestoTarea) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoTareaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (PresupuestoTarea) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoTareaDeleted"));
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

    @FacesConverter(forClass = PresupuestoTarea.class)
    public static class PresupuestoTareaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PresupuestoTareaController controller = (PresupuestoTareaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "presupuestoTareaController");
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
            if (object instanceof PresupuestoTarea) {
                PresupuestoTarea o = (PresupuestoTarea) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PresupuestoTarea.class.getName());
            }
        }

    }

    public List<PresupuestoTarea> getPresupuestostareasitems() {
        if (presupuestostareasitems == null) {
            presupuestostareasitems = new ArrayList<PresupuestoTarea>();
        }

        return this.presupuestostareasitems;
    }

    public void setPresupuestostareasitems(List<PresupuestoTarea> presupuestostareasitems) {
        this.presupuestostareasitems = presupuestostareasitems;
    }

    public String reinit() {
        this.current2 = new PresupuestoTarea();
        this.current2.setAportecomitente(BigDecimal.ZERO);
        this.current2.setAporteorganismo(BigDecimal.ZERO);
        this.current2.setAporteuniversidad(BigDecimal.ZERO);
        this.current2.setCantidad(BigDecimal.ONE);
        this.current2.setCostounitario(BigDecimal.ZERO);
        return null;
    }

    public String reinit(Rubro rubro) {

        this.current = new PresupuestoTarea();
        this.current.setRubro(rubro);
        this.current.setAportecomitente(BigDecimal.ZERO);
        this.current.setAporteorganismo(BigDecimal.ZERO);
        this.current.setAporteuniversidad(BigDecimal.ZERO);
        this.current.setCantidad(BigDecimal.ONE);
        this.current.setCostounitario(BigDecimal.ZERO);
        return null;
    }

    public String nuevoCurrent(){

        System.out.println("PresupuestoTareaController >> nuevoCurrent");

        this.current = new PresupuestoTarea();
        this.current.setAportecomitente(BigDecimal.ZERO);
        this.current.setAporteorganismo(BigDecimal.ZERO);
        this.current.setAporteuniversidad(BigDecimal.ZERO);
        this.current.setCantidad(BigDecimal.ONE);
        this.current.setCostounitario(BigDecimal.ZERO);
        return null;
    }

    public PresupuestoTarea getCurrent2() {
        return this.current2;
    }

    public void setCurrent2(PresupuestoTarea current2) {
        this.current2 = current2;
    }

    public void sumarTotalCurrent() {
        this.current.setTotal(this.current.getCostounitario().multiply(this.current.getCantidad()));
    }

    public boolean verificarAportes() {
        int valor = 0;
        BigDecimal sumaaportes = BigDecimal.ZERO;

        sumaaportes = this.current.getAportecomitente().add(this.current.getAporteorganismo()).add(this.current.getAporteuniversidad());

        if (sumaaportes.longValue() == this.current.getTotal().longValue()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verificarAportes2() {
        int valor = 0;
        BigDecimal sumaaportes = BigDecimal.ZERO;

        sumaaportes = this.current2.getAportecomitente().add(this.current2.getAporteorganismo()).add(this.current2.getAporteuniversidad());

        System.out.println("verificarAportes2 >> suma de aportes >>" + sumaaportes.floatValue());
        System.out.println("verificarAportes2 >> current2.getTotal() >>" + this.current2.getTotal());

        if(null == this.current2){
            System.out.println("this.current2 NULO");
        }

        if(null == this.current2.getTotal()){
            System.out.println("this.current2.getTotal NULO");
        }

        if (sumaaportes.longValue() == this.current2.getTotal().longValue()) {
            System.out.println("verificarAportes2 >> suma de aportes == al total de current2");
            System.out.println("verificarAportes2 >> return TRUE");
            return true;
        } else {
            System.out.println("verificarAportes2 >> suma de aportes != al total de current2");
            System.out.println("verificarAportes2 >> return FALSE");
            return false;
        }
    }

    public void armarPresupuestoNodos() {

        FacesContext context = FacesContext.getCurrentInstance();
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        
        BigDecimal sumatotaltarea = BigDecimal.ZERO;
        BigDecimal sumaaportecomitentetarea = BigDecimal.ZERO;
        BigDecimal sumaaporteuniversidadtarea = BigDecimal.ZERO;
        BigDecimal sumaaporteorganismotarea = BigDecimal.ZERO;

        BigDecimal sumatotaletapa = BigDecimal.ZERO;
        BigDecimal sumaaportecomitenteetapa = BigDecimal.ZERO;
        BigDecimal sumaaporteuniversidadetapa = BigDecimal.ZERO;
        BigDecimal sumaaporteorganismoetapa = BigDecimal.ZERO;

        List<PresupuestoTarea> sumatoriaporetapa = new ArrayList<PresupuestoTarea>();
        List<PresupuestoTarea> sumatoriaportarea = new ArrayList<PresupuestoTarea>();

        int ie = 0;
        int it = 0;

        for (Etapa etapa : etapacontroller.getEtapas()) {

            sumatotaletapa = BigDecimal.ZERO;
            sumaaportecomitenteetapa = BigDecimal.ZERO;
            sumaaporteuniversidadetapa = BigDecimal.ZERO;
            sumaaporteorganismoetapa = BigDecimal.ZERO;

            for (Tarea tarea : etapa.getTareaList()) {

                sumaaportecomitentetarea = BigDecimal.ZERO;
                sumaaporteuniversidadtarea = BigDecimal.ZERO;
                sumaaporteorganismotarea = BigDecimal.ZERO;
                sumatotaltarea = BigDecimal.ZERO;

                for (PresupuestoTarea pt : tarea.getPresupuestoTareaList()) {

                    sumaaportecomitentetarea = sumaaportecomitentetarea.add(pt.getAportecomitente());
                    sumaaporteuniversidadtarea = sumaaporteuniversidadtarea.add(pt.getAporteuniversidad());
                    sumaaporteorganismotarea = sumaaporteorganismotarea.add(pt.getAporteorganismo());
                    sumatotaltarea = sumaaportecomitentetarea.add(sumaaporteuniversidadtarea.add(sumaaporteorganismotarea));
                
                }
                
                sumaaportecomitenteetapa = sumaaportecomitenteetapa.add(sumaaportecomitentetarea);
                sumaaporteuniversidadetapa = sumaaporteuniversidadetapa.add(sumaaporteuniversidadtarea);
                sumaaporteorganismoetapa = sumaaporteorganismoetapa.add(sumaaporteorganismotarea);
                sumatotaletapa = sumaaportecomitenteetapa.add(sumaaporteuniversidadetapa.add(sumaaporteorganismoetapa));
                
                PresupuestoTarea ptx2 = new PresupuestoTarea();
                ptx2.setDescripcion("Tarea: " + tarea.getTarea());
                ptx2.setAportecomitente(sumaaportecomitentetarea);
                ptx2.setAporteuniversidad(sumaaporteuniversidadtarea);
                ptx2.setAporteorganismo(sumaaporteorganismotarea);
                ptx2.setTotal(sumatotaltarea);
                
                sumatoriaportarea.add(it, ptx2);
                it++;

            }

            PresupuestoTarea ptx = new PresupuestoTarea();
            ptx.setDescripcion("Etapa: " + etapa.getEtapa());
            ptx.setAportecomitente(sumaaportecomitenteetapa);
            ptx.setAporteuniversidad(sumaaporteuniversidadetapa);
            ptx.setAporteorganismo(sumaaporteorganismoetapa);
            ptx.setTotal(sumatotaletapa);

            sumatoriaporetapa.add(ie, ptx);

            ie++;

        }

        root = new DefaultTreeNode(new PresupuestoTarea(), null);
        root.setExpanded(false);

        ie = 0;
        it = 0;

        for (Etapa etapa : etapacontroller.getEtapas()) {

            TreeNode et = new DefaultTreeNode(sumatoriaporetapa.get(ie), root);
            et.setExpanded(false);

            for (Tarea tarea : etapa.getTareaList()) {
                
                TreeNode tar = new DefaultTreeNode(sumatoriaportarea.get(it), et);
                tar.setExpanded(false);

                for (PresupuestoTarea p : tarea.getPresupuestoTareaList()) {
                    TreeNode tp = new DefaultTreeNode(p, tar);
                }
                it++;
            }
            ie++;
        }

        System.out.println("");

    }

//  public void armarPresupuestosNodos2(Tarea presu){
//      if(this.getPresupuestosrubrositems().size()>0){
//         
//      }else{
//          
//       this.setPresupuestosrubrositems(this.ejbFacade.findByPresupuesto(presu));
//      }
//        this.root = null;
//        this.root = new DefaultTreeNode(new PresupuestoRubroitem(),null);
//       PresupuestoRubroitem pri;
//        TreeNode treeNode;
//        TreeNode treeNodech;
//      this.root.setExpanded(true);
//      this.root.setSelectable(false);
//     
//      for (Rubro rub : this.ejbFacadeRubro.findAll())
//      {
//          
//        pri = new PresupuestoRubroitem();
//        pri.setRubro(rub);
//        treeNode = new DefaultTreeNode(pri, this.root);
//        treeNode.setExpanded(true);
//        for (PresupuestoRubroitem p : getPresupuestosrubrositems()) {
//          if (p.getRubro().equals(pri.getRubro())) {
//            treeNodech = new DefaultTreeNode(p, treeNode);
//              
//          }
//        }
//      }
//   
//  }
    public TreeNode getRoot() {
        return this.root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public void agregarPresupuesto() {
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);

        if (verificarAportes()) {

            current2.setTarea(tareacontroller.getSelected());
            this.presupuestostareasitems.add(this.current);
            this.armarPresupuestoGeneral();
            this.acomodarTablapresupuestotareaitem();

            //this. armarPresupuestoNodos();
        } else {
            //reinit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total"));
        }

        armarGraficosPresupuesto();
    }

    public boolean agregarPresupuestoRRHHCONSULTOR(TareaAgente taprincipal) {

        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
        ProyectoAgente proyectoAgente = new ProyectoAgente();

        boolean resultado = false;

        for (ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()) {
            if (pa.getAgente().equals(taprincipal.getAgenteid())) {
                proyectoAgente = pa;
            }
        }

        int i = 0;
        int lugar = -1;

        for (PresupuestoTarea pts : presupuestostareasitems) {

            if (pts.getDescripcion().equals(taprincipal.getAgenteid().toString())) {
                lugar = i;
            } else {

            }
            ++i;
        }

        if (lugar == -1) {

            if (verificarAportes2()) {
                this.presupuestostareasitems.add(current2);
                this.armarPresupuestoGeneral();
                this.armarGraficosPresupuesto();
                resultado = true;

            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total en el rubro " + current2.getRubro().getRubro() + " y descripcion " + current2.getDescripcion()));
                resultado = false;
            }

        } else {

            if (verificarAportes2()) {
                this.presupuestostareasitems.set(lugar, current2);
                this.armarPresupuestoGeneral();
                this.armarGraficosPresupuesto();
                resultado = true;
            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total en el rubro " + current2.getRubro().getRubro() + " y descripcion " + current2.getDescripcion()));
                resultado = false;
            }

        }

        return resultado;
        //this. armarPresupuestoNodos();
        //armarGraficosPresupuesto();
    }

    public void eliminarPresupuesto(PresupuestoTarea pt) {
        int contador = 0;
        int posicion = 0;
        for (PresupuestoTarea p : getPresupuestostareasitems()) {
            if ((p.getRubro().equals(pt.getRubro()))
                    && (p.getDescripcion().equals(pt.getDescripcion()))
                    && (p.getTotal().equals(pt.getTotal()))) {
                posicion = contador;
            }

            contador++;
        }
        getPresupuestostareasitems().remove(posicion);
        this.armarPresupuestoGeneral();
    //getPresupuestosrubrositems().remove(pri);

        // armarPresupuestoNodos();
    }

    public void eliminarPresupuesto2(PresupuestoTarea pt) {

        getPresupuestostareasitems().remove(pt);
        this.armarPresupuestoGeneral();
        //ejbFacade.remove(pri);
    }

    public void armarGraficosPresupuesto() {
        this.pieModelAportes = new PieChartModel();
        this.pieModelRubros = new PieChartModel();
        BigDecimal sumaaportecomitente = BigDecimal.ZERO;
        BigDecimal sumaaporteorganismo = BigDecimal.ZERO;
        BigDecimal sumaaporteuniversidad = BigDecimal.ZERO;
        for (PresupuestoTarea pt : getPresupuestostareasitems()) {
            sumaaportecomitente = sumaaportecomitente.add(pt.getAportecomitente());
            sumaaporteorganismo = sumaaporteorganismo.add(pt.getAporteorganismo());
            sumaaporteuniversidad = sumaaporteuniversidad.add(pt.getAporteuniversidad());

        }
        this.pieModelAportes.set("Aporte Comitente", sumaaportecomitente);
        this.pieModelAportes.set("Aporte Organismo", sumaaporteorganismo);
        this.pieModelAportes.set("Aporte Universidad", sumaaporteuniversidad);

        BigDecimal sumarubro = BigDecimal.ZERO;
        for (Rubro rub : this.ejbFacadeRubro.findAll()) {
            for (PresupuestoTarea p : getPresupuestostareasitems()) {
                if (rub.getRubro().equals(p.getRubro().getRubro())) {
                    if (p.getTotal() == null) {
                        p.setTotal(BigDecimal.ZERO);
                    }
                    sumarubro = sumarubro.add(p.getTotal());
                }
            }
            this.pieModelRubros.set(rub.getRubro(), sumarubro);
            sumarubro = BigDecimal.ZERO;
        }
    }

    public PieChartModel getPieModelAportes() {

        if (this.pieModelAportes == null) {
            this.pieModelAportes = new PieChartModel();

            this.pieModelAportes.set("Gasto Organismo", 0);
            this.pieModelAportes.set("Gasto Comitente", 0);
            this.pieModelAportes.set("Gasto Universidad", 0);
            // pieModelAportes.setDiameter(150);
            pieModelAportes.setLegendPosition("e");
            pieModelAportes.setSeriesColors("21B2CE,9C4DAD,FF964A");
            pieModelAportes.setTitle("Aportes");
            pieModelAportes.setSliceMargin(5);
            pieModelAportes.setShowDataLabels(true);
        }
        return this.pieModelAportes;
    }

    public void setPieModelAportes(PieChartModel pieModelAportes) {
        this.pieModelAportes = pieModelAportes;
    }

    public PieChartModel getPieModelRubros() {
        if (this.pieModelRubros == null) {
            this.pieModelRubros = new PieChartModel();
            for (Rubro r : this.ejbFacadeRubro.findAll()) {
                this.pieModelRubros.set(r.getRubro(), Integer.valueOf(0));
            }
            //pieModelRubros.setDiameter(150);
            pieModelRubros.setLegendPosition("e");
            pieModelRubros.setSeriesColors("21B2CE,9C4DAD,FF964A,5ACB73,CE4131,DED7A5");
            pieModelRubros.setTitle("Rubros");
            pieModelRubros.setSliceMargin(5);
            pieModelRubros.setShowDataLabels(true);
        }
        return this.pieModelRubros;
    }

    public void setPieModelRubros(PieChartModel pieModelRubros) {
        this.pieModelRubros = pieModelRubros;
    }

    //sumo gastos 
    public void sumarGastos() {
        sumagastoorganismo = BigDecimal.ZERO;
        sumagastocomitente = BigDecimal.ZERO;
        sumagastouniversidad = BigDecimal.ZERO;
        sumatotal = BigDecimal.ZERO;

        for (PresupuestoTarea pt : this.getPresupuestostareas()) {

            this.sumagastocomitente = this.sumagastocomitente.add(pt.getAportecomitente());
            this.sumagastoorganismo = this.sumagastoorganismo.add(pt.getAporteorganismo());
            this.sumagastouniversidad = this.sumagastouniversidad.add(pt.getAporteuniversidad());
        }
        this.sumatotal = (sumagastoorganismo.add(sumagastocomitente)).add(sumagastouniversidad);

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

    public void acomodarTablapresupuestotareaitem() {
        List<PresupuestoTarea> ptn = new ArrayList<PresupuestoTarea>();
        for (Rubro r : this.ejbFacadeRubro.findAll()) {
            for (PresupuestoTarea pt : this.presupuestostareasitems) {
                if (pt.getRubro().equals(r)) {
                    ptn.add(pt);
                }
            }
        }
        this.presupuestostareasitems = ptn;
    }

    public List<PresupuestoTarea> buscarRubroenlistaPresupuestoTarea(Rubro rubro) {
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                if (rubro.equals(ptn.getRubro())) {
                    pt.add(ptn);
                }
            }
        }
        return pt;
    }

    public BigDecimal sumaraportesOrganismoRubroenlistaPresupuestoTarea(Rubro rubro) {
        BigDecimal sumagastoorganismorubro = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                if (rubro.equals(ptn.getRubro())) {
                    sumagastoorganismorubro = sumagastoorganismorubro.add(ptn.getAporteorganismo());
                }
            }
        }
        return sumagastoorganismorubro;
    }

    public BigDecimal sumaraportesUniversidadRubroenlistaPresupuestoTarea(Rubro rubro) {
        BigDecimal sumagastouniversidadrubro = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                if (rubro.equals(ptn.getRubro())) {
                    sumagastouniversidadrubro = sumagastouniversidadrubro.add(ptn.getAporteuniversidad());
                }
            }
        }
        return sumagastouniversidadrubro;
    }

    public BigDecimal sumaraportesComitenteRubroenlistaPresupuestoTarea(Rubro rubro) {
        BigDecimal sumagastocomitenterubro = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                if (rubro.equals(ptn.getRubro())) {
                    sumagastocomitenterubro = sumagastocomitenterubro.add(ptn.getAportecomitente());
                }
            }
        }
        return sumagastocomitenterubro;
    }

    public BigDecimal sumaraportesTotalesRubroenlistaPresupuestoTarea(Rubro rubro) {

        BigDecimal sumagastototalrubro = BigDecimal.ZERO;
        BigDecimal sumagastocomitenterubro = BigDecimal.ZERO;
        BigDecimal sumagastouniversidadrubro = BigDecimal.ZERO;
        BigDecimal sumagastoorganismorubro = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                if (rubro.equals(ptn.getRubro())) {
                    sumagastocomitenterubro = sumagastocomitenterubro.add(ptn.getAportecomitente());
                    sumagastouniversidadrubro = sumagastouniversidadrubro.add(ptn.getAporteuniversidad());
                    sumagastoorganismorubro = sumagastoorganismorubro.add(ptn.getAporteorganismo());
                }
            }
            sumagastototalrubro = (sumagastocomitenterubro.add(sumagastouniversidadrubro)).add(sumagastoorganismorubro);
        }
        return sumagastototalrubro;
    }

    public List<PresupuestoTarea> getPresupuestostareas() {

        return presupuestostareas;
    }

    public void setPresupuestostareas(List<PresupuestoTarea> presupuestostareas) {
        this.presupuestostareas = presupuestostareas;
    }

    public void armarPresupuestoGeneral() {

        presupuestostareas = new ArrayList<PresupuestoTarea>();
        FacesContext context = FacesContext.getCurrentInstance();
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);

        for (Etapa e : etapacontroller.getEtapas()) {
            for (Tarea t : e.getTareaList()) {
                for (PresupuestoTarea pt : t.getPresupuestoTareaList()) {
                    System.out.println("-------desc----------------" + pt.getDescripcion());
                    System.out.println("---------comitente--------------" + pt.getAportecomitente());
                    System.out.println("---------universidad--------------" + pt.getAporteuniversidad());
                    System.out.println("---------organismo--------------" + pt.getAporteorganismo());
                    System.out.println("----------cantidaad-------------" + pt.getCantidad());
                    System.out.println("---------costounitario--------------" + pt.getCostounitario());
                    this.presupuestostareas.add(pt);
                }

            }

        }
        this.sumarGastos();
        this.armarPresupuestoNodos();
    }

    /**
     * se llena la lista [presupuestostareas] para un proyecto determinado
     *
     * @param proyectoid
     * @param tiposolicitud
     */
    public void establecerListaPresupuestoTareaBienesPorProyecto(int proyectoid, String tiposolicitud) {

        // Obtenemos el controlador de tarea
        FacesContext context = FacesContext.getCurrentInstance();
        TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);

        // La lista de presupuestos de tarea
        List<PresupuestoTarea> resultado = new ArrayList<PresupuestoTarea>();

        // Seteamos las tareas de las etapas del proyecto en el controlador de tareas
        tareacontroller.establecerTareasPorProyecto(proyectoid);

        // Para cada tarea en el controlador, obtenermos su lista de [PresupuestoTarea]
        for (Tarea t : tareacontroller.getTareasdeproyecto()) {
            for (PresupuestoTarea p : t.getPresupuestoTareaList()) {
                resultado.add(p);
                // Se filtra que no sea de los rubros recursos humanos y servicios de terceros [POR AHORA]
            /*   if((tiposolicitud.equals("Anticipo")) | (tiposolicitud.equals("Adquisición"))){
                 if (!esRubroPorId(p, 4) && !esRubroPorId(p, 5)) {
                 resultado.add(p);
                 }
                 }
                 if(tiposolicitud.equals("Certificación")){
                 if (esRubroPorId(p, 4) && esRubroPorId(p, 5)) {
                 resultado.add(p);
                 }
                 }
                 */
            }
        }

        // La lista de presupuesto tareas del mb
        presupuestostareas = resultado;

        // La lista dual de presupuesto tareas para el pick list
        //this.setPlPresupuestoTarea(new DualListModel(presupuestostareas, new ArrayList<PresupuestoTarea>()));
//        // Vaciar lista de presupuestos items a solicitar
//        this.presupuestostareasitems = new ArrayList<PresupuestoTarea>();
    }

    /**
     * vacia la lista [presupuestostareasitems] que es la que mantiene los
     * presupuestostarea solicitados
     */
    public void vaciarListaPresupuestoTareaSolicitadosPorProyecto() {
        presupuestostareasitems = new ArrayList<PresupuestoTarea>();
    }

    /**
     * Condicion que devuelve verdadero si el presupuesto tarea es de un rubro
     * determinado, dado por la id del rubro
     *
     * @param p
     * @param rubroId
     * @return
     */
    public boolean esRubroPorId(PresupuestoTarea p, int rubroId) {
        if (p.getRubro().getId() == rubroId) {
            return true;
        } else {
            return false;
        }
    }

//    public void onPLTransfer(TransferEvent event) {
////        StringBuilder builder = new StringBuilder();
////        
////        for (Object item : event.getItems()) {
////            builder.append(((PresupuestoTarea) item).getDescripcion()).append("<br />");
////        }
//        
//        FacesContext context = FacesContext.getCurrentInstance();
//        DialogosController dialogoscontroller = (DialogosController) context.getApplication().evaluateExpressionGet(context, "#{dialogosController}", DialogosController.class);
//        
//        System.out.println("onTransfer pick list");
//        
//        //dialogoscontroller.dialogoSolicitudItem();
//        dialogoscontroller.dialogo();
//
////        FacesMessage msg = new FacesMessage();
////        msg.setSeverity(FacesMessage.SEVERITY_INFO);
////        msg.setSummary("Items Transferred");
////        msg.setDetail(builder.toString());
////
////        FacesContext.getCurrentInstance().addMessage(null, msg);
//        
//        System.out.println("onTransfer pick list fin");
//    }
//
//    public void onPLSelect(SelectEvent event) {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected", event.getObject().toString()));
//    }
//
//    public void onPLUnselect(UnselectEvent event) {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Unselected", event.getObject().toString()));
//    }
    /**
     * Agrega el item seleccionado de la lista [presupuestostareas] en la lista
     * [presupuestostareas]
     */
    public void agregarItemSolcitado() {
        System.out.println("solicitarItem");

        // Agregar en la lista de solicitados
        this.presupuestostareasitems.add(current);

        // Quitar de la lista de disponibles
        this.presupuestostareas.remove(current);

        for (PresupuestoTarea p : presupuestostareas) {
            System.out.println("PresupuestoTarea DISPONIBLE: " + p.getDescripcion());
        }

        for (PresupuestoTarea p : presupuestostareasitems) {
            System.out.println("PresupuestoTarea SOLICITADO: " + p.getDescripcion());
        }

        System.out.println("solicitarItem FIN");
    }

    public void quitarItemSolicitado(PresupuestoTarea item) {

        // Quita de la lista de solicitados
        this.presupuestostareasitems.remove(item);

        // Devuelve el item a la lista de disponibles
        this.presupuestostareas.add(item);
    }


    public BigDecimal sumaraportesUniversidadEnListaPresupuestoTareaItems() {
        BigDecimal aportesUniversidad = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                aportesUniversidad = aportesUniversidad.add(ptn.getAporteuniversidad());
            }
        }
        return aportesUniversidad;
    }

    public BigDecimal sumaraportesOrganismoEnListaPresupuestoTareaItems() {
        BigDecimal aportesOrganismo = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                aportesOrganismo = aportesOrganismo.add(ptn.getAporteorganismo());
            }
        }
        return aportesOrganismo;
    }

    public BigDecimal sumaraportesComitenteEnListaPresupuestoTareaItems() {
        BigDecimal aportesComitente = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                aportesComitente = aportesComitente.add(ptn.getAportecomitente());
            }
        }
        return aportesComitente;
    }

    public BigDecimal sumaraportesTotalesEnListaPresupuestoTareaItems() {
        BigDecimal aportesTotatles = BigDecimal.ZERO;
        List<PresupuestoTarea> pt = new ArrayList<PresupuestoTarea>();
        if (this.getPresupuestostareasitems() != null) {
            for (PresupuestoTarea ptn : this.getPresupuestostareasitems()) {
                aportesTotatles = aportesTotatles.add(ptn.getAportecomitente().add(ptn.getAporteorganismo().add(ptn.getAporteuniversidad())));
            }
        }
        return aportesTotatles;
    }





}
