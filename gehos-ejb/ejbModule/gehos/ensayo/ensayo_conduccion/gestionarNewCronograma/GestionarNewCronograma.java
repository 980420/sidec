package gehos.ensayo.ensayo_conduccion.gestionarNewCronograma;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;


@SuppressWarnings("unchecked")
@Name("gestionarNewCronograma")
@Scope(ScopeType.CONVERSATION)
public class GestionarNewCronograma {
	private Date fechaInicio;
	private Date fechaFin;
	@In(create = true, value = "reportManager")
	ReportManager reportManager;
	protected @In
	EntityManager entityManager;
	protected @In(create = true)
	FacesMessages facesMessages;
	protected @In
	IBitacora bitacora;
	@In
	private Usuario user;
	List<SujetoGeneralNewCronograma> sujetos;

	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;

	private Integer style;
	private Map reporteNewCronograma;
	private String nombreReport;
	private String pathExportedReport = "";
	private String fileformatToExport;
	private List<String> filesFormatCombo;
	private List<EstudioEntidad_ensayo> listaEntidadEst;

	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");

	private List<Estudio_ensayo> listaEstudioEntidad;
	private List<String> listarEstudios;
	private String estudio;
	private String entidad;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void cargarEstudios() {
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
				user.getId());
		listaEstudioEntidad = new ArrayList<Estudio_ensayo>();
		listarEstudios = new ArrayList<String>();
		listaEstudioEntidad = (List<Estudio_ensayo>) entityManager
				.createQuery(
						"select distinct estudioEnt.estudioEntidad.estudio from UsuarioEstudio_ensayo estudioEnt where estudioEnt.estudioEntidad.estudio.eliminado = false and estudioEnt.usuario.id=:idUser")
				.setParameter("idUser", usuario.getId()).getResultList();

