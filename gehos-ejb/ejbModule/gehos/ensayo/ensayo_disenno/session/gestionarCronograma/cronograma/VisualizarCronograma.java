package gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.management.entity.Funcionalidad_configuracion;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_estadisticas.utils.EasyQuery;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoCronograma_ensayo;
import gehos.ensayo.entity.EstadoGruposujeto_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.theme.ThemeSelector;

@Scope(ScopeType.CONVERSATION)
@Name("visualizarCronograma")
public class VisualizarCronograma {

	@In
	IActiveModule activeModule;
	@In
	Usuario user;

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	ReportManager reportManager;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;

	private List<String> filesFormatCombo;
	private String pathExportedReport;
	private String reportName;
	private List<String> subReportNames;

	private Map pars;
	private List<Map> subReportPars;
	// private List listToReport;
	private int results;

	private String selectedCenter;
	private List<String> allCenters;
	private List<Collection> listadoCronogramaGeneral;
	private List<MSProgramado> listadoMSProgramados;
	private List<MSNoProgramado> listadoMSNoProgramados;

	List<MomentoSeguimientoGeneral_ensayo> momentosProgramados = new ArrayList<MomentoSeguimientoGeneral_ensayo>();
	List<MomentoSeguimientoGeneral_ensayo> momentosNoProgramados = new ArrayList<MomentoSeguimientoGeneral_ensayo>();

	private long cid;
	private long idCronograma;
	private Cronograma_ensayo cronograma;
	private Estudio_ensayo estudio;
	private Entidad_ensayo entidad;
	private String fileformatToExport;

	// completar, aprobar, desaprobar
	boolean completar;
	boolean aprobar = true;
	boolean desaprobar;

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void inicializarExportarCronograma() {
		this.cronograma = (Cronograma_ensayo) entityManager.find(
				Cronograma_ensayo.class, idCronograma);
		this.estudio = seguridadEstudio.getEstudioEntidadActivo().getEstudio();

		this.entidad = entityManager.find(Entidad_ensayo.class, activeModule
				.getActiveModule().getEntidad().getId());
		reportName = "visualizar_cronograma";
		filesFormatCombo = reportManager.fileFormatsToExport();
		// this.cid =
		// bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraExportar_ens"));

	}

