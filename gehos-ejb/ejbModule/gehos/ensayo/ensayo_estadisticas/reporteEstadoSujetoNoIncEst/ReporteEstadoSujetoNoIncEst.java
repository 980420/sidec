package gehos.ensayo.ensayo_estadisticas.reporteEstadoSujetoNoIncEst;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
@Name("reporteEstadoSujetoNoIncEst")
@Scope(ScopeType.CONVERSATION)
public class ReporteEstadoSujetoNoIncEst{
	
	private Date fechaInicio;
	private Date fechaFin;
	
	//Para darle formato a la fecha
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<EstadoSujetoNoIncEst> estadoSujetos;
	
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteEstadoSujetosNoIncEst;
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

	
	//RF8 - Generar reporte del estado de los sujetos no incluidos en el estudio
	public void reporteEstadoSujetoNoIncEst()	{
		
		estadoSujetos = new ArrayList<EstadoSujetoNoIncEst>();
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
								"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo =:nombreGrupo")
						.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())
						.setParameter("nombreGrupo", "Grupo Pesquisaje").getResultList();
				
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
						haySujetos = true;
						
						for(int k=0;k<sujetosDelGrupo.size();k++) {
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							
							String fechaEvaluacion = "";
							String causaNoInc = "";				
													
							//Buscar los MS de Pesquisaje y obtener las fecha y las causa														
							List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
									.createQuery(
											"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.eliminado = false")									
									.setParameter("idSujeto",sujeto.getId()).getResultList();
				
							for(int l=0;l<momentosSujeto.size();l++) {
								MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
								if(momento.getMomentoSeguimientoGeneral().getNombre().contains("Pesquisaje")){
									List<CrdEspecifico_ensayo> crdMomento = entityManager
											.createQuery(
													"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
											.setParameter("idMomento", momento.getId()).getResultList();
									for(int m=0; m < crdMomento.size(); m++) {
										CrdEspecifico_ensayo crd=crdMomento.get(m);
										if(!hojaCrdDesasociadaEnMS(crdMomento.get(m).getHojaCrd().getId(),momento.getMomentoSeguimientoGeneral().getId())) {
											
											List<VariableDato_ensayo> variables = entityManager
											.createQuery(
													"select variable from VariableDato_ensayo variable where variable.crdEspecifico.id =:idCrd and (variable.eliminado = false or variable.eliminado is null)")
											.setParameter("idCrd", crd.getId()).getResultList();
											
											for(int n=0; n < variables.size(); n++){
												if(variables.get(n).getVariable().getNombreVariable().equals("Fecha de la Evaluación del Paciente")){
													if(variables.get(n).getValor() != null)
														fechaEvaluacion=variables.get(n).getValor().toString();	
												}
												else if(variables.get(n).getVariable().getNombreVariable().equals("Causa")){
													if(variables.get(n).getValor() != null)
														causaNoInc=getCausaNoInc(variables.get(n).getVariable().getId(),crd.getId());
												}
											}												
										}
									}
								}								
							
								EstadoSujetoNoIncEst estadoSujeto = new EstadoSujetoNoIncEst(sujeto.getCodigoPaciente(),fechaEvaluacion,causaNoInc);
								estadoSujetos.add(estadoSujeto);
							}					
						}					
					}				
				}
			}							
			
			if (haySujetos) {
				this.style = 1;
				reporteEstadoSujetosNoIncEst=new HashMap();
				reporteEstadoSujetosNoIncEst.put("nombreEstudio", this.estudio);
				reporteEstadoSujetosNoIncEst.put("totalSujetos", String.valueOf(cantSujetos));
				reporteEstadoSujetosNoIncEst.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteEstadoSujetosNoIncEst.put("fechaEvaluacion", SeamResourceBundle.getBundle().getString("fechaEvaluacion"));
				reporteEstadoSujetosNoIncEst.put("causaNoInc", SeamResourceBundle.getBundle().getString("causaNoInc"));
				nombreReport=reportManager.ExportReport("reportEstadoSujetoNoIncEst", reporteEstadoSujetosNoIncEst, estadoSujetos, FileType.HTML_FILE);
								
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
	
	private String getCausaNoInc(long idVariable, long idCrd) {
		// TODO Auto-generated method stub
		String causas = "";
		List<VariableDato_ensayo> variables = entityManager
				.createQuery(
						"select variable from VariableDato_ensayo variable where variable.variable.id =:idVariable and variable.crdEspecifico.id =:idCrd and (variable.eliminado = false or variable.eliminado is null) ")
				.setParameter("idVariable", idVariable)
				.setParameter("idCrd", idCrd).getResultList();
		if(variables.size()<=1)
			causas=variables.get(0).getValor().toString();
		else
			for (int i = 0; i < variables.size(); i++) {
				causas+=i+1 + ". " + variables.get(i).getValor().toString() + "\n";
			}
		
		return causas;
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
					"reportEstadoSujetoNoIncEst", reporteEstadoSujetosNoIncEst, estadoSujetos,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadoSujetoNoIncEst", reporteEstadoSujetosNoIncEst, estadoSujetos,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadoSujetoNoIncEst", reporteEstadoSujetosNoIncEst, estadoSujetos,
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
	
}