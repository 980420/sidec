package gehos.ensayo.ensayo_estadisticas.cantMSxSujeto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_extraccion.session.TrazasControladoraSource;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.session.common.auto.CrdEspecificoList_ensayo;
import gehos.ensayo.session.common.auto.MomentoSeguimientoEspecificoList_ensayo;

import org.ejbca.cvc.example.Parse;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.theme.Theme;

@Scope(ScopeType.CONVERSATION)
@Name("cMSxSujeto")
public class cantMSxSujeto {

	@In
	private EntityManager entityManager;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	@In
	gehos.autenticacion.entity.Usuario user;

	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;

	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;

	private String gruSujetos;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	private String pathToReport;
	private Boolean flag = false;
	private Boolean flag2 = true;
	private Map map;
	private String fileformatToExport;
	private String pathExportedReport = "";
				
	List<cantMSxSujetoSource> listaCantMSxS = new ArrayList<cantMSxSujetoSource>(); 
	List<cantMSxSujetoSource> listaTemp = new ArrayList<cantMSxSujetoSource>();
    private List<String> filesFormatCombo;
	
	
	public String getPathToReport() {
		return pathToReport;
	}

	public void setPathToReport(String pathToReport) {
		this.pathToReport = pathToReport;
	}

	public String getNoResult() {
		return noResult;
	}

	public void setNoResult(String noResult) {
		this.noResult = noResult;
	}

	public String getGruSujetos() {
		return gruSujetos;
	}

	public void setGruSujetos(String gruSujetos) {
		this.gruSujetos = gruSujetos;
	}
	
	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public Boolean getFlag2() {
		return flag2;
	}

	public void setFlag2(Boolean flag2) {
		this.flag2 = flag2;
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
	
	public List<String> getFilesFormatCombo() {
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}
	
	
	// Listado de los grupo sujetos por los que se puede buscar
	private List<String> gSujetosDisp = new ArrayList<String>();

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
		filesFormatCombo =  reportManager.fileFormatsToExport();
		
	}

	@End
	public void end() {
	}