	public boolean Completar() {
		try {
			if (EstadoElavorado() && UsuarioGerente() && MomentosGenerales()) {
				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean DesCompletar() {
		try {
			if (EstadoCompletado() && UsuarioGerente() && MomentosGenerales() && (this.cronograma.getEstadoReglas().getCodigo() == 1 || this.cronograma.getEstadoReglas().getCodigo() == 4)) {
				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean UsuarioGerente() {
		try {

			// GrupoSujetos_ensayo gruposujeto =
			// entityManager.find(GrupoSujetos_ensayo.class,idGrupoSujeto);

			UsuarioEstudio_ensayo usuario = new UsuarioEstudio_ensayo();

			String gerente = "ecGerDatos";
			usuario = (UsuarioEstudio_ensayo) entityManager
					.createQuery(
							"Select ue from UsuarioEstudio_ensayo ue "
									+ "JOIN ue.usuario u "
									+ "JOIN ue.estudioEntidad ee "
									+ "JOIN ue.role r " + "JOIN ee.estudio e "
									+ "JOIN ee.entidad ent "
									+ "where e.id =:id "
									+ "and ent.id =:ident "
									+ "and r.codigo =:gerente "
									+ "and u.id =:idUsuario "
									+ "and ee.eliminado <> true "
									+ "and ue.eliminado <> true "
									+ "and u.eliminado <> true "
									+ "and e.eliminado <> true ")
									.setParameter("ident", entidad.getId())
									.setParameter("id", estudio.getId())
									.setParameter("gerente", gerente)
									.setParameter("idUsuario", user.getId()).getSingleResult();

			if (usuario!= null) {
				return true;
			}
			else
				return false;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean EstadoElavorado() {
		try {
			boolean estado = false;

			if (cronograma.getEstadoCronograma().getCodigo() == 2
					|| cronograma.getEstadoCronograma().getCodigo() == 5 || cronograma.getEstadoCronograma().getCodigo() == 1) {
				estado = true;
			}
			return estado;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean Aprobar() {
		try {
			if (EstadoCompletado() && UsuarioPromotor()) {

				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean UsuarioPromotor() {
		try {

			UsuarioEstudio_ensayo usuario = new UsuarioEstudio_ensayo();

			String promotor = "ecProm";
			usuario = (UsuarioEstudio_ensayo) entityManager
					.createQuery(
							"Select ue from UsuarioEstudio_ensayo ue "
									+ "JOIN ue.usuario u "
									+ "JOIN ue.estudioEntidad ee "
									+ "JOIN ue.role r " + "JOIN ee.estudio e "
									+ "JOIN ee.entidad ent "
									+ "where e.id =:id "
									+ "and ent.id =:ident "
									+ "and r.codigo =:promotor "
									+ "and ee.eliminado <> true "
									+ "and ue.eliminado <> true "
									+ "and u.eliminado <> true "
									+ "and e.eliminado <> true ")
									.setParameter("ident", entidad.getId())
									.setParameter("id", estudio.getId())
									.setParameter("promotor", promotor).getSingleResult();

			if (usuario.getUsuario().getId() == user.getId()) {
				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean EstadoCompletado() {
		try {
			boolean estado = false;

			if (cronograma.getEstadoCronograma().getCodigo() == 3) {
				estado = true;
			}
			return estado;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean Desaprobar() {
		try {
			if (EstadoAprobado() && UsuarioPromotor()) {

				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean EstadoAprobado() {
		try {
			boolean estado = false;

			if (cronograma.getEstadoCronograma().getCodigo() == 4) {
				estado = true;
			}
			return estado;
		} catch (Exception e) {
			return false;
		}
	}

	public void Completarcronograma() {
		try {
			long idaprobado = 3;

			EstadoCronograma_ensayo estadocronograma = entityManager.find(
					EstadoCronograma_ensayo.class, idaprobado);
			cronograma.setEstadoCronograma(estadocronograma);
			entityManager.persist(cronograma);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void DesCompletarcronograma() {
		try {
			long idaprobado = 2;

			EstadoCronograma_ensayo estadocronograma = entityManager.find(
					EstadoCronograma_ensayo.class, idaprobado);
			cronograma.setEstadoCronograma(estadocronograma);
			entityManager.persist(cronograma);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void AprobarCronograma() {
		try {
			long idaprobado = 4;

			EstadoCronograma_ensayo estadocronograma = entityManager.find(
					EstadoCronograma_ensayo.class, idaprobado);
			cronograma.setEstadoCronograma(estadocronograma);
			entityManager.persist(cronograma);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void DesAprobarCronograma() {
		try {
			long idaprobado = 5;

			EstadoCronograma_ensayo estadocronograma = entityManager.find(
					EstadoCronograma_ensayo.class, idaprobado);
			cronograma.setEstadoCronograma(estadocronograma);
			entityManager.persist(cronograma);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void Momentos() {

		// Programados
		momentosProgramados = (ArrayList<MomentoSeguimientoGeneral_ensayo>) entityManager
				.createQuery(
						"select msProgramado from MomentoSeguimientoGeneral_ensayo msProgramado "
								+ "inner join msProgramado.cronograma cronograma "
								+ "where msProgramado.eliminado <> true "
								+ "and msProgramado.programado  = true "
								+ "and cronograma.id = :idCronograma")
								.setParameter("idCronograma", idCronograma).getResultList();

		// No Programados
		momentosNoProgramados = (ArrayList<MomentoSeguimientoGeneral_ensayo>) entityManager
				.createQuery(
						"select msProgramado from MomentoSeguimientoGeneral_ensayo msProgramado "
								+ "inner join msProgramado.cronograma cronograma "
								+ "where msProgramado.eliminado <> true "
								+ "and msProgramado.programado  = false "
								+ "and cronograma.id = :idCronograma")
								.setParameter("idCronograma", idCronograma).getResultList();

	}

	public boolean MomentosGenerales() {

		try {
			Momentos();

			int varAux = 0;
			//List<MomentoSeguimientoGeneralHojaCrd_ensayo> momentosGN;

			if(momentosProgramados.size() > momentosNoProgramados.size())
				varAux = momentosProgramados.size();
			else
				varAux = momentosNoProgramados.size();

			for (int i = 0; i < varAux; i++) {

				if (i < momentosProgramados.size()) {

					List<MomentoSeguimientoGeneralHojaCrd_ensayo> momentosGN = (ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>) entityManager
							.createQuery(
									"select msG from MomentoSeguimientoGeneralHojaCrd_ensayo msG where msG.eliminado <> true and msG.momentoSeguimientoGeneral.id =:id")
									.setParameter("id",
											momentosProgramados.get(i).getId())
											.getResultList();

					Long a = momentosGN.get(0).getId();

				}
				if (i < momentosNoProgramados.size()) {

					List<MomentoSeguimientoGeneralHojaCrd_ensayo>momentosGN = (ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>) entityManager
							.createQuery(
									"select msG from MomentoSeguimientoGeneralHojaCrd_ensayo msG where msG.eliminado <> true and msG.momentoSeguimientoGeneral.id =:id")
									.setParameter("id",
											momentosNoProgramados.get(i).getId())
											.getResultList();

					Long a = momentosGN.get(0).getId();
				}

			}

			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public void exportReportToFileFormat() {

		listadoCronogramaGeneral = new ArrayList<Collection>();
		listadoMSProgramados = new ArrayList<MSProgramado>();
		listadoMSNoProgramados = new ArrayList<MSNoProgramado>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String now = sdf.format(new Date());

		Momentos();

		if (momentosProgramados.size() > 0) {
			for (int i = 0; i < momentosProgramados.size(); i++) {
				String nbMS = momentosProgramados.get(i).getNombre();
				String eva = diasEtapa("evaluacion",momentosProgramados.get(i).getDia());
				String tra = diasEtapa("tratamiento",momentosProgramados.get(i).getDia());
				String seg = diasEtapa("seguimiento",momentosProgramados.get(i).getDia());
				listadoMSProgramados.add(new MSProgramado(nbMS, eva, tra, seg));
			}
			listadoCronogramaGeneral.add(listadoMSProgramados);
		}

		subReportNames = new ArrayList<String>();
		subReportPars = new ArrayList<Map>();
		Map pars1 = new HashMap();
		pars1.put(
				"listadoMS",
				SeamResourceBundle.getBundle().getString(
						"lbl_listadoProgramados_ens"));
		pars1.put("nombre_MS",
				SeamResourceBundle.getBundle().getString("prm_nombre_ens"));
		pars1.put("etapa_evaluacion",
				SeamResourceBundle.getBundle().getString("prm_evaluacion_ens"));
		pars1.put("etapa_tratamiento", SeamResourceBundle.getBundle()
				.getString("prm_tratamiento_ens"));
		pars1.put("etapa_seguimiento", SeamResourceBundle.getBundle()
				.getString("prm_seguimiento_ens"));
		subReportPars.add(pars1);
		subReportNames.add("subReport_visualizar_cronograma_programados");

		if (momentosNoProgramados.size() > 0) {
			for (int i = 0; i < momentosNoProgramados.size(); i++) {
				String nbMS = momentosNoProgramados.get(i).getNombre();
				String ocurre = momentosNoProgramados.get(i).getDescripcion();

				listadoMSNoProgramados.add(new MSNoProgramado(nbMS, ocurre));
			}
			listadoCronogramaGeneral.add(listadoMSNoProgramados);

			subReportNames.add("subReport_visualizar_cronograma_noProgramados");
			Map pars2 = new HashMap();
			pars2.put(
					"listadoMS",
					SeamResourceBundle.getBundle().getString(
							"lbl_listadoNoProgramados_ens"));
			pars2.put("nombre_MS",
					SeamResourceBundle.getBundle().getString("prm_nombre_ens"));
			pars2.put("ocurre",
					SeamResourceBundle.getBundle().getString("lbl_ocurre_ens"));
			subReportPars.add(pars2);
			reportName = "visualizar_cronograma_noProgramados";
		}

		pars = new HashMap();
		pars.put("P_TITULO",
				SeamResourceBundle.getBundle().getString("prm_titulo_ens"));
		pars.put("ESTUDIO",
				SeamResourceBundle.getBundle().getString("prm_estudio_ens"));
		pars.put("nb_ESTUDIO", estudio.getNombre());
		pars.put("GRUPO_SUJETO",
				SeamResourceBundle.getBundle().getString("prm_grupoSujeto_ens"));
		pars.put("nb_GRUPO_SUJETO", cronograma.getGrupoSujetos()
				.getNombreGrupo());

		if (fileformatToExport.equals(filesFormatCombo.get(0))) {

			pathExportedReport = reportManager.ExportReportWithSubReports(
					reportName, pars, new ArrayList<String>(),
					FileType.PDF_FILE, subReportNames,
					listadoCronogramaGeneral, subReportPars);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReportWithSubReports(
					reportName, pars, new ArrayList<String>(),
					FileType.RTF_FILE, subReportNames,
					listadoCronogramaGeneral, subReportPars);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReportWithSubReports(
					reportName, pars, new ArrayList<String>(),
					FileType.EXCEL_FILE, subReportNames,
					listadoCronogramaGeneral, subReportPars);
		}

	}
	public List<String> listaDias(String dias){

		List<String> listaDias= new ArrayList<String>();
		String[] listaDiasSelecc;
		String[] listaUltDiaSelecc;
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
		if (dias.contains(",")) {
			listaDiasSelecc = dias.split(", ");
			listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
					.split(" " + y + " ");
			for (int i = 0; i < listaDiasSelecc.length - 1; i++) {
				listaDias.add(listaDiasSelecc[i]);

			}
			listaDias.add(listaUltDiaSelecc[0]);
			listaDias.add(listaUltDiaSelecc[1]);


		} else if (dias.contains(y)) {
			listaUltDiaSelecc = dias.split(" " + y + " ");
			listaDias.add(listaUltDiaSelecc[0]);
			listaDias.add(listaUltDiaSelecc[1]);			

		} else {
			listaDias.add(dias);			

		}
		return listaDias;
	}
	// Metodo para construir la cadena de dias
	public String construirCadenaDias(List<String> listaDias) {

		String cadenaDias = "";
		String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
		for (int i = 0; i < listaDias.size(); i++) {
			if (i == listaDias.size() - 2)
				cadenaDias += listaDias.get(i) + " " + y + " ";
			else if (i == listaDias.size() - 1)
				cadenaDias += listaDias.get(i);
			else
				cadenaDias += listaDias.get(i) + ", ";
		}
		return cadenaDias;


	}
	// Metodo para construir la cadena con los dias de visita.
	public String diasEtapa(String etapa, String dias) {
		//add try catch para los grupos pesquisage, Evelio
		try {
			List<String> listaDias=new ArrayList<String>();
			listaDias=listaDias(dias);
			List<String> listaDiasEtapa = new ArrayList<String>();
			List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
					.createQuery(
							"select e from Etapa_ensayo e "
									+ "where e.cronograma.id = :idCrono "
									+ "order by e.id")
									.setParameter("idCrono", cronograma.getId()).getResultList();
			for (int i = 0; i < listaDias.size(); i++) {
				Integer dia = Integer.parseInt(listaDias.get(i));
				if (dia == 0 && etapa.equals("evaluacion")) {
					listaDiasEtapa.add(dia.toString());
					return construirCadenaDias(listaDiasEtapa);
					/**
					 * @author Tania
					 */
				} else if (dia >= 1 && dia <= etapas.get(1).getFinEtapa()
						&& etapa.equals("tratamiento"))
					listaDiasEtapa.add(dia.toString());
				else if (dia >= etapas.get(2).getInicioEtapa()
						&& dia <= etapas.get(2).getFinEtapa()
						&& etapa.equals("seguimiento"))
					listaDiasEtapa.add(dia.toString());
			}
			return construirCadenaDias(listaDiasEtapa);
		} catch (Exception e) {
			return "";// TODO: handle exception
		}
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

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

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

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public boolean isCompletar() {
		return completar;
	}

	public void setCompletar(boolean completar) {
		this.completar = completar;
	}

	public boolean isAprobar() {
		return aprobar;
	}

	public void setAprobar(boolean aprobar) {
		this.aprobar = aprobar;
	}

	public boolean isDesaprobar() {
		return desaprobar;
	}

	public void setDesaprobar(boolean desaprobar) {
		this.desaprobar = desaprobar;
	}

	public long getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(long idCronograma) {
		this.idCronograma = idCronograma;
	}

	public Cronograma_ensayo getCronograma() {
		return cronograma;
	}

	public void setCronograma(Cronograma_ensayo cronograma) {
		this.cronograma = cronograma;
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}

	public Entidad_ensayo getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_ensayo entidad) {
		this.entidad = entidad;
	}

	public List<String> getSubReportNames() {
		return subReportNames;
	}

	public void setSubReportNames(List<String> subReportNames) {
		this.subReportNames = subReportNames;
	}

	public Map getPars() {
		return pars;
	}

	public void setPars(Map pars) {
		this.pars = pars;
	}

	public List<Map> getSubReportPars() {
		return subReportPars;
	}

	public void setSubReportPars(List<Map> subReportPars) {
		this.subReportPars = subReportPars;
	}

	public List<Collection> getListadoCronogramaGeneral() {
		return listadoCronogramaGeneral;
	}

	public void setListadoCronogramaGeneral(
			List<Collection> listadoCronogramaGeneral) {
		this.listadoCronogramaGeneral = listadoCronogramaGeneral;
	}

	public List<MSNoProgramado> getListadoMSNoProgramados() {
		return listadoMSNoProgramados;
	}

	public void setListadoMSNoProgramados(
			List<MSNoProgramado> listadoMSNoProgramados) {
		this.listadoMSNoProgramados = listadoMSNoProgramados;
	}

}
