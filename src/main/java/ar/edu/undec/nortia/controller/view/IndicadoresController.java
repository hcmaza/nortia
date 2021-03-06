/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.nortia.controller.view;

import ar.edu.undec.nortia.controller.*;
import ar.edu.undec.nortia.model.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.sun.mail.imap.protocol.Item;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author mpaez
 */
@ManagedBean(name = "indicadoresController")
@SessionScoped
public class IndicadoresController implements Serializable {

    @EJB
    private ProyectoFacade proyectoFacade;

    @EJB
    private SolicitudFacade solicitudFacade;

    @EJB
    private DesembolsoFacade desembolsoFacade;

    @EJB
    private PresupuestoTareaFacade presupuestoTareaFacade;

    @EJB
    private RubroFacade rubroFacade;

    @EJB
    private TareaFacade tareaFacade;

    @EJB
    private EtapaFacade etapaFacade;

    public TareaFacade getTareaFacade() { return tareaFacade; }
    public EtapaFacade getEtapaFacade() { return etapaFacade; }

    private List<Solicitud> listaSolicitudes;
    private List<PresupuestoTarea> listaPresupuestosTarea;
    private HashMap<String, Float> listaSaldosRubro;

    private LinkedHashMap<Rubro, Float> listaSaldosDeRubro;
    public LinkedHashMap<Rubro, Float> getListaSaldosDeRubro() { return listaSaldosDeRubro; }

    private List<String> columnasListaSaldosRubro;
    private List<Float> valoresListaSaldosRubro;

    // Total presupuestado proyecto
    private float totalPresupuestoProyecto = 0.0f;

    // grafico de ejecutado del proyecto
    private MeterGaugeChartModel indicadorEjecutado;

    // Ejecutado por rubro
    private List<ItemRubro> listaEjecutadoRubro;
    private PieChartModel chartEjecutadoPorRubro;

    // Ejecutado por Fecha
    private LineChartModel chartEjecutadoPorFecha;
    //private List<ItemRubro> listaEjecutadoFecha;
    //private PieChartModel chartEjecutadoPorRubro;

    // Saldo del proyecto
    private float saldoProyecto = 0.0f;
    
    // Monto ejecutado del proyecto
    private float ejecutadoProyecto = 0.0f;

    // Monto rendido por proyecto
    private float rendidoProyecto = 0.0f;

    // Monto pendiente de rendicion por proyecto
    private float pendienteRendicionProyecto;

    // Porcentaje Ejecutado
    private float porcentajeEjecutado = 0.0f;

    // Porcentaje de saldo disponible sobre ultimo desembolso
    private int porcentajeSaldoSobreUltimoDesembolso = 0;

    private float desembolsadoProyecto = 0.0f;

    private float totalEjecutadoEnProyecto = 0.0f;


    //    // total desembolsado
//    private float totalDesembolsado;

    public SolicitudFacade getSolicitudFacade() {
        return solicitudFacade;
    }

    private Map<String,Integer> mapaAvancesEtapasYTareas = new HashMap<String, Integer>();
    public Map<String, Integer> getMapaAvancesEtapasYTareas() { return mapaAvancesEtapasYTareas; }

    public DesembolsoFacade getDesembolsoFacade() {
        return desembolsoFacade;
    }

    public PresupuestoTareaFacade getPresupuestoTareaFacade() {
        return presupuestoTareaFacade;
    }

    public RubroFacade getRubroFacade() {
        return rubroFacade;
    }

    public List<Solicitud> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public List<PresupuestoTarea> getListaPresupuestosTarea() {
        return listaPresupuestosTarea;
    }

    public void setListaPresupuestosTarea(List<PresupuestoTarea> listaPresupuestosTarea) {
        this.listaPresupuestosTarea = listaPresupuestosTarea;
    }

    public List<String> getColumnasListaSaldosRubro() {
        return columnasListaSaldosRubro;
    }

    public List<Float> getValoresListaSaldosRubro() {
        return valoresListaSaldosRubro;
    }

    public HashMap<String, Float> getListaSaldosRubro() {
        return listaSaldosRubro;
    }

    public void setListaSaldosRubro(HashMap<String, Float> listaSaldosRubro) {
        this.listaSaldosRubro = listaSaldosRubro;
    }

    public List<ItemRubro> getListaEjecutadoRubro() {
        return listaEjecutadoRubro;
    }

    public List<ItemRubro> getListaEjecutadoRubroOrdenadoPorMontoDesc(){
        //ordenar la lista de rubros por monto descendente
        Collections.sort(listaEjecutadoRubro, new Comparator<ItemRubro>() {
            public int compare(ItemRubro ir1, ItemRubro ir2) {
                if(ir1.getMonto() < ir2.getMonto()){
                    return 1;
                } else{
                    return 0;
                }

            }
        });

        return listaEjecutadoRubro;
    }

    public PieChartModel getChartEjecutadoPorRubro() {
        return chartEjecutadoPorRubro;
    }

    public LineChartModel getChartEjecutadoPorFecha() {
        return chartEjecutadoPorFecha;
    }

    public float getSaldoProyecto() {
        return saldoProyecto;
    }

    public float getEjecutadoProyecto() {
        return ejecutadoProyecto;
    }
    public void setEjecutadoProyecto(float ejecutado) {
        this.ejecutadoProyecto=ejecutado;
    }
    public float getTotalPresupuestoProyecto() {
        return totalPresupuestoProyecto;
    }
    public float getRendidoProyecto() {
        return rendidoProyecto;
    }
    public float getPendienteRendicionProyecto() {
        return pendienteRendicionProyecto;
    }
    public float getPorcentajeEjecutado() {
        return porcentajeEjecutado;
    }
    public void setPorcentajeEjecutado(float porcentajeEjecutado) {
        this.porcentajeEjecutado = porcentajeEjecutado;
    }
    public String getPorcentajeEjecutadoString(){
        return String.format("%.02f", porcentajeEjecutado);
    }
    public int getPorcentajeEjecutadoEntero(){
        //return String.format("%.0f", porcentajeEjecutado);
        return (int) porcentajeEjecutado;
    }
    public ProyectoFacade getProyectoFacade() { return proyectoFacade; }
    public int getPorcentajeSaldoSobreUltimoDesembolso() {return porcentajeSaldoSobreUltimoDesembolso;}
    public float getDesembolsadoProyecto() { return desembolsadoProyecto; }
    public float getTotalEjecutadoEnProyecto() { return totalEjecutadoEnProyecto; }

