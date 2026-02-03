package gehos.ensayo.ensayo_estadisticas.session.reporte_causas;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma.MSNoProgramado;
import gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma.MSProgramado;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Scope(ScopeType.CONVERSATION)
@Name("reporteCausas")
public class ReporteCausas {
	@In IActiveModule activeModule;	
	@In Usuario user;

	@In EntityManager entityManager;	
	@In	FacesMessages facesMessages;
	final String seleccione = SeamResourceBundle.getBundle().getString("lbl_seleccione_ens"); 

	@In	ReportManager reportManager;
	//@In SeguridadEstudio seguridadEstudio;
	//@In IBitacora bitacora;


	private List<String> filesFormatCombo;
	private String fileformatToExport;
	private String pathExportedReport;
	private String pathToReport;
	private String reportName;
	//private Estudio_ensayo estudio;
	//private Entidad_ensayo entidad;
	private List<Causa_ensayo> listadoCausas;
	private List<CausaEnsayo> listadoCausasEnsayo;
	private Date fechaInicio;
	private Date fechaFin;
	private Map pars;

	private boolean inicioConversacion=false;


	public void inicializar(){	
		fileformatToExport="";
		pathExportedReport="";
		pathToReport="";
		//this.estudio = seguridadEstudio.getEstudioEntidadActivo().getEstudio();		
		//this.entidad = entityManager.find(Entidad_ensayo.class,
		//activeModule.getActiveModule().getEntidad().getId());
		listadoCausas = new ArrayList<Causa_ensayo>();
		listadoCausasEnsayo = new ArrayList<CausaEnsayo>();
		reportName = "reportCausas";
		filesFormatCombo =  reportManager.fileFormatsToExport();
		inicioConversacion=true;

	}
	public void generarReporte(){
		try {
			listadoCausas=new ArrayList<Causa_ensayo>();
			listadoCausasEnsayo=new ArrayList<CausaEnsayo>();
			listadoCausas = entityManager.createQuery("select causa from Causa_ensayo causa "
					+ "where causa.fecha between :fechaInicio and :fechaFin").setParameter("fechaInicio", fechaInicio)
					.setParameter("fechaFin", fechaFin).getResultList();
			for(Causa_ensayo causa:listadoCausas){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String nombreUsuario = "";
				String nombreEstudio = "";
				String nombreGrupo = "";
				String nombreSujeto = "";
				String nombreMomento = "";
				String nombreCrd = "";
				if(causa.getUsuario()!=null)
					nombreUsuario = causa.getUsuario().getNombre();
				if(causa.getEstudio()!=null)
					nombreEstudio = causa.getEstudio().getNombre();
				if(causa.getCronograma()!=null)
					nombreGrupo = causa.getCronograma().getGrupoSujetos().getNombreGrupo();
				if(causa.getSujeto()!=null)
					nombreSujeto = causa.getSujeto().getCodigoPaciente();	
				if(causa.getMomentoSeguimientoEspecifico()!=null)
					nombreMomento = causa.getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre();
				if(causa.getCrdEspecifico()!=null)
					nombreCrd = causa.getCrdEspecifico().getHojaCrd().getNombreHoja();
				String descripcion = causa.getDescripcion();
				String tipoCausa = causa.getTipoCausa();	
				String fecha = sdf.format(causa.getFecha()); 
				listadoCausasEnsayo.add(new CausaEnsayo(nombreUsuario, nombreEstudio, nombreGrupo, nombreSujeto, nombreMomento, nombreCrd, descripcion, tipoCausa, fecha));
			}

			

			if(listadoCausasEnsayo.size()>0){
				pars = new HashMap();
				pars.put("P_TITULO", SeamResourceBundle.getBundle().getString("lbl_titulo_ens"));
				pars.put("listadoCausas",  SeamResourceBundle.getBundle().getString("prm_listadoCausas_ens"));
				pars.put("nombreUsuario", SeamResourceBundle.getBundle().getString("lbl_usuario_ens"));
				pars.put("nombreEstudio", SeamResourceBundle.getBundle().getString("lbl_estudio_ens"));
				pars.put("nombreGrupo", SeamResourceBundle.getBundle().getString("lbl_grupo_ens"));
				pars.put("nombreSujeto", SeamResourceBundle.getBundle().getString("lbl_sujeto_ens"));
				pars.put("nombreMomento", SeamResourceBundle.getBundle().getString("lbl_momento_ens"));
				pars.put("nombreCrd", SeamResourceBundle.getBundle().getString("lbl_crd_ens"));
				pars.put("descripcion", SeamResourceBundle.getBundle().getString("lbl_descripcion_ens"));
				pars.put("tipoCausa", SeamResourceBundle.getBundle().getString("lbl_tipoCausa_ens"));
				pars.put("fecha", SeamResourceBundle.getBundle().getString("lbl_fecha_ens"));
				
				pathToReport = reportManager.ExportReport(reportName, pars, listadoCausasEnsayo, FileType.HTML_FILE);
			}
			else
				pathToReport = "noResult";

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]);
		}
		
		this.fechaInicio=null;
		this.fechaFin=null;
	}

	public void exportReportToFileFormat(){
		if(this.fileformatToExport != null && !this.fileformatToExport.isEmpty()){

		if (fileformatToExport.equals(filesFormatCombo.get(0))){

			pathExportedReport = reportManager.ExportReport(reportName, pars, listadoCausasEnsayo, FileType.PDF_FILE);
		}
		else if (fileformatToExport.equals(filesFormatCombo.get(1))){
			pathExportedReport = reportManager.ExportReport(reportName, pars, listadoCausasEnsayo, FileType.RTF_FILE);
		}
		else if (fileformatToExport.equals(filesFormatCombo.get(2))){
			pathExportedReport = reportManager.ExportReport(reportName, pars, listadoCausasEnsayo, FileType.EXCEL_FILE);
		}

		}
		this.fileformatToExport = null;

	}
	public List<String> getFilesFormatCombo() {
		return filesFormatCombo;
	}
	public String getFileformatToExport() {
		return fileformatToExport;
	}
	public String getPathExportedReport() {
		return pathExportedReport;
	}
	public String getPathToReport() {
		return pathToReport;
	}
	public String getReportName() {
		return reportName;
	}
	public List<Causa_ensayo> getListadoCausas() {
		return listadoCausas;
	}
	public List<CausaEnsayo> getListadoCausasEnsayo() {
		return listadoCausasEnsayo;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public Map getPars() {
		return pars;
	}
	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}
	public void setFileformatToExport(String fileformatToExport) {
		if(fileformatToExport == null || fileformatToExport.isEmpty() || fileformatToExport.trim().equals(seleccione))
			fileformatToExport = null;
		this.fileformatToExport = fileformatToExport;
	}
	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}
	public void setPathToReport(String pathToReport) {
		this.pathToReport = pathToReport;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public void setListadoCausas(List<Causa_ensayo> listadoCausas) {
		this.listadoCausas = listadoCausas;
	}
	public void setListadoCausasEnsayo(List<CausaEnsayo> listadoCausasEnsayo) {
		this.listadoCausasEnsayo = listadoCausasEnsayo;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public void setPars(Map pars) {
		this.pars = pars;
	}
	public boolean isInicioConversacion() {
		return inicioConversacion;
	}
	public void setInicioConversacion(boolean inicioConversacion) {
		this.inicioConversacion = inicioConversacion;
	}
}
