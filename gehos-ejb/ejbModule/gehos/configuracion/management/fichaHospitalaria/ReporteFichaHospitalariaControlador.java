package gehos.configuracion.management.fichaHospitalaria;

import java.util.*;

import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.management.entity.Entidad_configuracion;

import javax.persistence.EntityManager;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("reporteFichaHospitalariaControlador")
@Scope(ScopeType.CONVERSATION)
public class ReporteFichaHospitalariaControlador {

	@SuppressWarnings("unchecked")
	Map pars;// Valores de las columnas o etiquetas
	private Entidad_configuracion entidad = new Entidad_configuracion();
	private String entidadSeleccionada = "";
	List<String> data = new ArrayList<String>();

	private String fileformatToExport; // Formato seleccionado
	private List<String> filesFormatCombo;// Formatos PDF TXT RTF ...
	private String pathExportedReport;
	private String pathToReport;

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IActiveModule activeModule;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;
	
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin(){
		// Cargo los formatos
		filesFormatCombo = reportManager.fileFormatsToExport();
		pathToReport = null;
	}

	@SuppressWarnings("unchecked")
	public List<String> entidadesDelSistema() {
		return entityManager
				.createQuery(
						"select e.nombre from Entidad_configuracion e where e.perteneceARhio = true")
				.getResultList();
	}

	@SuppressWarnings("unchecked")	
	public void crearReporte() {

		// Cargo la entidad
		List<Entidad_configuracion> le = entityManager
				.createQuery(
						"select e from Entidad_configuracion e where e.nombre =:entidadNombre")
				.setParameter("entidadNombre", entidadSeleccionada)
				.getResultList();
		if (le.size() != 0)
			entidad = le.get(0);

		// Cargo las etiquetas y valores fijos
		pars = new HashMap();
		pars.put("P_TITULO", SeamResourceBundle.getBundle().getString(
				"tituloReporte"));
		pars.put("P_SUBTITULO", "");

		pars.put("eFechaElaboracion", SeamResourceBundle.getBundle().getString(
				"eFechaElaboracion"));
		pars.put("fechaElaboracion", SeamResourceBundle.getBundle().getString(
				"fechaElaboracion"));

		pars.put("eNombreEntidad", SeamResourceBundle.getBundle().getString(
				"eNombreEntidad"));
		pars.put("nombreEntidad", SeamResourceBundle.getBundle().getString(
				"nombreEntidad"));

		pars.put("eTipoEntidad", SeamResourceBundle.getBundle().getString(
				"eNombreEntidad"));
		pars.put("tipoEntidad", SeamResourceBundle.getBundle().getString(
				"tipoEntidad"));

		pars.put("eDireccion", SeamResourceBundle.getBundle().getString(
				"eDireccion"));
		pars.put("direccion", SeamResourceBundle.getBundle().getString(
				"direccion"));

		pars
				.put("eNoCamas", SeamResourceBundle.getBundle().getString(
						"eCamas"));

		pars.put("eNoCamasArquitectonicas", SeamResourceBundle.getBundle()
				.getString("eNoCamasArquitectonicas"));
		pars.put("noCamasArquitectonicas", SeamResourceBundle.getBundle()
				.getString("noCamasArquitectonicas"));

		pars.put("eNoCamasPresupuestadas", SeamResourceBundle.getBundle()
				.getString("eNoCamasPresupuestadas"));
		pars.put("noCamasPresupuestadas", SeamResourceBundle.getBundle()
				.getString("noCamasPresupuestadas"));

		pars.put("eNoCamasFuncionando", SeamResourceBundle.getBundle()
				.getString("eNoCamasFuncionando"));
		pars.put("noCamasFuncionando", SeamResourceBundle.getBundle()
				.getString("noCamasFuncionando"));

		pars.put("ePoblacionAreaInfluencia", SeamResourceBundle.getBundle()
				.getString("ePoblacionAreaInfluencia"));
		pars.put("noPoblacionAreaInfluencia", SeamResourceBundle.getBundle()
				.getString("noPoblacionAreaInfluencia"));

		pars.put("eTelefono", SeamResourceBundle.getBundle().getString(
				"eTelefono"));
		pars.put("telefono", SeamResourceBundle.getBundle().getString(
				"telefono"));

		pars.put("eFax", SeamResourceBundle.getBundle().getString("eFax"));
		pars.put("fax", SeamResourceBundle.getBundle().getString("fax"));

		pars
				.put("eCorreo", SeamResourceBundle.getBundle().getString(
						"eCorreo"));
		pars.put("correo", SeamResourceBundle.getBundle().getString("correo"));

		pars.put("eEntidadFederal", SeamResourceBundle.getBundle().getString(
				"eEntidadFederal"));
		pars.put("entidadFederal", SeamResourceBundle.getBundle().getString(
				"entidadFederal"));

		pars.put("eProvincia", SeamResourceBundle.getBundle().getString(
				"eProvincia"));
		pars.put("provincia", SeamResourceBundle.getBundle().getString(
				"provincia"));

		pars.put("eMunicipio", SeamResourceBundle.getBundle().getString(
				"eMunicipio"));
		pars.put("municipio", SeamResourceBundle.getBundle().getString(
				"municipio"));

		pars.put("eLocalidad", SeamResourceBundle.getBundle().getString(
				"eLocalidad"));
		pars.put("localidad", SeamResourceBundle.getBundle().getString(
				"localidad"));

		pars.put("eFechaApertura", SeamResourceBundle.getBundle().getString(
				"eFechaApertura"));
		pars.put("fechaApertura", SeamResourceBundle.getBundle().getString(
				"fechaApertura"));

		pars.put("eAnnosFuncionamiento", SeamResourceBundle.getBundle()
				.getString("eAnnosFuncionamiento"));
		pars.put("annosFuncionamiento", SeamResourceBundle.getBundle()
				.getString("annosFuncionamiento"));

		pars.put("eAreaTerreno", SeamResourceBundle.getBundle().getString(
				"eAreaTerreno"));
		pars.put("areaContruccion", SeamResourceBundle.getBundle().getString(
				"areaContruccion"));

		pathToReport = reportManager.ExportReport("fichaHospitalaria", pars,
				data, FileType.HTML_FILE);
	}

	public void exportReportToFileFormat() {
		pathExportedReport = "";
		FileType tipo;
		if (fileformatToExport.equals(filesFormatCombo.get(0)))
			tipo = FileType.PDF_FILE;
		else if (fileformatToExport.equals(filesFormatCombo.get(1)))
			tipo = FileType.RTF_FILE;
		else if (fileformatToExport.equals(filesFormatCombo.get(2)))
			tipo = FileType.EXCEL_FILE;
		else if (fileformatToExport.equals(filesFormatCombo.get(3)))
			tipo = FileType.XML_FILE;
		else if (fileformatToExport.equals(filesFormatCombo.get(4)))
			tipo = FileType.CSV_FILE;
		else
			tipo = FileType.PLAIN_TEXT_FILE;
		pathExportedReport = reportManager.ExportReport("fichaHospitalaria",
				pars, data, tipo);
	}

	public String getPathToReport() {
		return pathToReport;
	}

	public void setPathToReport(String pathToReport) {
		this.pathToReport = pathToReport;
	}

	public void salir() {
	}

	public void cancelar() {
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

	public String getPathExportedReport() {
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public String getEntidadSeleccionada() {
		return entidadSeleccionada;
	}

	public void setEntidadSeleccionada(String entidadSeleccionada) {
		this.entidadSeleccionada = entidadSeleccionada;
	}

}
