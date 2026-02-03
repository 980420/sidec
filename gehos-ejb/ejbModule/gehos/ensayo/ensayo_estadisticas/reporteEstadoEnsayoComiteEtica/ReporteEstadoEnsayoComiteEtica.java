package gehos.ensayo.ensayo_estadisticas.reporteEstadoEnsayoComiteEtica;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import groovyjarjarasm.asm.tree.IntInsnNode;

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
@Name("reporteEstadoEnsayoComiteEtica")
@Scope(ScopeType.CONVERSATION)
public class ReporteEstadoEnsayoComiteEtica{
	
	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<EstadoEnsayoComiteEtica> estadosGrupo;
	//List<String> listaSujetos = new ArrayList<String>(); //lista para almacenar los sujetos y luego contar los valores unicos
		
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteEstadoEnsayoComiteEtica;
	private String nombreReport;
	private String pathExportedReport = "";

	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	
	private List<Estudio_ensayo> listaEstudioEntidad;
	private List<Entidad_ensayo> listaEntidadEst;
	private List<EstudioEntidad_ensayo> listaEntidadEstSeleccionado;
	private List<String> listarEstudios;
	private List<String> listarEntidades;
	private List<String> listarEntidadesEstSeleccionado = new ArrayList<String>();
	private String estudio;
	private String entidad;	

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
	
