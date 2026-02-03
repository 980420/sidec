package gehos.comun.reportes.session;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.reportes.entity.ChartParameters;
import gehos.comun.reportes.entity.ReportPrint;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.management.entity.Entidad_configuracion;



import gehos.autenticacion.entity.Usuario;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.theme.Theme;
import org.jboss.seam.theme.ThemeSelector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.SortOrder;

@Name("reportManager")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ReportManager {

	@In
	private IActiveModule activeModule;
	@In
	Usuario user;
	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;
	
	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;
	
	
	private String reportURL="";
	private JasperReport jasperReport;
    private JasperPrint jasperPrint;
    private String savedFile, destFile;
    private Integer typeFile;
    private String uUID;
    private String referencetoreturn;
    private String plantillaFILE;
    private String imagesURI;
    private String reportName;
    private Map    parameters;
    public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
	FacesContext aFacesContext; 
    ServletContext context ;
    
    @Create
    public void Inicializar(){
    	aFacesContext = FacesContext.getCurrentInstance();
        context = (ServletContext)aFacesContext.getExternalContext().getContext();
        parameters =  new HashMap();
    }
    
    public List<String> fileFormatsToExport(){
    	
    	
    	List<String> result  ;
    	
    	result = new ArrayList<String>();
    	//result.add("CSV");
    	result.add("PDF");
    	result.add("WORD");
    	//result.add("TXT");
    	result.add("EXCEL");
    	//result.add("XML");    	
    	
    	
    	return result;
    }
    
    public FileType intercambiar(FileType fileType){
    	if(fileType.equals(FileType.PDF_FILE))
    		return FileType.CSV_FILE;
    	if(fileType.equals(FileType.RTF_FILE))
    		return FileType.PDF_FILE;
    	if(fileType.equals(FileType.EXCEL_FILE))
    		return FileType.RTF_FILE;
    	if(fileType.equals(FileType.XML_FILE))
    		return FileType.PLAIN_TEXT_FILE;
    	if(fileType.equals(FileType.CSV_FILE))
    		return FileType.EXCEL_FILE;
    	if(fileType.equals(FileType.PLAIN_TEXT_FILE))
    		return FileType.XML_FILE;
    	return FileType.HTML_FILE;
    }
    
    private String loadNombreModuloContenedorPlantillas(){
    	
    	String contenedor;
    	Funcionalidad func = activeModule.getActiveModule();
    	contenedor = func.getNombre();
		
    	Boolean stop =  false;
    	while (!func.getContenedorIconos()) {
		
    		func = func.getFuncionalidadPadre();
    		contenedor = func.getNombre();
    		
		} 
    	
    	
    	
    	return contenedor;
    }
    
    @SuppressWarnings("unchecked")
	public void settingParametersInitialInformation(String reportName, Map parameters){
    	 this.reportName =  reportName;
         
         //-1 Cargar el encabezado comun
    	 Funcionalidad func  = activeModule.getActiveModule(); 
         Entidad_configuracion hospital  = func.getEntidad();
         hospital = entityManager.merge(hospital);
         String imgpath = context.getRealPath("/resources");
         this.parameters = parameters;
         
         String autor = user.getNombre() + " " + user.getPrimerApellido() + " " + user.getSegundoApellido() ; 
         String datePatternMessage = SeamResourceBundle.getBundle().getString("datePattern");
         String separadorMessage = SeamResourceBundle.getBundle().getString("separador");
         DateFormat formatter = new SimpleDateFormat(datePatternMessage);
         String fecha = formatter.format(new Date());
         //temporal
         String dpto = func.getLabel(); 
         //Quitar
         //String pageMessage = SeamResourceBundle.getBundle().getString("page");
         //fecha = SeamResourceBundle.getBundle().getString("fechaconfecReporte") + fecha;
         //this.parameters.put("PAGINA", pageMessage);
         
/*         String path = "/resources/modCommon/entidades_logos/"
        	 + theme.getTheme().get("name") + "/"
        	 + theme.getTheme().get("color") + "/"
        	 + node.getValue().getLogo();	
*/
         this.parameters.put("AUTOR_RPT", autor);
         this.parameters.put("LOGO_URL", imgpath + "/modCommon/appLogos/" + "header.png");
         this.parameters.put("Header", imgpath + "/modCommon/appLogos/" + "header.png");
         this.parameters.put("Footer", imgpath + "/modCommon/appLogos/" + "footer.png");
         this.parameters.put("LOGO_URL", imgpath + "/modCommon/appLogos/" + "header.png");
         this.parameters.put("LOGO1_URL", imgpath + "/modCommon/appLogos/" + "footer.png");
         this.parameters.put("PRODUCT_IMG",imgpath + "/modCommon/entidades_logos/" + theme.getTheme().get("name") +"/"+ theme.getTheme().get("color") +"/" + hospital.getLogo() );
         this.parameters.put("SEPARADOR", separadorMessage);
         this.parameters.put("DATE_TODAY", fecha);
         this.parameters.put("DEPARTAMENTO", dpto);
         
         
         String modulo = loadNombreModuloContenedorPlantillas();
         
       //1-Camino donde est� la plantilla
     	plantillaFILE= context.getRealPath("/resources") + "/reports/plantillas/" + modulo + "/" + reportName + ".jrxml";
    }
    
    @SuppressWarnings("unchecked")
	public void settingParametersInitialInformationConfiguracion(String reportName, Map parameters){
   	 this.reportName =  reportName;
        
        //-1 Cargar el encabezado comun
   	 Funcionalidad func  = activeModule.getActiveModule(); 
        String imgpath = context.getRealPath("/resources");
        this.parameters = parameters;
        
        String autor = user.getNombre() + " " + user.getPrimerApellido() + " " + user.getSegundoApellido() ; 
        String datePatternMessage = SeamResourceBundle.getBundle().getString("datePattern");
        String separadorMessage = SeamResourceBundle.getBundle().getString("separador");
        DateFormat formatter = new SimpleDateFormat(datePatternMessage);
        String fecha = formatter.format(new Date());
        //temporal
        String dpto = func.getLabel(); 
        //Quitar
        //String pageMessage = SeamResourceBundle.getBundle().getString("page");
        //fecha = SeamResourceBundle.getBundle().getString("fechaconfecReporte") + fecha;
        //this.parameters.put("PAGINA", pageMessage);
        
/*         String path = "/resources/modCommon/entidades_logos/"
       	 + theme.getTheme().get("name") + "/"
       	 + theme.getTheme().get("color") + "/"
       	 + node.getValue().getLogo();	
*/

        this.parameters.put("AUTOR_RPT", autor);
        this.parameters.put("Header", imgpath + "/modCommon/appLogos/" + "header.png");
        this.parameters.put("Footer", imgpath + "/modCommon/appLogos/" + "footer.png");
        this.parameters.put("LOGO_URL", imgpath + "/modCommon/appLogos/" + "header.png");
        this.parameters.put("LOGO1_URL", imgpath + "/modCommon/appLogos/" + "footer.png");
        this.parameters.put("SEPARADOR", separadorMessage);
        this.parameters.put("DATE_TODAY", fecha);
        this.parameters.put("DEPARTAMENTO", dpto);
        
        
        String modulo = loadNombreModuloContenedorPlantillas();
        
      //1-Camino donde est� la plantilla
    	plantillaFILE= context.getRealPath("/resources") + "/reports/plantillas/" + modulo + "/" + reportName + ".jrxml";
   }
    
    private void CommonInformation(String reportName, Map parameters, Collection data) throws Exception{
    	try{
        jasperReport = null;
        jasperPrint = null;
        settingParametersInitialInformation(reportName, parameters);
      //0- Convertimos el data source a JRBeanCollectionDataSource
    	JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
    	
    	//2-Compilamos el archivo XML y lo cargamos en memoria
    	
        jasperReport = JasperCompileManager.compileReport(plantillaFILE);

        //3-Llenamos el reporte con la informaci�n (de la DB) y par�metros necesarios para la consulta
        if(data.size() == 0)
        	jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        else
        	jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        String modulo = loadNombreModuloContenedorPlantillas();
       
         uUID = UUID.randomUUID().toString() ;
        savedFile = "/reports/tempPages/" + modulo  +"/" + user.getUsername() + activeModule.getActiveModule().getNombre() + reportName; 
        destFile = context.getRealPath("/resources") + savedFile;
        
        referencetoreturn = "/reports/tempPages/" + modulo +"/" + user.getUsername() + activeModule.getActiveModuleName() + reportName;
        imagesURI = context.getContextPath()  +  "/resources/reports/tempPages/" + modulo +"/" + user.getUsername() + activeModule.getActiveModule().getNombre() + reportName + ".html_files/";///image?image=";
        
    	}
    	catch (Exception e) {
			// TODO: handle exception
    		throw e;
		}
        
    }
    
    private void CommonInformationConfiguracion(String reportName, Map parameters, Collection data) throws Exception{
    	try{
        jasperReport = null;
        jasperPrint = null;
        settingParametersInitialInformationConfiguracion(reportName, parameters);
      //0- Convertimos el data source a JRBeanCollectionDataSource
    	JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
    	
    	//2-Compilamos el archivo XML y lo cargamos en memoria
    	
        jasperReport = JasperCompileManager.compileReport( plantillaFILE);

        //3-Llenamos el reporte con la informaci�n (de la DB) y par�metros necesarios para la consulta
        if(data.size() == 0)
        	jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        else
        	jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        String modulo = loadNombreModuloContenedorPlantillas();
       
         uUID = UUID.randomUUID().toString() ;
        savedFile = "/reports/tempPages/" + modulo  +"/" + user.getUsername() + activeModule.getActiveModule().getNombre() + reportName; 
        destFile = context.getRealPath("/resources") + savedFile;
        
        referencetoreturn = "/reports/tempPages/" + modulo +"/" + user.getUsername() + activeModule.getActiveModuleName() + reportName;
        imagesURI = context.getContextPath()  +  "/resources/reports/tempPages/" + modulo +"/" + user.getUsername() + activeModule.getActiveModule().getNombre() + reportName + ".html_files/";///image?image=";
        
    	}
    	catch (Exception e) {
			// TODO: handle exception
    		throw e;
		}
        
    }
    
    private void CommonInformation(String reportName, Map parameters, Collection data, Map<String, String> reportParameters) throws Exception{
    	try{
       jasperPrint = null;
       jasperReport = null;
        settingParametersInitialInformation(reportName, parameters);
        //0- Convertimos el data source a JRBeanCollectionDataSource
    	JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
    	
    	//2-Compilamos el archivo XML y lo cargamos en memoria
    	String jrxml = "";
    	try{
    		Scanner scan = new Scanner(new File(plantillaFILE));
    		while(scan.hasNext()){
    			jrxml += scan.nextLine() + " ";
    		}
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	for (String key : reportParameters.keySet()) {
    		jrxml = jrxml.replace("$" + key.toString() + "$", reportParameters.get(key));
		}
    	ByteArrayInputStream input = new ByteArrayInputStream(jrxml.getBytes());
    	jasperReport = JasperCompileManager.compileReport(input);
        //jasperReport = JasperCompileManager.compileReport( plantillaFILE);

        //3-Llenamos el reporte con la informaci�n (de la DB) y par�metros necesarios para la consulta
        if(data.size() == 0)
        	jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        else
        	jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        String modulo = loadNombreModuloContenedorPlantillas();
       
         uUID = UUID.randomUUID().toString() ;
        savedFile = "/reports/tempPages/" + modulo  +"/" + user.getUsername() + activeModule.getActiveModule().getNombre() + reportName +  uUID; 
        destFile = context.getRealPath("/resources") + savedFile;
        
        referencetoreturn = "/reports/tempPages/" + modulo +"/" + user.getUsername() + activeModule.getActiveModuleName() + reportName +  uUID;
        imagesURI = context.getContextPath()  +  "/resources/reports/tempPages/" + modulo +"/" + user.getUsername() + activeModule.getActiveModule().getNombre() + reportName +  uUID + ".html_files/";///image?image=";
        
    	}
    	catch (Exception e) {
			// TODO: handle exception
    		throw e;
		}
        
    }
    
	public String getReportURL() {
		return reportURL;
	}

	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}

	public String getSavedFile() {
		return savedFile;
	}

	public void setSavedFile(String savedFile) {
		this.savedFile = savedFile;
	}

	public String getDestFile() {
		return destFile;
	}

	public void setDestFile(String destFile) {
		this.destFile = destFile;
	}
	
	/*
	 * Object[] - Array con datos necesarios para obtener el reporte serializado de la BD.
	 * Object[0] --> id del usuario que genera el reporte.
	 * Object[1] --> id módulo activo.
	 * Object[2] --> UUID reporte generado.
	 * */
	public Object[] PrintReport(String reportName, Map parameters, Collection data){
		
		Object[] resultArray = null;
		try{
			resultArray= new Object[4];
			CommonInformation(reportName, parameters, data);
			ReportPrint reportToPrint =  new ReportPrint();
			Funcionalidad func = activeModule.getActiveModule();
			func =  entityManager.merge(func);
			//Funcionalidad funcE = entityManager.find(Funcionalidad.class, func.getId());
			reportToPrint.setFuncionalidad(func);
			reportToPrint.setIdUsuario(user.getId());
			reportToPrint.setUuid(uUID);
			//JasperPrint jasperPrint =  null;
			byte[] result = {-1};
			ByteArrayOutputStream resul  = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(resul);
			out.writeObject(jasperPrint);
	        out.close();
	        result = resul.toByteArray();
			reportToPrint.setReporttoprint(result);
			entityManager.persist(reportToPrint);
			entityManager.flush();
			
			
			resultArray[0] = user.getId();
			resultArray[1] = activeModule.getActiveModule().getId();
			resultArray[2] = uUID;
			
			/*
			 * Formar cadena de internacionalización del applet para imprimir
			 * */
			
			String cadenaInterApplet = "";
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("general_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("servImp_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("nombre_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("rangoImp_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("todasPag_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("paginas_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("aLa_aaplet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("copias_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("numCopiad_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("imprimir_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("cancelar_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("color_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("escalaGrises_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("colores_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("calidad_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("borrador_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("normal_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("alta_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("orientation_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("vertical_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("verticalInv_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("horizontal_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("horizontalInv_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("avanzado_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("noReport_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("numPagPositivos_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("numPagIFinal_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("formRangNum_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("error_Applet");
			
			resultArray[3] = cadenaInterApplet;
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resultArray;
	}
	
	
	/*
	 * Object[] - Array con datos necesarios para obtener el reporte serializado de la BD.
	 * Object[0] --> id del usuario que genera el reporte.
	 * Object[1] --> id módulo activo.
	 * Object[2] --> UUID reporte generado.
	 * */
	public  Object[] PrintReportWithSubReports(String reportName, Map parameters, Collection data, FileType fileType, List<String> subReportsList, List<Collection> subReportsDataSourceList, List<Map> subReportsParameters ){
		Object[] resultArray = null;
		try{
			resultArray= new Object[4];
			loadSubReports(subReportsList, parameters, subReportsDataSourceList,  subReportsParameters );
			CommonInformation(reportName, parameters, data);
			ReportPrint reportToPrint =  new ReportPrint();
			Funcionalidad func = activeModule.getActiveModule();
			func =  entityManager.merge(func);
			//Funcionalidad funcE = entityManager.find(Funcionalidad.class, func.getId());
			reportToPrint.setFuncionalidad(func);
			reportToPrint.setIdUsuario(user.getId());
			reportToPrint.setUuid(uUID);
			//JasperPrint jasperPrint =  null;
			byte[] result = {-1};
			ByteArrayOutputStream resul  = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(resul);
			out.writeObject(jasperPrint);
	        out.close();
	        result = resul.toByteArray();
			reportToPrint.setReporttoprint(result);
			entityManager.persist(reportToPrint);
			entityManager.flush();
			
			
			resultArray[0] = user.getId();
			resultArray[1] = activeModule.getActiveModule().getId();
			resultArray[2] = uUID;
			
			/*
			 * Formar cadena de internacionalización del applet para imprimir
			 * */
			
			String cadenaInterApplet = "";
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("general_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("servImp_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("nombre_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("rangoImp_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("todasPag_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("paginas_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("aLa_aaplet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("copias_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("numCopiad_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("imprimir_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("cancelar_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("color_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("escalaGrises_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("colores_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("calidad_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("borrador_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("normal_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("alta_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("orientation_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("vertical_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("verticalInv_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("horizontal_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("horizontalInv_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("avanzado_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("noReport_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("numPagPositivos_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("numPagIFinal_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("formRangNum_Applet");
			cadenaInterApplet+=  SeamResourceBundle.getBundle().getString("error_Applet");
			
			resultArray[3] = cadenaInterApplet;

			}
			catch (Exception e) {
				// TODO: handle exception
			} 
		
			return resultArray;
		
	}
	
	public void ShowReport(String reportName, Map parameters, Collection data, FileType streamingFile){
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			
			HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
			HttpServletRequest request = (HttpServletRequest)   context.getExternalContext().getRequest();
			ServletOutputStream servletOutputStream = response.getOutputStream();
			CommonInformation(reportName, parameters, data);
			response.setHeader("Content-disposition", "inline");
			
			byte[] aux = new  byte[1];
			 typeFile = streamingFile.ordinal();
		        switch (typeFile) {
				case 0: 
					response.setContentType("application/pdf");
					aux = HISExportManager.exportReportToPdfStream(jasperPrint);
					break;
				case 1: 
					response.setContentType("application/rtf");
					aux = HISExportManager.exportReportToRtf(jasperPrint); 
					break;
				case 2: 
					response.setContentType("application/xls");
					aux = HISExportManager.exportReportToExcelStream(jasperPrint); 
					break;
				case 3: 
					response.setContentType("text/html");
					aux = HISExportManager.exportReportToHtmlStream(jasperPrint, imagesURI);  	
					break;
				case 4: 
					response.setContentType("application/xml");
					aux = HISExportManager.exportReportToXmlStream(jasperPrint);  
					break;
				case 5:
					response.setContentType("application/csv");
					aux = HISExportManager.exportReportToCSVStream(jasperPrint); 
					break;
					
				case 6:
					response.setContentType("application/txt");
					aux = HISExportManager.exportReportToPlainTextStream(jasperPrint); 
					break;

				default:
					break;
				}
			
			
			
			request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
			Enumeration temp =  request.getSession().getAttributeNames();
			servletOutputStream.write(aux);
			servletOutputStream.flush();
			servletOutputStream.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.responseComplete();
	}
	
	public String ExportReport(String reportName, Map parameters, Collection data, FileType fileType, Map<String, String> reportParameters ){
		 try {
	        	
	        	CommonInformation(reportName, parameters, data, reportParameters);
	        	 //typeFile = intercambiar(fileType).ordinal();
	        	typeFile = fileType.ordinal();
		        switch (typeFile) {
				case 0: 
					referencetoreturn = referencetoreturn + ".pdf";
					destFile = destFile + ".pdf";
					HISExportManager.exportReportToPdfFile(jasperPrint, destFile);
					break;
				case 1: 
					referencetoreturn = referencetoreturn + ".rtf";
					destFile = destFile + ".rtf";
					HISExportManager.exportReportToRtfFile(jasperPrint, destFile);
					break;
				case 2: 
					referencetoreturn = referencetoreturn + ".xls";
					destFile = destFile + ".xls";
					HISExportManager.exportReportToExcelFile(jasperPrint, destFile);
					break;
				case 3: 
					referencetoreturn = referencetoreturn + ".html";
					destFile = destFile + ".html";
					
					HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
					break;
				case 4: 
					referencetoreturn = referencetoreturn + ".jrpxml";
					destFile = destFile + ".jrpxml";
					HISExportManager.exportReportToXmlFile(jasperPrint, destFile, true);	
					break;
				case 5:
					referencetoreturn = referencetoreturn + ".csv";
					destFile = destFile + ".csv";
					HISExportManager.exportReportToCsvFile(jasperPrint, destFile);
					break;
					
				case 6:
					referencetoreturn = referencetoreturn + ".txt";
					destFile = destFile + ".txt";
					HISExportManager.exportReportToPlainTextFile(jasperPrint, destFile);
					break;

				default:
					referencetoreturn = referencetoreturn + ".html";
					destFile = destFile + ".html";
					
					HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
					break;
				}
		        reportURL= "/resources" + referencetoreturn;
		        String generado = SeamResourceBundle.getBundle().getString("greporte");
		        String condatos = SeamResourceBundle.getBundle().getString("rdatos");
		        String titulo = " ";
		        if (parameters.containsKey("P_TITULO"))
		        titulo = parameters.get("P_TITULO").toString();
		        String subtitulo  = " ";
		        if (parameters.containsKey("P_TITULO"))
		        subtitulo= parameters.get("P_SUBTITULO").toString();
		        String text = generado + titulo + condatos + subtitulo;
		        bitacora.registrarInicioDeAccion(text);
		        
		    	}catch (Exception e){
		    	      System.out.println(e);
		    	      e.printStackTrace();
		    	    }
		    	return reportURL;
	}
	
	@SuppressWarnings({"unchecked"})
	public String ExportReport(String reportName, Map parameters, Collection data, FileType fileType ){
		
        try {
        	
        	CommonInformation(reportName, parameters, data);
        	 //typeFile = intercambiar(fileType).ordinal();
        	typeFile = fileType.ordinal();
	        switch (typeFile) {
			case 0: 
				referencetoreturn = referencetoreturn + ".pdf";
				destFile = destFile + ".pdf";
				HISExportManager.exportReportToPdfFile(jasperPrint, destFile);
				break;
			case 1: 
				referencetoreturn = referencetoreturn + ".rtf";
				destFile = destFile + ".rtf";
				HISExportManager.exportReportToRtfFile(jasperPrint, destFile);
				break;
			case 2: 
				referencetoreturn = referencetoreturn + ".xls";
				destFile = destFile + ".xls";
				HISExportManager.exportReportToExcelFile(jasperPrint, destFile);
				break;
			case 3: 
				referencetoreturn = referencetoreturn + ".html";
				destFile = destFile + ".html";
				
				HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
				break;
			case 4: 
				referencetoreturn = referencetoreturn + ".jrpxml";
				destFile = destFile + ".jrpxml";
				HISExportManager.exportReportToXmlFile(jasperPrint, destFile, true);	
				break;
			case 5:
				referencetoreturn = referencetoreturn + ".csv";
				destFile = destFile + ".csv";
				HISExportManager.exportReportToCsvFile(jasperPrint, destFile);
				break;
				
			case 6:
				referencetoreturn = referencetoreturn + ".txt";
				destFile = destFile + ".txt";
				HISExportManager.exportReportToPlainTextFile(jasperPrint, destFile);
				break;

			default:
				referencetoreturn = referencetoreturn + ".html";
				destFile = destFile + ".html";
				
				HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
				break;
			}
	        reportURL= "/resources" + referencetoreturn;
	        String generado = SeamResourceBundle.getBundle().getString("greporte");
	        String condatos = SeamResourceBundle.getBundle().getString("rdatos");
	        String titulo = "  ";
	        
	        if (parameters.containsKey("P_TITULO"))
	        	parameters.get("P_TITULO").toString();

	        String subtitulo = " "; 
	        if (parameters.containsKey("P_SUBTITULO"))	
	        	parameters.get("P_SUBTITULO").toString();
	        String text = generado + titulo + condatos + subtitulo;
	        bitacora.registrarInicioDeAccion(text);
	        
	    	}catch (Exception e){
	    	      System.out.println(e);
	    	      e.printStackTrace();
	    	    }
	    	return reportURL;
	}
	
public String ExportReportConfiguracion(String reportName, Map parameters, Collection data, FileType fileType ){
		
        try {
        	
        	CommonInformationConfiguracion(reportName, parameters, data);
        	 //typeFile = intercambiar(fileType).ordinal();
        	typeFile = fileType.ordinal();
	        switch (typeFile) {
			case 0: 
				referencetoreturn = referencetoreturn + ".pdf";
				destFile = destFile + ".pdf";
				HISExportManager.exportReportToPdfFile(jasperPrint, destFile);
				break;
			case 1: 
				referencetoreturn = referencetoreturn + ".rtf";
				destFile = destFile + ".rtf";
				HISExportManager.exportReportToRtfFile(jasperPrint, destFile);
				break;
			case 2: 
				referencetoreturn = referencetoreturn + ".xls";
				destFile = destFile + ".xls";
				HISExportManager.exportReportToExcelFile(jasperPrint, destFile);
				break;
			case 3: 
				referencetoreturn = referencetoreturn + ".html";
				destFile = destFile + ".html";
				
				HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
				break;
			case 4: 
				referencetoreturn = referencetoreturn + ".jrpxml";
				destFile = destFile + ".jrpxml";
				HISExportManager.exportReportToXmlFile(jasperPrint, destFile, true);	
				break;
			case 5:
				referencetoreturn = referencetoreturn + ".csv";
				destFile = destFile + ".csv";
				HISExportManager.exportReportToCsvFile(jasperPrint, destFile);
				break;
				
			case 6:
				referencetoreturn = referencetoreturn + ".txt";
				destFile = destFile + ".txt";
				HISExportManager.exportReportToPlainTextFile(jasperPrint, destFile);
				break;

			default:
				referencetoreturn = referencetoreturn + ".html";
				destFile = destFile + ".html";
				
				HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
				break;
			}
	        reportURL= "/resources" + referencetoreturn;
	        String generado = SeamResourceBundle.getBundle().getString("greporte");
	        String condatos = SeamResourceBundle.getBundle().getString("rdatos");
	        String titulo = "  ";
	        
	        if (parameters.containsKey("P_TITULO"))
	        	parameters.get("P_TITULO").toString();

	        String subtitulo = " "; 
	        if (parameters.containsKey("P_SUBTITULO"))	
	        	parameters.get("P_SUBTITULO").toString();
	        String text = generado + titulo + condatos + subtitulo;
	        bitacora.registrarInicioDeAccion(text);
	        
	    	}catch (Exception e){
	    	      System.out.println(e);
	    	      e.printStackTrace();
	    	    }
	    	return reportURL;
	}
	
	
	public JasperReport CompileReport(String plantilla){
		String modulo = loadNombreModuloContenedorPlantillas();
		JasperReport tempReport=null;
		String file;
		try {
				file = context.getRealPath("/resources") + "/reports/plantillas/" + modulo + "/" + plantilla + ".jrxml";
				tempReport = JasperCompileManager.compileReport(file);
			
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tempReport;
	}
	
	public JasperPrint FillReport(JasperReport report, Map param, Collection datasource){
		 //3-Llenamos el reporte con la informaci�n (de la DB) y par�metros necesarios para la consulta
      try{ 
		if(datasource.size() == 0)
        	return JasperFillManager.fillReport(report, param, new JREmptyDataSource());
        else
        	return JasperFillManager.fillReport(report, param, new JRBeanCollectionDataSource(datasource));
      }
      catch (Exception e) {
		e.printStackTrace();
		return null;
	}
      
      
	}
	
	
	private void loadSubReports(List<String> subReportsList, Map parametersSub, List<Collection> subReportsDataSourceList, List<Map> subReportsParameters ){
		String modulo = loadNombreModuloContenedorPlantillas();
		Map<String, JasperReport> subReportMap =  new HashMap<String, JasperReport>();
		Map<String, JRBeanCollectionDataSource> datasources =  new HashMap<String, JRBeanCollectionDataSource>();
		Map<String, Map> subParameters =  new HashMap<String, Map>();
		String name;
		JasperReport tempReport;
		String file;
		try {
			for (int i = 0; i < subReportsList.size(); i++) {
				name = subReportsList.get(i);
				file = context.getRealPath("/resources") + "/reports/plantillas/" + modulo + "/" + name + ".jrxml";
				tempReport = JasperCompileManager.compileReport(file);
				subReportMap.put(name.toUpperCase() , tempReport);
				datasources.put(name.toUpperCase() + "_DATASOURCE", new JRBeanCollectionDataSource(subReportsDataSourceList.get(i)));
				subParameters.put(name.toUpperCase() + "_PARAMETERS", subReportsParameters.get(i));
				
			}
			parametersSub.put("SUBREPORTS", subReportMap);
			parametersSub.put("SUBREPORTS_DATASOURCES", datasources);
			parametersSub.put("SUBREPORTS_PARAMETERS", subParameters);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@SuppressWarnings({"unchecked"})
	public String ExportReportWithSubReports(String reportName, Map parameters, Collection data, FileType fileType, List<String> subReportsList, List<Collection> subReportsDataSourceList, List<Map> subReportsParameters  ){
		
        try {
        	loadSubReports(subReportsList, parameters, subReportsDataSourceList,  subReportsParameters );
        	CommonInformation(reportName, parameters, data);
        	 typeFile = fileType.ordinal();
	        switch (typeFile) {
			case 0: 
				referencetoreturn = referencetoreturn + ".pdf";
				destFile = destFile + ".pdf";
				HISExportManager.exportReportToPdfFile(jasperPrint, destFile);
				break;
			case 1: 
				referencetoreturn = referencetoreturn + ".rtf";
				destFile = destFile + ".rtf";
				HISExportManager.exportReportToRtfFile(jasperPrint, destFile);
				break;
			case 2: 
				referencetoreturn = referencetoreturn + ".xls";
				destFile = destFile + ".xls";
				HISExportManager.exportReportToExcelFile(jasperPrint, destFile);
				break;
			case 3: 
				referencetoreturn = referencetoreturn + ".html";
				destFile = destFile + ".html";
				
				HISExportManager.exportReportToHtmlFile(jasperPrint, destFile, imagesURI);	
				break;
			case 4: 
				referencetoreturn = referencetoreturn + ".jrpxml";
				destFile = destFile + ".jrpxml";
				HISExportManager.exportReportToXmlFile(jasperPrint, destFile, true);	
				break;
			case 5:
				referencetoreturn = referencetoreturn + ".csv";
				destFile = destFile + ".csv";
				HISExportManager.exportReportToCsvFile(jasperPrint, destFile);
				break;
				
			case 6:
				referencetoreturn = referencetoreturn + ".txt";
				destFile = destFile + ".txt";
				HISExportManager.exportReportToPlainTextFile(jasperPrint, destFile);
				break;

			default:
				break;
			}
	        reportURL= "/resources" + referencetoreturn;
	        String generado = SeamResourceBundle.getBundle().getString("greporte");
	        String condatos = SeamResourceBundle.getBundle().getString("rdatos");
	        String titulo = "  ";
	        
	        if (parameters.containsKey("P_TITULO"))
	        	parameters.get("P_TITULO").toString();

	        String subtitulo = " "; 
	        if (parameters.containsKey("P_SUBTITULO"))	
	        	parameters.get("P_SUBTITULO").toString();
	        String text = generado + titulo + condatos + subtitulo;
	        bitacora.registrarInicioDeAccion(text);
	        
	    	}catch (Exception e){
	    	      System.out.println(e);
	    	      e.printStackTrace();
	    	    }
	    	return reportURL;
	}


	public IActiveModule getActiveModule() {
		return activeModule;
	}


	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	//Graficos
	
	public BufferedImage extractImage(JFreeChart chart, int width, int height) {
	       BufferedImage img =
	               new BufferedImage(width, height,
	               BufferedImage.TYPE_INT_RGB);
	 
	       Graphics2D g2 = img.createGraphics();
	      chart.draw(g2, new Rectangle2D.Double(0, 0, width, height));
	 
	       g2.dispose();
	       return img;
	 
	   }

	
	public JFreeChart createPieChart(DefaultPieDataset pieData){
		JFreeChart pieChart  = ChartFactory.createPieChart("", pieData, true, true, false);
		PiePlot piePlot = (PiePlot)pieChart.getPlot();
		
		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator());
		
		return pieChart;
	}
	
	public JFreeChart createPieChartCustomized(DefaultPieDataset pieData, Map<ChartParameters, Object> parameters){
		JFreeChart pieChart  = ChartFactory.createPieChart("", pieData, true, true, false);
		PiePlot piePlot = (PiePlot)pieChart.getPlot();
		
		if (parameters.get(ChartParameters.PIE_SECTION_LABEL_GENERATOR)!=null)
			piePlot.setLabelGenerator((PieSectionLabelGenerator)parameters.get(ChartParameters.PIE_SECTION_LABEL_GENERATOR));
		else
			piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator());
		
		return pieChart;
	}
	
	public JFreeChart createPie3DChart(DefaultPieDataset pieData){
		JFreeChart pie3DChart  = ChartFactory.createPieChart3D("", pieData, true, true, false);
		PiePlot piePlot = (PiePlot)pie3DChart.getPlot();
		
		
		return pie3DChart;
	}
	public JFreeChart createPie3DChartCustomized(DefaultPieDataset pieData, Map<ChartParameters, Object> parameters){
		JFreeChart pie3DChart  = ChartFactory.createPieChart3D("", pieData, true, true, false);
		PiePlot3D piePlot = (PiePlot3D)pie3DChart.getPlot();
		
		if (parameters.get(ChartParameters.PIE_SECTION_LABEL_GENERATOR)!=null)
			piePlot.setLabelGenerator((PieSectionLabelGenerator)parameters.get(ChartParameters.PIE_SECTION_LABEL_GENERATOR));
		else
			piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator());
		return pie3DChart;
	}
	
	public JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls ){
		JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxisLabel,valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		
		return barChart;
	}
	@SuppressWarnings("deprecation")
	public JFreeChart createBarChartCustomized(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters  ){
		JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxisLabel,valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		CategoryPlot barPlot = barChart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) barPlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) barPlot.getRangeAxis();
		CategoryAxis axis =  barPlot.getDomainAxis();
		
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
		renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
		
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
	    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
			barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
			
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
				barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		
		barPlot.setRenderer(renderer);
		return barChart;
	}
	
	public JFreeChart createBar3DChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls  ){
		JFreeChart barChart = ChartFactory.createBarChart3D(title, categoryAxisLabel,valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		
		return barChart;
	}
	@SuppressWarnings("deprecation")
	public JFreeChart createBar3DChartCustomized(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters  ){
		JFreeChart barChart = ChartFactory.createBarChart3D(title, categoryAxisLabel,valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		CategoryPlot barPlot = barChart.getCategoryPlot();
		BarRenderer3D renderer = (BarRenderer3D) barPlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) barPlot.getRangeAxis();
		CategoryAxis axis =  barPlot.getDomainAxis();
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
		renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
		
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
	    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
			barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
			
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
				barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		
		barPlot.setRenderer(renderer);
		return barChart;
	}
	
	public JFreeChart createStackedBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls ){
		JFreeChart barChart = ChartFactory.createStackedBarChart(title, categoryAxisLabel,valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		
		return barChart;
	}
	@SuppressWarnings("deprecation")
	public JFreeChart createStackedBarChartCustomized(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters  ){
		JFreeChart barChart = ChartFactory.createStackedBarChart(title, categoryAxisLabel,valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		CategoryPlot barPlot = barChart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) barPlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) barPlot.getRangeAxis();
		CategoryAxis axis =  barPlot.getDomainAxis();
		
		if (parameters.get(ChartParameters.HIDEAXISX) !=null)
		numberAxis.setVisible((Boolean)parameters.get(ChartParameters.HIDEAXISX));
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
		renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
		
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
	    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
			barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
			
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
				barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		
		barPlot.setRenderer(renderer);
		return barChart;
	}
	
	public JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, 
			boolean legend, boolean tooltips, boolean urls){
		JFreeChart lineChart = ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		
		
		return lineChart;
	}
	public JFreeChart createLineChartCustomized(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, 
			boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters){
		JFreeChart lineChart = ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
		CategoryPlot linePlot = lineChart.getCategoryPlot();
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) linePlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) linePlot.getRangeAxis();
		CategoryAxis axis =  linePlot.getDomainAxis();
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
		linePlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
		
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
			linePlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
			renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
			
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
		    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.SHAPES_VISIBLE)!=null)
		    renderer.setShapesVisible((Boolean)parameters.get(ChartParameters.SHAPES_VISIBLE));
		
		if (parameters.get(ChartParameters.SHAPES_FILLED)!=null)
		    renderer.setShapesFilled((Boolean)parameters.get(ChartParameters.SHAPES_FILLED));
		
		if (parameters.get(ChartParameters.BASE_LINES_VISIBLE)!=null)
		    renderer.setBaseLinesVisible((Boolean)parameters.get(ChartParameters.BASE_LINES_VISIBLE));
		
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		
		
		
		
		linePlot.setRenderer(renderer);
		
		return lineChart;
	}
	
	
	public JFreeChart createBarLineChartCustomized(String title, String categoryAxisLabel, String barValueAxisLabel, CategoryDataset barDataset,String lineValueAxisLabel, CategoryDataset lineDataset ,PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters  ){
		JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxisLabel,barValueAxisLabel, barDataset, orientation, legend, tooltips, urls);
		CategoryPlot barPlot = barChart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) barPlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) barPlot.getRangeAxis();
		CategoryAxis axis =  barPlot.getDomainAxis();
		
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
		renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
		
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
	    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
			barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
			
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
				barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		
		barPlot.setRenderer(renderer);
		
		
		//combine with LineChart
		
		CategoryPlot categoryplot = (CategoryPlot)barChart.getPlot();
		categoryplot.setDataset(1, lineDataset);
		categoryplot.mapDatasetToRangeAxis(1, 0);
		CategoryAxis categoryaxis = categoryplot.getDomainAxis();
		LineAndShapeRenderer lineandshaperenderer = new LineAndShapeRenderer();
		lineandshaperenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		categoryplot.setRenderer(1, lineandshaperenderer);
		categoryplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
		ChartUtilities.applyCurrentTheme(barChart);
		
		
		
		
		
		return barChart;
	}
	
	
	public JFreeChart crea1teBarLineChartEpidemiologia(String title, String categoryAxisLabel, String barValueAxisLabel, CategoryDataset barDataset,String lineValueAxisLabel, CategoryDataset lineDataset ,PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters  ){
		JFreeChart barChart = ChartFactory.createBarChart(title, categoryAxisLabel,barValueAxisLabel, barDataset, orientation, legend, tooltips, urls);
		CategoryPlot barPlot = barChart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) barPlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) barPlot.getRangeAxis();
		CategoryAxis axis =  barPlot.getDomainAxis();
		
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
		renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
		
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
	    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
			barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
			
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
				barPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		
		barPlot.setRenderer(renderer);
		
		
		//combine with LineChart
		
		CategoryPlot categoryplot = (CategoryPlot)barChart.getPlot();
		categoryplot.setDataset(1, lineDataset);
		categoryplot.mapDatasetToRangeAxis(1, 0);
		CategoryAxis categoryaxis = categoryplot.getDomainAxis();
		LineAndShapeRenderer lineandshaperenderer = new LineAndShapeRenderer();
		lineandshaperenderer.setSeriesPaint(0,Color.BLUE);
		lineandshaperenderer.setSeriesPaint(1,Color.RED);
		lineandshaperenderer.setSeriesPaint(2,Color.GREEN);
		
		lineandshaperenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		categoryplot.setRenderer(1, lineandshaperenderer);
		categoryplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
		ChartUtilities.applyCurrentTheme(barChart);
		
		
		
		
		
		return barChart;
	}
	public JFreeChart createAreaChartEpidemiologia(String title, String categoryAxisLabel, String barValueAxisLabel, CategoryDataset barDataset,String lineValueAxisLabel, CategoryDataset lineDataset ,PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, Map<ChartParameters, Object> parameters  ){
		JFreeChart areaChart = ChartFactory.createAreaChart(title, categoryAxisLabel,barValueAxisLabel, lineDataset, orientation, legend, tooltips, urls);
		CategoryPlot areaPlot = areaChart.getCategoryPlot();
		AreaRenderer renderer = (AreaRenderer) areaPlot.getRenderer();
		final NumberAxis numberAxis  = (NumberAxis) areaPlot.getRangeAxis();
		CategoryAxis axis =  areaPlot.getDomainAxis();
		
		
		if (parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS) !=null)
			axis.setCategoryLabelPositions((CategoryLabelPositions) parameters.get(ChartParameters.CATEGORY_LABEL_POSITIONS));
		
		if (parameters.get(ChartParameters.ITEM_LABELS_VISIBLE)!=null)
		renderer.setItemLabelsVisible((Boolean)parameters.get(ChartParameters.ITEM_LABELS_VISIBLE) );
		
		if (parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE)!=null)
	    renderer.setBaseItemLabelsVisible((Boolean)parameters.get(ChartParameters.BASE_ITEM_LABELS_VISIBLE));
		
		if (parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR)!=null)
			renderer.setItemLabelGenerator((CategoryItemLabelGenerator)parameters.get(ChartParameters.CATEGORY_ITEM_LABEL_GENERATOR) );
		
		if (parameters.get(ChartParameters.TICK_UNIT_SOURCE)!=null)
		numberAxis.setStandardTickUnits((TickUnitSource) parameters.get(ChartParameters.TICK_UNIT_SOURCE));
		
		if (parameters.get(ChartParameters.ROW_ORDER)!=null)
			areaPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.ROW_ORDER));
			
		if (parameters.get(ChartParameters.COLUMN_ORDER)!=null)
				areaPlot.setRowRenderingOrder((SortOrder)parameters.get(ChartParameters.COLUMN_ORDER));
		
		if (parameters.get(ChartParameters.ITEM_LABEL_POSITIONS)!=null){
			renderer.setBaseNegativeItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
			renderer.setBasePositiveItemLabelPosition((ItemLabelPosition)parameters.get(ChartParameters.ITEM_LABEL_POSITIONS));
		}
		renderer.setSeriesPaint(0,Color.BLUE);
		renderer.setSeriesPaint(1,Color.RED);
		renderer.setSeriesPaint(2,Color.GREEN);		
		
		areaPlot.setRenderer(renderer);
		
		
		
		
		return areaChart;
	}
	
	 public Object[] addSubReport(String nombre,Map parametros,Collection listaDatos){
		 JasperReport subCompilado =CompileReport(nombre);
		 JRBeanCollectionDataSource data = new JRBeanCollectionDataSource(listaDatos);

		 return new Object[]{subCompilado,parametros,data};
	 }

	
}