    /**
     * Creates a new instance of IndicadoresController
     */
    public IndicadoresController() {

    }

    /**
     * Método que ejecuta los calculos de obtencion de saldos y ejecucion por
     * rubro
     *
     */
    public void obtenerCalculos() {

        calcularDesembolsadoPorProyecto();

        calcularTotalesPorProyecto();

        // calcula ejecutado por rubro y
        // genera el grafico de dona con los porcentajes para cada rubro
        calcularSaldosPorRubro();

        // calcula ejecutado por rubro y
        // genera el grafico de barra horizonal stack
        calcularEjecutadoPorRubro();

        crearIndicadorEjecutado();

        generarChartEjecutadoPorRubro();
        
        generarChartEjecutadoPorFecha();

        calcularSaldoProyecto();

        calcularAvancesEtapas();

        // genera el grafico de linea,
        // con la evolucion de desembolsos y ejecuciones
        crearModeloLinealEvolucionDesembolsosEjecuciones();

    }

    public void calcularDesembolsadoPorProyecto(){
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        DesembolsoController desembolsoController = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}",DesembolsoController.class);

        desembolsadoProyecto = desembolsoController.sumarDesembolsosPorProyecto(proyectocontroller.getSelected().getId());

    }

    public void calcularSaldosPorRubro() {

        // Saldo del proyecto
        saldoProyecto = 0.0f;

        // Saldos por Rubro
        LinkedHashMap<Rubro, Float> saldos = new LinkedHashMap<Rubro, Float>();

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de presupuestos tarea por proyecto
        listaPresupuestosTarea = getPresupuestoTareaFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Llenamos la lista de solicitudes anteriores
        listaSolicitudes = getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        listaSolicitudes.addAll(getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));

        // Lista de solicitudes disponibles
        List<Solicitud> listaSolicitudesDisponibles = new ArrayList<Solicitud>();

        // Lista de rubros
        List<Rubro> listaRubros = getRubroFacade().findAll();

        //ordenar la lista de rubros
        Collections.sort(listaRubros, new Comparator<Rubro>() {
            public int compare(Rubro r1, Rubro r2) {
                if (r1.getOrden() == null || r2.getOrden() == null) {
                    return 0;
                }
                return r1.getOrden().compareTo(r2.getOrden());
            }
        });

        // Llenamos el hashmap de saldos y la lista de ejecucion
        for (Rubro r : listaRubros) {
            saldos.put(r, 0.0f);
        }

        // Obtenemos los importes de solicitud disponibles
        for (PresupuestoTarea p : listaPresupuestosTarea) {

            Solicitud solicitud = new Solicitud();
            solicitud.setPresupuestotarea(p);
            solicitud.setImporte(p.getTotal());
            solicitud.setDisponible(solicitud.getImporte());

            // buscamos si el presupuestotarea ya fue solicitado anteriormente y aprobado o ejecutado, de ser asi, restamos el importe o lo removemos
            Iterator i = listaSolicitudes.iterator();

            while (i.hasNext()) {
                Solicitud solicitudAprobadaOEnEjecucion = (Solicitud) i.next();

                // si encontramos el presupuestotarea en una solicitud anterior y siendo una solicitud aprobada
                if (p.getId() == solicitudAprobadaOEnEjecucion.getPresupuestotarea().getId()) {
                    // restamos al importe de la solicitud disponible, el importe de la solicitud anterior
                    //solicitud.setImporte(p.getTotal().subtract(solicitudAprobadaOEnEjecucion.getImporte()));
                    solicitud.setImporte(solicitud.getImporte().subtract(solicitudAprobadaOEnEjecucion.getImporte()));
                    solicitud.setDisponible(solicitud.getImporte());
                }
            }

            // Agregamos a la lista de solicitudes disponibles si el importe es distinto de cero y sumamos al saldo
            if (solicitud.getImporte().floatValue() != 0.00) {
                listaSolicitudesDisponibles.add(solicitud);

                // Acumulamos en la lista de saldos
                saldos.put(solicitud.getPresupuestotarea().getRubro(), saldos.get(solicitud.getPresupuestotarea().getRubro()) + solicitud.getDisponible().floatValue());

                

            }
        }

        // Saldos por Rubro
        listaSaldosDeRubro = saldos;

        System.out.println("listaSaldosDeRubro **********");
        for (Map.Entry<Rubro,Float> e : listaSaldosDeRubro.entrySet()){
            System.out.println("Rubro >> " +e.getKey().getRubro() + " - Saldo >> " +e.getValue() );
        }

        // genera el grafico de dona
        crearModeloDonaDisponiblePorRubro();
        
