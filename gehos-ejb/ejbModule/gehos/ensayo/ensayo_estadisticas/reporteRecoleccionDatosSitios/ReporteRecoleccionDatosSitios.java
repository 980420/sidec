package gehos.ensayo.ensayo_estadisticas.reporteRecoleccionDatosSitios;

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
import gehos.ensayo.ensayo_estadisticas.reporteRecoleccionDatosSitios.DatoSitio;
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
@Name("reporteRecoleccionDatosSitios")
@Scope(ScopeType.CONVERSATION)
public class ReporteRecoleccionDatosSitios{
	
	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<SujetoGeneral> sujetos;
	List<DatoSitio> datosSitios; 

	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteRecoleccionDatosSitios;
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

	//RF6 - Generar reporte cumplimiento de la recolección de datos por sitio
	public void reporteRecoleccionDatosSitios()	{
		Date fechaActual=Calendar.getInstance().getTime();
		datosSitios = new ArrayList<DatoSitio>();
		style = -1;
		Map<Long, String> listaGrupo = new HashMap<Long, String>();
		int totalCrdTotal=0;
		int totalCrdFirmada=0;
		int totalIncluidos=0;
		int totalFaltan=0;
		float totalPorcientoCumplimiento=0;
		
		Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
		for (int j = 0; j < listaEstudioEntidad.size(); j++) {
			if(listaEstudioEntidad.get(j).getNombre().equals(this.estudio)){
				estudioEnsayo = listaEstudioEntidad.get(j);
				break;
			}
		}
		
		List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager.createQuery("select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
				.setParameter("idEstudio",estudioEnsayo.getId()).getResultList();
		
		haySujetos=false;
		if(fechaFin != null && fechaInicio != null && !this.estudio.equals("")) {
			List<EstudioEntidad_ensayo> listaEntidadEst;
			listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager.createQuery("select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false and estudioEnt.estudio.eliminado = false")
					.setParameter("idEst",estudioEnsayo.getId()).getResultList();
			for (int z = 0; z < listaEntidadEst.size(); z++) {
				int crdTotal=0;
				int crdFirmada=0;
				int crdFalta=0;
				float porcientoCumplimiento=0;
				int crdNoIniciadas=0;
				int crdIniciadas=0;
				int crdCompletadas=0;
				Integer cantSujetos=0;
				
				
				for (int i = 0; i < grupos.size(); i++) {
					
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager.createQuery(
							"select sujeto from Sujeto_ensayo sujeto where sujeto.estadoInclusion.codigo=:codigoP and sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin")				
					.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())					
					.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())					
					.setParameter("idGrupo",grupos.get(i).getId())
					.setParameter("codigoP",Long.valueOf(4))
					.setParameter("fechaInicio",fechaInicio)
					.setParameter("fechaFin",fechaFin).getResultList();
					
					cantSujetos+=sujetosDelGrupo.size();
					
					if(!sujetosDelGrupo.isEmpty()) {
						haySujetos = true;
						listaGrupo.put(grupos.get(i).getId(), grupos.get(i).getNombreGrupo());
						
						for(int k=0;k<sujetosDelGrupo.size();k++) {											
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
									.createQuery(
											"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.momentoSeguimientoGeneral.programado = 'true' and momento.eliminado = false and momento.fechaInicio <=:fechaActual")
									.setParameter("idSujeto", sujeto.getId())
									.setParameter("fechaActual", fechaActual).getResultList();
																		
							for(int l=0;l<momentosSujeto.size();l++) {		
								//Para los sujetos interrumpido contar solo hasta la fecha de interrupcion
								if(sujeto.getFechaInterrupcion()!=null && momentosSujeto.get(l).getFechaInicio().before(sujeto.getFechaInterrupcion())){
									MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
									List<CrdEspecifico_ensayo> crdMomento = entityManager
											.createQuery(
													"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
											.setParameter("idMomento", momento.getId()).getResultList();
									
									for(int m=0; m < crdMomento.size(); m++) {								
										if(!hojaCrdDesasociadaEnMS(crdMomento.get(m).getHojaCrd().getId(),momento.getMomentoSeguimientoGeneral().getId())) {
											if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==1)
												crdIniciadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==2)
												crdNoIniciadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==3)
												crdCompletadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==4)
												crdFirmada++;
										}
									}
									crdTotal = crdIniciadas + crdNoIniciadas + crdCompletadas + crdFirmada;
									crdFalta = crdTotal - crdFirmada;	
									porcientoCumplimiento = (float)crdFirmada / crdTotal * 100;
									porcientoCumplimiento = (float) (Math.floor(porcientoCumplimiento * 100) / 100);
								}
								else{
									MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
									List<CrdEspecifico_ensayo> crdMomento = entityManager
											.createQuery(
													"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
											.setParameter("idMomento", momento.getId()).getResultList();
									
									for(int m=0; m < crdMomento.size(); m++) {								
										if(!hojaCrdDesasociadaEnMS(crdMomento.get(m).getHojaCrd().getId(),momento.getMomentoSeguimientoGeneral().getId())) {
											if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==1)
												crdIniciadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==2)
												crdNoIniciadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==3)
												crdCompletadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==4)
												crdFirmada++;
										}
									}
									crdTotal = crdIniciadas + crdNoIniciadas + crdCompletadas + crdFirmada;
									crdFalta = crdTotal - crdFirmada;	
									porcientoCumplimiento = (float)crdFirmada / crdTotal * 100;
									porcientoCumplimiento = (float) (Math.floor(porcientoCumplimiento * 100) / 100);
								}
							}															
						}						
					}				
				}
				
				DatoSitio datoSitio = new DatoSitio(listaEntidadEst.get(z).getEntidad().getNombre(), cantSujetos, crdTotal , crdFirmada, crdFalta, String.valueOf(porcientoCumplimiento+"%"), this.style.toString());
				datosSitios.add(datoSitio);	
				totalIncluidos+=cantSujetos;
				totalCrdFirmada+=crdFirmada;
				totalCrdTotal+=crdTotal;
				totalFaltan=totalCrdTotal-totalCrdFirmada;
			}
			if (haySujetos){						
				this.style = 1;
				
				totalPorcientoCumplimiento = (float)totalCrdFirmada / totalCrdTotal * 100;
				totalPorcientoCumplimiento = (float) (Math.floor(totalPorcientoCumplimiento * 100) / 100);
				
				DatoSitio datoSitioTotal = new DatoSitio("Total estudio", totalIncluidos, totalCrdTotal,totalCrdFirmada, totalFaltan, String.valueOf(totalPorcientoCumplimiento+"%"),this.style.toString());
				datosSitios.add(datoSitioTotal);				
				
				reporteRecoleccionDatosSitios=new HashMap();
				reporteRecoleccionDatosSitios.put("nombreEstudio", this.estudio);
				reporteRecoleccionDatosSitios.put("nombreSitio", SeamResourceBundle.getBundle().getString("nombreSitio"));
				reporteRecoleccionDatosSitios.put("totalIncluidos",SeamResourceBundle.getBundle().getString("totalIncluidos"));
				reporteRecoleccionDatosSitios.put("planMomentosProgramados", SeamResourceBundle.getBundle().getString("planMomentosProgramados"));
				reporteRecoleccionDatosSitios.put("crdFirmada", SeamResourceBundle.getBundle().getString("crdFirmada"));
				reporteRecoleccionDatosSitios.put("crdFalta", SeamResourceBundle.getBundle().getString("crdFalta"));	
				reporteRecoleccionDatosSitios.put("porcientoCumplimiento", SeamResourceBundle.getBundle().getString("porcientoCumplimiento"));
				nombreReport=reportManager.ExportReport("reporteRecoleccionDatosSitios", reporteRecoleccionDatosSitios, datosSitios, FileType.HTML_FILE);
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
		this.estudio = "";
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
					"reporteRecoleccionDatosSitios", reporteRecoleccionDatosSitios, datosSitios,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reporteRecoleccionDatosSitios", reporteRecoleccionDatosSitios, datosSitios,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reporteRecoleccionDatosSitios", reporteRecoleccionDatosSitios, datosSitios,
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

	public List<SujetoGeneral> getSujetos() {
		return sujetos;
	}

	public void setSujetos(List<SujetoGeneral> sujetos) {
		this.sujetos = sujetos;
	}

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

}