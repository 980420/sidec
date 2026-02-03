package gehos.ensayo.ensayo_disenno.session.reglas;

import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;

public class ReglasExport {

	private final String template = "reportListadoReglas.jrxml";
	private List<ReglaWrapper> reglas;
	private Map<String, String> parametros;
	private List<String> subReports;
	
	private List<String> filesFormatCombo; 
	private String fileformatToExport; 
	private String pathExportedReport; 
    private String reglaNombre;
    private String variableNombre;
    private String mensajeRegla;
    private String ifPart;
    private String nombreHoja;

	@In ReportManager reportManager; 
	public ReglasExport(String reglaNombre, String variableNombre, String ifPart, String mensajeRegla,String nombreHoja) {
        this.reglaNombre = reglaNombre;
        this.variableNombre = variableNombre;
        this.ifPart = ifPart;
        this.mensajeRegla = mensajeRegla;
        this.nombreHoja = nombreHoja;
        
        this.filesFormatCombo =new ArrayList<String>(); 
    	this.filesFormatCombo.add("PDF");
    	this.filesFormatCombo.add("WORD");
    	this.filesFormatCombo.add("EXCEL");
    }
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportListadoReglas", parametros, reglas, FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportListadoReglas", parametros, reglas, FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportListadoReglas", parametros, reglas, FileType.EXCEL_FILE);
		}
	}
	
	public String getReglaNombre() {
		return reglaNombre;
	}

	public void setReglaNombre(String reglaNombre) {
		this.reglaNombre = reglaNombre;
	}
	
	public String getnombreHoja() {
		return nombreHoja;
	}

	public void setnombreHoja(String nombreHoja) {
		this.nombreHoja = nombreHoja;
	}

	public String getVariableNombre() {
		return variableNombre;
	}

	public void setVariableNombre(String variableNombre) {
		this.variableNombre = variableNombre;
	}

	public String getMensajeRegla() {
		return mensajeRegla;
	}

	public void setMensajeRegla(String mensajeRegla) {
		this.mensajeRegla = mensajeRegla;
	}
	public String getifPart() {
        return ifPart;
    }

    public void setifPart(String ifPart) {
        this.ifPart = ifPart;
    }
	public String getFileformatToExport() { 
		return fileformatToExport; 
	} 
 
	public void setFileformatToExport(String fileformatToExport) { 
		this.fileformatToExport = fileformatToExport; 
	} 
 
	public String getPathExportedReport() { 
		return pathExportedReport; 
	} 
 
	public void setPathExportedReport(String pathExportedReport) { 
		this.pathExportedReport = pathExportedReport; 
	} 

	
}