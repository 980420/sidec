package gehos.ensayo.ensayo_estadisticas.reporteEstadoConduccionEnsayo;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;
import gehos.ensayo.ensayo_estadisticas.entity.Ensayo;
import gehos.ensayo.ensayo_estadisticas.entity.Entidad;
import gehos.ensayo.ensayo_estadisticas.entity.GrupoSujeto;
import gehos.ensayo.ensayo_estadisticas.entity.Pais;
import gehos.ensayo.ensayo_estadisticas.entity.Provincia;
import gehos.ensayo.ensayo_estadisticas.entity.Sujeto;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.Estado_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Nacion_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
@SuppressWarnings("unchecked")
@Name("reporteEstadoConduccionEstudio")
@Scope(ScopeType.CONVERSATION)
public class ReporteEstadoConduccionEstudio {
	
	private Date fechaInicio;
	private Date fechaFin;
	
	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	private Boolean flag=false;
	private Boolean haySujetos = false;
	
	//Paises
	private List<Nacion_ensayo> listadoNacion = new ArrayList<Nacion_ensayo>();
	private List<Nacion_ensayo> listadoNacionSelecionadas = new ArrayList<Nacion_ensayo>();
	
	private List<Provincia> listaPaises= new ArrayList<Provincia>();
	
	//Provincias
	private List<Estado_ensayo> listadoEstados= new ArrayList<Estado_ensayo>();
	private List<Estado_ensayo> listadoEstadosSelecionadas= new ArrayList<Estado_ensayo>();
	
	//Entidades
	private List<Entidad_ensayo> listadoEntidades = new ArrayList<Entidad_ensayo>();
	private List<Entidad_ensayo> listadoEntidadesSeleccionadas = new ArrayList<Entidad_ensayo>();
	
	

	//Estudios o ensayos
	private List<Estudio_ensayo> listadoEstudios = new ArrayList<Estudio_ensayo>();
	private List<Estudio_ensayo> listadoEstudiosSeleccionados = new ArrayList<Estudio_ensayo>();
	
	//Grupos de sujetos
	private List<GrupoSujetos_ensayo> listadoGrupos = new ArrayList<GrupoSujetos_ensayo>();
	private List<GrupoSujetos_ensayo> listadoGruposSeleccionados = new ArrayList<GrupoSujetos_ensayo>();
	
	//parsPedro = new HashMap();
	  
	/*parsPedro.put("v_despues", SeamResourceBundle.getBundle().getString("v_despues"));
	  pathToReport= reportManager.ExportReport("reportTrazas", parsPedro, listaPedro, FileType.HTML_FILE);
		*/
	
	private Map reporteConduccion;
	private String nombreReport;
	private String pathExportedReport = "";
	
	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	
	
	
	
	private String fileformatToExport;
	private List<String> filesFormatCombo;
	
	List<Pais> naciones= new ArrayList<Pais>();


	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void cargarNaciones(){
		this.listadoNacion = entityManager.createQuery("select nacion from Nacion_ensayo nacion order by nacion.valor ").getResultList();
		estadosNacion();
		
	}
	
	public void estadosNacion()
	{
		listadoEstados.clear();
		for(int i=0;i<listadoNacionSelecionadas.size();i++)
		{
			List<Estado_ensayo> E = entityManager
					.createQuery(
							"select estado from Estado_ensayo estado where estado.nacion.id =:idNacion order by estado.valor asc")
					.setParameter("idNacion",
							listadoNacionSelecionadas.get(i).getId()).getResultList();
			listadoEstados.addAll(E);
		}
		Collections.sort(listadoEstados, new Comparator<Estado_ensayo>(){

			@Override
			public int compare(Estado_ensayo o1, Estado_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getValor().compareTo(o2.getValor());
			}
			
		});
		validationEstadosNacion();
	}
	
	public void validationEstadosNacion()
	{
		
		boolean estadoAsignado = false;
		
		for(int j=0;j<listadoEstadosSelecionadas.size();j++)
		{
			for(int i=0;i<listadoEstados.size();i++)
			{
				if(listadoEstados.get(i).equals(listadoEstadosSelecionadas.get(j)))
				{
					listadoEstados.remove(i);
					i--;
					estadoAsignado = true;
					break;
				}
			}
			if(estadoAsignado == false)
			{
				listadoEstadosSelecionadas.remove(j);
				j--;
			}
			else
				estadoAsignado = false;
		}
		
		entidades();
		
	}
	
