package gehos.ensayo.ensayo_estadisticas.notificaciones;

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
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.Notificacion_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
@Name("listaNotificaciones")
@Scope(ScopeType.CONVERSATION)
public class Notificaciones{
	
	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<Notificacion> listNotificaciones;
	List<String> listaSujetos = new ArrayList<String>(); //lista para almacenar los sujetos y luego contar los valores unicos
	
	
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteNotificaciones;
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

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void cargarEstudios() {
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class, user.getId());
		listaEstudioEntidad = new ArrayList<Estudio_ensayo>();
		listarEstudios = new ArrayList<String>();
		listaEstudioEntidad = (List<Estudio_ensayo>) entityManager
				.createQuery("select distinct estudioEnt.estudioEntidad.estudio from UsuarioEstudio_ensayo estudioEnt where estudioEnt.estudioEntidad.estudio.eliminado = false and estudioEnt.estudioEntidad.estudio.estadoEstudio = 3 and estudioEnt.usuario.id=:idUser")
				.setParameter("idUser", usuario.getId())
				.getResultList();
		for (int i = 0; i < listaEstudioEntidad.size(); i++) {
				listarEstudios.add(listaEstudioEntidad.get(i).getNombre());	
		}
	}
	
	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}
	
	public int sizeMaxString(List<String> listReturn){
        if(listReturn.size() == 0)
            return 150;
        int max = listReturn.get(0).length();
        for(int i = 1; i < listReturn.size(); i++){
            if(listReturn.get(i).length() > max)
                max = listReturn.get(i).length();
        }
        if ((max*6) > 150)
            return max*6;
        return 150;
    }

	//RF10 - Generar reporte de notificaciones
	public void reporteNotificaciones()	{
		
		listNotificaciones = new ArrayList<Notificacion>();
		style = -1;
		Integer cantSujetos=0;
		Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
		
		for (int j = 0; j < listaEstudioEntidad.size(); j++) {
			if(listaEstudioEntidad.get(j).getNombre().equals(this.estudio)){
				estudioEnsayo = listaEstudioEntidad.get(j);
				break;
			}
		}
		
		haySujetos=false;
		
		if(fechaFin != null && fechaInicio != null && !this.estudio.equals("")){
			List<EstudioEntidad_ensayo> listaEntidadEst;
			listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
					.createQuery(
							"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false")
					.setParameter("idEst",estudioEnsayo.getId()).getResultList();
			
			for (int z = 0; z < listaEntidadEst.size(); z++) {
				List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager
						.createQuery(
								"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
						.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
				
				for (int i = 0; i < grupos.size(); i++) {
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager
							.createQuery(
									"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin")				
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())	
							.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
							.setParameter("idGrupo",grupos.get(i).getId())
							.setParameter("fechaInicio",fechaInicio)
							.setParameter("fechaFin",fechaFin).getResultList();
					
					cantSujetos+=sujetosDelGrupo.size();
					
					if(!sujetosDelGrupo.isEmpty()) {
						for(int k=0;k<sujetosDelGrupo.size();k++) {
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							List<Notificacion_ensayo> notificacionesSujeto = entityManager
									.createQuery(
											"select notificacion from Notificacion_ensayo notificacion where notificacion.idSujeto =:idSujeto")									
									.setParameter("idSujeto",sujeto.getId()).getResultList();
							
							for (int l = 0; l < notificacionesSujeto.size(); l++) {
								Notificacion_ensayo notificacion = notificacionesSujeto.get(l);
								String eventoAdverso = "";
								String fechaInicioEA = "";
								String fechaFinEA = "";
								
								List<VariableDato_ensayo> variable = entityManager
										.createQuery(
												"select variable from VariableDato_ensayo variable where variable.crdEspecifico.id =:idCrd and variable.eliminado = false and variable.contGrupo =:noGrupo")
										.setParameter("idCrd", notificacion.getCrdEspecifico().getId())
										.setParameter("noGrupo", notificacion.getVariableDato().getContGrupo()).getResultList();
								
								if(variable.size()>0){																			
									haySujetos = true;
									listaSujetos.add(sujeto.getCodigoPaciente());
									eventoAdverso = getNombreEvento(variable);
									fechaInicioEA = getFechaInicioEA(variable);
									fechaFinEA = getFechaFinEA(variable);
									Notificacion notificacionSuj = new Notificacion(sujeto.getCodigoPaciente(), eventoAdverso, fechaInicioEA, fechaFinEA, this.style.toString());
									listNotificaciones.add(notificacionSuj);
								}
							}								
						}					
					}					
				}				
			}							
			
			if (haySujetos) {
				this.style = 1;
				Set<String> distintSet = new HashSet<String>(listaSujetos); //Valores distindos dentro de la lista
				int totalSujetos = distintSet.size(); // total de valores distintos
				reporteNotificaciones=new HashMap();
				reporteNotificaciones.put("nombreEstudio", this.estudio);
				reporteNotificaciones.put("totalSujetos", totalSujetos);
				reporteNotificaciones.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteNotificaciones.put("eventoAdverso", SeamResourceBundle.getBundle().getString("eventoAdverso"));
				reporteNotificaciones.put("fechaInicioEA", SeamResourceBundle.getBundle().getString("fechaInicioEA"));
				reporteNotificaciones.put("fechaFinEA", SeamResourceBundle.getBundle().getString("fechaFinEA"));				
				nombreReport=reportManager.ExportReport("reportEstadisticaNotificacionesPorSujetos", reporteNotificaciones, listNotificaciones, FileType.HTML_FILE);
				flag=true;
				flag2=false;
			} 
			else {
				flag = false;
				flag2 = true;
			}			
		}
		else {
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
			pathExportedReport = reportManager.ExportReport(
					"reportEstadisticaNotificacionesPorSujetos", reporteNotificaciones, listNotificaciones,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadisticaNotificacionesPorSujetos", reporteNotificaciones, listNotificaciones,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadisticaNotificacionesPorSujetos", reporteNotificaciones, listNotificaciones,
					FileType.EXCEL_FILE);
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

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}	

	public String getNombreEvento(List<VariableDato_ensayo> variables) {
		String nombreEvento="";
		for(int n=0; n < variables.size(); n++)
			if(variables.get(n).getVariable().getNombreVariable().equals("Evento Adverso")){
				nombreEvento = variables.get(n).getValor().toString();
				break;
			}
		return nombreEvento;
	}		

	public String getFechaInicioEA(List<VariableDato_ensayo> variables) {
		String fInicioEA="";		
		for(int n=0; n < variables.size(); n++)
			if(variables.get(n).getVariable().getNombreVariable().equals("Fecha de inicio")){
				fInicioEA = variables.get(n).getValor().toString();
				break;
			}
		return fInicioEA;
	}
	
	public String getFechaFinEA(List<VariableDato_ensayo> variables) {
		String fFinEA="";
		for(int n=0; n < variables.size(); n++)
			if(variables.get(n).getVariable().getNombreVariable().equals("Fecha de fin")){
				fFinEA = variables.get(n).getValor().toString();
				break;
			}
		return fFinEA;
	}

}