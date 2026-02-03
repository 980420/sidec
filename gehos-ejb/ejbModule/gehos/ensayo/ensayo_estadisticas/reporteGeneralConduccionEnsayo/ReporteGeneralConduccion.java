package gehos.ensayo.ensayo_estadisticas.reporteGeneralConduccionEnsayo;

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
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("unchecked")
@Name("reporteGeneralConduccion")
@Scope(ScopeType.CONVERSATION)
public class ReporteGeneralConduccion{
	//sdfdfg

	private Date fechaInicio;
	private Date fechaFin;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<SujetoGeneral> sujetos;

	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	
	private Integer style;

	private Map reporteConduccion;
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
		sujetos = new ArrayList<SujetoGeneral>();
		style = -1;
		Integer cantSujetos=0;
		Integer completadosSinFirmar=0;
		Integer totalMomentosNoIniciados=0;
		Integer totalMomentosIniciados=0;
		Integer totalMomentosCompletados=0;
		Integer totalMomentosFirmados=0;
		Integer totalMomentosAtrasados=0;
		Integer totalMomentosTotal=0;	
		Integer totalCrdNoIniciadas=0;
		Integer totalCrdIniciadas=0;
		Integer totalCrdCompletadas=0;
		Integer totalCrdFirmada=0;
		Integer totalCrdTotal=0;
		Integer totalMomentosMonNoIniciados = 0;
		Integer totalMomentosMonIniciados = 0;
		Integer totalMomentosMonCompletados = 0;
		Integer totalcompletadosSinFirmar=0;
		Integer totalNotasSinResolver = 0;
		
		Map<Long, String> listaPais = new HashMap<Long, String>(); 
		Map<Long, String> listaProvicia = new HashMap<Long, String>();
		Map<Long, String> listaEntidad = new HashMap<Long, String>();
		Map<Long, String> listaGrupo = new HashMap<Long, String>();
		
