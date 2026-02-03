package gehos.ensayo.ensayo_estadisticas.reporteRecoleccionDatosEstudios;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_estadisticas.entity.Ensayo;
import gehos.ensayo.ensayo_estadisticas.entity.Entidad;
import gehos.ensayo.ensayo_estadisticas.entity.GrupoSujeto;
import gehos.ensayo.ensayo_estadisticas.entity.Pais;
import gehos.ensayo.ensayo_estadisticas.entity.Provincia;
import gehos.ensayo.ensayo_estadisticas.entity.Sujeto;
import gehos.ensayo.ensayo_estadisticas.entity.SujetoGeneral;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.Estado_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.Nacion_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.ReCumplimientoEstudios_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import net.sf.dynamicreports.report.builder.component.TotalPagesBuilder;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("unchecked")
@Name("reporteRecoleccionDatosEstudios")
@Scope(ScopeType.CONVERSATION)
public class ReporteRecoleccionDatosEstudios{
	
	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	//List<SujetoGeneral> sujetos;
	List<DatoEstudio> datosEstudios; 

	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	//private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteRecoleccionDatosEstudios;
	private String nombreReport;
	private String pathExportedReport = "";

	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	
	private List<Estudio_ensayo> listaEstudioEntidad;
	private List<String> listarEstudios;
	private String estudio;
	private String fileformatToExport;
	private List<String> filesFormatCombo;


	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}
	
	private List<ReCumplimientoEstudios_ensayo> listaCumpEst;

	//RF6 - Generar reporte cumplimiento de la recolección de datos por sitio
	public void reporteRecoleccionDatosEstudios()	{
		style = 1;
		datosEstudios = new ArrayList<DatoEstudio>();
		listaCumpEst = (List<ReCumplimientoEstudios_ensayo>)entityManager.createQuery("select cumplEst from ReCumplimientoEstudios_ensayo cumplEst").getResultList();
		for (int i = 0; i < listaCumpEst.size(); i++) {
			DatoEstudio datoEstudio = new DatoEstudio(listaCumpEst.get(i).getNombreEstudio(), listaCumpEst.get(i).getCantSujetos(), listaCumpEst.get(i).getPlanTotalMSProgramados(),listaCumpEst.get(i).getRealFirmados(), listaCumpEst.get(i).getFaltanFirmar(), String.valueOf(listaCumpEst.get(i).getPorcientoCumplimiento()+"%"),this.style.toString());
			datosEstudios.add(datoEstudio);
		}
		
		reporteRecoleccionDatosEstudios=new HashMap();
		reporteRecoleccionDatosEstudios.put("nombreEstudio", SeamResourceBundle.getBundle().getString("nombreEstudio"));
		reporteRecoleccionDatosEstudios.put("totalIncluidos",SeamResourceBundle.getBundle().getString("totalIncluidos"));
		reporteRecoleccionDatosEstudios.put("planMomentosProgramados", SeamResourceBundle.getBundle().getString("planMomentosProgramados"));
		reporteRecoleccionDatosEstudios.put("crdFirmada", SeamResourceBundle.getBundle().getString("crdFirmada"));
		reporteRecoleccionDatosEstudios.put("crdFalta", SeamResourceBundle.getBundle().getString("crdFalta"));	
		reporteRecoleccionDatosEstudios.put("porcientoCumplimiento", SeamResourceBundle.getBundle().getString("porcientoCumplimiento"));
		nombreReport=reportManager.ExportReport("reporteRecoleccionDatosEstudios", reporteRecoleccionDatosEstudios, datosEstudios, FileType.HTML_FILE);
		flag=true;
		flag2=false;
		
		this.estudio = "";
		this.fechaInicio=null;
		this.fechaFin=null;
		
	}
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport(
					"reporteRecoleccionDatosEstudios", reporteRecoleccionDatosEstudios, datosEstudios,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reporteRecoleccionDatosEstudios", reporteRecoleccionDatosEstudios, datosEstudios,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reporteRecoleccionDatosEstudios", reporteRecoleccionDatosEstudios, datosEstudios,
					FileType.EXCEL_FILE);
		}

	}
	
	public Date resetFecha(){
		Date fecha;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)); //Establecer el año actual
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)); //Establecer el mes actual
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)); //Establecer el día predeterminado
		fecha = calendar.getTime(); //Asignar la fecha predeterminada a la variable Date
		return fecha;
	}

	public String getNombreReport() {
		return nombreReport;
	}

	public void setNombreReport(String nombreReport) {
		this.nombreReport = nombreReport;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
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
		filesFormatCombo = reportManager.fileFormatsToExport();
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Boolean getFlag2() {
		return flag2;
	}

	public void setFlag2(Boolean flag2) {
		this.flag2 = flag2;
	}

	public String getNoResult() {
		return noResult;
	}

	public void setNoResult(String noResult) {
		this.noResult = noResult;
	}

	public List<String> getListarEstudios() {
		return listarEstudios;
	}

	public void setListarEstudios(List<String> listarEstudios) {
		this.listarEstudios = listarEstudios;
	}

	public String getEstudio() {
		return estudio;
	}

	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public List<Estudio_ensayo> getListaEstudioEntidad() {
		return listaEstudioEntidad;
	}

	public void setListaEstudioEntidad(List<Estudio_ensayo> listaEstudioEntidad) {
		this.listaEstudioEntidad = listaEstudioEntidad;
	}

	/*public List<SujetoGeneral> getSujetos() {
		return sujetos;
	}

	public void setSujetos(List<SujetoGeneral> sujetos) {
		this.sujetos = sujetos;
	}*/

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

}