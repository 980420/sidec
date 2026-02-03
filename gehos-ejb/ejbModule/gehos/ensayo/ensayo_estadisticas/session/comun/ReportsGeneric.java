package gehos.ensayo.ensayo_estadisticas.session.comun;

import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionesManager;
import gehos.ensayo.ensayo_estadisticas.session.comun.Utiles_estadisticas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

public abstract class ReportsGeneric {
	
	protected @In EntityManager entityManager;
	protected @In(create=true) Utiles_estadisticas utiles_estadisticas;
	protected @In(create=true) IActiveModule activeModule;
	protected @In(create=true) UbicacionesManager ubicacionesManager;
	protected @In(create=true) ReportManager reportManager;
	protected @In FacesMessages facesMessages;
	
	protected String querySelect;
	protected String queryFrom;
	protected String queryWhere;
	protected String queryOrder;
	protected String query;
	protected Map<Integer, Object>  columnas;
	protected Map<String,Object> queryParameters;
	
	protected String tituloReporte;
	protected String subtituloReporte;
	protected String pathExportedReport;
	protected String fileformatToExport;
	protected List<String> filesFormatCombo;
	protected String pathToReport;
	protected Map<String,Object> parametros;
	protected List<Object> lista;
	protected String template;
	protected String seleccione=SeamResourceBundle.getBundle().getString("seleccione");
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport(template, parametros, lista,FileType.PDF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(template, parametros, lista,FileType.RTF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(template, parametros, lista,FileType.EXCEL_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(3))) {
			pathExportedReport = reportManager.ExportReport(template, parametros, lista,FileType.XML_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(4))) {
			pathExportedReport = reportManager.ExportReport(template, parametros, lista,FileType.CSV_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(5))) {
			pathExportedReport = reportManager.ExportReport(template, parametros, lista,FileType.PLAIN_TEXT_FILE);
		}
	}

	public ReportManager getReportManager() {
		return reportManager;
	}

	public void setReportManager(ReportManager reportManager) {
		this.reportManager = reportManager;
	}

	public String getPathExportedReport() {
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}

	public String getFileformatToExport() {
		return fileformatToExport;
	}

	public void setFileformatToExport(String fileformatToExport) {
		this.fileformatToExport = fileformatToExport;
	}

	public List<String> getFilesFormatCombo() {
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}

	public String getPathToReport() {
		return pathToReport;
	}

	public void setPathToReport(String pathToReport) {
		this.pathToReport = pathToReport;
	}

	public Map<String,Object> getParametros() {
		return parametros;
	}

	public void setParametros(Map<String,Object> parametros) {
		this.parametros = parametros;
	}

	public List<Object> getLista() {
		return lista;
	}

	public void setLista(ArrayList<Object> lista) {
		this.lista = lista;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public boolean empty(String valor){
		return (valor==null || valor.equals(""));
	}

	public String getTituloReporte() {
		return tituloReporte;
	}

	public void setTituloReporte(String tituloReporte) {
		this.tituloReporte = tituloReporte;
	}

	public String getSubtituloReporte() {
		return subtituloReporte;
	}

	public void setSubtituloReporte(String subtituloReporte) {
		this.subtituloReporte = subtituloReporte;
	}

	public void setLista(List<Object> lista) {
		this.lista = lista;
	}

	
	public Map<Integer, Object> getColumnas() {
		this.buildColumns();
		return columnas;
	}

	public void setColumnas(Map<Integer, Object> columnas) {
		this.columnas = columnas;
	}
	
	public Map<String, Object> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(Map<String, Object> queryParameters) {
		this.queryParameters = queryParameters;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	//Metodos abstractos a implementar en cada reporte
	public abstract void buildColumns();
	public abstract void GenerarReporte();
	public abstract void buildQuery();
	public abstract List<Object> getResultadosFormateados(List<Object> resultados);
}
