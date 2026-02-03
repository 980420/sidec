package gehos.comun.updater;

import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("keysReport")
@Scope(ScopeType.CONVERSATION)
public class KeysReport {

	@In(value = "#{remoteAddr}", required = false)
	private String ipString;
	@In
	private RulesParser rulesParser;	
	
	public boolean allowed(){
		RuleBase ruleBase = null;
		try {
			ruleBase = rulesParser.readRule(
					"/comun/KeysIpAllowed.drl",
					RulesDirectoryBase.business_rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IpKey ip = new IpKey();
		ip.setIp(ipString);
		StatefulSession session = ruleBase.newStatefulSession();
		session.insert(ip);
		session.fireAllRules();
		return ip.isAllowed();
	}
    
    private List<KeyRow> keys(Date desde, Date hasta){
    	List<Date> dates = new ArrayList<Date>();
    	Date desdeAux = new Date(desde.getTime());
    	while(desdeAux.getTime() <= hasta.getTime()){
    		dates.add(new Date(desdeAux.getTime()));
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(desdeAux);
    		calendar.add(Calendar.DAY_OF_MONTH, 1);
    		desdeAux = calendar.getTime();
    	}
    	List<KeyRow> result = new ArrayList<KeyRow>();
    	 SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    	for (Date date : dates) {
    		String[] keysValues = KeysGenerator.keys(date);        	
    		result.add(new KeyRow(keysValues[0], keysValues[1], keysValues[2], keysValues[3],
        			keysValues[4], keysValues[5], keysValues[6], keysValues[7], format.format(date), "1"));
    		result.add(new KeyRow(keysValues[8], keysValues[9], keysValues[10], keysValues[11],
        			keysValues[12], keysValues[13], keysValues[14], keysValues[15], format.format(date), "2"));
    		result.add(new KeyRow(keysValues[16], keysValues[17], keysValues[18], keysValues[19],
        			keysValues[20], keysValues[21], keysValues[22], keysValues[23], format.format(date), "3"));
    		result.add(new KeyRow(keysValues[24], keysValues[25], keysValues[26], keysValues[27],
        			keysValues[28], keysValues[29], keysValues[30], keysValues[31], format.format(date), "4"));
		}
    	return result;    	    	
    }
    
    private Date desde;
	private Date hasta;
    
    @In(create = true, value = "reportManager")
	ReportManager reportManager;
	private Map parameters  =  new  HashMap();
	
	private String pathExportedReport;
	private String fileformatToExport;
	
	public String exportAccion(){
		if(pathExportedReport == null)
			return "return false;";
		if(!pathExportedReport.equals("")){			
			return "window.open('" + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + 
				pathExportedReport + "'); Richfaces.hideModalPanel('exportPanel')";
		}
		return "return false;";
	}
	
	public void export(){
		pathExportedReport = "";
		pathExportedReport = reportManager.ExportReportConfiguracion("Keys", parameters, keys(desde, hasta), 
				FileType.HTML_FILE);
	}
	
	public void exportReportToFileFormat(){
		pathExportedReport = "";
		if (fileformatToExport.equals(reportManager.fileFormatsToExport().get(0))){
			pathExportedReport = reportManager.ExportReportConfiguracion("Keys", parameters, keys(desde, hasta), 
					FileType.PDF_FILE);
		}
		else if (fileformatToExport.equals(reportManager.fileFormatsToExport().get(1))){
			pathExportedReport = reportManager.ExportReportConfiguracion("Keys", parameters, keys(desde, hasta), 
					FileType.RTF_FILE);
		}
		else if (fileformatToExport.equals(reportManager.fileFormatsToExport().get(2))){
			pathExportedReport = reportManager.ExportReportConfiguracion("Keys", parameters, keys(desde, hasta), 
					FileType.EXCEL_FILE);
		}
	}
	
	public List<String> getFilesFormatCombo() {
		return reportManager.fileFormatsToExport();
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
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
	
	
	
}
