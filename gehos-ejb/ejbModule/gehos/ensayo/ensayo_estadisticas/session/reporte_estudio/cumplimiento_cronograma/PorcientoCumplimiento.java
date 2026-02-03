package gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma.wrapper.SubjectPercentage;
import gehos.ensayo.ensayo_estadisticas.utils.EasyQuery;
import gehos.ensayo.entity.Entidad_ensayo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.theme.ThemeSelector;

@Name("porcientoComplimiento")
@Scope(ScopeType.CONVERSATION)
public class PorcientoCumplimiento implements ExportInterface
{
	@In
	EntityManager entityManager;
	@In
	ReportManager reportManager;

	@In EasyQuery easyQuery;
	
	@In IBitacora bitacora;
	
	@In IActiveModule activeModule;
	
	@In Usuario user;
	
	@In
	ThemeSelector themeSelector;

	private String exportFormat;
	private List<String> availableFormats;
	private String pathExportedReport;
	private String reportName;

	private Map pars;
	private List listToReport;
	private int results;

	//BEGIN----report specific data
	String selectedCenter;
	List<String> allCenters;

	//END ----report specific data
	List<SubjectPercentage> list;
	
	public void previewReport()
	{
		list = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");		
		String now = sdf.format(new Date());
		
		for(int i=1; i<=32;i++)
		{
			String name = String.format("Subject %d", i);
			String study = String.format("Study %d", i);
			String datestart = now;
			String dateend = now;
			String momentcount = String.valueOf(1);
			String percent = String.format("%d", 100-i);	
			SubjectPercentage sp = new SubjectPercentage(name, study, datestart, dateend, momentcount, percent);
			list.add(sp);
		}
		
		SubjectPercentProvider spProvider = new SubjectPercentProvider(entityManager);
		listToReport = spProvider.getAll(0);
		list = listToReport;
		results = list.size();
		
		pars = new HashMap();		
		
		String themeName = themeSelector.getTheme();
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
	
		String entityLogo = activeModule.getActiveModule().getEntidad().getLogo();
		String relativePath = String.format("/resources/modCommon/entidades_logos/alas-layout/green/%s", entityLogo);
		String centerLogoPath = context.getRealPath(relativePath);
		String absolutePath = "/resources/modCommon/appLogos/logo.png";
		absolutePath = context.getRealPath(absolutePath);
		InputStream IsSystemLogo = null;
		InputStream IsCenterLogo = null;
		try 
		{
			IsCenterLogo = new FileInputStream(centerLogoPath);
			IsSystemLogo = new FileInputStream(absolutePath);
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		
		pars.put("module_name", activeModule.getActiveModule().getNombre());
		pars.put("user_gen", user.getUsername());		
		pars.put("date_gen", now);
		pars.put("footer_page", "P\u00C1gina");
		pars.put("P_TITULO", "Porciento de cumplimiento del cronograma espec\u00EDfico");
		pars.put("header_name", "Sujeto");
		pars.put("header_study", "Estudio");
		pars.put("header_date_start", "Fecha inicio");
		pars.put("header_date_end", "Fecha fin");
		pars.put("header_count", "Cantidad MS");
		pars.put("header_percent", "%");
		
		pars.put("center_logo", centerLogoPath);
		pars.put("system_logo", absolutePath);
		pars.put("total_pages", String.valueOf(getReportTotalPages(list.size())));
		
		
		
		
		
		pathExportedReport = reportManager.ExportReport(reportName, pars, list, FileType.HTML_FILE);		
	}
	
	private int getReportTotalPages(int results)
	{
		int quotient  = results/31;
		int remainder = results%31;
		
		return results==0 ? 1:(remainder>0 ? quotient+1 : quotient); 
	}
	
	public void print()
	{
		
	}
	public void cancel()
	{
		
	}
	
	public String buidExportAction()
	{
		if(pathExportedReport == null)
			return "return false;";
		if(!pathExportedReport.equals("")){			
			return "window.open('" + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + 
					pathExportedReport + "'); Richfaces.hideModalPanel('exportPanel')";
		}
		return "return false;";
	}

	@Create
	@Begin(flushMode= FlushModeType.MANUAL, join=true)
	public void initialization()
	{
		reportName = "porciento_cumpl";
		availableFormats =  reportManager.fileFormatsToExport();
		allCenters = easyQuery.selectField(Entidad_ensayo.class, "nombre", String.class);		

	}


	//BEGIN----methods override
	@Override
	public void exportReportToFileFormat() 
	{
		pathExportedReport = "";

		if (exportFormat.equals(availableFormats.get(0))){
			pathExportedReport = reportManager.ExportReport(reportName, pars, listToReport, FileType.PDF_FILE);
		}
		else if (exportFormat.equals(availableFormats.get(1))){
			pathExportedReport = reportManager.ExportReport(reportName, pars, listToReport, FileType.RTF_FILE);
		}
		else if (exportFormat.equals(availableFormats.get(2))){
			pathExportedReport = reportManager.ExportReport(reportName, pars, listToReport, FileType.EXCEL_FILE);
		}

	}
	@Override
	public String getFileformatToExport() 
	{
		return exportFormat;
	}
	@Override
	public void setFileformatToExport(String fileformatToExport) 
	{
		this.exportFormat = exportFormat;
	}
	@Override
	public List<String> getFilesFormatCombo() {		
		return availableFormats;
	}
	@Override
	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.availableFormats = availableFormats;

	}
	@Override
	public String getPathExportedReport() {		
		return pathExportedReport;
	}
	@Override
	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;

	}
	//END----methods override

	public String getSelectedCenter() {
		return selectedCenter;
	}

	public void setSelectedCenter(String selectedCenter) {
		this.selectedCenter = selectedCenter;
	}

	public List<String> getAllCenters() {
		return allCenters;
	}

	public void setAllCenters(List<String> allCenters) {
		this.allCenters = allCenters;
	}

	public int getResults() {
		return results;
	}

	public void setResults(int results) {
		this.results = results;
	}

	public String getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
	}





}