		//Necesario para actualizar el estado atrasado de los momentos de seguimiento
		Calendar cal=Calendar.getInstance();
		long idEstadoSeguimientoAtra=4;
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasado=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoAtra);
		
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
					.createQuery("select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false")
					.setParameter("idEst",estudioEnsayo.getId()).getResultList();
			for (int z = 0; z < listaEntidadEst.size(); z++) {
				Boolean haySujetosRafa=false;
				List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager.createQuery("select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
						.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
				
				for (int i = 0; i < grupos.size(); i++) {
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager.createQuery(
							"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin order by sujeto.numeroInclucion")				
					.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())	
					.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
					.setParameter("idGrupo",grupos.get(i).getId()).setParameter("fechaInicio",fechaInicio)
					.setParameter("fechaFin",fechaFin).getResultList();
					
					cantSujetos+=sujetosDelGrupo.size();
					
					if(!sujetosDelGrupo.isEmpty())
					{
						haySujetos = true;
						haySujetosRafa = true;
						listaGrupo.put(grupos.get(i).getId(), grupos.get(i).getNombreGrupo());
						for(int k=0;k<sujetosDelGrupo.size();k++){
							Integer momentosNoIniciados=0;
							Integer momentosIniciados=0;
							Integer momentosCompletados=0;
							Integer momentosFirmados=0;
							Integer momentosAtrasados=0;
							Integer momentosTotal=0;
							Integer crdNoIniciadas=0;							
							Integer crdIniciadas=0;
							Integer crdCompletadas=0;
							Integer crdFirmada=0;
							Integer crdTotal=0;
							Integer momentosMonNoIniciados = 0;
							Integer momentosMonIniciados = 0;
							Integer momentosMonCompletados = 0;
							Integer notasSinResolver = 0;		
							Integer momentosMonNoIniciadosReal = 0;
							
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
									.createQuery(
											"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.eliminado = false")
									.setParameter("idSujeto", sujeto.getId()).getResultList();						
							
							
							for(int l=0;l<momentosSujeto.size();l++){								
									
								MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
								boolean shouldCountMomento=false;
								
								//Actualizar el estado de los momentos atrasados
								if(momento.getFechaFin()!=null && ((cal.getTime()).compareTo(momento.getFechaFin()) > 0) && (momento.getEstadoMomentoSeguimiento().getCodigo() == 2)){
									momento.setEstadoMomentoSeguimiento(estadoMomentoAtrasado);
									entityManager.persist(momento);
								}
								
								// Estado de los momentos si el paciente es interrumpido	
								if(sujeto.getFechaInterrupcion()!=null)	{									
									if(momento.getFechaInicio().before(sujeto.getFechaInterrupcion())){
										
											if(momento.getEstadoMomentoSeguimiento().getCodigo()==1)
												momentosIniciados++;
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==2)
												momentosNoIniciados++;
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==3)
												momentosCompletados++;									
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
												momentosAtrasados++;
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==5)
												momentosFirmados++;	
											
											shouldCountMomento=true;
										
									}else {
										if(!momento.getMomentoSeguimientoGeneral().getProgramado()){
											if(momento.getEstadoMomentoSeguimiento().getCodigo()==1)
												momentosIniciados++;
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==2)
												momentosNoIniciados++;
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==3)
												momentosCompletados++;									
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
												momentosAtrasados++;
											else if(momento.getEstadoMomentoSeguimiento().getCodigo()==5)
												momentosFirmados++;	
											shouldCountMomento=true;
										}
									}
																	
								} else{
										//Estado de los momentos si el paciente no es interrumpido	
										if(momento.getEstadoMomentoSeguimiento().getCodigo()==1)
											momentosIniciados++;
										else if(momento.getEstadoMomentoSeguimiento().getCodigo()==2)
											momentosNoIniciados++;
										else if(momento.getEstadoMomentoSeguimiento().getCodigo()==3)
											momentosCompletados++;	
										//else if(sujeto.getFechaInterrupcion()!=null && momentosSujeto.get(l).getFechaInicio().before(sujeto.getFechaInterrupcion()) && momento.getEstadoMomentoSeguimiento().getCodigo()==4 )
										else if(momento.getEstadoMomentoSeguimiento().getCodigo()==4)
											momentosAtrasados++;
										else if(momento.getEstadoMomentoSeguimiento().getCodigo()==5)
											momentosFirmados++;
										shouldCountMomento=true;
								}	
								
								//Estado del monitoreo de los momentos
								
								if(shouldCountMomento){
									if (momento.getEstadoMonitoreo().getCodigo() == 1)
										momentosMonIniciados++;
									else if (momento.getEstadoMonitoreo().getCodigo() == 2)
										momentosMonNoIniciados++;
									else if (momento.getEstadoMonitoreo().getCodigo() == 3)
										momentosMonCompletados++;
									
									List<CrdEspecifico_ensayo> crdMomento = entityManager
											.createQuery(
													"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false " )
											.setParameter("idMomento", momento.getId()).getResultList();
									
									for(int m=0;m<crdMomento.size();m++){								
										//Estado de las hojas CRD
										if(!hojaCrdDesasociadaEnMS(crdMomento.get(m).getHojaCrd().getId(),momento.getMomentoSeguimientoGeneral().getId())){
											if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==1)
												crdIniciadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==2)
												crdNoIniciadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==3)
												crdCompletadas++;
											else if(crdMomento.get(m).getEstadoHojaCrd().getCodigo()==4)
												crdFirmada++;
											
											CrdEspecifico_ensayo crd = crdMomento.get(m);
											List<Nota_ensayo> notas = entityManager.createQuery(
															"select nota from Nota_ensayo nota where nota.crdEspecifico.id =:idCRD and nota.notaSitio = false and nota.eliminado = false and nota.notaPadre = null")
													.setParameter("idCRD",crd.getId()).getResultList();
		
											for (int y = 0; y < notas.size(); y++) {
												if(notas.get(y).getEstadoNota().getCodigo() == 1 || notas.get(y).getEstadoNota().getCodigo() == 2){
													notasSinResolver ++;
												}
											}
										}							
									}
								}								
								
								crdTotal = crdNoIniciadas + crdIniciadas + crdCompletadas + crdFirmada;
								//Monitoreo no iniciados son solo aquellos que deben iniciar, quitando los momentos que no estan completados ya que sin estar completados no se puede iniciar el monitoreo
								momentosMonNoIniciadosReal = momentosMonNoIniciados - momentosNoIniciados - momentosAtrasados;
								
							}
							momentosTotal = momentosIniciados + momentosCompletados + momentosAtrasados + momentosFirmados;
							completadosSinFirmar=momentosMonCompletados-momentosFirmados;							
							SujetoGeneral sujetoGeneral = new SujetoGeneral(sujeto.getCodigoPaciente(), momentosIniciados, momentosCompletados, momentosFirmados, momentosAtrasados, momentosTotal, crdNoIniciadas, crdIniciadas, crdCompletadas, crdFirmada, crdTotal, momentosMonNoIniciadosReal, momentosMonIniciados, momentosMonCompletados,completadosSinFirmar,notasSinResolver, listaEntidadEst.get(z).getEntidad().getEstado().getNacion().getValor(), listaEntidadEst.get(z).getEntidad().getEstado().getValor(), listaEntidadEst.get(z).getEntidad().getFax(), grupos.get(i).getNombreGrupo(), this.style.toString());
							sujetos.add(sujetoGeneral);
							
							//Valores de la fila de totales
							totalMomentosNoIniciados+=momentosNoIniciados;
							totalMomentosIniciados+=momentosIniciados;
							totalMomentosCompletados+=momentosCompletados;
							totalMomentosTotal+=momentosTotal;
							totalMomentosAtrasados+=momentosAtrasados;
							totalMomentosFirmados+=momentosFirmados;
							totalCrdNoIniciadas+=crdNoIniciadas;
							totalCrdIniciadas+=crdIniciadas;
							totalCrdCompletadas+=crdCompletadas;
							totalCrdTotal+=crdTotal;
							totalCrdFirmada+=crdFirmada;
							totalMomentosMonNoIniciados+=momentosMonNoIniciadosReal;
							totalMomentosMonIniciados +=momentosMonIniciados ;
							totalMomentosMonCompletados += momentosMonCompletados;
							totalNotasSinResolver += notasSinResolver;
							totalcompletadosSinFirmar+=completadosSinFirmar;							 
						}					
					}						
				}
				if(haySujetosRafa){
					listaEntidad.put(listaEntidadEst.get(z).getEntidad().getId(), listaEntidadEst.get(z).getEntidad().getNombre());
					listaProvicia.put(listaEntidadEst.get(z).getEntidad().getEstado().getId(), listaEntidadEst.get(z).getEntidad().getEstado().getValor());
					listaPais.put(listaEntidadEst.get(z).getEntidad().getEstado().getNacion().getId(), listaEntidadEst.get(z).getEntidad().getEstado().getNacion().getValor());
				}				
			}
				this.style = 1;
				 SujetoGeneral sujetoGeneral1 = new SujetoGeneral("Total estudio", totalMomentosIniciados, totalMomentosCompletados, totalMomentosFirmados, totalMomentosAtrasados, totalMomentosTotal, totalCrdNoIniciadas, totalCrdIniciadas, totalCrdCompletadas, totalCrdFirmada, totalCrdTotal, totalMomentosMonNoIniciados, totalMomentosMonIniciados, totalMomentosMonCompletados, totalcompletadosSinFirmar, totalNotasSinResolver, String.valueOf(listaPais.size()),String.valueOf(listaProvicia.size()),String.valueOf(listaEntidad.size()),String.valueOf(listaGrupo.size()),this.style.toString());
				 sujetos.add(sujetoGeneral1);
			
			
			
			if (haySujetos) {				
				reporteConduccion=new HashMap();				
				reporteConduccion.put("nombreEstudio", this.estudio);
				reporteConduccion.put("cantSujetos", cantSujetos.toString());				
				reporteConduccion.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteConduccion.put("momentoSeguimiento", SeamResourceBundle.getBundle().getString("momentoSeguimiento"));
				reporteConduccion.put("momentosIniciados", SeamResourceBundle.getBundle().getString("momentosIniciados"));
				reporteConduccion.put("momentosCompletados", SeamResourceBundle.getBundle().getString("momentosCompletados"));
				reporteConduccion.put("momentosFirmados", SeamResourceBundle.getBundle().getString("momentosFirmados"));
				reporteConduccion.put("momentosAtrasados", SeamResourceBundle.getBundle().getString("momentosAtrasados"));
				reporteConduccion.put("momentosTotal", SeamResourceBundle.getBundle().getString("momentosTotal"));
				reporteConduccion.put("hojaCrd", SeamResourceBundle.getBundle().getString("hojaCrd"));
				reporteConduccion.put("crdNoIniciadas", SeamResourceBundle.getBundle().getString("crdNoIniciadas"));
				reporteConduccion.put("crdIniciadas", SeamResourceBundle.getBundle().getString("crdIniciadas"));
				reporteConduccion.put("crdCompletadas", SeamResourceBundle.getBundle().getString("crdCompletadas"));
				reporteConduccion.put("crdFirmadas", SeamResourceBundle.getBundle().getString("crdFirmadas"));
				reporteConduccion.put("crdTotal", SeamResourceBundle.getBundle().getString("crdTotal"));				
				reporteConduccion.put("monitoreoMomento", SeamResourceBundle.getBundle().getString("monitoreoMomento"));
				reporteConduccion.put("monitoreoNoIniciados", SeamResourceBundle.getBundle().getString("monitoreoNoIniciados"));
				reporteConduccion.put("monitoreoIniciados", SeamResourceBundle.getBundle().getString("monitoreoIniciados"));
				reporteConduccion.put("monitoreoCompletados", SeamResourceBundle.getBundle().getString("monitoreoCompletados"));
				reporteConduccion.put("completadosSinFirmar", SeamResourceBundle.getBundle().getString("completadosSinFirmar"));
				reporteConduccion.put("notasSinResolver", SeamResourceBundle.getBundle().getString("notasSinResolver"));
				reporteConduccion.put("pais", SeamResourceBundle.getBundle().getString("pais"));
				reporteConduccion.put("provincia", SeamResourceBundle.getBundle().getString("provincia"));
				reporteConduccion.put("entidad", SeamResourceBundle.getBundle().getString("entidad"));
				reporteConduccion.put("grupo", SeamResourceBundle.getBundle().getString("grupo"));
				
				//reporteConduccion.put("entidades", SeamResourceBundle.getBundle().getString("entidades"));
				//reporteConduccion.put("totalEstudio", SeamResourceBundle.getBundle().getString("totalEstudio"));
				
				
				nombreReport=reportManager.ExportReport("reportGeneralConduccion", reporteConduccion, sujetos, FileType.HTML_FILE);
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
					"reportGeneralConduccion", reporteConduccion, sujetos,
					FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportGeneralConduccion", reporteConduccion, sujetos,
					FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportGeneralConduccion", reporteConduccion, sujetos,
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