	// llenar grupo sujetos que se puede buscar
	public List<String> llenarGSujetos() 
	{
		gSujetosDisp = entityManager
				.createQuery(
						"select distinct g.nombreGrupo from GrupoSujetos_ensayo g where g.eliminado = false and g.estudio.id =:estudio and g.nombreGrupo <> 'Grupo Validación'")
				.setParameter(
						"estudio",seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
		return gSujetosDisp;
	}
	
	
	public void Generar()
	{
		noResult = SeamResourceBundle.getBundle().getString(
				"noResult2");
		 if(gruSujetos!="")
		 {
			 
			 pathToReport = "";
			 listaCantMSxS.clear();
			
			   		 
			 ArrayList<Object[]> reportePrint = new ArrayList();
			 
			 if(seguridadEstudio.getEstudioEntidadActivo().getEntidad().getTipoEntidad().getId() == 7)
			 {
				 reportePrint = (ArrayList<Object[]>)(entityManager.createQuery("select s.codigoPaciente, gs.nombreGrupo, msg.nombre, str(count(s.id)) "
					 		+ "from MomentoSeguimientoEspecifico_ensayo mse "
					 		+ "inner join mse.momentoSeguimientoGeneral msg inner join mse.estadoMomentoSeguimiento ems inner join mse.sujeto s  inner join s.grupoSujetos gs "
					 		+ "where mse.id in(select distinct msEsp.id from VariableDato_ensayo vd inner join vd.crdEspecifico crdE inner join crdE.momentoSeguimientoEspecifico msEsp "
					 		+ "where msEsp.sujeto.grupoSujetos.nombreGrupo =:gruSujetos) "
					 		+ "and s.eliminado = FALSE and gs.nombreGrupo =:gruSujetos group by s.codigoPaciente, msg.id, gs.id order by msg.nombre")
					 		.setParameter("gruSujetos", gruSujetos)).getResultList();
					 
			 }
			 else
			 {
				 reportePrint = (ArrayList<Object[]>)(entityManager.createQuery("select s.codigoPaciente, gs.nombreGrupo, msg.nombre, str(count(s.id)) "
					 		+ "from MomentoSeguimientoEspecifico_ensayo mse "
					 		+ "inner join mse.momentoSeguimientoGeneral msg inner join mse.estadoMomentoSeguimiento ems inner join mse.sujeto s  inner join s.grupoSujetos gs "
					 		+ "where mse.id in(select distinct msEsp.id from VariableDato_ensayo vd inner join vd.crdEspecifico crdE inner join crdE.momentoSeguimientoEspecifico msEsp "
					 		+ "where msEsp.sujeto.entidad.id =:idEntidad and msEsp.sujeto.grupoSujetos.nombreGrupo =:gruSujetos) "
					 		+ "and s.eliminado = FALSE and gs.nombreGrupo =:gruSujetos and mse.sujeto.entidad.id =:idEntidad group by s.codigoPaciente, msg.id, gs.id order by msg.nombre")
					 		.setParameter("gruSujetos", gruSujetos)).setParameter("idEntidad", seguridadEstudio.getEstudioEntidadActivo().getEntidad().getId()).getResultList();
			 }
			 
			 			 
			 if(reportePrint.size()>0)
			 {
				 String gsNombre=reportePrint.get(0)[1].toString();
				 String usr = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
				 
				 
				 for(int i = 0; i < reportePrint.size(); i++)
				 {
					 
					 String cdPaciente = reportePrint.get(i)[0].toString();
					 String msgNombre = reportePrint.get(i)[2].toString();
					 String contador = reportePrint.get(i)[3].toString();
					
					 
					 cantMSxSujetoSource aux = new cantMSxSujetoSource(cdPaciente,msgNombre,contador);
					 
					 
					 
					 listaCantMSxS.add(aux);
					 listaTemp.add(aux);
					 
				 }
				 
				 map = new HashMap();
				 map.put("g_sujeto",gsNombre);
				 map.put("usuario",usr);
				 map.put("sujetos", SeamResourceBundle.getBundle().getString("sujetos"));
				 map.put("msg", SeamResourceBundle.getBundle().getString("msg"));
				 map.put("gs", SeamResourceBundle.getBundle().getString("gs"));
				 map.put("cantMS", SeamResourceBundle.getBundle().getString("cantMS"));
				 map.put("grupoS", SeamResourceBundle.getBundle().getString("grupoS"));
				 map.put("cant", SeamResourceBundle.getBundle().getString("cant"));
				 //map.put("nombreEstudio", SeamResourceBundle.getBundle().getString("nombreEstudio"));
				 map.put("nombreEstudio", this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getNombre());
				 
				 pathToReport= reportManager.ExportReport("reportMSxSSSS", map, listaCantMSxS, FileType.HTML_FILE);
				
				 flag=true;
				 flag2=false;
				 
				}
			 else{
			     flag = false;
			     flag2 = true;
			     }
			 
		 }
		 else
		 {
			 noResult = SeamResourceBundle.getBundle().getString(
						"noResult3");
			 flag2 = true;
		 }
	}
	
	
	public String exportAccion(){
		exportReportToFileFormat();
		if(pathExportedReport == null)
			return "return false;";
		if(!pathExportedReport.equals("")){
			listaTemp.clear();			
			fileformatToExport = "";
			return "window.open('" + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + 
				pathExportedReport + "'); Richfaces.hideModalPanel('trazaExportPanel')";			
		}
		return "return false;";
	}
	
	
	public void exportReportToFileFormat(){
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportMSxSSSS", map, listaCantMSxS,FileType.PDF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportMSxSSSS", map, listaCantMSxS,FileType.RTF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportMSxSSSS", map, listaCantMSxS,FileType.EXCEL_FILE);
		}
	
	}
	
	
	

}