//        columnasListaSaldosRubro = new ArrayList<String>(saldos.keySet());
//        valoresListaSaldosRubro = new ArrayList<Float>(saldos.values());

    }

    public void calcularEjecutadoPorRubro() {

        List<Solicitud> listaSolicitudes;

        // Total Ejecutado del proyecto
        ejecutadoProyecto = 0.0f;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de solicitudes (Aprobadas, en ejecucion, evaluacion y Rendidas)
        listaSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));

        // Ejecutado por Rubro
        List<ItemRubro> ejecutado = new ArrayList<ItemRubro>();

        // Llenamos la lista de ejecucion
        List<Rubro> listaRubros = getRubroFacade().findAll();

        // ordenamos la lista de rubros
        Collections.sort(listaRubros, new Comparator<Rubro>() {
            public int compare(Rubro o1, Rubro o2) {
                if (o1.getOrden() == null || o2.getOrden() == null) {
                    return 0;
                }
                return o1.getOrden().compareTo(o2.getOrden());
            }
        });


        // llenamos la lista de item-rubro >> ejecutado
        for (Rubro r : listaRubros) {


            ejecutado.add(new ItemRubro(r.getId(), r.getRubro(), 0.0f, r.getIcono(), r.getColoricono()));
        }


        // recorremos la lista de solicitudes ejecutadas y acumulamos en el rubro correspondiente
        for (Solicitud solicitud : listaSolicitudes) {

            //Acumulamos en el total ejecutado del proyecto
            ejecutadoProyecto += solicitud.getImporte().floatValue();

            Iterator j = ejecutado.iterator();

            // Buscamos en la lista de ejecutado por rubro para acumular
            while (j.hasNext()) {
                ItemRubro irt = (ItemRubro) j.next();

                // Acumulamos en la lista de ejecutado por rubro
                if (irt.getId() == solicitud.getPresupuestotarea().getRubro().getId()) {
                    irt.setMonto(irt.getMonto() + solicitud.getImporte().floatValue());
                }
            }

        }

        // Ejecutado por Rubro
        listaEjecutadoRubro = ejecutado;

        // colores de rubro
        StringBuilder sb = new StringBuilder();

        for(ItemRubro ir : this.getListaEjecutadoRubroOrdenadoPorMontoDesc()){
            sb.append(ir.getColoricono().replace("#", "") + ", ");
        }

        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "");
        System.out.println("COLORES BARRA HORIZONTAL EJECUCION POR RUBROS >> " + sb.toString());

        // generar grafico de barra horizontal stack
        crearModeloBarraHorizontal(sb.toString());

    }

    public void generarChartEjecutadoPorRubro() {
        chartEjecutadoPorRubro = new PieChartModel();

        for (ItemRubro ir : this.getListaEjecutadoRubroOrdenadoPorMontoDesc()) {
            chartEjecutadoPorRubro.set(ir.nombrerubro, ir.monto);
        }

        chartEjecutadoPorRubro.setMouseoverHighlight(true);
        chartEjecutadoPorRubro.setDiameter(65);
        chartEjecutadoPorRubro.setShowDataLabels(true);

        //chartEjecutadoPorRubro.setTitle("Simple Pie");
        //chartEjecutadoPorRubro.setLegendPosition("w");
        chartEjecutadoPorRubro.setExtender("torta");
        chartEjecutadoPorRubro.setSeriesColors("21B2CE,FF964A,5ACB73,9C4DAD,CE4131,DED7A5");

    }

    public void generarChartEjecutadoPorFecha() {

        List<Solicitud> colSolicitudes;
        List<Desembolso> colDesembolsos;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de solicitudes (Aprobadas)
        colSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));
        
        // Llenamos la lista de desembolsos
        colDesembolsos = this.getDesembolsoFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Ordenar las colecciones por fecha
        // SOLICITUDES
        Collections.sort(colSolicitudes, new Comparator<Solicitud>() {
            public int compare(Solicitud o1, Solicitud o2) {
                if (o1.getFechasolicitud() == null || o2.getFechasolicitud() == null) {
                    return 0;
                }
                return o1.getFechasolicitud().compareTo(o2.getFechasolicitud());
            }
        });
        
        // DESEMBOLSOS
        Collections.sort(colDesembolsos, new Comparator<Desembolso>() {
            public int compare(Desembolso o1, Desembolso o2) {
                if (o1.getFechacarga() == null || o2.getFechacarga()== null) {
                    return 0;
                }
                return o1.getFechacarga().compareTo(o2.getFechacarga());
            }
        });
        
        // Formato de fecha
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
        
        // Serie de Solicitudes
        chartEjecutadoPorFecha = new LineChartModel();
        
        LineChartSeries solcitudes = new LineChartSeries();
        //solcitudes.setLabel("Solicitudes");
 
        //solcitudes.set("2014-01-01", 51);
        
        float as = 0f;
        
        for(Solicitud s : colSolicitudes){
            as = as + s.getImporte().floatValue();
            solcitudes.set(sdf.format(s.getFechaaprobacion()),as);
        }
 
        // Serie de Desembolsos
        LineChartSeries desembolsos = new LineChartSeries();
        //desembolsos.setLabel("Desembolsos");
 
        float ad = 0f;
        
        for(Desembolso d : colDesembolsos){
            ad = ad + d.getMonto().floatValue();
            desembolsos.set(sdf.format(d.getFechacarga()),ad);
        }

        chartEjecutadoPorFecha.addSeries(desembolsos);
        chartEjecutadoPorFecha.addSeries(solcitudes);
         
        chartEjecutadoPorFecha.setTitle(""); // Zoom for Details
        chartEjecutadoPorFecha.setZoom(false);
        chartEjecutadoPorFecha.getAxis(AxisType.Y); //.setLabel("Values");
        DateAxis axis = new DateAxis(""); // Fechas
        //axis.setTickAngle(-50);
        //axis.setMax("2016-02-01");
        axis.setTickFormat("%b %#d, %y"); // %b %#d, %y
        
        chartEjecutadoPorFecha.setLegendRows(0);
        chartEjecutadoPorFecha.setLegendCols(0);
        chartEjecutadoPorFecha.setShadow(false);
        chartEjecutadoPorFecha.setShowPointLabels(false);
         
        chartEjecutadoPorFecha.getAxes().put(AxisType.X, axis);
        
        chartEjecutadoPorFecha.setExtender("ejecutadofecha");
        chartEjecutadoPorFecha.setSeriesColors("21B2CE,CE4131,FF964A,5ACB73,9C4DAD,DED7A5");

    }

    // Indicador de Ejecucion
    public MeterGaugeChartModel getIndicadorEjecutado() {

        if (indicadorEjecutado == null) {
            crearIndicadorEjecutado();
        }

        return indicadorEjecutado;
    }

    private MeterGaugeChartModel inicializarModeloIndicadorPorcentaje() {
        List<Number> intervalos = new ArrayList<Number>() {
            {
                add(25);
                add(50);
                add(75);
            }
        };

        
        porcentajeEjecutado = (ejecutadoProyecto / totalPresupuestoProyecto) * 100;
        
        //return new MeterGaugeChartModel(Integer.parseInt(String.valueOf(ejecutado)), intervalos);
        return new MeterGaugeChartModel((int) porcentajeEjecutado, intervalos);
    }

    private void crearIndicadorEjecutado() {
        indicadorEjecutado = inicializarModeloIndicadorPorcentaje();

        indicadorEjecutado.setSeriesColors("66cc66,93b75f,E7E658,cc6666");

        indicadorEjecutado.setGaugeLabelPosition("bottom");
        //indicadorEjecutado.setShowTickLabels(false);

        //indicadorEjecutado.setIntervalInnerRadius(25);
        //indicadorEjecutado.setIntervalOuterRadius(70);

        indicadorEjecutado.setExtender("indicador");
        
        indicadorEjecutado.setSeriesColors("21B2CE,9C4DAD,FF964A,5ACB73,CE4131,DED7A5");

    }

    public void calcularTotalesPorProyecto() {
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(proyectocontroller.getSelected().getId());
    }

    public void calcularPendienteRendicion() {
        this.pendienteRendicionProyecto = 12588.12f;
    }

    private void calcularSaldoProyecto() {
        
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);
        ProyectoController proyectoController = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        
        saldoProyecto = desembolsadoProyecto - ejecutadoProyecto;

        // calcular el porcentaje del saldo disponible sobre la ultimo desembolso
        calcularPorcentajeSaldoSobreUltimoDesembolso(proyectoController.getSelected().getId(),saldoProyecto);
    }

    public void calcularPorcentajeSaldoSobreUltimoDesembolso(int proyectoId, float saldoProyecto){

        porcentajeSaldoSobreUltimoDesembolso = (int) ((this.getDesembolsoFacade().obtenerImporteUltimoDesembolsoProyecto(proyectoId)*100) / saldoProyecto);

    }

    public float calcularEjecutadoPorProyecto(Integer idProyecto) {

        List<Solicitud> listaSolicitudes;

        // Total Ejecutado del proyecto
        ejecutadoProyecto = 0.0f;        

        // Llenamos la lista de solicitudes (Aprobadas, en ejecucion, evaluacion y Rendidas)
        listaSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(idProyecto);
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(idProyecto));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(idProyecto));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(idProyecto));
       
        for (Solicitud solicitud : listaSolicitudes) {
            ejecutadoProyecto += solicitud.getImporte().floatValue();
        }
        return ejecutadoProyecto;
    }
    
     public String calcularPorcenjateEjecutadoPorProyecto(Integer idProyecto) {
        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(idProyecto); 
        ejecutadoProyecto=this.calcularEjecutadoPorProyecto(idProyecto);
        porcentajeEjecutado = (ejecutadoProyecto / totalPresupuestoProyecto) * 100;
        return String.format("%.02f", porcentajeEjecutado);
    }
     
     public float calcularTotalesPorProyecto(Integer idProyecto) {
        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(idProyecto);        
        return totalPresupuestoProyecto;
    }
          
    public static class SaldoRubro {

        private String nombre;
        private float monto;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public float getMonto() {
            return monto;
        }

        public void setMonto(float monto) {
            this.monto = monto;
        }

    }
    
