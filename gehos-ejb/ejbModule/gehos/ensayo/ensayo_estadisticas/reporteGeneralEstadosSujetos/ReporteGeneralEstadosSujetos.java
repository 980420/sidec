package gehos.ensayo.ensayo_estadisticas.reporteGeneralEstadosSujetos;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_estadisticas.entity.Ensayo;
import gehos.ensayo.ensayo_estadisticas.entity.Entidad;
import gehos.ensayo.ensayo_estadisticas.entity.EstadoSujetoGeneral;
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
import gehos.ensayo.entity.Nacion_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

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
@Name("reporteGeneralEstadosSujetos")
@Scope(ScopeType.CONVERSATION)
public class ReporteGeneralEstadosSujetos{
	//Rafa

	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	@In
	private Usuario user;
	
	
	List<EstadoSujetoGeneral> listaSujetos;

	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteGeneralEstado;
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

	List<Pais> naciones = new ArrayList<Pais>();

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

	public void reporteConduccion()	{
		
		listaSujetos= new ArrayList<EstadoSujetoGeneral>();
		this.style = -1;
		//yopo
		Integer totalEvaluacion=0;
		Integer totalNoIncluido=0;
		Integer totalIncluido=0;
		Integer totalMalIncluido=0;
		Integer totalInterrupcion=0;
		Map<Long, String> listaPais = new HashMap<Long, String>(); 
		Map<Long, String> listaProvicia = new HashMap<Long, String>();
		Map<Long, String> listaEntidad = new HashMap<Long, String>();
		
		
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
			listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager.createQuery("select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false")
					.setParameter("idEst",estudioEnsayo.getId()).getResultList();
			for (int z = 0; z < listaEntidadEst.size(); z++) {
				Boolean haySujetosRafa=false;
				List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager.createQuery("select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
						.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
				
				for (int i = 0; i < grupos.size(); i++) {
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager.createQuery(
							"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin")				
					.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())	
					.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
					.setParameter("idGrupo",grupos.get(i).getId()).setParameter("fechaInicio",fechaInicio)
					.setParameter("fechaFin",fechaFin).getResultList();
					
					Integer Evaluados=0;
					Integer NoIncluidos=0;
					Integer Incluidos=0;
					Integer MalIncluidos=0;
					Integer Interrumpidos=0;
					
					if(!sujetosDelGrupo.isEmpty())
					{
						haySujetos = true;
						haySujetosRafa = true;
						//listaGrupo.put(grupos.get(i).getId(), grupos.get(i).getNombreGrupo());
						for(int k=0;k<sujetosDelGrupo.size();k++)
						{
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							if(sujeto.getEstadoInclusion().getCodigo()==1){
								Evaluados++;
							}else if(sujeto.getEstadoInclusion().getCodigo()==2){
								MalIncluidos++;
							}else if(sujeto.getEstadoInclusion().getCodigo()==3){
								NoIncluidos++;
							}else if(sujeto.getEstadoInclusion().getCodigo()==4){
								Incluidos++;
								
							}
							
							if(sujeto.getEstadoTratamiento().getCodigo() == 4){
								Interrumpidos++;
							}
						}
						EstadoSujetoGeneral estadoSujeto = new EstadoSujetoGeneral(grupos.get(i).getNombreGrupo(), Evaluados, NoIncluidos, Incluidos, MalIncluidos, Interrumpidos, listaEntidadEst.get(z).getEntidad().getEstado().getNacion().getValor(), listaEntidadEst.get(z).getEntidad().getEstado().getValor(), listaEntidadEst.get(z).getEntidad().getFax(), this.style.toString());
						listaSujetos.add(estadoSujeto);
						 totalEvaluacion+=Evaluados;
						 totalNoIncluido+=NoIncluidos;
						 totalIncluido+=Incluidos;
						 totalMalIncluido+=MalIncluidos;
						 totalInterrupcion+=Interrumpidos;
					
					}
				}
				if(haySujetosRafa){
					listaEntidad.put(listaEntidadEst.get(z).getEntidad().getId(), listaEntidadEst.get(z).getEntidad().getNombre());
					listaProvicia.put(listaEntidadEst.get(z).getEntidad().getEstado().getId(), listaEntidadEst.get(z).getEntidad().getEstado().getValor());
					listaPais.put(listaEntidadEst.get(z).getEntidad().getEstado().getNacion().getId(), listaEntidadEst.get(z).getEntidad().getEstado().getNacion().getValor());
				}
				
			}
				 this.style = 1;
				 EstadoSujetoGeneral sujetoGeneral1 = new EstadoSujetoGeneral("Total estudio", totalEvaluacion, totalNoIncluido, totalIncluido, totalMalIncluido, totalInterrupcion,String.valueOf(listaPais.size()),String.valueOf(listaProvicia.size()),String.valueOf(listaEntidad.size()),this.style.toString());
				 listaSujetos.add(sujetoGeneral1);
			
			
			
			if (haySujetos) {
				
				reporteGeneralEstado=new HashMap();
				
				reporteGeneralEstado.put("pais", SeamResourceBundle.getBundle().getString("pais"));
				reporteGeneralEstado.put("sujetos", SeamResourceBundle.getBundle().getString("sujetos1"));
				reporteGeneralEstado.put("provincia", SeamResourceBundle.getBundle().getString("provincia"));
				reporteGeneralEstado.put("entidades", SeamResourceBundle.getBundle().getString("entidades"));
				reporteGeneralEstado.put("estudio", SeamResourceBundle.getBundle().getString("estudio"));
				reporteGeneralEstado.put("grupo", SeamResourceBundle.getBundle().getString("grupo"));
				reporteGeneralEstado.put("entidad", SeamResourceBundle.getBundle().getString("entidad"));
				reporteGeneralEstado.put("nombreEstudio", this.estudio);
				reporteGeneralEstado.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				
				reporteGeneralEstado.put("totalEstudio", SeamResourceBundle.getBundle().getString("totalEstudio"));
				reporteGeneralEstado.put("notasSinResolver", SeamResourceBundle.getBundle().getString("notasSinResolver"));
				reporteGeneralEstado.put("evaluado", SeamResourceBundle.getBundle().getString("evaluado"));
				reporteGeneralEstado.put("noIncluido", SeamResourceBundle.getBundle().getString("noIncluido"));
				reporteGeneralEstado.put("incluido", SeamResourceBundle.getBundle().getString("incluido"));
				reporteGeneralEstado.put("malIncluido", SeamResourceBundle.getBundle().getString("malIncluido"));
				reporteGeneralEstado.put("interrumpido", SeamResourceBundle.getBundle().getString("interrumpido"));
				
				
				nombreReport=reportManager.ExportReport("reportGeneralEstadosSujetos", reporteGeneralEstado, listaSujetos, FileType.HTML_FILE);
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
			pathExportedReport = reportManager.ExportReport(
					"reportGeneralEstadosSujetos", reporteGeneralEstado, listaSujetos,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportGeneralEstadosSujetos", reporteGeneralEstado, listaSujetos,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportGeneralEstadosSujetos", reporteGeneralEstado, listaSujetos,
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

	public List<EstadoSujetoGeneral> getListaSujetos() {
		return listaSujetos;
	}

	public void setListaSujetos(List<EstadoSujetoGeneral> listaSujetos) {
		this.listaSujetos = listaSujetos;
	}

	

}