	public void entidades()
	{
		listadoEntidades.clear();
		Usuario user = (Usuario)Component.getInstance("user");
        
        
		for(int i=0;i<listadoEstadosSelecionadas.size();i++)
		{
			List<Entidad_ensayo> E = entityManager
					.createQuery(
							"select distinct ee.entidad from EstudioEntidad_ensayo ee where ee.entidad.estado.id =:idEstado order by ee.entidad.nombre")
					.setParameter("idEstado",
							listadoEstadosSelecionadas.get(i).getId()).getResultList();
			
			List<Entidad_configuracion> Ec = entityManager
					.createQuery(
							"select distinct ent " +
							"from Entidad_configuracion ent " +
							"inner join ent.servicioInEntidads servInEnt inner join servInEnt.usuarios u with u.id=:idUser "
							        + "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//									+ "where ent.perteneceARhio = true "
									+ "and (ent.eliminado = null or ent.eliminado = false) and ent.estado.id =:idEstado "
									+ "order by ent.nombre")
					.setParameter("idEstado",
							listadoEstadosSelecionadas.get(i).getId())
							.setParameter("idUser", user.getId()/*userTools.getUser().getId()*/).getResultList();
			
			for (int h = 0; h < Ec.size(); h++) 
			{
				for(int j=0;j<E.size();j++)
				{
					if(!E.get(j).getNombre().equals(null) && Ec.get(h).getNombre().equals(E.get(j).getNombre()))
						listadoEntidades.add(E.get(j));
				}
			} 
		} 
		Collections.sort(listadoEntidades, new Comparator<Entidad_ensayo>(){
			@Override
			public int compare(Entidad_ensayo o1, Entidad_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getNombre().compareTo(o2.getNombre());
			}				
		});
		validationEntidades();
	}
	
	public void validationEntidades()
	{
		
		boolean entidadAsignada = false;
		
		for(int j = 0;j<listadoEntidadesSeleccionadas.size();j++)
		{
			for(int i =0;i<listadoEntidades.size();i++)
			{
				if(listadoEntidades.get(i).equals(listadoEntidadesSeleccionadas.get(j)))
				{
					listadoEntidades.remove(i);
					i--;
					entidadAsignada = true;
					break;
				}
			}
			if(entidadAsignada == false)
			{
				listadoEntidadesSeleccionadas.remove(j);
				j--;
			}
			else
				entidadAsignada = false;
		}
		
		estudios();
		
	}
	
	public void estudios()
	{
		listadoEstudios.clear();
		Usuario user = (Usuario)Component.getInstance("user");
		
		for(int i=0;i<listadoEntidadesSeleccionadas.size();i++)
		{
			List<Estudio_ensayo> E = entityManager
					.createQuery(
							"select distinct ue.estudioEntidad.estudio from UsuarioEstudio_ensayo ue where ue.estudioEntidad.entidad.id =:idEntidad " 
					+ " and ue.estudioEntidad.estudio.eliminado != True and ue.estudioEntidad.estudio.estadoEstudio.codigo = 3 and ue.usuario.id=:idUser order by ue.estudioEntidad.estudio.nombre ")
					.setParameter("idEntidad",
							listadoEntidadesSeleccionadas.get(i).getId()).setParameter("idUser", user.getId()).getResultList();
			
			for(int j=0;j<E.size();j++)
			{
				if(!E.get(j).getNombre().equals(null))
					listadoEstudios.add(E.get(j));
			}
			
		}
		Collections.sort(listadoEstudios, new Comparator<Estudio_ensayo>(){
			@Override
			public int compare(Estudio_ensayo o1, Estudio_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getNombre().compareTo(o2.getNombre());
			}
		});
		validationEstudios();
	}
	
	public void validationEstudios()
	{
		
		boolean estudioAsignado = false;
		for(int j = 0;j<listadoEstudiosSeleccionados.size();j++)
		{
			for(int i = 0;i<listadoEstudios.size();i++)
			{
				if(listadoEstudios.get(i).equals(listadoEstudiosSeleccionados.get(j)))
				{
					listadoEstudios.remove(i);
					i--;
					estudioAsignado = true;
					break;
				}
			}
			if(estudioAsignado==false)
			{
				listadoEstudiosSeleccionados.remove(j);
				j--;
			}
			else 
				estudioAsignado = false;
		}
		
		grupos_Sujetos();
		
	}
	
	
	
