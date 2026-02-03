package gehos.ensayo.ensayo_estadisticas.reporteEventoAdvGrave;

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
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
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
@Name("reporteEventoAdvGrave")
@Scope(ScopeType.CONVERSATION)
public class ReporteEventoAdvGrave{
	
	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<EventoAdversoGrave> eventosAdversos;
	List<String> listaSujetos = new ArrayList<String>(); //lista para almacenar los sujetos y luego contar los valores unicos
	
	//List<SujetoGeneral> sujetos;
	
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteEventoAdversoGrave;
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

	//RF9 - Generar reporte de Eventos Adversos Graves
	public void reporteEventoAdversoGrave()	{
		
		eventosAdversos = new ArrayList<EventoAdversoGrave>();
		style = -1;
		Integer cantSujetos=0;
		Map<Long, String> listaGrupo = new HashMap<Long, String>();
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
						listaGrupo.put(grupos.get(i).getId(), grupos.get(i).getNombreGrupo());
						for(int k=0;k<sujetosDelGrupo.size();k++) {
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
									.createQuery(
											"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.momentoSeguimientoGeneral.programado = 'false' and momento.eliminado = false")									
									.setParameter("idSujeto",sujeto.getId()).getResultList();
							
							for(int l=0;l<momentosSujeto.size();l++) {
								MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
								List<CrdEspecifico_ensayo> crdMomento = entityManager
										.createQuery(
												"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
										.setParameter("idMomento", momento.getId()).getResultList();
								
								for(int m=0; m < crdMomento.size(); m++) {
									CrdEspecifico_ensayo crd=crdMomento.get(m);
									String eventoAdverso = "";
									String causalidad = "";
									String fechaInicioEA = "";
									String fechaFinEA = "";
									
									if(!hojaCrdDesasociadaEnMS(crdMomento.get(m).getHojaCrd().getId(),momento.getMomentoSeguimientoGeneral().getId())) {
										
										//List<Long> gruposVarables = Obtener los grupos de la hoja	
										
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
											
											/*if(esGraveSerioRelacionado(variable)){
												haySujetos = true;
												listaSujetos.add(sujeto.getCodigoPaciente()); //adicionar el codigo del sujeto
												eventoAdverso = getNombreEvento(variable);
												causalidad = getCausalidad(variable);
												fechaInicioEA = getFechaInicioEA(variable);
												fechaFinEA = getFechaFinEA(variable);
												EventoAdversoGrave eventoAdversoSuj = new EventoAdversoGrave(sujeto.getCodigoPaciente(), eventoAdverso, fechaInicioEA, fechaFinEA, causalidad, this.style.toString());
												eventosAdversos.add(eventoAdversoSuj);
											}*/
											
											for(int o=0; o < variable.size(); o++){
												if(variable.get(o).getVariable().getNombreVariable().equals("Gravedad") && variable.get(o).getValor().equals("Grave/Serio")){
													if(getCausalidad(variable).equals("Definitiva") || getCausalidad(variable).equals("Muy Probable") || getCausalidad(variable).equals("Probable") || getCausalidad(variable).equals("Posible")){
														haySujetos = true;
														listaSujetos.add(sujeto.getCodigoPaciente()); //adicionar el codigo del sujeto
														eventoAdverso = getNombreEvento(variable);
														causalidad = getCausalidad(variable);
														fechaInicioEA = getFechaInicioEA(variable);
														fechaFinEA = getFechaFinEA(variable);
														EventoAdversoGrave eventoAdversoSuj = new EventoAdversoGrave(sujeto.getCodigoPaciente(), eventoAdverso, fechaInicioEA, fechaFinEA, causalidad, this.style.toString());
														eventosAdversos.add(eventoAdversoSuj);
													}
												}
											}
										}								
									}
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
				reporteEventoAdversoGrave=new HashMap();
				reporteEventoAdversoGrave.put("nombreEstudio", this.estudio);
				reporteEventoAdversoGrave.put("totalSujetos", String.valueOf(totalSujetos));
				reporteEventoAdversoGrave.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteEventoAdversoGrave.put("eventoAdverso", SeamResourceBundle.getBundle().getString("eventoAdverso"));
				reporteEventoAdversoGrave.put("fechaInicioEA", SeamResourceBundle.getBundle().getString("fechaInicioEA"));
				reporteEventoAdversoGrave.put("fechaFinEA", SeamResourceBundle.getBundle().getString("fechaFinEA"));
				reporteEventoAdversoGrave.put("causalidad", SeamResourceBundle.getBundle().getString("causalidad"));				
				nombreReport=reportManager.ExportReport("reportEAG", reporteEventoAdversoGrave, eventosAdversos, FileType.HTML_FILE);
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
	
	private boolean hojaCrdDesasociadaEnMS(long idCrd,long id_momento){
		List<MomentoSeguimientoGeneralHojaCrd_ensayo> ms_crd=
				this.entityManager.createQuery("select ms_crd from MomentoSeguimientoGeneralHojaCrd_ensayo ms_crd where ms_crd.eliminado=true and ms_crd.hojaCrd.id=:id_crd_p and ms_crd.momentoSeguimientoGeneral.id=:id_momento")
				.setParameter("id_crd_p", idCrd).setParameter("id_momento", id_momento).getResultList();			
		return ms_crd.size()>0;
	}
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEAG", reporteEventoAdversoGrave, eventosAdversos,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEAG", reporteEventoAdversoGrave, eventosAdversos,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEAG", reporteEventoAdversoGrave, eventosAdversos,
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
	
	public boolean esGraveSerioRelacionado(List<VariableDato_ensayo> variables){
		boolean relacionado = false;
		for(int p=0; p < variables.size(); p++){
			if(variables.get(p).getVariable().getNombreVariable().equals("Gravedad") && variables.get(p).getValor().equals("Grave/Serio")){
				if(getCausalidad(variables).equals("No relacionada") || getCausalidad(variables).equals("Desconocida")){
					relacionado=false;
					break;
				}
				else {
					relacionado=true;
					break;
				}
			}
		}
		return relacionado;
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
	
	public String getCausalidad(List<VariableDato_ensayo> variables) {
		String causalidad="";				
		for(int n=0; n < variables.size(); n++)
			if(variables.get(n).getVariable().getNombreVariable().equals("Causalidad")){
				causalidad = variables.get(n).getValor().toString();
				break;
			}
		return causalidad;
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