	public void cargarEntidades(){
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class, user.getId());
		listaEntidadEst = new ArrayList<Entidad_ensayo>();
		listarEntidades = new ArrayList<String>();
		listaEntidadEst = (List<Entidad_ensayo>) entityManager.createQuery("select distinct estudioEnt.estudioEntidad.entidad from UsuarioEstudio_ensayo estudioEnt where estudioEnt.estudioEntidad.entidad.eliminado = false and estudioEnt.usuario.id=:idUser")
				.setParameter("idUser", usuario.getId())
				.getResultList();
		for (int i = 0; i < listaEntidadEst.size(); i++) {			
			listarEntidades.add(listaEntidadEst.get(i).getNombre());						
		}		
	}
	
	public void cargarEntidadesEstudio(){
		listarEntidadesEstSeleccionado.clear();
		listaEntidadEstSeleccionado = (List<EstudioEntidad_ensayo>)entityManager
				.createQuery(
						"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.nombre=:nomEst and estudioEnt.estudio.eliminado = false")
				.setParameter("nomEst",this.estudio).getResultList();
		for (int j = 0; j < listaEntidadEstSeleccionado.size(); j++) {
			listarEntidadesEstSeleccionado.add(listaEntidadEstSeleccionado.get(j).getEntidad().getNombre());			
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

	
	//RF11 - Generar reporte de estado del ensayo para comité de ética
	public void reporteEstadoEnsayoComiteEtica()	{
		
		int totalEvaluacion = 0;	
		int totalIncluidos = 0;
		int totalNoIncluidos = 0;
		int totalInterrupciones = 0;
		int tatalEventoAG = 0;
		
		estadosGrupo = new ArrayList<EstadoEnsayoComiteEtica>();
		style = -1;
		Integer cantSujetos=0;
		Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
		Entidad_ensayo entidadEnsayo = new Entidad_ensayo();
		
		for (int j = 0; j < listaEstudioEntidad.size(); j++) {
			if(listaEstudioEntidad.get(j).getNombre().equals(this.estudio)){
				estudioEnsayo = listaEstudioEntidad.get(j);
				break;
			}
		}
		
		for (int k = 0; k < listaEntidadEst.size(); k++) {
			if(listaEntidadEst.get(k).getNombre().equals(this.entidad)){
				entidadEnsayo = listaEntidadEst.get(k);
				break;
			}
		}
		
		haySujetos=false;
		
		if(fechaFin != null && fechaInicio != null && !this.estudio.equals("") && !this.entidad.equals("")){
	
			List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager
					.createQuery(
							"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
					.setParameter("idEstudio",estudioEnsayo.getId()).getResultList();
			
			for (int i = 0; i < grupos.size(); i++) {
				int evaluacion = 0;	
				int incluidos = 0;
				int noIncluidos = 0;
				int interrupciones = 0;
				int eventoAG=0;
				List<Sujeto_ensayo> sujetosDelGrupo = entityManager
						.createQuery(
								"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin")				
						.setParameter("idEstudio",estudioEnsayo.getId())	
						.setParameter("idEntidad",entidadEnsayo.getId())
						.setParameter("idGrupo",grupos.get(i).getId())
						.setParameter("fechaInicio",fechaInicio)
						.setParameter("fechaFin",fechaFin).getResultList();
				
				cantSujetos+=sujetosDelGrupo.size();
				
				if(!sujetosDelGrupo.isEmpty()) {
					haySujetos = true;
					
					for(int j=0;j<sujetosDelGrupo.size();j++) {
						if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==1)
							evaluacion++;
						else if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==3)
							noIncluidos++;
						else if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==4)
							incluidos++;
						
						if(sujetosDelGrupo.get(j).getEstadoTratamiento().getCodigo() == 4)
							interrupciones++;
						
						//Para buscar los EAG
						List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
								.createQuery(
										"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.momentoSeguimientoGeneral.programado = 'false' and momento.eliminado = false")									
								.setParameter("idSujeto",sujetosDelGrupo.get(j).getId()).getResultList();
						
						for(int l=0;l<momentosSujeto.size();l++) {
							MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
							List<CrdEspecifico_ensayo> crdMomento = entityManager
									.createQuery(
											"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
									.setParameter("idMomento", momento.getId()).getResultList();
							
							for(int m=0; m < crdMomento.size(); m++) {
								CrdEspecifico_ensayo crd=crdMomento.get(m);
								
								if(!hojaCrdDesasociadaEnMS(crdMomento.get(m).getHojaCrd().getId(),momento.getMomentoSeguimientoGeneral().getId())) {
									
									List<Long> gruposVariables = entityManager
											.createQuery(
													"select DISTINCT variableDato.contGrupo from VariableDato_ensayo variableDato "
															+ "where variableDato.crdEspecifico.id =:idCrd "
															+ "and variableDato.eliminado = false")
											.setParameter("idCrd", crd.getId()).getResultList();
									
									for (int n = 0; n < gruposVariables.size(); n++) {
										List<VariableDato_ensayo> variable = entityManager
												.createQuery(
														"select variable from VariableDato_ensayo variable where variable.crdEspecifico.id =:idCrd and variable.eliminado = false and variable.contGrupo =:noGrupo")
												.setParameter("idCrd", crd.getId())
												.setParameter("noGrupo", gruposVariables.get(n)).getResultList();
										
										for(int o=0; o < variable.size(); o++){
											if(variable.get(o).getVariable().getNombreVariable().equals("Gravedad") && variable.get(o).getValor().equals("Grave/Serio")){
												eventoAG++;
												/*if(getCausalidad(variable).equals("Definitiva") || getCausalidad(variable).equals("Muy Probable") || getCausalidad(variable).equals("Probable") || getCausalidad(variable).equals("Posible")){
													haySujetos = true;
													listaSujetos.add(sujeto.getCodigoPaciente()); //adicionar el codigo del sujeto
													eventoAdverso = getNombreEvento(variable);
													causalidad = getCausalidad(variable);
													fechaInicioEA = getFechaInicioEA(variable);
													fechaFinEA = getFechaFinEA(variable);
													EstadoEnsayoComiteEtica eventoAdversoSuj = new EstadoEnsayoComiteEtica(sujeto.getCodigoPaciente(), eventoAdverso, fechaInicioEA, fechaFinEA, causalidad, this.style.toString());
													eventosAdversos.add(eventoAdversoSuj);
												}*/
											}
										}										
									}									
								}								
							}
						}
					}
					EstadoEnsayoComiteEtica estadoGupo = new EstadoEnsayoComiteEtica(grupos.get(i).getNombreGrupo(), evaluacion, incluidos, noIncluidos, interrupciones, eventoAG);
					estadosGrupo.add(estadoGupo);
					totalEvaluacion += evaluacion;	
					totalIncluidos += incluidos;
					totalNoIncluidos += noIncluidos;
					totalInterrupciones += interrupciones;
					tatalEventoAG += eventoAG;
				}
			}				
			
			if (haySujetos) {
				this.style = 1;
				
				EstadoEnsayoComiteEtica totalEstudio = new EstadoEnsayoComiteEtica("Total estudio", totalEvaluacion, totalIncluidos, totalNoIncluidos, totalInterrupciones, tatalEventoAG);
				estadosGrupo.add(totalEstudio);
				
				reporteEstadoEnsayoComiteEtica=new HashMap();
				reporteEstadoEnsayoComiteEtica.put("nombreEstudio", this.estudio);
				reporteEstadoEnsayoComiteEtica.put("nombreEntidad", this.entidad);
				reporteEstadoEnsayoComiteEtica.put("nombreGrupo", SeamResourceBundle.getBundle().getString("nombreGrupo"));
				reporteEstadoEnsayoComiteEtica.put("enEvaluacion", SeamResourceBundle.getBundle().getString("enEvaluacion"));
				reporteEstadoEnsayoComiteEtica.put("incluidos", SeamResourceBundle.getBundle().getString("incluidos"));
				reporteEstadoEnsayoComiteEtica.put("noIncluidos", SeamResourceBundle.getBundle().getString("noIncluidos"));
				reporteEstadoEnsayoComiteEtica.put("cantInterrupciones", SeamResourceBundle.getBundle().getString("cantInterrupciones"));	
				reporteEstadoEnsayoComiteEtica.put("noEAG", SeamResourceBundle.getBundle().getString("noEAG"));	
				nombreReport=reportManager.ExportReport("reportEstadoEnsayoComiteEtica", reporteEstadoEnsayoComiteEtica, estadosGrupo, FileType.HTML_FILE);
										
				
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
		this.entidad="";
		this.fechaInicio=null;
		this.fechaFin=null;
	}
	
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadoEnsayoComiteEtica", reporteEstadoEnsayoComiteEtica, estadosGrupo,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadoEnsayoComiteEtica", reporteEstadoEnsayoComiteEtica, estadosGrupo,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadoEnsayoComiteEtica", reporteEstadoEnsayoComiteEtica, estadosGrupo,
					FileType.EXCEL_FILE);
		}

	}
	
	private boolean hojaCrdDesasociadaEnMS(long idCrd,long id_momento){
		List<MomentoSeguimientoGeneralHojaCrd_ensayo> ms_crd=
				this.entityManager.createQuery("select ms_crd from MomentoSeguimientoGeneralHojaCrd_ensayo ms_crd where ms_crd.eliminado=true and ms_crd.hojaCrd.id=:id_crd_p and ms_crd.momentoSeguimientoGeneral.id=:id_momento")
				.setParameter("id_crd_p", idCrd).setParameter("id_momento", id_momento).getResultList();			
		return ms_crd.size()>0;
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

	public List<String> getListarEntidades() {
		return listarEntidades;
	}

	public void setListarEntidades(List<String> listarEntidades) {
		this.listarEntidades = listarEntidades;
	}
	
	public List<String> getListarEntidadesEstSeleccionado() {
		return listarEntidadesEstSeleccionado;
	}

	public void setListarEntidadesEstSeleccionado(
			List<String> listarEntidadesEstSeleccionado) {
		this.listarEntidadesEstSeleccionado = listarEntidadesEstSeleccionado;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
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