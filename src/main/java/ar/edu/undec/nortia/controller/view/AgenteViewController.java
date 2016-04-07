/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.AgenteFacade;
import ar.edu.undec.nortia.controller.EncriptarSHA256;
import ar.edu.undec.nortia.controller.UsuarioFacade;
import ar.edu.undec.nortia.controller.view.util.ConnectJDBCPostgresql;
import ar.edu.undec.nortia.model.Agente;
import ar.edu.undec.nortia.model.Agentecargo;
import ar.edu.undec.nortia.model.ProyectoAgente;
import ar.edu.undec.nortia.model.Tipodocumento;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

/**
 *
 * @author Hugo
 */
@ManagedBean
@ViewScoped
public class AgenteViewController implements Serializable {
    private Agente current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.nortia.controller.AgenteFacade ejbFacade;
    @EJB
    private ar.edu.undec.nortia.controller.AgentecargoFacade ejbFacadeac;
    @EJB
    private ar.edu.undec.nortia.controller.UsuarioFacade ejbFacadeu;
    @EJB
    private ar.edu.undec.nortia.controller.ProyectoAgenteFacade ejbPAFacade;
    @EJB
    private ar.edu.undec.nortia.controller.ConfiguracionFacade ejbFacadec;
    private ar.edu.undec.nortia.controller.view.util.PaginationHelper pagination;
    private int selectedItemIndex;
    
    private List<Agente> agentes = new ArrayList<Agente>() ;
    private List<Agente> collectoragentes = new ArrayList<Agente>() ;
    private Agente agente1;
    private boolean filtroapellidonombre=false;
    private boolean filtrodocumento=false;
    private SelectItem[] itemsAgentes;
    
    
    /**
     * Creates a new instance of AgenteViewController
     */
    public AgenteViewController() {
    }
    
     public Agente getSelected() {
        if (current == null) {
            current = new Agente();
            selectedItemIndex = -1;
        }
        return current;
    }

    private AgenteFacade getFacade() {
        return ejbFacade;
    }
    
    private UsuarioFacade getFacadeu() {
        return ejbFacadeu;
    }