//    public void calcularTotalDesembolsado() {
//        
//        // Obtenemos los controladores necesarios
//        FacesContext context = FacesContext.getCurrentInstance();
//        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
//        
//        totalDesembolsado = 0f;
//        
//        // Llenamos la lista de desembolsos
//        List<Desembolso> colDesembolsos;
//        
//        try{
//            colDesembolsos = this.getDesembolsoFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());
//        } catch(Exception e){
//            colDesembolsos = new ArrayList<Desembolso>();
//        }
//        
//        for(Desembolso d : colDesembolsos){
//            totalDesembolsado = totalDesembolsado + d.getMonto().floatValue();
//        }
//    }

    public static class ItemRubro {

        private int id;
        private String nombrerubro;
        private float monto;
        private String icono;
        private String coloricono;

        public ItemRubro() {
        }

        public ItemRubro(int id, String nombrerubro, float monto) {
            this.id = id;
            this.nombrerubro = nombrerubro;
            this.monto = monto;
        }

        public ItemRubro(int id, String nombrerubro, float monto, String icono, String coloricono) {
            this.id = id;
            this.nombrerubro = nombrerubro;
            this.monto = monto;
            this.icono = icono;
            this.coloricono = coloricono;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombrerubro() {
            return nombrerubro;
        }

        public void setNombrerubro(String nombrerubro) {
            this.nombrerubro = nombrerubro;
        }

        public float getMonto() {
            return monto;
        }

        public void setMonto(float monto) {
            this.monto = monto;
        }

        public String getIcono() {return icono;}

        public void setIcono(String icono) { this.icono = icono; }

        public String getColoricono() { return coloricono; }

        public void setColoricono(String coloricono) { this.coloricono = coloricono; }

    }

    /**
     *
     * DASHBOARD DE SOLICITUDES, MODIFICACION DE PRESUPUESTO
     *
     */


    //    INDICADOR LINEAL > EVOLUCION DESEMBOLSOS Y EJECUCION
    
    private LineChartModel modeloLinealEvolucionDesembolsosEjecuciones;

    public LineChartModel getModeloLinealEvolucionDesembolsosEjecuciones() {
        return modeloLinealEvolucionDesembolsosEjecuciones;
    }

    public void setModeloLinealEvolucionDesembolsosEjecuciones(LineChartModel modeloLinealEvolucionDesembolsosEjecuciones) {
        this.modeloLinealEvolucionDesembolsosEjecuciones = modeloLinealEvolucionDesembolsosEjecuciones;
    }
    
    public void crearModeloLinealEvolucionDesembolsosEjecuciones() {
        modeloLinealEvolucionDesembolsosEjecuciones = iniciarModeloLinealEvolucionDesembolsosEjecuciones();
        modeloLinealEvolucionDesembolsosEjecuciones.setLegendPosition("s");
        Axis yAxis = modeloLinealEvolucionDesembolsosEjecuciones.getAxis(AxisType.Y);
        yAxis.setMin(0);

        Axis xAxis = modeloLinealEvolucionDesembolsosEjecuciones.getAxis(AxisType.X);
        xAxis.setMin(0);
        
    }

    public LineChartModel iniciarModeloLinealEvolucionDesembolsosEjecuciones() {

        LineChartModel modelo = new LineChartModel();
        modelo.setExtender("extensorLineal");
        modelo.setSeriesColors("BBE7E7, 6EE0F9");

        List<Solicitud> colSolicitudes;
        List<Desembolso> colDesembolsos;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de solicitudes (Aprobadas)
        colSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));

        // Llenamos la lista de desembolsos
        colDesembolsos = this.getDesembolsoFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Ordenar las colecciones por fecha
        // SOLICITUDES
        Collections.sort(colSolicitudes, new Comparator<Solicitud>() {
            public int compare(Solicitud o1, Solicitud o2) {
                if (o1.getFechasolicitud() == null || o2.getFechasolicitud() == null) {
                    return 0;
                }
                return o1.getFechasolicitud().compareTo(o2.getFechasolicitud());
            }
        });

        // DESEMBOLSOS
        Collections.sort(colDesembolsos, new Comparator<Desembolso>() {
            public int compare(Desembolso o1, Desembolso o2) {
                if (o1.getFechacarga() == null || o2.getFechacarga() == null) {
                    return 0;
                }
                return o1.getFechacarga().compareTo(o2.getFechacarga());
            }
        });

        // ARMAR SERIES
        LineChartSeries desembolsos = new LineChartSeries();
        desembolsos.setLabel("Desembolsos");

        LineChartSeries solcitudes = new LineChartSeries();
        solcitudes.setLabel("Ejecución");

        // ACUMULADOR DESEMBOLSOS
        float ad = 0f;
        int id = 0;
        for(Desembolso d : colDesembolsos){
            ad = ad + d.getMonto().floatValue();
            desembolsos.set(id,ad);
            id++;
        }

        // ACUMULADOR SOLICITUDES (EJECUCION)
        float as = 0f;
        int is = 0;
        for(Solicitud s : colSolicitudes){
            as = as + s.getImporte().floatValue();
            solcitudes.set(is,as);
            is++;
        }

        // agregar series al modelo
        modelo.addSeries(desembolsos);
        modelo.addSeries(solcitudes);

        return modelo;
    }
    
