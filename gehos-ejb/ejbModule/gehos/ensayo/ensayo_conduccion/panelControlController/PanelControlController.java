package gehos.ensayo.ensayo_conduccion.panelControlController;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.reportes.session.FileType;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.gestionarEstudio.verEstudioControlador;
import gehos.ensayo.ensayo_estadisticas.entity.SujetoGeneral;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
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

@Name("panelControlController")
@Scope(ScopeType.CONVERSATION)
public class PanelControlController {

	protected @In
	EntityManager entityManager;
	
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;

	private Long sujetosIncluidosProcesoInclusion;
	private Long sujetosEvaluadosProcesoInclusion;

	public Long evaluadosProcesoInclusion() {

		try {
			
			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select  count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "where es.id = :idEstudiodActivo "
									+ "and s.eliminado is false "
									+ "and gs.eliminado is false "
									+ "and gs.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEstudiodActivo",
							seguridadEstudio.getActiveStudyId()).getSingleResult();

			sujetosEvaluadosProcesoInclusion = result.longValue();			
			
			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error evaluadosProcesoInclusion: "
					+ e.getMessage());

			return null;
		}
	}

	public Long incluidosProcesoInclusion() {

		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select  count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "where es.id = :idEstudioActivo "
									+ "and s.eliminado is false "
									+ "and gs.eliminado is false "
									+ "and gs.nombre_grupo <> 'Grupo Validación' "
									+ "and s.id_estado_inclusion = 4")
					.setParameter("idEstudioActivo",
							seguridadEstudio.getActiveStudyId()).getSingleResult();

			sujetosIncluidosProcesoInclusion = result.longValue();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error incluidosProcesoInclusion: "
					+ e.getMessage());
			return null;
		}
	}

	public float porCientoReclutamientoProcesoInclusion() {

		try {

			BigInteger totalSujetosEstudiosEntidad = (BigInteger) entityManager
					.createNativeQuery(
							"select  count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "join ensayo.estudio_entidad eu on eu.id_estudio = es.id "
									+ "join comun.entidad en on en.id = eu.id_entidad "
									+ "where s.id_entidad = :idEntidadActiva "
									+ "and eu.eliminado is false "
									+ "and gs.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEntidadActiva",
							verEstudioControlador.idEntidad).getSingleResult();

			//return (sujetosIncluidosProcesoInclusion * 100) / totalSujetosEstudiosEntidad.longValue();
			float resultado = (float) (sujetosIncluidosProcesoInclusion * 100) / totalSujetosEstudiosEntidad.longValue();
			resultado = (float) (Math.round(resultado * 100.0) / 100.0);
			
			return resultado;

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error porCientoReclutamientoProcesoInclusion: "
					+ e.getMessage());
			return 0;
		}
	}

	public float porCientoInclusionProcesoInclusion() {

		try {
			//return (sujetosIncluidosProcesoInclusion * 100) / sujetosEvaluadosProcesoInclusion;			
			float resultado = (float) (sujetosIncluidosProcesoInclusion * 100) / sujetosEvaluadosProcesoInclusion;
			resultado = (float) (Math.round(resultado * 100.0) / 100.0);
		
			return resultado;

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error porCientoInclusionProcesoInclusion: "
					+ e.getMessage());
			return 0;
		}
	}

	/** Proceso Evaluacion **/

	public Long sujetosEnEvaluacionProcesoEvaluacion() {

		try {

			/*BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "join ensayo.estudio_entidad eu on eu.id_estudio = es.id "
									+ "join comun.entidad en on en.id = eu.id_entidad "
									+ "where s.id_entidad = :idEntidadActiva "
									+ "and s.eliminado is false "
									+ "and s.id_estado_tratamiento = 1 "
									+ "and (es.id_estado = 3 OR es.id_estado = 6)")
					.setParameter("idEntidadActiva",
							verEstudioControlador.idEntidad).getSingleResult();*/
			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "where es.id = :idEstudioActivo "
									+ "and s.eliminado is false "
									+ "and s.id_estado_tratamiento = 1 "
									+ "and gs.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEstudioActivo",
							seguridadEstudio.getActiveStudyId()).getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error sujetosEnEvaluacionProcesoEvaluacion: "
					+ e.getMessage());
			return null;
		}
	}

	public Long sujetosEnTratamientoEvaluacionProcesoEvaluacion() {

		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "where es.id = :idEstudioActivo "
									+ "and s.eliminado is false "
									+ "and s.id_estado_tratamiento = 2 "
									+ "and gs.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEstudioActivo",
							seguridadEstudio.getActiveStudyId()).getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error sujetosEnTratamientoProcesoEvaluacion: "
					+ e.getMessage());
			return null;
		}
	}

	public Long sujetosEnSeguimientoEvaluacionProcesoEvaluacion() {

		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "where es.id = :idEstudioActivo "
									+ "and s.eliminado is false "
									+ "and s.id_estado_tratamiento = 3 "
									+ "and gs.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEstudioActivo",
							seguridadEstudio.getActiveStudyId()).getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error sujetosEnSeguimientoProcesoEvaluacion: "
					+ e.getMessage());
			return null;
		}
	}

	public Long sujetosTerminadosEvaluacionProcesoEvaluacion() {

		try {			
			BigInteger result = (BigInteger) entityManager.createNativeQuery("SELECT ensayo.dashboard_total_sujetos_terminados(:idEstudioParam)")
			          .setParameter("idEstudioParam", seguridadEstudio.getEstudioActivo().getId())
			          .getSingleResult();
			
			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error sujetosTerminadosProcesoEvaluacion: "
					+ e.getMessage());
			return null;
		}
	}

	public Long sujetosInterrumpidosEvaluacionProcesoEvaluacion() {

		try {

			BigInteger result = (BigInteger) entityManager
					.createNativeQuery(
							"select count(s.*) from ensayo.sujeto s "
									+ "join ensayo.grupo_sujetos gs on gs.id = s.id_grupo_sujetos "
									+ "join ensayo.estudio es on es.id = gs.id_estudio "
									+ "where es.id = :idEstudioActivo "
									
									+ "and s.eliminado is false "
									+ "and s.id_estado_tratamiento = 4 "
									+ "and gs.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEstudioActivo",
							seguridadEstudio.getActiveStudyId()).getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error sujetosInterrumpidosProcesoEvaluacion: "
					+ e.getMessage());
			return null;
		}
	}	
	
	/*** PROCESO COMPLETAMIENTO DE DATOS URQUIJO ***/
	public Integer noIniciadosProcesoCompletamientoDatos = 0;
	public Integer iniciadosProcesoCompletamientoDatos = 0;
	public Integer completadosProcesoCompletamientoDatos = 0;
	public Integer atrasadosProcesoCompletamientoDatos = 0;
	public Integer firmadosProcesoCompletamientoDatos = 0;	
	
	/*** PROCESO DE MONITOREO URQUIJO***/
	public Integer iniciadosProcesoMonitoreo = 0;
	public Integer noIniciadosProcesoMonitoreo = 0;
	public Integer completadosProcesoMonitoreo = 0;
	public Integer notasSinResolverProcesoMonitoreo = 0;
	public Integer completadosPendienteFirmaProcesoMonitoreo = 0;
	
	@In private Usuario user;
	private List<Estudio_ensayo> listaEstudioEntidad;
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void cargarEstudios() {
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class, user.getId());
		listaEstudioEntidad = new ArrayList<Estudio_ensayo>();
		listaEstudioEntidad = (List<Estudio_ensayo>) entityManager
				.createQuery("select distinct estudioEnt.estudioEntidad.estudio from UsuarioEstudio_ensayo estudioEnt where estudioEnt.estudioEntidad.estudio.eliminado = false and estudioEnt.usuario.id=:idUser")
				.setParameter("idUser", usuario.getId())
				.getResultList();	
	}
	public void procesoCompletamientoDatosP() {

		try {			
			Object[] results = (Object[]) entityManager.createNativeQuery("SELECT * FROM ensayo.dashboard_data_completion_process(:idEstudioParam)")
		              .setParameter("idEstudioParam", seguridadEstudio.getEstudioActivo().getId())
		              .getSingleResult();

		    // Obtener los valores devueltos en variables individuales
		    setNoIniciadosProcesoCompletamientoDatos((Integer) results[0]);
			setIniciadosProcesoCompletamientoDatos((Integer) results[1]);
			setCompletadosProcesoCompletamientoDatos((Integer) results[2]);
			setAtrasadosProcesoCompletamientoDatos((Integer) results[3]);
			setFirmadosProcesoCompletamientoDatos((Integer) results[4]);
			
			setIniciadosProcesoMonitoreo((Integer) results[5]);
			setNoIniciadosProcesoMonitoreo((Integer) results[6]);
			setCompletadosProcesoMonitoreo((Integer) results[7]);
			setCompletadosPendienteFirmaProcesoMonitoreo((Integer) results[8]);
			setNotasSinResolverProcesoMonitoreo((Integer) results[9]);
			
		}catch (Exception e) {
	        System.out.println("Error in completamientoDatosProcesoCompletamientoDatos: " + e.getMessage());			
		}			
	}
	/*@SuppressWarnings("unchecked")	
	public void procesoCompletamientoDatos(){
		cargarEstudios();
		Integer completadosSinFirmar=0;
		Integer totalMomentosNoIniciados=0;
		Integer totalMomentosIniciados=0;
		Integer totalMomentosCompletados=0;
		Integer totalMomentosFirmados=0;
		Integer totalMomentosAtrasados=0;
		Integer totalMomentosTotal=0;	
		//Integer totalCrdNoIniciadas=0;
		//Integer totalCrdIniciadas=0;
		//Integer totalCrdCompletadas=0;
		//Integer totalCrdFirmada=0;
		//Integer totalCrdTotal=0;
		Integer totalMomentosMonNoIniciados = 0;
		Integer totalMomentosMonIniciados = 0;
		Integer totalMomentosMonCompletados = 0;
		Integer totalcompletadosSinFirmar=0;
		Integer totalNotasSinResolver = 0;		
				
		//Necesario para actualizar el estado atrasado de los momentos de seguimiento
		Calendar cal=Calendar.getInstance();
		long idEstadoSeguimientoAtra=4;
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasado=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoAtra);
		
		for(int a = 0; a < listaEstudioEntidad.size(); a++){
			if(listaEstudioEntidad.get(a).getEstadoEstudio().getCodigo() == 3 || listaEstudioEntidad.get(a).getEstadoEstudio().getCodigo() == 6){
				List<EstudioEntidad_ensayo> listaEntidadEst;
				listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
						.createQuery("select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false")
						.setParameter("idEst",listaEstudioEntidad.get(a).getId()).getResultList();
				for (int z = 0; z < listaEntidadEst.size(); z++) {
					List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager.createQuery("select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
					
					for (int i = 0; i < grupos.size(); i++) {
						List<Sujeto_ensayo> sujetosDelGrupo = entityManager.createQuery(
								"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false order by sujeto.numeroInclucion")				
						.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())	
						.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
						.setParameter("idGrupo",grupos.get(i).getId()).getResultList();
											
						if(!sujetosDelGrupo.isEmpty())
						{
							for(int k=0;k<sujetosDelGrupo.size();k++){
								Integer momentosNoIniciados=0;
								Integer momentosIniciados=0;
								Integer momentosCompletados=0;
								Integer momentosFirmados=0;
								Integer momentosAtrasados=0;
								Integer momentosTotal=0;
								//Integer crdNoIniciadas=0;							
								//Integer crdIniciadas=0;
								//Integer crdCompletadas=0;
								//Integer crdFirmada=0;
								//Integer crdTotal=0;
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
									
									//Actualizar el estado de los momentos atrasados
									if(momento.getFechaFin()!=null && ((cal.getTime()).compareTo(momento.getFechaFin()) > 0) && (momento.getEstadoMomentoSeguimiento().getCodigo() == 2)){
										momento.setEstadoMomentoSeguimiento(estadoMomentoAtrasado);
										entityManager.persist(momento);
									}
									
										//Estado de los momentos si el paciente es interrumpido	
									if(sujeto.getFechaInterrupcion()!=null && momentosSujeto.get(l).getFechaInicio().before(sujeto.getFechaInterrupcion()))	{
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
									}
									//Estado de los momentos si el paciente no es interrumpido	
									else if(momento.getEstadoMomentoSeguimiento().getCodigo()==1)
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
									
									//Estado del monitoreo de los momentos
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
									//crdTotal = crdNoIniciadas + crdIniciadas + crdCompletadas + crdFirmada;
									//Monitoreo no iniciados son solo aquellos que deben iniciar, quitando los momentos que no estan completados ya que sin estar completados no se puede iniciar el monitoreo
									momentosMonNoIniciadosReal = momentosMonNoIniciados - momentosNoIniciados - momentosAtrasados - momentosIniciados;
									
								}
								//momentosTotal = momentosIniciados + momentosCompletados + momentosAtrasados + momentosFirmados;
								completadosSinFirmar=momentosMonCompletados-momentosFirmados;							
								
								//Valores de la fila de totales
								totalMomentosNoIniciados+=momentosNoIniciados;
								totalMomentosIniciados+=momentosIniciados;
								totalMomentosCompletados+=momentosCompletados;
								totalMomentosTotal+=momentosTotal;
								totalMomentosAtrasados+=momentosAtrasados;
								totalMomentosFirmados+=momentosFirmados;
								//totalCrdNoIniciadas+=crdNoIniciadas;
								//totalCrdIniciadas+=crdIniciadas;
								//totalCrdCompletadas+=crdCompletadas;
								//totalCrdTotal+=crdTotal;
								//totalCrdFirmada+=crdFirmada;
								totalMomentosMonNoIniciados+=momentosMonNoIniciadosReal;
								totalMomentosMonIniciados+=momentosMonIniciados;
								totalMomentosMonCompletados+=momentosMonCompletados;
								totalNotasSinResolver+=notasSinResolver;
								totalcompletadosSinFirmar+=completadosSinFirmar;							 
							}					
						}						
					}								
				}			
			}
		}		
		
		setNoIniciadosProcesoCompletamientoDatos(totalMomentosNoIniciados);
		setIniciadosProcesoCompletamientoDatos(totalMomentosIniciados);
		setCompletadosProcesoCompletamientoDatos(totalMomentosCompletados);
		setAtrasadosProcesoCompletamientoDatos(totalMomentosAtrasados);
		setFirmadosProcesoCompletamientoDatos(totalMomentosFirmados);
		setIniciadosProcesoMonitoreo(totalMomentosMonIniciados);
		setNoIniciadosProcesoMonitoreo(totalMomentosMonNoIniciados);
		setCompletadosProcesoMonitoreo(totalMomentosMonCompletados);
		setNotasSinResolverProcesoMonitoreo(totalNotasSinResolver);
		setCompletadosPendienteFirmaProcesoMonitoreo(totalcompletadosSinFirmar);
	}*/	
		

	private boolean hojaCrdDesasociadaEnMS(long idCrd,long id_momento){
		List<MomentoSeguimientoGeneralHojaCrd_ensayo> ms_crd=
				this.entityManager.createQuery("select ms_crd from MomentoSeguimientoGeneralHojaCrd_ensayo ms_crd where ms_crd.eliminado=true and ms_crd.hojaCrd.id=:id_crd_p and ms_crd.momentoSeguimientoGeneral.id=:id_momento")
				.setParameter("id_crd_p", idCrd).setParameter("id_momento", id_momento).getResultList();			
		return ms_crd.size()>0;
	}

	public Integer getNoIniciadosProcesoCompletamientoDatos() {
		return noIniciadosProcesoCompletamientoDatos;
	}

	public void setNoIniciadosProcesoCompletamientoDatos(
			Integer noIniciadosProcesoCompletamientoDatos) {
		this.noIniciadosProcesoCompletamientoDatos = noIniciadosProcesoCompletamientoDatos;
	}

	public Integer getIniciadosProcesoCompletamientoDatos() {
		return iniciadosProcesoCompletamientoDatos;
	}

	public void setIniciadosProcesoCompletamientoDatos(
			Integer iniciadosProcesoCompletamientoDatos) {
		this.iniciadosProcesoCompletamientoDatos = iniciadosProcesoCompletamientoDatos;
	}

	public Integer getCompletadosProcesoCompletamientoDatos() {
		return completadosProcesoCompletamientoDatos;
	}

	public void setCompletadosProcesoCompletamientoDatos(
			Integer completadosProcesoCompletamientoDatos) {
		this.completadosProcesoCompletamientoDatos = completadosProcesoCompletamientoDatos;
	}

	public Integer getAtrasadosProcesoCompletamientoDatos() {
		return atrasadosProcesoCompletamientoDatos;
	}

	public void setAtrasadosProcesoCompletamientoDatos(
			Integer atrasadosProcesoCompletamientoDatos) {
		this.atrasadosProcesoCompletamientoDatos = atrasadosProcesoCompletamientoDatos;
	}

	public Integer getFirmadosProcesoCompletamientoDatos() {
		return firmadosProcesoCompletamientoDatos;
	}

	public void setFirmadosProcesoCompletamientoDatos(
			Integer firmadosProcesoCompletamientoDatos) {
		this.firmadosProcesoCompletamientoDatos = firmadosProcesoCompletamientoDatos;
	}

	public Integer getIniciadosProcesoMonitoreo() {
		return iniciadosProcesoMonitoreo;
	}

	public void setIniciadosProcesoMonitoreo(Integer iniciadosProcesoMonitoreo) {
		this.iniciadosProcesoMonitoreo = iniciadosProcesoMonitoreo;
	}

	public Integer getNoIniciadosProcesoMonitoreo() {
		return noIniciadosProcesoMonitoreo;
	}

	public void setNoIniciadosProcesoMonitoreo(Integer noIniciadosProcesoMonitoreo) {
		this.noIniciadosProcesoMonitoreo = noIniciadosProcesoMonitoreo;
	}

	public Integer getCompletadosProcesoMonitoreo() {
		return completadosProcesoMonitoreo;
	}

	public void setCompletadosProcesoMonitoreo(Integer completadosProcesoMonitoreo) {
		this.completadosProcesoMonitoreo = completadosProcesoMonitoreo;
	}

	public Integer getNotasSinResolverProcesoMonitoreo() {
		return notasSinResolverProcesoMonitoreo;
	}

	public void setNotasSinResolverProcesoMonitoreo(
			Integer notasSinResolverProcesoMonitoreo) {
		this.notasSinResolverProcesoMonitoreo = notasSinResolverProcesoMonitoreo;
	}

	public Integer getCompletadosPendienteFirmaProcesoMonitoreo() {
		return completadosPendienteFirmaProcesoMonitoreo;
	}

	public void setCompletadosPendienteFirmaProcesoMonitoreo(
			Integer completadosPendienteFirmaProcesoMonitoreo) {
		this.completadosPendienteFirmaProcesoMonitoreo = completadosPendienteFirmaProcesoMonitoreo;
	}
				

}