    public ar.edu.undec.nortia.controller.view.util.PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new ar.edu.undec.nortia.controller.view.util.PaginationHelper(10000000) {

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
        current = (Agente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Agente();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AgenteCreated"));
            return prepareCreate();
        } catch (Exception e) {
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Agente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AgenteUpdated"));
            return "View";
        } catch (Exception e) {
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    public String solograbar() {
        try {
            getFacade().edit(current);
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AgenteUpdated"));
          return null;  
        } catch (Exception e) {
            //JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           System.out.println(e); 
           return null;
        }
    }

    public String destroy() {
        current = (Agente) getItems().getRowData();
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
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AgenteDeleted"));
        } catch (Exception e) {
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
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
        return ar.edu.undec.nortia.controller.view.util.JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return ar.edu.undec.nortia.controller.view.util.JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }
    
    public void findAgente(String nombreusuario){
        if(current==null){
             current= (Agente) getFacade().findAgente(nombreusuario);
            System.out.println("9999999999999999999999999999999999999999999999999"+current.getId());
        }
    }

    @FacesConverter(forClass = Agente.class)
    public static class AgenteViewControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AgenteViewController controller = (AgenteViewController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "agenteViewController");
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
            if (object instanceof Agente) {
                Agente o = (Agente) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Agente.class.getName());
            }
        }

    }
    
    public void editarMiCuenta(){
        try {
            current.getUsuarioid().setUsuarioclave(new EncriptarSHA256().hash256(current.getUsuarioid().getUsuarioclave().trim()) );
            getFacade().edit(current);
            if(current.getUsuarioid().getUsuarioclave().length()>5){
                getFacadeu().edit(current.getUsuarioid());
            }
            
            // FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "What we do in life", "Echoes in eternity.");  
               
        } catch (Exception e) {
            ar.edu.undec.nortia.controller.view.util.JsfUtil.addErrorMessage(e, "No se pudo Modificar su cuenta!");
            
        }
    }
    
    public void filtroDocumentooApellido(){
//        if(nrodocoapellido.length()>2)
//            System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"+nrodocoapellido);
//        this.setItemsAgentes(ar.edu.undec.nortia.controller.view.util.JsfUtil.getSelectItems(ejbFacade.filtroDocumentooApellido(nrodocoapellido), false));
    }

    
    public List<Agente> getAgentes() {
        return agentes;
    }

    public void setAgentes(List<Agente> agentes) {
        this.agentes = agentes;
    }

    public List<Agente> getCollectoragentes() {
        return collectoragentes;
    }

    public void setCollectoragentes(List<Agente> collectoragentes) {
        this.collectoragentes = collectoragentes;
    }
    
    public void agregarEquipo(){
        boolean inserto =false;
        
        for(Agente a:collectoragentes){
            if((current.getId().equals(a.getId())) | (current.getApellido().isEmpty()) ){
                inserto=true;
            }
            
            
        }
        if(!inserto){
            collectoragentes.add(current);
            
        }
    }
    
    public void removerAgenteCollector(){
        this.collectoragentes.remove(agente1);
    }
   
    public void setSelected(Agente agente){
        current = agente;
    }
    
    public String reinit() {
        current = new Agente();
         
        return null;
    }

    public Agente getAgente1() {
        if(agente1 == null){
         agente1 = new Agente();
        }
        return agente1;
    }
    
    public void setAgente1(Agente agente1) {
        this.agente1 = agente1;
    }
    
    public void registrar(){
        
        System.out.println("agenteViewController -  registrar");
        
        if((this.ejbFacade.agentedocumento(agente1.getNumerodocumento())==null) || (ejbFacade.filtroDocumentooCuil(agente1.getCuil())==null)  ){
             
            System.out.println("agenteViewController -  registrar - if");
            
            ejbFacade.createWithPersist(agente1);
            
            System.out.println("agenteViewController -  registrar - if - persistido");
           
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "La registracion fue Satisfactoria")); 
        }else{
           
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya Existe Una Persona con Ese Numero de Documento/CUIL")); 
        }
    }

    public boolean isFiltroapellidonombre() {
        return filtroapellidonombre;
    }

    public void setFiltroapellidonombre(boolean filtroapellidonombre) {
        this.filtroapellidonombre = filtroapellidonombre;
    }

    public boolean isFiltrodocumento() {
        return filtrodocumento;
    }

    public void setFiltrodocumento(boolean filtrodocumento) {
        this.filtrodocumento = filtrodocumento;
    }
    
    public SelectItem[] getItemsAgentes() {
        return itemsAgentes;
    }
    
    public void setItemsAgentes(SelectItem[] i) {
        itemsAgentes=i;
    }
    
   
    //importo datos del mapuche mediante JDBC
    public void importardatos(){
        
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DATE);
          
        ConnectJDBCPostgresql connectjdbcpostgresql = new ConnectJDBCPostgresql();
        try {
            connectjdbcpostgresql.connect(ejbFacadec.findAtributo("mapuchehost").getValor(), ejbFacadec.findAtributo("mapucheport").getValor(), ejbFacadec.findAtributo("mapuchedatabase").getValor(), ejbFacadec.findAtributo("mapucheuser").getValor(), ejbFacadec.findAtributo("mapuchepassword").getValor());
        } catch (SQLException ex) {
            Logger.getLogger(AgenteViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            PreparedStatement ps= connectjdbcpostgresql.getConn().prepareStatement(" select dh01.nro_legaj, desc_appat,desc_apmat,desc_nombr,tipo_docum,nro_docum,nro_cuil1, nro_cuil, nro_cuil2,tipo_sexo,fec_nacim, sum(cant_horas) as cant_horas, max(cant_horas) as horasmayordedicacion " +
                    "	from mapuche.dh03 \n" +
                    "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj)\n" +
                    "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ)\n" +
                    "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic)\n" +
                    "	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO' group by dh01.nro_legaj ");
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
               Agente ai = new Agente();
               
           ai = ejbFacade.filtroDocumentooCuil(rs.getString("nro_docum"));
            if(ai == null){
                ai = new Agente();
                 ai.setLegajo(rs.getInt("nro_legaj"));
                ai.setApellido(rs.getString("desc_appat"));
                ai.setNombres(rs.getString("desc_nombr"));
                ai.setNumerodocumento(rs.getString("nro_docum"));
                ai.setCuil(rs.getString("nro_cuil1")+"-"+rs.getString("nro_cuil")+"-"+rs.getString("nro_cuil2"));
                ai.setTipodocumentoid(new Tipodocumento(1));
                ai.setHoraslaborales(rs.getInt("cant_horas"));
                ai.setHorasmayordedicacion(rs.getInt("horasmayordedicacion"));
                this.ejbFacade.create(ai);
                
            
            }else{
                 ai.setHoraslaborales(rs.getInt("cant_horas"));
                ai.setHorasmayordedicacion(rs.getInt("horasmayordedicacion"));
                this.ejbFacade.edit(ai);
            }
           }
        
        } catch (SQLException ex) {
            Logger.getLogger(AgenteViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        for(DatosMapuche dm :this.findAllUNDEC()){
//            Agente ai =new Agente();
//            ai.setLegajo(dm.getNro_legaj());
//            ai.setApellido(dm.getDesc_appat());
//            ai.setNombres(dm.getDesc_nombr());
//            ai.setNumerodocumento(""+dm.getNro_docum());
//            ai.setCuil(dm.getNro_cuil1()+"-"+dm.getNro_cuil()+"-"+dm.getNro_cuil2());
//            ai.setTipodocumentoid(new Tipodocumento(1));
//            if(ejbFacade.filtroDocumentooCuil(ai.getNumerodocumento()).isEmpty()){
//                this.ejbFacade.create(ai);
//            
//            }
//        }
        // Importo Cargos
        try {
            PreparedStatement ps= connectjdbcpostgresql.getConn().prepareStatement(" select nro_cargo, cant_horas, dh01.nro_legaj, codc_uacad, impp_basic" +
                    "	from mapuche.dh03 \n" +
                    "left join mapuche.dh01 on (dh01.nro_legaj=dh03.nro_legaj)\n" +
                    "left join mapuche.dh11 on (dh03.codc_categ=dh11.codc_categ)\n" +
                    "left join mapuche.dh31 on (dh11.codc_dedic=dh31.codc_dedic)\n" +
                    "	where dh03.vig_caano > " + (año-1) +" and (fec_baja > '"+año+"-"+mes+"-"+dia+"' or fec_baja is NULL) and dh11.codc_dedic != 'NODO'  ");
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
               Agentecargo aci = new Agentecargo();
               
           aci = ejbFacadeac.filtrolegajo(rs.getInt("nro_legaj"));
            if(aci == null){
                aci = new Agentecargo();
                 aci.setNroLegajo(rs.getInt("nro_legaj"));
                aci.setCant_horas(rs.getInt("cant_horas"));
                aci.setNroCargo(rs.getInt("nro_cargo"));
                aci.setCodcUacad(rs.getString("codc_uacad"));
               aci.setImppBasic(rs.getBigDecimal("impp_basic"));
                this.ejbFacadeac.create(aci);
                
            
            }else{
                 aci.setCant_horas(rs.getInt("cant_horas"));
                aci.setImppBasic(rs.getBigDecimal("impp_basic"));
                this.ejbFacadeac.edit(aci);
            }
           }
        
        } catch (SQLException ex) {
            Logger.getLogger(AgenteViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    public SelectItem[] getItemsAvailableSelectOneProyectoAgente() {
         FacesContext context = FacesContext.getCurrentInstance();    
         ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
         List<Agente> as = new ArrayList<Agente>();
         for(ProyectoAgente pa:proyectoagentecontroller.getEquipotrabajo()){
             as.add(pa.getAgente());
         }
        return ar.edu.undec.nortia.controller.view.util.JsfUtil.getSelectItems(as, true);
    }
    
    // CREAR USUARIO
    // Rellenar combo de Agentes sin Usuario en Usuarios/Create
    public SelectItem[] getItemsAvailableSelectOneAgentesSinUsuario() {

        System.out.println("Agentes sin Usuario");

        for (Agente a : this.ejbFacade.agentesSinUsuario()) {
            System.out.println(a.getApellido() + ", " + a.getNombres());
        }

        System.out.println("-------------");

        return ar.edu.undec.nortia.controller.view.util.JsfUtil.getSelectItems(ejbFacade.agentesSinUsuario(), true);
    }

    public ProyectoAgente buscarProyectoAgentePorAgenteId(int agenteId, int proyectoid){
        try{
            return ejbPAFacade.buscarPorAgenteYProyecto(agenteId,proyectoid);
        }catch (Exception e){
            System.out.println("No se pudo obtener el ProyectoAgente");
            e.printStackTrace();
            return null;
        }
    }
    
}