	public void grupos_Sujetos()
	{
		listadoGrupos.clear();
		//List<EstudioEntidad_ensayo> listadoEstudioEntidad = new ArrayList<EstudioEntidad_ensayo>();
		for(int i=0;i<listadoEstudiosSeleccionados.size();i++)
		{
						List<GrupoSujetos_ensayo> G = entityManager
						.createQuery(
								"select distinct ee.estudio.grupoSujetoses from EstudioEntidad_ensayo ee where ee.estudio.id =:idEstudio ")
						.setParameter("idEstudio",
								listadoEstudiosSeleccionados.get(i).getId())
						.getResultList();
				for (int j = 0; j < G.size(); j++) 
				{
					if(!G.get(j).getEliminado())
						listadoGrupos.add(G.get(j));
				}
				//listadoGrupos.addAll(G);
						
		}
		Collections.sort(listadoGrupos, new Comparator<GrupoSujetos_ensayo>(){

			@Override
			public int compare(GrupoSujetos_ensayo o1, GrupoSujetos_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getNombreGrupo().compareTo(o2.getNombreGrupo());
			}
			
		});
		
		validationGrupoSujetos();
	}
	
	public void validationGrupoSujetos()
	{
		
		boolean grupoSujetosAsignado = false;
		for(int j =0;j<listadoGruposSeleccionados.size();j++)
		{
			for(int i=0;i<listadoGrupos.size();i++)
			{
				if(listadoGrupos.get(i).equals(listadoGruposSeleccionados.get(j)))
				{
					listadoGrupos.remove(i);
					i--;
					grupoSujetosAsignado = true;
					break;
				}
			}
			if(grupoSujetosAsignado==false)
			{
				listadoGruposSeleccionados.remove(j);
				j--;
			}
			else
				grupoSujetosAsignado = false;
		}
		
	}
	