		for (int i = 0; i < listaEstudioEntidad.size(); i++) {
			if (listaEstudioEntidad.get(i).getEstadoEstudio().getCodigo() == 3
					|| listaEstudioEntidad.get(i).getEstadoEstudio()
							.getCodigo() == 6) {
				listarEstudios.add(listaEstudioEntidad.get(i).getNombre());
			}
		}
		listarEstudios.add(0, "<Seleccione>");
	}

	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}

	public int sizeMaxString(List<String> listReturn) {
		if (listReturn.size() == 0)
			return 150;
		int max = listReturn.get(0).length();
		for (int i = 1; i < listReturn.size(); i++) {
			if (listReturn.get(i).length() > max)
				max = listReturn.get(i).length();
		}
		if ((max * 6) > 150)
			return max * 6;
		return 150;
	}

	public void reporteNewCronograma() {

		String saltodelinea = "\n";

		sujetos = new ArrayList<SujetoGeneralNewCronograma>();
		style = -1;
		Integer cantSujetos = 0;

		Map<Long, String> listaEntidad = new HashMap<Long, String>();
		Map<Long, String> listaGrupo = new HashMap<Long, String>();
		Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
		for (int j = 0; j < listaEstudioEntidad.size(); j++) {
			if (listaEstudioEntidad.get(j).getNombre().equals(this.estudio)) {
				estudioEnsayo = listaEstudioEntidad.get(j);
				break;
			}
		}
		
		haySujetos = false;
		if (fechaFin != null && fechaInicio != null && !this.estudio.equals("")) {
			List<EstudioEntidad_ensayo> listaEntidadEst;
			listaEntidadEst = (List<EstudioEntidad_ensayo>) entityManager
					.createQuery(
							"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false")
					.setParameter("idEst", estudioEnsayo.getId())
					.getResultList();
			
			
			for (int z = 0; z < listaEntidadEst.size(); z++) {
				Boolean haySujetosRafa = false;
				List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager
						.createQuery(
								"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio")
						.setParameter("idEstudio",
								listaEntidadEst.get(z).getEstudio().getId())
						.getResultList();

				for (int i = 0; i < grupos.size(); i++) {
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager
							.createQuery(
									"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  order by sujeto.numeroInclucion")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())
							.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
							.setParameter("idGrupo", grupos.get(i).getId()).getResultList();					

					if (!sujetosDelGrupo.isEmpty()) {						
						haySujetosRafa = true;
						listaGrupo.put(grupos.get(i).getId(), grupos.get(i)
								.getNombreGrupo());

						// Para cada sujeto del grupo
						for (int k = 0; k < sujetosDelGrupo.size(); k++) {
                             
							Sujeto_ensayo sujeto = sujetosDelGrupo.get(k);
							List<MomentoSeguimientoEspecifico_ensayo> momentosPorSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
							
							// lista de momentos
                            if(sujeto.getFechaInterrupcion()!=null)	{
                            	momentosPorSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
    									.createQuery(
    											"select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.eliminado = FALSE and ms.sujeto.id=:idSujeto and ms.momentoSeguimientoGeneral.programado = TRUE and ms.fechaInicio>=:fechaInicio and ms.fechaInicio<=:fechaFin and ms.fechaInicio<=:fechaInterrupcion order by fechaInicio desc")
    									.setParameter("idSujeto", sujeto.getId())									
    									.setParameter("fechaInicio", fechaInicio)
    									.setParameter("fechaFin", fechaFin)
    									.setParameter("fechaInterrupcion", sujeto.getFechaInterrupcion())
    									.getResultList();
                            }
                            else{
                            	momentosPorSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
    									.createQuery(
    											"select s from MomentoSeguimientoEspecifico_ensayo s where s.eliminado = FALSE and s.sujeto.id=:idSujeto and s.momentoSeguimientoGeneral.programado = TRUE and s.fechaInicio>=:fechaInicio and s.fechaInicio<=:fechaFin order by fechaInicio desc")
    									.setParameter("idSujeto", sujeto.getId())									
    									.setParameter("fechaInicio", fechaInicio)
    									.setParameter("fechaFin", fechaFin).getResultList();
                            }
							
							String fechaMomentoGeneral = "";
							String nombreMomentoGeneral = "";
							if(!momentosPorSujeto.isEmpty()){
								haySujetos = true;
								/////////
								for (int j = 0; j < momentosPorSujeto.size(); j++) {
									SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
									
									fechaMomentoGeneral += sdf.format(momentosPorSujeto.get(j).getFechaInicio())+ saltodelinea;
									nombreMomentoGeneral += momentosPorSujeto.get(j).getMomentoSeguimientoGeneral().getNombre()+ saltodelinea;																								
								}
								
								sujetos.add(new SujetoGeneralNewCronograma(sujeto.getCodigoPaciente(), nombreMomentoGeneral,fechaMomentoGeneral, sujeto.getEntidad()
												.getNombre(),sujeto.getGrupoSujetos().getNombreGrupo(),this.style.toString()));
	                            cantSujetos++;
							}
						}
					}
				}
				if (haySujetosRafa) {
					listaEntidad.put(listaEntidadEst.get(z).getEntidad().getId(), listaEntidadEst.get(z).getEntidad().getNombre());
				}
			}

			if (haySujetos) {
				reporteNewCronograma = new HashMap();
				reporteNewCronograma.put("nombreEstudio", this.estudio);
				reporteNewCronograma.put("cantSujetos", String.valueOf(sujetos.size()));
				reporteNewCronograma.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteNewCronograma.put("entidad", SeamResourceBundle.getBundle().getString("entidad"));
				reporteNewCronograma.put("grupo", SeamResourceBundle.getBundle().getString("grupo"));
				reporteNewCronograma.put("nombreMSP", SeamResourceBundle.getBundle().getString("nombreMSP"));
				reporteNewCronograma.put("fechaCMSP", SeamResourceBundle.getBundle().getString("fechaCMSP"));
                nombreReport = reportManager.ExportReport("reporNewCronograma",reporteNewCronograma, sujetos, FileType.HTML_FILE);
                flag=true;
				 flag2=false;
			} else {
				flag = false;
				flag2 = true;
			}
		}else
		 {
			flag = false;
			 flag2 = true;
		 }
		
		this.estudio="";
		this.fechaInicio=null;
		this.fechaFin=null;
   }
	
	 public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reporNewCronograma", reporteNewCronograma, sujetos,FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reporNewCronograma", reporteNewCronograma, sujetos,FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reporNewCronograma", reporteNewCronograma, sujetos,FileType.EXCEL_FILE);
		}

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

	public List<SujetoGeneralNewCronograma> getSujetos() {
		return sujetos;
	}

	public void setSujetos(List<SujetoGeneralNewCronograma> sujetos) {
		this.sujetos = sujetos;
	}

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public List<EstudioEntidad_ensayo> getListaEntidadEst() {
		return listaEntidadEst;
	}

	public void setListaEntidadEst(List<EstudioEntidad_ensayo> listaEntidadEst) {
		this.listaEntidadEst = listaEntidadEst;
	}

}
