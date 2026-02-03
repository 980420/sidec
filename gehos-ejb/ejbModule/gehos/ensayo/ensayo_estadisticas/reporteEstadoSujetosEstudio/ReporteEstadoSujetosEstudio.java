package gehos.ensayo.ensayo_estadisticas.reporteEstadoSujetosEstudio;

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
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.Estado_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.Nacion_ensayo;
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
@Name("reporteEstadoSujetosEstudio")
@Scope(ScopeType.CONVERSATION)
public class ReporteEstadoSujetosEstudio {
	
	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	private Boolean flag=false;
	private Boolean haySujetos = false;
	
	private Date fechaInicio;
	private Date fechaFin;
	
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
	
		
	private Map reporteSujetos;
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
		this.listadoNacion = entityManager.createQuery("select nacion from Nacion_ensayo nacion where nacion.id=2 order by nacion.valor ").getResultList();		
		
	}
	
	public void crear()
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
			long totalSENacion=0;
			long totalSNINacion=0;
			long totalSINacion=0;
			long totalSMINacion=0;
			long totalSujIntNacion=0;
			long totalSujEvTNacion=0;
			long totalSujTraNacion=0;
			long totalSujSeNacion=0;
			long totalSujTTNacion=0;
			long totalTINacion=0;
			List<Provincia> estados= new ArrayList<Provincia>();
			
			if(listadoEstadosSelecionadas.isEmpty())
			{
				listadoEstadosSelecionadas=listadoEstados;
			}
			for(int e=0;e<listadoEstadosSelecionadas.size();e++)
			{
				long totalSEEstado=0;
				long totalSNIEstado=0;
				long totalSIEstado=0;
				long totalSMIEstado=0;
				long totalSujIntEstado=0;
				long totalSujEvTEstado=0;
				long totalSujTraEstado=0;
				long totalSujSeEstado=0;
				long totalSujTTEstado=0;
				long totalTIEstado=0;
				if(listadoEstadosSelecionadas.get(e).getNacion().getCodigo().equals(listadoNacionSelecionadas.get(n).getCodigo()))
				{
					List<Entidad> entidades= new ArrayList<Entidad>();
					
					if(listadoEntidades.isEmpty())
					{
						for(int i=0;i<listadoEstadosSelecionadas.size();i++)
						{
							List<Entidad_ensayo> E = entityManager
									.createQuery(
											"select ee.entidad from EstudioEntidad_ensayo ee where ee.entidad.estado.id =:idEstado")
									.setParameter(
											"idEstado",
											listadoEstadosSelecionadas.get(
													i).getId())
									.getResultList();
							for(int j=0;j<E.size();j++)
							{
								if(!E.get(j).getNombre().equals(null))
									listadoEntidades.add(E.get(j));
							}
							
							
						}
					}
					if(listadoEntidadesSeleccionadas.isEmpty())
						listadoEntidadesSeleccionadas=listadoEntidades;
						
					
					for(int t=0;t<listadoEntidadesSeleccionadas.size();t++)
					{
						long totalSEEntidad=0;
						long totalSNIEntidad=0;
						long totalSIEntidad=0;
						long totalSMIEntidad=0;
						long totalSujIntEntidad=0;
						long totalSujEvTEntidad=0;
						long totalSujTraEntidad=0;
						long totalSujSeEntidad=0;
						long totalITEntidad=0;
						long totalSujTTentidad=0;
						
						if(listadoEntidadesSeleccionadas.get(t).getEstado().getId()==listadoEstadosSelecionadas.get(e).getId())
						{
							List<Ensayo> estudios= new ArrayList<Ensayo>();
							
							if(listadoEstudios.isEmpty() && listadoEntidadesSeleccionadas.get(t).getTipoEntidad().getId()==7)
							{
								for(int i=0;i<listadoEntidadesSeleccionadas.size();i++)
								{
									List<Estudio_ensayo> E = entityManager
											.createQuery(
													"select estudio from Estudio_ensayo estudio where estudio.entidad.id =:idEntidad and estudio.eliminado != True and estudio.estadoEstudio.codigo = 3")
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
													"select ee.estudio from EstudioEntidad_ensayo ee where ee.estudio.entidad.id =:idEntidad and ee.estudio.eliminado != True and estudio.estadoEstudio.codigo = 3")
											.setParameter(
													"idEntidad",
													listadoEntidadesSeleccionadas
															.get(i).getId())
											.getResultList();
									listadoEstudios.addAll(E);
								}
							}
							if(listadoEstudiosSeleccionados.isEmpty())
								listadoEstudiosSeleccionados=listadoEstudios;
								
							for(int s=0;s<listadoEstudiosSeleccionados.size();s++)
							{
								long totalSujetoEvaluados=0;
								long totalSujetosNoInc=0;
								long totalSujetoInc=0;
								long totalSujetoMalInc=0;
								long totalSujetoInt=0;
								long totalsujetoEvaluT=0;
								long totalsujetoTratamiento=0;
								long totalsujetoSeguimiento=0;
								long totalSujetoTotalIn=0;
								long totalSujetoTotalTr=0;
								
								//if(listadoEstudiosSeleccionados.get(s).getEntidad().getId()==listadoEntidadesSeleccionadas.get(t).getId())
								{
									List<GrupoSujeto> grupoSujetoses= new ArrayList<GrupoSujeto>();
									
									if(listadoGrupos.isEmpty() && listadoEntidadesSeleccionadas.get(t).getTipoEntidad().getId()==7)
									{
										for(int i=0;i<listadoEstudiosSeleccionados.size();i++)
										{
											List<GrupoSujetos_ensayo> G = entityManager
													.createQuery(
															"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado != True and grupo.estudio.id =:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
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
															"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado != True and grupo.estudio.id =:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
													.setParameter("idEstudio",
															listadoEstudiosSeleccionados.get(i).getId()).getResultList();
											for(int j=0;j<G.size();j++)
											{
												listadoGrupos.add(G.get(j));
											}
											
										}
										//listadoGruposSeleccionados=listadoGrupos;
									}
									if(listadoGruposSeleccionados.isEmpty())
										listadoGruposSeleccionados=listadoGrupos;
										
									
									for(int g=0;g<listadoGruposSeleccionados.size();g++)
									{
										if(listadoGruposSeleccionados.get(g).getEstudio().getId()==listadoEstudiosSeleccionados.get(s).getId())
										{
											GrupoSujetos_ensayo grupo = listadoGruposSeleccionados.get(g);
											long sujetoEvaluados=0;
											long sujetosNoInc=0;
											long sujetoInc=0;
											long sujetoMalInc=0;
											long sujetoInt=0;
											long sujEvaluadoT=0;
											long sujTratamiento=0;
											long sujSeguimiento=0;
											long totalIn=0;
											long totalTr=0;
											
											List<Sujeto_ensayo> sujetosDelGrupo = new ArrayList<Sujeto_ensayo>();
											
											if(listadoEntidadesSeleccionadas.get(t).getTipoEntidad().getId()==7)
											{
												sujetosDelGrupo = entityManager.createQuery(
														"select distinct sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio =:idEstudio and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin ")
												.setParameter("idEstudio",listadoEstudiosSeleccionados.get(s))	
												.setParameter("idGrupo",grupo.getId()).setParameter("fechaInicio",fechaInicio)
												.setParameter("fechaFin",fechaFin).getResultList();
											}
											else{
											sujetosDelGrupo = entityManager.createQuery(
													"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio =:idEstudio and sujeto.entidad =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin")
											.setParameter("idEntidad",listadoEntidadesSeleccionadas.get(t))
											.setParameter("idEstudio",listadoEstudiosSeleccionados.get(s))
											.setParameter("idGrupo",grupo.getId()).setParameter("fechaInicio",fechaInicio)
											.setParameter("fechaFin",fechaFin).getResultList();
											}
											
											
											if(!sujetosDelGrupo.isEmpty())
											{
												haySujetos = true;
												for(int j=0;j<sujetosDelGrupo.size();j++)
												{
													if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==1)
														sujetoEvaluados++;
													else if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==2)
														sujetoMalInc++;
													else if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==3)
														sujetosNoInc++;
													else if(sujetosDelGrupo.get(j).getEstadoInclusion().getCodigo()==4)
														sujetoInc++;
													
													if(sujetosDelGrupo.get(j).getEstadoTratamiento().getCodigo() == 4)
														sujetoInt++;
													else if(sujetosDelGrupo.get(j).getEstadoTratamiento().getCodigo() == 1)
														sujEvaluadoT++;
													else if(sujetosDelGrupo.get(j).getEstadoTratamiento().getCodigo() == 2)
														sujTratamiento++;
													else if(sujetosDelGrupo.get(j).getEstadoTratamiento().getCodigo() == 3)
														sujSeguimiento++;
													
													//falta ver como se calculan los sujetos interrumpidos
												}totalIn= sujetoEvaluados + sujetoMalInc + sujetosNoInc + sujetoInc;
												totalTr= sujetoInt + sujEvaluadoT + sujTratamiento + sujSeguimiento;  
												
												GrupoSujeto grupoNuevo =new GrupoSujeto(listadoGruposSeleccionados.get(g).getNombreGrupo(), sujetoEvaluados, sujetosNoInc, sujetoInc, sujetoMalInc, sujetoInt, sujEvaluadoT, sujTratamiento, sujSeguimiento, totalIn, totalTr);
												grupoSujetoses.add(grupoNuevo);
												totalSujetoEvaluados+=sujetoEvaluados;
												totalSujetosNoInc+=sujetosNoInc;
												totalSujetoInc+=sujetoInc;
												totalSujetoMalInc+=sujetoMalInc;
												totalSujetoInt+=sujetoInt;
												totalsujetoEvaluT+=sujEvaluadoT;
												totalsujetoTratamiento+=sujTratamiento;
												totalsujetoSeguimiento+=sujSeguimiento;
												totalSujetoTotalIn+=totalIn;
												totalSujetoTotalTr+=totalTr;
												
											}
											
										}
										
									}
									if(!grupoSujetoses.isEmpty())
									{
										Ensayo ensayo =new Ensayo(listadoEstudiosSeleccionados.get(s).getNombre(), grupoSujetoses, totalSujetoEvaluados, totalSujetosNoInc, totalSujetoInc, totalSujetoMalInc, totalSujetoInt, totalsujetoEvaluT, totalsujetoTratamiento, totalsujetoSeguimiento, totalSujetoTotalIn, totalSujetoTotalTr);
										estudios.add(ensayo);
										totalSEEntidad+=totalSujetoEvaluados;
										totalSNIEntidad+=totalSujetosNoInc;
										totalSIEntidad+=totalSujetoInc;
										totalSMIEntidad+=totalSujetoMalInc;
										totalSujIntEntidad+=totalSujetoInt;
										totalSujEvTEntidad+=totalsujetoEvaluT;
										totalSujTraEntidad+=totalsujetoTratamiento;
										totalSujSeEntidad+=totalsujetoSeguimiento;
										totalITEntidad+=totalSujetoTotalIn;
										totalSujTTentidad+=totalSujetoTotalTr;
										
									}
								
								}
							}
							if(!estudios.isEmpty())
							{
								Entidad entidad= new Entidad(listadoEntidadesSeleccionadas.get(t).getNombre(), estudios, totalSEEntidad, totalSNIEntidad, totalSIEntidad, totalSMIEntidad, totalSujIntEntidad, totalSujEvTEntidad, totalSujTraEntidad, totalSujSeEntidad, totalITEntidad, totalSujTTentidad);
								entidades.add(entidad);
								totalSEEstado+=totalSEEntidad;
								totalSNIEstado+=totalSNIEntidad;
								totalSIEstado+=totalSIEntidad;
								totalSMIEstado+=totalSMIEntidad;
								totalSujIntEstado+=totalSujIntEntidad;
								totalSujEvTEstado+=totalSujEvTEntidad;
								totalSujTraEstado+=totalSujTraEntidad;
								totalSujSeEstado+=totalSujSeEntidad;
								totalSujTTEstado+=totalITEntidad;
								totalTIEstado+=totalSujTTentidad;
							}
							
						}
						
					}
					if(!entidades.isEmpty())
					{
						Provincia provincia = new Provincia(listadoEstadosSelecionadas.get(e).getValor(), entidades, totalSEEstado, totalSNIEstado, totalSIEstado, totalSMIEstado, totalSujIntEstado, totalSujEvTEstado, totalSujTraEstado, totalSujSeEstado, totalSujTTEstado, totalTIEstado);
						estados.add(provincia);
						totalSENacion+=totalSEEstado;
						totalSNINacion+=totalSNIEstado;
						totalSINacion+=totalSIEstado;
						totalSMINacion+=totalSMIEstado;
						totalSujIntNacion+=totalSujIntEstado;
					    totalSujEvTNacion=totalSujEvTEstado;
						totalSujTraNacion=totalSujTraEstado;
						totalSujSeNacion=totalSujSeEstado;
						totalSujTTNacion+=totalSujTTEstado;
						totalTINacion+=totalTIEstado;
						
					}
					
				}
				
				
			}
			if(totalSENacion==0 && totalSNINacion==0 && totalSINacion==0 && totalSMINacion==0 && totalSujIntNacion==0)
				break;
			
			if(estados!=null && haySujetos)
			{
				Pais pais = new Pais(listadoNacionSelecionadas.get(n).getValor(), estados, totalSENacion, totalSNINacion, totalSINacion, totalSMINacion, totalSujIntNacion, totalSujEvTNacion, totalSujTraNacion, totalSujSeNacion, totalSujTTNacion, totalTINacion);
				naciones.add(pais);
			}
			
		}
		
		if (haySujetos) {
		reporteSujetos=new HashMap();
		
		reporteSujetos.put("pais", SeamResourceBundle.getBundle().getString("pais"));
		reporteSujetos.put("sujetos", SeamResourceBundle.getBundle().getString("sujetos"));
		reporteSujetos.put("provincia", SeamResourceBundle.getBundle().getString("provincia"));
		reporteSujetos.put("entidad", SeamResourceBundle.getBundle().getString("entidad"));
		reporteSujetos.put("estudio", SeamResourceBundle.getBundle().getString("estudio"));
		reporteSujetos.put("grupos", SeamResourceBundle.getBundle().getString("grupos"));
		reporteSujetos.put("evaluado", "En evaluación");
		reporteSujetos.put("noIncluido", SeamResourceBundle.getBundle().getString("noIncluido"));
		reporteSujetos.put("incluido", SeamResourceBundle.getBundle().getString("incluido"));
		reporteSujetos.put("malIncluido", SeamResourceBundle.getBundle().getString("malIncluido"));
		reporteSujetos.put("interrumpido", SeamResourceBundle.getBundle().getString("interrumpido"));
		reporteSujetos.put("estInclusion", SeamResourceBundle.getBundle().getString("estInclusion"));
		reporteSujetos.put("estTrata", SeamResourceBundle.getBundle().getString("estTrata"));
		reporteSujetos.put("seguimiento", SeamResourceBundle.getBundle().getString("seguimiento"));
		reporteSujetos.put("tratamiento", SeamResourceBundle.getBundle().getString("tratamiento"));
		reporteSujetos.put("totalEstudio", SeamResourceBundle.getBundle().getString("totalEstudio"));
		reporteSujetos.put("totalEntidad", SeamResourceBundle.getBundle().getString("totalEntidad"));
		reporteSujetos.put("totalEstado", SeamResourceBundle.getBundle().getString("totalEstado"));
		reporteSujetos.put("totalNacion", SeamResourceBundle.getBundle().getString("totalNacion"));
		reporteSujetos.put("totalInclusion", SeamResourceBundle.getBundle().getString("totalInclusion"));
		reporteSujetos.put("totalTrata", SeamResourceBundle.getBundle().getString("totalTrata"));	
		
		
		
		nombreReport=reportManager.ExportReport("reportEstadistica", reporteSujetos, naciones, FileType.HTML_FILE);
		 flag=true;
		 flag2=false;
		} 
			else 
		 	{
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
		 
		this.fechaInicio=null;
		this.fechaFin=null;
	}

	
	
	
	public void estadosNacion()
	{
		listadoEstados.clear();
		for(int i=0;i<listadoNacionSelecionadas.size();i++)
		{
			List<Estado_ensayo> E = entityManager
					.createQuery(
							"select estado from Estado_ensayo estado where estado.nacion.id =:idNacion ")
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
							"select distinct ee.entidad from EstudioEntidad_ensayo ee where ee.entidad.estado.id =:idEstado")
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
					+ "and ue.estudioEntidad.estudio.eliminado != True and ue.estudioEntidad.estudio.estadoEstudio.codigo > 2 and ue.usuario.id=:idUser")
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
	

	@SuppressWarnings("unchecked")
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
	
	
	public void exportReportToFileFormat(){
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportEstadistica", reporteSujetos, naciones,FileType.PDF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportEstadistica", reporteSujetos, naciones,FileType.RTF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportEstadistica", reporteSujetos, naciones,FileType.EXCEL_FILE);
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

	

	
	//CU 16 Crear cronograma espec�fico.
	//Cronnograma Especifico by Eiler
	
	//Codigo para picar la cadena de dias by Evelio.
	
	//Listado de momentos de seguimiento especificos by Eiler
	/*public List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto()
	{
		List<MomentoSeguimientoEspecifico_ensayo> momentos=new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
		try {
			momentos=entityManager.createQuery("select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:id order by mse.fechaInicio asc").setParameter("id", sujeto.getId()).getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return momentos;
	}
	
	//Listado de nombres de los momentos de Seguimiento by Eiler
	public List<String>	nombresMomentos()
	{
		List<MomentoSeguimientoEspecifico_ensayo> momentos=momentosSujeto();
		List<String> nombres=new ArrayList<String>();
		for (int i = 0; i < momentos.size(); i++) {
			int cont=0;
			for (int j = 0; j < nombres.size(); j++) {
				if (momentos.get(i).getMomentoSeguimientoGeneral().getNombre().equals(nombres.get(j))) {
					cont++;
				}
			}
			if (cont==0) {
				nombres.add(momentos.get(i).getMomentoSeguimientoGeneral().getNombre());
			}
			
		}
		return nombres;
	}
	
	//Compruebo si el nombre del momentopertenece al momento de seguimiento by Eiler
	public Boolean PerteneceMomento(MomentoSeguimientoEspecifico_ensayo momento, String nombre,Date fecha)
	{
		String nombreMomento=momento.getMomentoSeguimientoGeneral().getNombre();
		Date fechaMomento=momento.getFechaInicio();
		if ((nombre.equals(nombreMomento))&&(fecha.equals(fechaMomento)))
		{
			return true;
		}
		return false;
	}
	
	@End 
	public void salir(){		
		
	}
	
	@Remove @Destroy
	public void destroy(){}
	
	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	public Long getIdSujeto() {
		return idSujeto;
	}
	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}
	public Date getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(Date fechainicio) {
		this.fechainicio = fechainicio;
	}*/
	
	
}
