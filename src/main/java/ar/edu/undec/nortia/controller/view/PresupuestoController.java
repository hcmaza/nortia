package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.model.Presupuesto;
import ar.edu.undec.nortia.controller.view.util.JsfUtil;
import ar.edu.undec.nortia.controller.view.util.PaginationHelper;
import ar.edu.undec.nortia.controller.PresupuestoFacade;
import ar.edu.undec.nortia.model.PresupuestoRubro;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name = "presupuestoController")
@SessionScoped
public class PresupuestoController implements Serializable {

    private Presupuesto current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.PresupuestoFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.PresupuestoRubroFacade ejbFacadeR;

    private PaginationHelper pagination;
    private int selectedItemIndex;
    private BigDecimal sumagastoorganismo = BigDecimal.ZERO;
    private BigDecimal sumagastocomitente = BigDecimal.ZERO;
    private BigDecimal sumagastouniversidad = BigDecimal.ZERO;
    private BigDecimal sumatotal = BigDecimal.ZERO;
    private PieChartModel pieModelAportes = new PieChartModel();
    private PieChartModel pieModelRubro = new PieChartModel();
    private List<PresupuestoRubro> presupuestosrubrosedilist = null;

    public PresupuestoController() {
    }

    public Presupuesto getSelected() {
        if (current == null) {
            current = new Presupuesto();
            selectedItemIndex = -1;
        }

        return current;
    }