//    CHART DE DONA
      
    private DonutChartModel modeloDonaDisponiblePorRubro;

    public DonutChartModel getModeloDonaDisponiblePorRubro() {
        return modeloDonaDisponiblePorRubro;
    }

    public void setModeloDonaDisponiblePorRubro(DonutChartModel modeloDonaDisponiblePorRubro) {
        this.modeloDonaDisponiblePorRubro = modeloDonaDisponiblePorRubro;
    }
    public void crearModeloDonaDisponiblePorRubro(){
        modeloDonaDisponiblePorRubro = iniciarModeloDona();
        modeloDonaDisponiblePorRubro.setLegendPosition("e");
        modeloDonaDisponiblePorRubro.setSliceMargin(4);
        modeloDonaDisponiblePorRubro.setShowDataLabels(true);
//        modeloDonaDisponiblePorRubro.setDataLabelFormatString("color: #000000;");
        modeloDonaDisponiblePorRubro.setDataFormat("value");
        modeloDonaDisponiblePorRubro.setShadow(false);

        StringBuilder sb = new StringBuilder();
        StringBuilder sbColoresCero = new StringBuilder();

        for(Map.Entry<Rubro,Float> e : listaSaldosDeRubro.entrySet()){
            /*if(e.getValue() != 0.0f) {
                sb.append(e.getKey().getColoricono().replace("#","") + ", ");
            } else {
                sbColoresCero.append(e.getKey().getColoricono().replace("#","") + ", ");
            }*/
            sb.append(e.getKey().getColoricono().replace("#","") + ", ");

        }

        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "");
//        sbColoresCero.replace(sbColoresCero.lastIndexOf(","), sbColoresCero.lastIndexOf(",") + 1, "");
        System.out.println("COLORES DONA > 0  >> " + sb.toString());
//        System.out.println("COLORES DONA == 0 >> " + sbColoresCero.toString());

//        sb.append(", " + sbColoresCero.toString());

        System.out.println("COLORES TODOS >> " + sb.toString());

        //modeloDonaDisponiblePorRubro.setSeriesColors("2898C5, 394249, 667382, 84888B, BCC3C8");
        //modeloDonaDisponiblePorRubro.setSeriesColors("03A9F4, 8E24AA, F7D100, FF4081, FB8C00, E53935, 43A047");

        modeloDonaDisponiblePorRubro.setSeriesColors(sb.toString());
    }
    
    private DonutChartModel iniciarModeloDona() {
        DonutChartModel modelo = new DonutChartModel();
        modelo.setExtender("extensorDona");
         
        Map<String, Number> mapaSaldosRubro = new LinkedHashMap<String, Number>();

        for(Map.Entry<Rubro,Float> e : listaSaldosDeRubro.entrySet()){
            mapaSaldosRubro.put(e.getKey().getRubro(), e.getValue());
        }

        modelo.addCircle(mapaSaldosRubro);

        return modelo;
    }
    