	public void reporteConduccion()
	{
		
		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[1];

		r[0] = validations.requeridoList(this.listadoNacionSelecionadas,
				"ListShuttleNacion", facesMessages);
		
		naciones.clear();
		haySujetos=false;
		
		if(fechaFin != null && fechaInicio != null && listadoNacionSelecionadas.size()>0)
		{		
		for(int n=0;n<listadoNacionSelecionadas.size();n++)
		{
			long totalMINacion=0;
			long totalMCNacion=0;
			long totalMTNacion=0;
			long totalMANacion=0;
			long totalMFNacion=0;
			long totalMNINacion=0;
			long totalCINacion=0;
			long totalCCNacion=0;
			long totalCTNacion=0;
			long totalCFNacion=0;
			long totalCNINacion=0;
			long totalMMINacion = 0;
			long totalMMCNacion = 0;
			long totalNSRNacion = 0;
			List<Provincia> estados= new ArrayList<Provincia>();
			if(listadoEstadosSelecionadas.isEmpty())
			{
				listadoEstadosSelecionadas=listadoEstados;
			}
				
			for(int e=0;e<listadoEstadosSelecionadas.size();e++)
			{
				long totalMIEstado=0;
				long totalMCEstado=0;
				long totalMTEstado=0;
				long totalMAEstado=0;
				long totalMFEstado=0;
				long totalMNIEstado=0;
				long totalCIEstado=0;
				long totalCCEstado=0;
				long totalCTEstado=0;
				long totalCFEstado=0;
				long totalCNIEstado=0;
				long totalMMIEstado = 0;
				long totalMMCEstado = 0;
				long totalNSREstado = 0;
				if(listadoEstadosSelecionadas.get(e).getNacion().getCodigo().equals(listadoNacionSelecionadas.get(n).getCodigo()))
				{
					List<Entidad> entidades= new ArrayList<Entidad>();
					if(listadoEntidades.isEmpty())
					{
						for(int i=0;i<listadoEstadosSelecionadas.size();i++)
						{
							List<Entidad_ensayo> E = entityManager
									.createQuery(
											"select distinct ee.entidad from EstudioEntidad_ensayo ee where ee.entidad.estado.id =:idEstado")
									.setParameter("idEstado",
											listadoEstadosSelecionadas.get(i).getId()).getResultList();
							for(int j=0;j<E.size();j++)
							{
								if(!E.get(j).getNombre().equals(null))
									listadoEntidades.add(E.get(j));
							}
							
							
						}
						//listadoEntidadesSeleccionadas=listadoEntidades;
					}
					if(listadoEntidadesSeleccionadas.isEmpty())
						listadoEntidadesSeleccionadas=listadoEntidades;
						
					
					for(int t=0;t<listadoEntidadesSeleccionadas.size();t++)
					{
						long totalMIEntidad=0;
						long totalMCEntidad=0;
						long totalMTEntidad=0;
						long totalMAEntidad=0;
						long totalMFEntidad=0;
						long totalMNIEntidad=0;
						long totalCIEntidad=0;
						long totalCCEntidad=0;
						long totalCTEntidad=0;
						long totalCFEntidad=0;
						long totalCNIEntidad=0;
						long totalMMIEntidad = 0;
						long totalMMCEntidad = 0;
						long totalNSREntidad = 0;
						if(listadoEntidadesSeleccionadas.get(t).getEstado().getId()==listadoEstadosSelecionadas.get(e).getId())
						{
							List<Ensayo> estudios= new ArrayList<Ensayo>();
							
							if(listadoEstudios.isEmpty() && listadoEntidadesSeleccionadas.get(t).getTipoEntidad().getId()==7)
							{
								for(int i=0;i<listadoEntidadesSeleccionadas.size();i++)
								{
									List<Estudio_ensayo> E = entityManager
											.createQuery(
													"select estudio from Estudio_ensayo estudio where estudio.entidad.id =:idEntidad and estudio.eliminado != True")
											.setParameter("idEntidad",listadoEntidadesSeleccionadas.get(i).getId()).getResultList();
									listadoEstudios.addAll(E);
								}
								//listadoEstudiosSeleccionados=listadoEstudios;
							}
							else if(listadoEstudios.isEmpty())
							{
								for(int i=0;i<listadoEntidadesSeleccionadas.size();i++)
								{
									List<Estudio_ensayo> E = entityManager
											.createQuery(
													"select ee.estudio from EstudioEntidad_ensayo ee where ee.entidad.id =:idEntidad and ee.estudio.eliminado != True ")
											.setParameter("idEntidad",
													listadoEntidadesSeleccionadas.get(i).getId()).getResultList();
									listadoEstudios.addAll(E);
								}
								//listadoEstudiosSeleccionados=listadoEstudios;
							}
							if(listadoEstudiosSeleccionados.isEmpty())
								listadoEstudiosSeleccionados=listadoEstudios;
								
							
							for(int s=0;s<listadoEstudiosSeleccionados.size();s++)
							{
								long totalMIEstudio=0;
								long totalMCEstudio=0;
								long totalMTEstudio=0;
								long totalMAEstudio=0;
								long totalMFEstudio=0;
								long totalMNIEstudio=0;
								long totalCIEstudio=0;
								long totalCCEstudio=0;
								long totalCTEstudio=0;
								long totalCFestudio=0;
								long totalCNIestudio=0;
								long totalMMIEstudio = 0;
								long totalMMCEstudio = 0;
								long totalNSREstudio = 0;
								//if(listadoEstudiosSeleccionados.get(s).getEntidad().getId()==listadoEntidadesSeleccionadas.get(t).getId())
								{
									List<GrupoSujeto> grupoSujetos= new ArrayList<GrupoSujeto>();
									
									if(listadoGrupos.isEmpty() && listadoEntidadesSeleccionadas.get(t).getTipoEntidad().getId()==7)
									{
										for(int i=0;i<listadoEstudiosSeleccionados.size();i++)
										{
											List<GrupoSujetos_ensayo> G = entityManager
													.createQuery(
															"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado != True and grupo.estudio.id =:idEstudio")
															.setParameter("idEstudio",listadoEstudiosSeleccionados.get(i).getId()).getResultList();
											for(int j=0;j<G.size();j++)
											{
												listadoGrupos.add(G.get(j));
											}
											
										}
										//listadoGruposSeleccionados=listadoGrupos;
									}	
									
									else if(listadoGrupos.isEmpty())
									{
										for(int i=0;i<listadoEstudiosSeleccionados.size();i++)
										{
											List<GrupoSujetos_ensayo> G = entityManager
													.createQuery(
															"select distinct ee.estudio.grupoSujetoses from EstudioEntidad_ensayo ee where ee.estudio.id =:idEstudio and ee.entidad.id =:idEntidad ")
													.setParameter("idEstudio",listadoEstudiosSeleccionados.get(i).getId())
													.setParameter("idEntidad",listadoEntidadesSeleccionadas.get(t).getId()).getResultList();
											for(int j=0;j<G.size();j++)
											{
												if(!G.get(j).getEliminado())
												listadoGrupos.add(G.get(j));
											}
											
										}
										//listadoGruposSeleccionados=listadoGrupos; select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado != True and grupo.estudio.id =:idEstudio
									}
									if(listadoGruposSeleccionados.isEmpty())
										listadoGruposSeleccionados=listadoGrupos;
										
									for(int g=0;g<listadoGruposSeleccionados.size();g++)
									{
										long totalMomentosIniciados=0;
										long totalMomentosCompletados=0;
										long totalMomentosTotal=0;
										long totalMomentosAtrasados=0;
										long totalMomentosFirmados=0;
										long totalMomentosNoIniciados=0;
										long totalCrdIniciadas=0;
										long totalCrdCompletadas=0;
										long totalCrdTotal=0;
										long totalCrdFirmada=0;
										long totalCrdNoIniciadas=0;
										long totalMomentosMonIniciados = 0;
										long totalMomentosMonCompletados = 0;
										long totalNotasSinResolver = 0;
										if(listadoGruposSeleccionados.get(g).getEstudio().getId()==listadoEstudiosSeleccionados.get(s).getId())
										{
											GrupoSujetos_ensayo grupo = listadoGruposSeleccionados.get(g);
											List<Sujeto_ensayo> sujetosDelGrupo = new ArrayList<Sujeto_ensayo>();
											
											if(listadoEntidadesSeleccionadas.get(t).getTipoEntidad().getId()==7)
											{
												sujetosDelGrupo = entityManager.createQuery(
														"select distinct sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin ")
												.setParameter("idEstudio",listadoEstudiosSeleccionados.get(s).getId())	
												.setParameter("idGrupo",grupo.getId()).setParameter("fechaInicio",fechaInicio)
												.setParameter("fechaFin",fechaFin).getResultList();
											}
											else{
											sujetosDelGrupo = entityManager.createQuery(
													"select distinct sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.entidad.id =:idEntidad and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin ")
											.setParameter("idEntidad",listadoEntidadesSeleccionadas.get(t).getId())
											.setParameter("idEstudio",listadoEstudiosSeleccionados.get(s).getId())	
											.setParameter("idGrupo",grupo.getId()).setParameter("fechaInicio",fechaInicio)
											.setParameter("fechaFin",fechaFin).getResultList();
											}
											
											List<Sujeto> sujetos= new ArrayList<Sujeto>();
											if(!sujetosDelGrupo.isEmpty())
											{
												haySujetos = true;
												for(int k=0;k<sujetosDelGrupo.size();k++)
												{
													if(sujetosDelGrupo.get(k).getGrupoSujetos().getId()==listadoGruposSeleccionados.get(g).getId())
													{
														long momentosIniciados=0;
														long momentosCompletados=0;
														long momentosTotal=0;
														long momentosAtrasados=0;
														long momentosFirmados=0;
														long momentosNoIniciados=0;
														long crdIniciadas=0;
														long crdCompletadas=0;
														long crdTotal=0;
														long crdFirmada=0;
														long crdNoIniciadas=0;
														long momentosMonIniciados = 0;
														long momentosMonCompletados = 0;
														long notasSinResolver = 0;
														
														
														Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
														List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
																.createQuery(
																		"select distinct momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.eliminado = false")
																.setParameter("idSujeto",
																		sujeto.getId()).getResultList();
														for(int l=0;l<momentosSujeto.size();l++)
														{
															MomentoSeguimientoEspecifico_ensayo momento=momentosSujeto.get(l);
															
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
															//momentosTotal++;
															
															if (momento.getEstadoMonitoreo().getCodigo() == 1)
																momentosMonIniciados++;
															else if (momento.getEstadoMonitoreo().getCodigo() == 3)
																momentosMonCompletados++;
															
															List<CrdEspecifico_ensayo> crdMomento = entityManager
																	.createQuery(
																			"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
																	.setParameter("idMomento",
																			momento.getId()).getResultList();
															for(int m=0;m<crdMomento.size();m++)
															{
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
																				"select nota from Nota_ensayo nota where nota.crdEspecifico.id =:idCRD and nota.eliminado = false and nota.notaPadre = null")
																		.setParameter("idCRD",crd.getId()).getResultList();

																for (int y = 0; y < notas.size(); y++) {
																	if(notas.get(y).getEstadoNota().getCodigo() == 1 || notas.get(y).getEstadoNota().getCodigo() == 2){
																		notasSinResolver ++;
																	}
																}
																
																//crdTotal++;   + crdNoIniciadas   + momentosNoIniciados
															}crdTotal = crdIniciadas + crdCompletadas + crdFirmada ;
															
														}momentosTotal = momentosIniciados + momentosCompletados + momentosAtrasados + momentosFirmados ;
														
														Sujeto sujetoEstadisticas =new Sujeto(sujeto.getCodigoPaciente(), momentosIniciados, momentosCompletados, momentosTotal, momentosAtrasados, momentosNoIniciados, crdIniciadas, crdCompletadas, crdTotal, momentosFirmados, crdFirmada,momentosMonIniciados,momentosMonCompletados,notasSinResolver, crdNoIniciadas);
														sujetos.add(sujetoEstadisticas);
														 totalMomentosIniciados+=momentosIniciados;
														 totalMomentosCompletados+=momentosCompletados;
														 totalMomentosTotal+=momentosTotal;
														 totalMomentosAtrasados+=momentosAtrasados;
														 totalMomentosFirmados+=momentosFirmados;
														 totalMomentosNoIniciados+=momentosNoIniciados;
														 totalCrdIniciadas+=crdIniciadas;
														 totalCrdCompletadas+=crdCompletadas;
														 totalCrdTotal+=crdTotal;
														 totalCrdFirmada+=crdFirmada;
														 totalCrdNoIniciadas+=crdNoIniciadas;
														 totalMomentosMonIniciados +=momentosMonIniciados ;
														 totalMomentosMonCompletados += momentosMonCompletados;
														 totalNotasSinResolver += notasSinResolver;
													
													}
												}
											}
											if(!sujetos.isEmpty())
											{
												GrupoSujeto grupoNuevo = new GrupoSujeto(grupo.getNombreGrupo(), sujetos, totalMomentosIniciados, totalMomentosCompletados, totalMomentosTotal, totalMomentosAtrasados, totalCrdIniciadas, totalCrdCompletadas, totalCrdTotal, totalMomentosFirmados, totalCrdFirmada,totalMomentosMonIniciados,totalMomentosMonCompletados, totalNotasSinResolver, totalCrdNoIniciadas, totalMomentosNoIniciados);
												grupoSujetos.add(grupoNuevo);
												totalMIEstudio+=totalMomentosIniciados;
												totalMCEstudio+=totalMomentosCompletados;
												totalMTEstudio+=totalMomentosTotal;
												totalMAEstudio+=totalMomentosAtrasados;
												totalMFEstudio+=totalMomentosFirmados;
												totalMNIEstudio+=totalMomentosNoIniciados;
												totalCIEstudio+=totalCrdIniciadas;
												totalCCEstudio+=totalCrdCompletadas;
												totalCTEstudio+=totalCrdTotal;
												totalCFestudio+=totalCrdFirmada;
												totalCNIestudio+=totalCrdNoIniciadas;
												totalMMIEstudio += totalMomentosMonIniciados;
												totalMMCEstudio += totalMomentosMonCompletados;
												totalNSREstudio += totalNotasSinResolver;
											}
											
										}
										
									}
									if(!grupoSujetos.isEmpty())
									{
										Ensayo ensayo = new Ensayo(listadoEstudiosSeleccionados.get(s).getNombre(), grupoSujetos,totalMIEstudio,totalMCEstudio, totalMTEstudio, totalMAEstudio, totalCIEstudio, totalCCEstudio, totalCTEstudio, totalMFEstudio, totalCFestudio,totalMMIEstudio, totalMMCEstudio,totalNSREstudio, totalCNIestudio, totalMNIEstudio);
										estudios.add(ensayo);
										totalMIEntidad+=totalMIEstudio;
										totalMCEntidad+=totalMCEstudio;
										totalMTEntidad+=totalMTEstudio;
										totalMAEntidad+=totalMAEstudio;
										totalMFEntidad+=totalMFEstudio;
										totalMNIEntidad+=totalMNIEstudio;
										totalCIEntidad+=totalCIEstudio;
										totalCCEntidad+=totalCCEstudio;
										totalCTEntidad+=totalCTEstudio;
										totalCFEntidad+=totalCFestudio;
										totalCNIEntidad+=totalCNIestudio;
										totalMMIEntidad += totalMMIEstudio;
										totalMMCEntidad += totalMMCEstudio;
										totalNSREntidad += totalNSREstudio;
									}
									
							
								}
								
							}
							if(!estudios.isEmpty())
							{
								Entidad entidad= new Entidad(listadoEntidadesSeleccionadas.get(t).getNombre(), estudios,totalMIEntidad,totalMCEntidad, totalMTEntidad, totalMAEntidad, totalCIEntidad, totalCCEntidad, totalCTEntidad, totalMFEntidad, totalCFEntidad,totalMMIEntidad,totalMMCEntidad,totalNSREntidad, totalCNIEntidad, totalMNIEntidad);
								entidades.add(entidad);
								totalMIEstado+=totalMIEntidad;
								totalMCEstado+=totalMCEntidad;
								totalMTEstado+=totalMTEntidad;
								totalMAEstado+=totalMAEntidad;
								totalMFEstado+=totalMFEntidad;
								totalMNIEstado+=totalMNIEntidad;
								totalCIEstado+=totalCIEntidad;
								totalCCEstado+=totalCCEntidad;
								totalCTEstado+=totalCTEntidad;
								totalCFEstado+=totalCFEntidad;
								totalCNIEstado+=totalCNIEntidad;
								totalMMIEstado += totalMMIEntidad;
								totalMMCEstado += totalMMCEntidad;
								totalNSREstado += totalNSREntidad;
							}
							
						}
						
					}
					if(!entidades.isEmpty())
					{
						Provincia provincia = new Provincia(listadoEstadosSelecionadas.get(e).getValor(), entidades, totalMIEstado,totalMCEstado, totalMTEstado, totalMAEstado, totalCIEstado, totalCCEstado, totalCTEstado, totalMFEstado, totalCFEstado,totalMMIEstado,totalMMCEstado,totalNSREstado, totalCNIEstado, totalMNIEstado);
						estados.add(provincia);
						totalMINacion+=totalMIEstado;
						totalMCNacion+=totalMCEstado;
						totalMTNacion+=totalMTEstado;
						totalMANacion+=totalMAEstado;
						totalMFNacion+=totalMFEstado;
						totalMNINacion+=totalMNIEstado;
						totalCINacion+=totalCIEstado;
						totalCCNacion+=totalCCEstado;
						totalCTNacion+=totalCTEstado;
						totalCFNacion+=totalCFEstado;
						totalCNINacion+=totalCNIEstado;
						totalMMINacion += totalMMIEstado;
						totalMMCNacion += totalMMCEstado;
						totalNSRNacion += totalNSREstado;
					}
					
				}
				
				
			}
			if(totalMINacion==0 && totalMCNacion==0 && totalMFNacion==0 && totalMANacion==0 && totalMTNacion==0 && totalCINacion==0 && totalCCNacion==0 && totalCFNacion==0 && totalCTNacion==0 && totalMNINacion==0 && totalCNINacion==0)
			break;
			if(estados!=null && haySujetos)
			{
				Pais pais = new Pais(listadoNacionSelecionadas.get(n).getValor(), estados, totalMINacion,totalMCNacion, totalMTNacion, totalMANacion, totalCINacion, totalCCNacion, totalCTNacion, totalMFNacion, totalCFNacion,totalMMINacion,totalMMCNacion,totalNSRNacion, totalCNINacion, totalMNINacion);
				naciones.add(pais);
			}
			
			
		}
		

		if (haySujetos) {
		
		reporteConduccion=new HashMap();
		
		reporteConduccion.put("pais", SeamResourceBundle.getBundle().getString("pais"));
		reporteConduccion.put("sujetos", SeamResourceBundle.getBundle().getString("sujetos"));
		reporteConduccion.put("provincia", SeamResourceBundle.getBundle().getString("provincia"));
		reporteConduccion.put("entidad", SeamResourceBundle.getBundle().getString("entidad"));
		reporteConduccion.put("estudio", SeamResourceBundle.getBundle().getString("estudio"));
		reporteConduccion.put("grupo", SeamResourceBundle.getBundle().getString("grupo"));
		
		reporteConduccion.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
		reporteConduccion.put("momentosIniciados", SeamResourceBundle.getBundle().getString("momentosIniciados"));
		reporteConduccion.put("momentosCompletados", SeamResourceBundle.getBundle().getString("momentosCompletados"));
		reporteConduccion.put("momentosTotal", SeamResourceBundle.getBundle().getString("momentosTotal"));
		reporteConduccion.put("totalMomentoF", SeamResourceBundle.getBundle().getString("totalMomentoF"));
		reporteConduccion.put("momentosAtrasados", SeamResourceBundle.getBundle().getString("momentosAtrasados"));
		reporteConduccion.put("momentosNoIni", SeamResourceBundle.getBundle().getString("momentosNoIni"));
		reporteConduccion.put("crdNoIniciadas", SeamResourceBundle.getBundle().getString("crdNoIniciadas"));
		reporteConduccion.put("crdIniciadas", SeamResourceBundle.getBundle().getString("crdIniciadas"));
		reporteConduccion.put("crdCompletadas", SeamResourceBundle.getBundle().getString("crdCompletadas"));
		reporteConduccion.put("crdFirmadas", SeamResourceBundle.getBundle().getString("crdFirmadas"));
		reporteConduccion.put("crdTotal", SeamResourceBundle.getBundle().getString("crdTotal"));
		reporteConduccion.put("momentoSeguimiento", SeamResourceBundle.getBundle().getString("momentoSeguimiento"));
		reporteConduccion.put("hojaCrd", SeamResourceBundle.getBundle().getString("hojaCrd"));
		reporteConduccion.put("monitoreoMomento", SeamResourceBundle.getBundle().getString("monitoreoMomento"));
		
		reporteConduccion.put("totalGrupo", SeamResourceBundle.getBundle().getString("totalGrupo"));
		reporteConduccion.put("totalEstudio", SeamResourceBundle.getBundle().getString("totalEstudio"));
		reporteConduccion.put("totalEntidad", SeamResourceBundle.getBundle().getString("totalEntidad"));
		reporteConduccion.put("totalEstado", SeamResourceBundle.getBundle().getString("totalEstado"));
		reporteConduccion.put("totalNacion", SeamResourceBundle.getBundle().getString("totalNacion"));
		
		nombreReport=reportManager.ExportReport("reportEstadisticaConduccion", reporteConduccion, naciones, FileType.HTML_FILE);
		 flag=true;
		 flag2=false;
		} else {
			flag = false;
			flag2 = true;
			noResult = SeamResourceBundle.getBundle().getString(
					"noResult3");
		}
		}
		else
		 {
			flag = false;
			 flag2 = true;
		 }
		 
		
	}
	