    private PresupuestoFacade getFacade() {
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
        current = (Presupuesto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Presupuesto();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Presupuesto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Presupuesto) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoDeleted"));
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

    public void soloCrear() {
        getFacade().create(current);
    }

    @FacesConverter(forClass = Presupuesto.class)
    public static class PresupuestoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PresupuestoController controller = (PresupuestoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "presupuestoController");
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
            if (object instanceof Presupuesto) {
                Presupuesto o = (Presupuesto) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Presupuesto.class.getName());
            }
        }

    }

    public void findProyecto(int id) {

        System.out.println("PresupuestoController: findProyecto" + id);

        current = getFacade().findporProyecto(id);

    }

    public void generarPresupuestoPorProyecto(int proyectoId) {
        
        current = getFacade().findporProyecto(proyectoId);
        
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoRubroController presupuestorubrocontroller = (PresupuestoRubroController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoRubroController}", PresupuestoRubroController.class);

        presupuestorubrocontroller.buscarPorPresupuesto(current.getId());
        presupuestorubrocontroller.sumarGastos(null);
    }

    public void sumarGastosView() {
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoRubroController presupuestorubrocontroller = (PresupuestoRubroController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoRubroController}", PresupuestoRubroController.class);
        // if(presupuestorubrocontroller.getPresupuestosrubros()==null){
        presupuestorubrocontroller.findporPresupuestoEdit(current.getId());
        // }
        presupuestorubrocontroller.sumarGastos(null);
//                    Iterator it=this.getSelected().getPresupuestoRubroList().iterator();
//                 BigDecimal totalcomitente=BigDecimal.ZERO;
//                BigDecimal totaluniversidad=BigDecimal.ZERO;
//                BigDecimal totalorganismo=BigDecimal.ZERO;
//                 sumagastoorganismo=BigDecimal.ZERO;
//                  sumagastocomitente=BigDecimal.ZERO;
//                  sumagastouniversidad=BigDecimal.ZERO;
//                    int contador=-1;
//                 while(it.hasNext()){
//                     contador++;
//                     PresupuestoRubro pr=(PresupuestoRubro)it.next();
//                    totalcomitente=totalcomitente.add(new BigDecimal(pr.getGastocomitente().setScale(2).toString()));
//                   totaluniversidad=totaluniversidad.add(new BigDecimal(pr.getGastouniversidad().setScale(2).toString()));
//                    totalorganismo=totalorganismo.add(new BigDecimal(pr.getGastoorganismo().setScale(2).toString()));
//                  // sumagastoentidad=new BigDecimal(totalentidad).setScale(2);
//                  // sumagastouniversidad=new BigDecimal(totaluniversidad).setScale(2);
//                 
//                   // pr.setTotal(pr.getGastoorganismo().add(pr.getGastocomitente()).add(pr.getGastouniversidad()));
//                 
//                   // this.presupuestosrubros.get(contador).setTotal(pr.getTotal());
//                 }
//                 sumagastocomitente=totalcomitente;
//                 sumagastouniversidad=totaluniversidad;
//                 sumagastoorganismo=totalorganismo;
//                 sumatotal=sumagastoorganismo.add(sumagastouniversidad).add(sumagastocomitente);
//                 
//                  pieModelAportes = new PieChartModel();  
//  
//                pieModelAportes.set("Aporte Organismo", sumagastoorganismo);  
//                pieModelAportes.set("Aporte Comitente", sumagastocomitente);  
//                pieModelAportes.set("Aporte Universidad", sumagastouniversidad);  
//                //pieModelAportes.setDiameter(150);
//                pieModelAportes.setLegendPosition("e");
//                pieModelAportes.setTitle("Aportes");
//                pieModelAportes.setSliceMargin(5);
//                pieModelAportes.setShowDataLabels(true);
//                  pieModelAportes = new PieChartModel(); 
//                   pieModelRubro = new PieChartModel();  
//            for(PresupuestoRubro pre : getSelected().getPresupuestoRubroList()){
//                pieModelRubro.set(pre.getRubro().getRubro(), pre.getTotal());
//                
//            }
//              //pieModelRubro.setDiameter(150);
//                pieModelRubro.setLegendPosition("e");
//                pieModelRubro.setTitle("Rubros");
//                pieModelRubro.setSliceMargin(5); 
//                pieModelRubro.setShowDataLabels(true);
//                
//                // RequestContext.getCurrentInstance().update(s.getClientId(FacesContext.getCurrentInstance()) +  ":" + event.getRowIndex() +  ":isAutomatic");
//                  //  RequestContext.getCurrentInstance().update(":tpresupuesto:" + event.getRowIndex() +  ":total");
//  //               System.out.println("gggggggggggggggggggggggggggggg");
////                  UIData table = (UIData) event.getComponent();
////                    String updateClientId = table.getClientId() + ":" + table.getRowIndex() + ":total";
////                    FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(updateClientId);

    }

    public void sumarGastosEdit() {

//                 BigDecimal totalcomitente=BigDecimal.ZERO;
//                BigDecimal totaluniversidad=BigDecimal.ZERO;
//                BigDecimal totalorganismo=BigDecimal.ZERO;
//                 sumagastoorganismo=BigDecimal.ZERO;
//                  sumagastocomitente=BigDecimal.ZERO;
//                  sumagastouniversidad=BigDecimal.ZERO;
//                    int contador=-1;
//                    if(presupuestosrubrosedilist!=null){
//                    Iterator it=this.presupuestosrubrosedilist.iterator();
//                    
//                 while(it.hasNext()){
//                     contador++;
//                     PresupuestoRubro pr=(PresupuestoRubro)it.next();
//                    totalcomitente=totalcomitente.add(new BigDecimal(pr.getGastocomitente().setScale(2).toString()));
//                   totaluniversidad=totaluniversidad.add(new BigDecimal(pr.getGastouniversidad().setScale(2).toString()));
//                    totalorganismo=totalorganismo.add(new BigDecimal(pr.getGastoorganismo().setScale(2).toString()));
//                  
//                   pr.setTotal(pr.getGastoorganismo().add(pr.getGastocomitente()).add(pr.getGastouniversidad()));
//                  
//                   this.presupuestosrubrosedilist.get(contador).setTotal(pr.getTotal());
//                 }
//                    }
//                 sumagastocomitente=totalcomitente;
//                 sumagastouniversidad=totaluniversidad;
//                 sumagastoorganismo=totalorganismo;
//                 sumatotal=sumagastoorganismo.add(sumagastouniversidad).add(sumagastocomitente);
//                 
//                   pieModelAportes=new PieChartModel();
//                    pieModelRubro=new PieChartModel();
//                    pieModelAportes.set("Aporte Organismo", sumagastoorganismo);  
//                    pieModelAportes.set("Aporte Comitente", sumagastocomitente);  
//                    pieModelAportes.set("Aporte Universidad", sumagastouniversidad);  
//                
//                   
//            for(PresupuestoRubro pre : this.getPresupuestosrubrosedilist()){
//                pieModelRubro.set(pre.getRubro().getRubro(), pre.getTotal());
//                
//            }
//                
//              
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoRubroController presupuestorubrocontroller = (PresupuestoRubroController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoRubroController}", PresupuestoRubroController.class);
        // if(presupuestorubrocontroller.getPresupuestosrubros()==null){
        presupuestorubrocontroller.findporPresupuestoEdit(current.getId());
        // }
        presupuestorubrocontroller.sumarGastos(null);
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

    public PieChartModel getPieModelAportes() {
        return pieModelAportes;
    }

    public void setPieModel(PieChartModel pieModelAportes) {
        this.pieModelAportes = pieModelAportes;
    }

    public PieChartModel getPieModelRubro() {
        return pieModelRubro;
    }

    public void setPieModelRubro(PieChartModel pieModelRubro) {
        this.pieModelRubro = pieModelRubro;
    }

    public List<PresupuestoRubro> getPresupuestosrubrosedilist() {
        if (presupuestosrubrosedilist == null) {
            presupuestosrubrosedilist = this.getSelected().getPresupuestoRubroList();
        }
        return presupuestosrubrosedilist;
    }

    public void setPresupuestosrubrosedilist(List<PresupuestoRubro> presupuestosrubrosedilist) {
        this.presupuestosrubrosedilist = presupuestosrubrosedilist;
    }

    // Paneles de control y Listados
    public BigDecimal getPresupuestoTotalProyecto(int idProyecto) {
        if (current == null) {
            return BigDecimal.ZERO;
        } else {
            findProyecto(idProyecto);
            sumarGastosView();

            System.out.println("Total " + this.getSumatotal().toString());

            return this.getSumatotal();
        }
    }

    public BigDecimal obtenerTotal(int idProyecto) {

        this.findProyecto(idProyecto);

        Iterator it = this.getSelected().getPresupuestoRubroList().iterator();

        BigDecimal totalcomitente = BigDecimal.ZERO;
        BigDecimal totaluniversidad = BigDecimal.ZERO;
        BigDecimal totalorganismo = BigDecimal.ZERO;
        sumagastoorganismo = BigDecimal.ZERO;
        sumagastocomitente = BigDecimal.ZERO;
        sumagastouniversidad = BigDecimal.ZERO;

        while (it.hasNext()) {
            PresupuestoRubro pr = (PresupuestoRubro) it.next();
            totalcomitente = totalcomitente.add(new BigDecimal(pr.getGastocomitente().setScale(2).toString()));
            totaluniversidad = totaluniversidad.add(new BigDecimal(pr.getGastouniversidad().setScale(2).toString()));
            totalorganismo = totalorganismo.add(new BigDecimal(pr.getGastoorganismo().setScale(2).toString()));
        }
        sumagastocomitente = totalcomitente;
        sumagastouniversidad = totaluniversidad;
        sumagastoorganismo = totalorganismo;
        sumatotal = sumagastocomitente.add(sumagastouniversidad).add(sumagastoorganismo);

        System.out.println("PresupuestoController >> obtenerTotal(" + current.getProyectoid().getNombre() + "): " + current.getId().toString() + " TOTAL " + sumatotal.toString());

        return sumatotal;
    }

}