//    BARRA HORIZONTAL
    
    private HorizontalBarChartModel modeloBarraEjecutadoPorRubros;

    public HorizontalBarChartModel getModeloBarraEjecutadoPorRubros() {
        return modeloBarraEjecutadoPorRubros;
    }

    public void setModeloBarraEjecutadoPorRubros(HorizontalBarChartModel modeloBarraEjecutadoPorRubros) {
        this.modeloBarraEjecutadoPorRubros = modeloBarraEjecutadoPorRubros;
    }
    
    public void crearModeloBarraHorizontal(String colores) {
        modeloBarraEjecutadoPorRubros = new HorizontalBarChartModel();
        modeloBarraEjecutadoPorRubros.setAnimate(true);
        //        modeloBarraEjecutadoPorRubros.setTitle("Ejecutado por Rubros");
        modeloBarraEjecutadoPorRubros.setLegendPosition("e");
        modeloBarraEjecutadoPorRubros.setStacked(true);
        modeloBarraEjecutadoPorRubros.setExtender("extensorBarraRubros");


        //modeloBarraEjecutadoPorRubros.setSeriesColors("EEB337, D74149, 58B14D, 2898C5, 394249");
        System.out.println("crearModeloBarraHorizontal >> COLORES >> " + colores);
        modeloBarraEjecutadoPorRubros.setSeriesColors(colores);

        for(ItemRubro ir : this.getListaEjecutadoRubroOrdenadoPorMontoDesc()){
            ChartSeries serie = new ChartSeries();
            serie.setLabel(ir.getNombrerubro());
            serie.set(ir.getNombrerubro(), ir.getMonto());
            modeloBarraEjecutadoPorRubros.addSeries(serie);

        }

        Axis xAxis = modeloBarraEjecutadoPorRubros.getAxis(AxisType.X);
        xAxis.setMin(0);

        Axis yAxis = modeloBarraEjecutadoPorRubros.getAxis(AxisType.Y);

    }

    public void calcularAvancesEtapasYTarea(){

        mapaAvancesEtapasYTareas = new HashMap<String, Integer>();

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        try{

            List<Etapa> listaEtapasProyecto = this.getEtapaFacade().buscarEtapasProyecto(proyectocontroller.getSelected().getId());

            Integer promedioEtapa = 0;
            Integer acumuladorTarea = 0;
            Integer contadorTarea = 0;

            for(Etapa e : listaEtapasProyecto){

                promedioEtapa = 0;

                String clave = "Etapa: " + e.getEtapa();

                mapaAvancesEtapasYTareas.put(clave, 0 );

                acumuladorTarea = 0;
                contadorTarea = 0;

                for(Tarea t : e.getTareaList()){

                    acumuladorTarea += (null == t.getAvance() ? 0 : t.getAvance());
                    contadorTarea++;

                    mapaAvancesEtapasYTareas.put("\t Tarea: " + t.getTarea(),t.getAvance());

                }

                promedioEtapa = Math.round(acumuladorTarea / contadorTarea);
                mapaAvancesEtapasYTareas.put(clave, promedioEtapa );

            }

            System.out.println("mapaAvancesEtapasYTareas ********");
            for(Map.Entry<String,Integer> e : mapaAvancesEtapasYTareas.entrySet()){
                System.out.println(e.getKey() + " >> " + e.getValue() );
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void calcularAvancesEtapas(){

        mapaAvancesEtapasYTareas = new HashMap<String, Integer>();

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        try{

            List<Etapa> listaEtapasProyecto = this.getEtapaFacade().buscarEtapasProyecto(proyectocontroller.getSelected().getId());

            Integer promedioEtapa = 0;
            Integer acumuladorTarea = 0;
            Integer contadorTarea = 0;

            for(Etapa e : listaEtapasProyecto){

                promedioEtapa = 0;

                String clave = "Etapa: " + e.getEtapa();

                mapaAvancesEtapasYTareas.put(clave, 0 );

                acumuladorTarea = 0;
                contadorTarea = 0;

                for(Tarea t : e.getTareaList()){

                    acumuladorTarea += (null == t.getAvance() ? 0 : t.getAvance());
                    contadorTarea++;

                    //mapaAvancesEtapasYTareas.put("\t Tarea: " + t.getTarea(),t.getAvance());

                }

                promedioEtapa = Math.round(acumuladorTarea / contadorTarea);
                mapaAvancesEtapasYTareas.put(clave, promedioEtapa );

            }

            System.out.println("mapaAvancesEtapasYTareas ********");
            for(Map.Entry<String,Integer> e : mapaAvancesEtapasYTareas.entrySet()){
                System.out.println(e.getKey() + " >> " + e.getValue() );
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     *
     * DASHBOARD GENERALES >> INDEX
     *
     */

    private Integer cantidadProyectos = 0;
    public Integer getCantidadProyectos() { return cantidadProyectos;  }

    private Integer cantidadProyectosEnFormalizacion = 0;
    public Integer getCantidadProyectosEnFormalizacion() {return cantidadProyectosEnFormalizacion;   }

    private Integer cantidadProyectosEnDesarrollo = 0;
    public Integer getCantidadProyectosEnDesarrollo() {return cantidadProyectosEnDesarrollo;  }

    private Integer cantidadIdeasProyecto = 0;
    public Integer getCantidadIdeasProyecto() {return cantidadIdeasProyecto;  }

    private List<Proyecto> listaProyectos;
    public List<Proyecto> getListaProyectos() { if(null==listaProyectos){listaProyectos=new ArrayList<Proyecto>();} return listaProyectos; }

    private float totalDesembolsado = 0f;
    public float getTotalDesembolsado() {return totalDesembolsado;  }

    private float totalEjecutado = 0f;
    public float getTotalEjecutado() { return totalEjecutado; }

    private int porcentajeEjecutadoTodosProyectos = 0;
    public int getPorcentajeEjecutadoTodosProyectos() {return porcentajeEjecutadoTodosProyectos;  }

    private float totalDisponibleSinEjecutar = 0f;
    public float getTotalDisponibleSinEjecutar() { return totalDisponibleSinEjecutar; }

    private Integer cantidadRendicionesPendientes = 0;
    public Integer getCantidadRendicionesPendientes() { return cantidadRendicionesPendientes;    }

    private float totalRendicionesPendientes = 0f;
    public float getTotalRendicionesPendientes() { return totalRendicionesPendientes; }

    private Map<Proyecto,Integer> mapaProyectos = new HashMap<Proyecto, Integer>();
    public Map<Proyecto, Integer> getMapaProyectos() {return mapaProyectos; }

    private List<String> listaColores;
    public List<String> getListaColores() { if(null == listaColores){listaColores = new ArrayList<String>();} return listaColores; }

    private Map<Convocatoria,Integer> mapaConvocatorias = new HashMap<Convocatoria, Integer>();
    public Map<Convocatoria, Integer> getMapaConvocatorias() { return mapaConvocatorias; }

    // metodo dashboard general DOCENTE
    public void dashboardGeneralDocente(){

        System.out.println("dashboardGeneralDocente");

        // cantidad de proyectos
        cantidadProyectos = obtenerCantidadProyectosAgente();

        // clasificar los proyectos por estado
        clasificarProyectosPorEstado();

        // total desembolsado
        totalDesembolsado = calcularTotalDesembolsosEnProyectos();

        // total ejecutado
        totalEjecutado = calcularTotalEjecutadoEnProyectos();

        // porcentaje ejecutado contra lo presupuestado
        calcularPorcentajeEjecutado();

        // total disponible sin ejecutar
        calcularTotalDisponibleSinEjecutar();

        // pendientes de rendicion
        calcularPendientesRendicion();

        //armar el mapa de proyectos en desarrollo y su avance promedio
        armarMapaProyectosFormalizadosConAvance();

        // inicializamos la lista de colores
        inicializarListaColores();

    }

    // metodo dashboard general ADMINISTRADOR
    public void dashboardGeneralAdministrador(){

        System.out.println("dashboardGeneralAdministrador");

        // cantidad de proyectos
        cantidadProyectos = obtenerCantidadTotalProyectos();

        // clasificar los proyectos por estado
        clasificarProyectosPorEstado();

        // total desembolsado
        totalDesembolsado = calcularTotalDesembolsosEnProyectos();

        // total ejecutado
        totalEjecutado = calcularTotalEjecutadoEnProyectos();

        // porcentaje ejecutado contra lo presupuestado
        calcularPorcentajeEjecutado();

        // total disponible sin ejecutar
        calcularTotalDisponibleSinEjecutar();

        // pendientes de rendicion
        calcularPendientesRendicion();

        //armar el mapa de proyectos en desarrollo y su avance promedio
        armarMapaProyectosFormalizadosConAvance();

        //armar el mapa de convocatorias
        armarMapaConvocatoriasConAvance();

        // inicializamos la lista de colores
        inicializarListaColores();
    }

    /*
        CANTIDAD DE PROYECTOS
     */

    public Integer obtenerCantidadProyectosAgente(){
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        AgenteController agenteController = (AgenteController) context.getApplication().evaluateExpressionGet(context, "#{agenteController}", AgenteController.class);

        System.out.println("ID Agente Actual = " + agenteController.getSelected().getId());

        try{
            this.listaProyectos = this.getProyectoFacade().buscarProyectoAgente(agenteController.getSelected().getId());
            return listaProyectos.size();

        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void clasificarProyectosPorEstado(){

        cantidadIdeasProyecto = 0;
        cantidadProyectosEnFormalizacion = 0;
        cantidadProyectosEnDesarrollo = 0;

        for(Proyecto p : listaProyectos){
            switch(p.getEstadoproyectoid().getId()){
                // Ideas Proyecto
                case 1: case 2: case 3: case 4: case 7:
                    cantidadIdeasProyecto++;
                    break;
                // Proyectos en Formalizacion
                case 5: case 6: case 8: case 9: case 10: case 11:
                    cantidadProyectosEnFormalizacion++;
                    break;
                // Proyectos en Ejecucion
                case 12:
                    cantidadProyectosEnDesarrollo++;
                    break;
                default:
                    cantidadProyectosEnDesarrollo++;
                    break;
            }
        }
    }

    public Integer obtenerCantidadTotalProyectos(){
        try{
            listaProyectos = this.getProyectoFacade().findAll();
            return listaProyectos.size();
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /*
        TOTAL DESEMBOLSADO
     */

    public float calcularTotalDesembolsosEnProyectos(){

        float total = 0f;
        List<Desembolso> listaDesembolsos = new ArrayList<Desembolso>();

        try{
            for(Proyecto p : this.getListaProyectos()){
                listaDesembolsos = this.getDesembolsoFacade().obtenerPorProyecto(p.getId());
                for(Desembolso d : listaDesembolsos){
                    total += d.getMonto().floatValue();
                }
            }
            return total;

        } catch(Exception e){
            e.printStackTrace();
            return 0f;
        }
    }

    /*
        TOTAL EJECUTADO
    */

    public float calcularTotalEjecutadoEnProyectos(){
        float total = 0f;
        List<Solicitud> listaSolicitudes = new ArrayList<Solicitud>();

        try{
            for(Proyecto p : this.getListaProyectos()){
                listaSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(p.getId()));
                listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(p.getId()));
                listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(p.getId()));
            }

            for(Solicitud s : listaSolicitudes){
                total += s.getImporte().floatValue();
            }

            return total;
        } catch(Exception e){
            e.printStackTrace();
            return 0f;
        }

    }

    public void calcularPorcentajeEjecutado(){

        float totalPresupuesto = 0f;

        try{
            for(Proyecto p : listaProyectos){
                totalPresupuesto += this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(p.getId());
            }

            porcentajeEjecutadoTodosProyectos = Math.round((totalEjecutado * 100) / totalPresupuesto) ;

        }catch(Exception e){
            e.printStackTrace();
            porcentajeEjecutadoTodosProyectos = 0;
        }
    }

    /*
        TOTAL DISPONIBLE POR EJECUTAR
     */
    public void calcularTotalDisponibleSinEjecutar(){
        totalDisponibleSinEjecutar = this.getTotalDesembolsado() - this.getTotalEjecutado();
    }

    /*
        PENDIENTES DE RENDICION
     */

    public void calcularPendientesRendicion(){

        float total = 0f;
        List<Solicitud> listaSolicitudes = new ArrayList<Solicitud>();

        try{
            for(Proyecto p : this.getListaProyectos()){
                listaSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(p.getId()));
            }

            for(Solicitud s : listaSolicitudes){
                total += s.getImporte().floatValue();
            }

            totalRendicionesPendientes = total;
            cantidadRendicionesPendientes = listaSolicitudes.size();

        } catch(Exception e){
            e.printStackTrace();

        }
    }


    /*
              Proyectos Formalizados y Su Avance
    */
    public void armarMapaProyectosFormalizadosConAvance(){

        // ordenamos la lista de proyectos por fecha
        Collections.sort(listaProyectos, comparadorFechas);

        for(Proyecto p : listaProyectos){
            // si el proyecto está aprobado (formalizado) o en ejecucion, agregamos a la nueva lista
            if(p.getEstadoproyectoid().getId() == 8 || p.getEstadoproyectoid().getId() == 12){

                try{
                    List<Tarea> listaTareasProyecto = this.getTareaFacade().buscarTareasProyecto(p.getId());
                    Integer acumulador = 0;
                    Integer contador = 0;
                    Integer promedio = 0;

                    for(Tarea t : listaTareasProyecto){

                        acumulador += (null == t.getAvance() ? 0 : t.getAvance());

                        contador++;
                    }

                    promedio = Math.round(acumulador / contador);

                    mapaProyectos.put(p,promedio);

                    System.out.println("Proyecto Formalizado >> " + p.getNombre() + " - Promedio Avance >> " + promedio);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    public class PromedioAvanceConvocatoria{

        private Convocatoria convocatoria;
        private Integer contador;
        private Integer acumulador;
        private Float promedio;

        public Convocatoria getConvocatoria() { return convocatoria; }
        public void setConvocatoria(Convocatoria convocatoria) { this.convocatoria = convocatoria;  }

        public Integer getContador() { return contador; }
        public void setContador(Integer contador) { this.contador = contador; }

        public Integer getAcumulador() { return acumulador; }
        public void setAcumulador(Integer acumulador) { this.acumulador = acumulador; }

        public Float getPromedio() { return promedio; }
        public void setPromedio(Float promedio) { this.promedio = promedio; }
    }

    public void armarMapaConvocatoriasConAvance(){

        List<PromedioAvanceConvocatoria> listaAvanceConvenios = new ArrayList<PromedioAvanceConvocatoria>();

        for(Map.Entry<Proyecto,Integer> entrada : mapaProyectos.entrySet()){

            boolean encontrado = false;

            for(PromedioAvanceConvocatoria p : listaAvanceConvenios){
                if(entrada.getKey().getConvocatoriaid().equals(p.getConvocatoria())){
                    p.setContador(p.getContador() + 1);
                    p.setAcumulador(p.getAcumulador() + entrada.getValue());
                    Float promedio = p.getAcumulador().floatValue() / p.getContador().floatValue();
                    p.setPromedio(promedio);
                    encontrado = true;

                    System.out.println("Convocatoria: " + p.getConvocatoria().getConvocatoria() + " - Acumulador: " + p.getAcumulador() + " - Contador: " + p.getContador());
                }
            }

            if(!encontrado){
                PromedioAvanceConvocatoria nuevoPromedio = new PromedioAvanceConvocatoria();
                nuevoPromedio.setConvocatoria(entrada.getKey().getConvocatoriaid());
                nuevoPromedio.setAcumulador(entrada.getValue());
                nuevoPromedio.setContador(1);
                nuevoPromedio.setPromedio(nuevoPromedio.getAcumulador().floatValue() / nuevoPromedio.getContador().floatValue());

                listaAvanceConvenios.add(nuevoPromedio);
            }
        }

        for(PromedioAvanceConvocatoria p : listaAvanceConvenios){
            mapaConvocatorias.put(p.getConvocatoria(),Math.round(p.getPromedio()));
        }
    }

    Comparator<Proyecto> comparadorFechas = new Comparator<Proyecto>() {
        public int compare(Proyecto p1, Proyecto p2) {

            if(p2.getFecha().after(p1.getFecha())){
                return 1;
            } else{
                return -1;
            }
        }
    };

    public void inicializarListaColores(){
        this.getListaColores().add("RedBack");
        this.getListaColores().add("BlueBack");
        this.getListaColores().add("GreenBack");
        this.getListaColores().add("OrangeBack");
        this.getListaColores().add("PinkBack");
    }

    public String obtenerColorAleatorio(){
        Random generadorAleatorios = new Random();
        int index = generadorAleatorios.nextInt(listaColores.size());
        return listaColores.get(index);
    }


}