	public void exportReportToFileFormat(){
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportEstadisticaConduccion", reporteConduccion, naciones,FileType.PDF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportEstadisticaConduccion", reporteConduccion, naciones,FileType.RTF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportEstadisticaConduccion", reporteConduccion, naciones,FileType.EXCEL_FILE);
		}
	
	}
	
	
	
	

	public List<GrupoSujetos_ensayo> getListadoGrupos() {
		return listadoGrupos;
	}

	public void setListadoGrupos(List<GrupoSujetos_ensayo> listadoGrupos) {
		this.listadoGrupos = listadoGrupos;
	}

	public List<GrupoSujetos_ensayo> getListadoGruposSeleccionados() {
		return listadoGruposSeleccionados;
	}

	public void setListadoGruposSeleccionados(
			List<GrupoSujetos_ensayo> listadoGruposSeleccionados) {
		this.listadoGruposSeleccionados = listadoGruposSeleccionados;
	}

	public List<Estudio_ensayo> getListadoEstudios() {
		return listadoEstudios;
	}

	public void setListadoEstudios(List<Estudio_ensayo> listadoEstudios) {
		this.listadoEstudios = listadoEstudios;
	}
	
	public List<Estudio_ensayo> getListadoEstudiosSeleccionados() {
		return listadoEstudiosSeleccionados;
	}

	public void setListadoEstudiosSeleccionados(
			List<Estudio_ensayo> listadoEstudiosSeleccionados) {
		this.listadoEstudiosSeleccionados = listadoEstudiosSeleccionados;
	}
	
	public List<Nacion_ensayo> getListadoNacion() {
		return listadoNacion;
	}

	public void setListadoNacion(List<Nacion_ensayo> listadoNacion) {
		this.listadoNacion = listadoNacion;
	}

	public List<Nacion_ensayo> getListadoNacionSelecionadas() {
		return listadoNacionSelecionadas;
	}

	public void setListadoNacionSelecionadas(
			List<Nacion_ensayo> listadoNacionSelecionadas) {
		this.listadoNacionSelecionadas = listadoNacionSelecionadas;
	}
	
	public List<Estado_ensayo> getListadoEstados() {
		return listadoEstados;
	}

	public void setListadoEstados(List<Estado_ensayo> listadoEstados) {
		this.listadoEstados = listadoEstados;
	}

	public List<Estado_ensayo> getListadoEstadosSelecionadas() {
		return listadoEstadosSelecionadas;
	}

	public void setListadoEstadosSelecionadas(
			List<Estado_ensayo> listadoEstadosSelecionadas) {
		this.listadoEstadosSelecionadas = listadoEstadosSelecionadas;
	}
	
	public List<Entidad_ensayo> getListadoEntidades() {
		return listadoEntidades;
	}

	public void setListadoEntidades(List<Entidad_ensayo> listadoEntidades) {
		this.listadoEntidades = listadoEntidades;
	}
	
	public List<Entidad_ensayo> getListadoEntidadesSeleccionadas() {
		return listadoEntidadesSeleccionadas;
	}

	public void setListadoEntidadesSeleccionadas(
			List<Entidad_ensayo> listadoEntidadesSeleccionadas) {
		this.listadoEntidadesSeleccionadas = listadoEntidadesSeleccionadas;
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
		filesFormatCombo =  reportManager.fileFormatsToExport();
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

	


	
}
