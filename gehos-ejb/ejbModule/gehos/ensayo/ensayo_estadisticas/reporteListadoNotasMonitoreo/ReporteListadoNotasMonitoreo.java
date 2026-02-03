package gehos.ensayo.ensayo_estadisticas.reporteListadoNotasMonitoreo;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoNota_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

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












import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("unchecked")
@Name("reporteListadoNotasMonitoreo")
@Scope(ScopeType.CONVERSATION)
public class ReporteListadoNotasMonitoreo{
	
	private Date fechaInicio;
	private Date fechaFin;
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<NotasMonitoreo> notasMonitoreo;
	List<String> listaSujetos = new ArrayList<String>(); //lista para almacenar los sujetos y luego contar los valores unicos
	
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteListadoNotasMonitoreo;
	private String nombreReport;
	private String pathExportedReport = "";

	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	
	private List<Estudio_ensayo> listaEstudioEntidad;
	private List<Entidad_ensayo> listaEntidadEst;
	private List<EstudioEntidad_ensayo> listaEntidadEstSeleccionado;
	private List<GrupoSujetos_ensayo> listaGrupoEstSeleccionado;
	private List<String> listarEstudios;
	private List<String> listarEntidades;
	private List<String> listarEntidadesEstSeleccionado = new ArrayList<String>();
	private List<String> listarGruposEstSeleccionado = new ArrayList<String>();
	private String estudio;
	private String entidad;
	private String grupo;
	private String estadoN = "";
	
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
		listarEntidadesEstSeleccionado.add("<Seleccione>");
		for (int j = 0; j < listaEntidadEstSeleccionado.size(); j++) {
			listarEntidadesEstSeleccionado.add(listaEntidadEstSeleccionado.get(j).getEntidad().getNombre());			
		}
		cargarGruposEstudio();
		this.entidad = "";
		this.grupo = "";
		this.estadoN = "";
	}
	
	public void cargarGruposEstudio(){
		listarGruposEstSeleccionado.clear();
		listaGrupoEstSeleccionado = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.nombre=:nomEst and grupo.nombreGrupo <> 'Grupo Validación'")
				.setParameter("nomEst",this.estudio).getResultList();
		listarGruposEstSeleccionado.add("<Seleccione>");
		for (int i = 0; i < listaGrupoEstSeleccionado.size(); i++) {
			listarGruposEstSeleccionado.add(listaGrupoEstSeleccionado.get(i).getNombreGrupo());		
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
	
	@SuppressWarnings("unchecked")
	public List<String> listEstadoN() {
		List<EstadoNota_ensayo> listarEstados = (List<EstadoNota_ensayo>) entityManager
				.createQuery("select e from EstadoNota_ensayo e order by e.codigo ASC")
				.getResultList();
		List<String> nombreEst = new ArrayList<String>();
		nombreEst.add("<Seleccione>");
		for (int i = 0; i < listarEstados.size(); i++) {
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}

	//RF3 - Generar reporte de listado de notas de monitoreo 
	public void listadoNotasMonitoreo()	{
		
		notasMonitoreo = new ArrayList<NotasMonitoreo>();
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
			listaSujetos.clear();
			List<EstudioEntidad_ensayo> listaEntidadEst;
			if(this.entidad.equals("")  || this.entidad.equals("<Seleccione>")){				
				listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
						.createQuery(
								"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false")
						.setParameter("idEst",estudioEnsayo.getId()).getResultList();				
			}
			else{
				listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
						.createQuery(
								"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false and estudioEnt.entidad.nombre=:entidadNom and estudioEnt.entidad.eliminado = false")
						.setParameter("idEst",estudioEnsayo.getId())
						.setParameter("entidadNom", this.entidad).getResultList();
			}
				
			for (int z = 0; z < listaEntidadEst.size(); z++){
				List<GrupoSujetos_ensayo> grupos;
				if(this.grupo.equals("")  || this.grupo.equals("<Seleccione>")){
					grupos = (List<GrupoSujetos_ensayo>) entityManager
							.createQuery(
									"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
				}
				else{
					grupos = (List<GrupoSujetos_ensayo>) entityManager
							.createQuery(
									"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo=:grupoNom")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())
							.setParameter("grupoNom", this.grupo).getResultList();
				}
				for (int i = 0; i < grupos.size(); i++) {
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager
							.createQuery(
									"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false")	// and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin			
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())	
							.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
							.setParameter("idGrupo",grupos.get(i).getId()).getResultList();
					
					cantSujetos+=sujetosDelGrupo.size();
					
					if(!sujetosDelGrupo.isEmpty()) {
						for(int k=0;k<sujetosDelGrupo.size();k++) {
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							List<Nota_ensayo> notasSujeto;
							if(estadoN.equals("") || estadoN.equals("<Seleccione>")){
								notasSujeto = entityManager
										.createQuery(
												"select nota from Nota_ensayo nota where nota.eliminado = false and nota.notaPadre = null and nota.notaSitio = false and nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id=:idSujeto and nota.fechaCreacion >=:fechaInicio and nota.fechaCreacion <=:fechaFin")									
										.setParameter("idSujeto",sujeto.getId())
										.setParameter("fechaInicio",fechaInicio)
										.setParameter("fechaFin",fechaFin).getResultList();
							}
							else{
								notasSujeto = entityManager
										.createQuery(
												"select nota from Nota_ensayo nota where nota.eliminado = false and nota.notaPadre = null and nota.notaSitio = false and nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id=:idSujeto and nota.fechaCreacion >=:fechaInicio and nota.fechaCreacion <=:fechaFin and nota.estadoNota.nombre=:estadoNota")									
										.setParameter("idSujeto",sujeto.getId())
										.setParameter("fechaInicio",fechaInicio)
										.setParameter("fechaFin",fechaFin)
										.setParameter("estadoNota", this.estadoN).getResultList();
							}
								
							for(int m=0; m < notasSujeto.size(); m++) {
								Nota_ensayo nota=notasSujeto.get(m);
								String fechaCreacionNota="";
								String fechaActualizacionNota="";
								String fechaMS="";
								String estadoMS="";
								String nombreCRD="";
								String nombreVariable="";
								String valorVariable="";
								String descripcion="";
								String detallesNota="";
								String estadoNota="";
								int numeroNotas=0;								
												
								haySujetos = true;
								fechaCreacionNota = formatter.format(nota.getFechaCreacion());
								if(nota.getFechaActualizacion() != null)
									fechaActualizacionNota = formatter.format(nota.getFechaActualizacion());
								fechaMS = formatter.format(nota.getCrdEspecifico().getMomentoSeguimientoEspecifico().getFechaInicio());
								estadoMS = nota.getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre().toString() + " (" + nota.getCrdEspecifico().getMomentoSeguimientoEspecifico().getEstadoMomentoSeguimiento().getNombre() + ")";
								nombreCRD = nota.getCrdEspecifico().getHojaCrd().getNombreHoja();
								nombreVariable = nota.getVariable().getNombreVariable();	
																
								if(nota.getVariableDato() != null)
									valorVariable=nota.getVariableDato().getValor();
								else {
									//Obtener el valor de la variable (desde la tabla nota en ocaciones el valor id_variable_dato viene null)
									VariableDato_ensayo variables = (VariableDato_ensayo) entityManager
											.createQuery(
													"select variable from VariableDato_ensayo variable where variable.crdEspecifico.id =:idCrd and variable.variable.id =:idVariable and (variable.eliminado = false or variable.eliminado is null)")
											.setParameter("idCrd", nota.getCrdEspecifico().getId())
											.setParameter("idVariable", nota.getVariable().getId()).getSingleResult();
									
									valorVariable=variables.getValor();									
								}									
								
								descripcion = nota.getDescripcion();
								detallesNota = nota.getDetallesNota();
								estadoNota = nota.getEstadoNota().getNombre();
								numeroNotas = nota.getNotasHijas().size();
								
								listaSujetos.add(sujeto.getCodigoPaciente()); //adicionar el codigo del sujeto
								NotasMonitoreo notaMonitoreoSuj = new NotasMonitoreo(sujeto.getCodigoPaciente(), fechaCreacionNota, fechaActualizacionNota, fechaMS, estadoMS, nombreCRD, nombreVariable, valorVariable, descripcion, detallesNota, estadoNota, numeroNotas, this.style.toString());
								notasMonitoreo.add(notaMonitoreoSuj);
							}								
						}							 
					}					
				}
			} 
						
			if (haySujetos) {
				this.style = 1;
				Set<String> distintSet = new HashSet<String>(listaSujetos); //Valores distindos dentro de la lista
				int totalSujetos = distintSet.size(); // total de valores distintos
				reporteListadoNotasMonitoreo=new HashMap();
				reporteListadoNotasMonitoreo.put("nombreEstudio", this.estudio);
				reporteListadoNotasMonitoreo.put("totalSujetos", totalSujetos);
				reporteListadoNotasMonitoreo.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteListadoNotasMonitoreo.put("fechaCreacionNota", SeamResourceBundle.getBundle().getString("fechaCreacionNota"));
				reporteListadoNotasMonitoreo.put("fechaActualizacionNota", SeamResourceBundle.getBundle().getString("fechaActualizacionNota"));
				reporteListadoNotasMonitoreo.put("fechaMS", SeamResourceBundle.getBundle().getString("fechaMS"));
				reporteListadoNotasMonitoreo.put("estadoMS", SeamResourceBundle.getBundle().getString("estadoMS"));
				reporteListadoNotasMonitoreo.put("nombreCRD", SeamResourceBundle.getBundle().getString("nombreCRD"));
				reporteListadoNotasMonitoreo.put("nombreVariable", SeamResourceBundle.getBundle().getString("nombreVariable"));
				reporteListadoNotasMonitoreo.put("valorVariable", SeamResourceBundle.getBundle().getString("valorVariable"));
				reporteListadoNotasMonitoreo.put("descripcionNota", SeamResourceBundle.getBundle().getString("descripcionNota"));
				reporteListadoNotasMonitoreo.put("detallesNota", SeamResourceBundle.getBundle().getString("detallesNota"));
				reporteListadoNotasMonitoreo.put("estadoNota", SeamResourceBundle.getBundle().getString("estadoNota"));	
				reporteListadoNotasMonitoreo.put("numeroNotas", SeamResourceBundle.getBundle().getString("numeroNotas"));	
				nombreReport=reportManager.ExportReport("reportListadoNotasMonitoreo", reporteListadoNotasMonitoreo, notasMonitoreo, FileType.HTML_FILE);
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
		this.entidad = "";
		this.grupo = "";
		this.estadoN = "";
		this.fechaInicio=null;
		this.fechaFin=null;
	}
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportListadoNotasMonitoreo", reporteListadoNotasMonitoreo, notasMonitoreo, FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportListadoNotasMonitoreo", reporteListadoNotasMonitoreo, notasMonitoreo, FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportListadoNotasMonitoreo", reporteListadoNotasMonitoreo, notasMonitoreo, FileType.EXCEL_FILE);
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
		
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	public List<String> getListarGruposEstSeleccionado() {
		return listarGruposEstSeleccionado;
	}

	public void setListarGruposEstSeleccionado(
			List<String> listarGruposEstSeleccionado) {
		this.listarGruposEstSeleccionado = listarGruposEstSeleccionado;
	}

	public String getEstadoN() {
		return estadoN;
	}

	public void setEstadoN(String estadoN) {
		this.estadoN = estadoN;
		if(this.estadoN.equals("<Seleccione>")){
			this.estadoN = "";
		}
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