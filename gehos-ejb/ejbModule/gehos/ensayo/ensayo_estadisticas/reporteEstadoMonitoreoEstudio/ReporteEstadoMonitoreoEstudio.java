package gehos.ensayo.ensayo_estadisticas.reporteEstadoMonitoreoEstudio;

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
import gehos.ensayo.ensayo_estadisticas.entity.SujetoEstadisticaReporte;
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
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("unchecked")
@Name("reporteEstadoMonitoreoEstudio")
@Scope(ScopeType.CONVERSATION)
public class ReporteEstadoMonitoreoEstudio {

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	@In
	private Usuario user;

	private List<Estudio_ensayo> listaEstudioEntidad;
	private List<String> listarEstudios;
	private String estudio;
	
	protected @In
	EntityManager entityManager;
	protected @In(create = true)
	FacesMessages facesMessages;
	protected @In
	IBitacora bitacora;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	final String seleccione = SeamResourceBundle.getBundle().getString("lbl_seleccione_ens"); 
	private Date fechaInicio;
	private Date fechaFin;

	// Paises
	private List<Nacion_ensayo> listadoNacion = new ArrayList<Nacion_ensayo>();
	private List<Nacion_ensayo> listadoNacionSelecionadas = new ArrayList<Nacion_ensayo>();

	private List<Provincia> listaPaises = new ArrayList<Provincia>();

	// Provincias
	private List<Estado_ensayo> listadoEstados = new ArrayList<Estado_ensayo>();
	private List<Estado_ensayo> listadoEstadosSelecionadas = new ArrayList<Estado_ensayo>();

	// Entidades
	private List<Entidad_ensayo> listadoEntidades = new ArrayList<Entidad_ensayo>();
	private List<Entidad_ensayo> listadoEntidadesSeleccionadas = new ArrayList<Entidad_ensayo>();

	// Estudios o ensayos
	private List<Estudio_ensayo> listadoEstudios = new ArrayList<Estudio_ensayo>();
	private List<Estudio_ensayo> listadoEstudiosSeleccionados = new ArrayList<Estudio_ensayo>();

	// Grupos de sujetos
	private List<GrupoSujetos_ensayo> listadoGrupos = new ArrayList<GrupoSujetos_ensayo>();
	private List<GrupoSujetos_ensayo> listadoGruposSeleccionados = new ArrayList<GrupoSujetos_ensayo>();

	
	private Map reporteMonitoreo;
	private String nombreReport;
	private String pathExportedReport = "";

	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");

	private String fileformatToExport;
	private List<String> filesFormatCombo;

	List<Pais> naciones = new ArrayList<Pais>();
	List<SujetoEstadisticaReporte> sujetoslist;

	private Integer style;
	
	public String getEstudio() {
		return estudio;
	}

	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}

	public List<String> getListarEstudios() {
		if(listarEstudios == null || listarEstudios.isEmpty())
			this.cargarEstudios();
		return listarEstudios;
	}

	public void setListarEstudios(List<String> listarEstudios) {
		this.listarEstudios = listarEstudios;
	}

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


	public void estadosNacion() {
		listadoEstados.clear();
		for (int i = 0; i < listadoNacionSelecionadas.size(); i++) {
			List<Estado_ensayo> E = entityManager
					.createQuery(
							"select estado from Estado_ensayo estado where estado.nacion.id =:idNacion ")
					.setParameter("idNacion",
							listadoNacionSelecionadas.get(i).getId())
					.getResultList();
			listadoEstados.addAll(E);
		}
		Collections.sort(listadoEstados, new Comparator<Estado_ensayo>() {

			@Override
			public int compare(Estado_ensayo o1, Estado_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getValor().compareTo(o2.getValor());
			}

		});

		validationEstadosNacion();
	}

	public void validationEstadosNacion() {

		boolean estadoAsignado = false;

		for (int j = 0; j < listadoEstadosSelecionadas.size(); j++) {
			for (int i = 0; i < listadoEstados.size(); i++) {
				if (listadoEstados.get(i).equals(
						listadoEstadosSelecionadas.get(j))) {
					listadoEstados.remove(i);
					i--;
					estadoAsignado = true;
					break;
				}
			}
			if (estadoAsignado == false) {
				listadoEstadosSelecionadas.remove(j);
				j--;
			} else
				estadoAsignado = false;
		}

		entidades();

	}

	public void entidades() {
		listadoEntidades.clear();
		Usuario user = (Usuario) Component.getInstance("user");

		for (int i = 0; i < listadoEstadosSelecionadas.size(); i++) {
			List<Entidad_ensayo> E = entityManager
					.createQuery(
							"select distinct ee.entidad from EstudioEntidad_ensayo ee where ee.entidad.estado.id =:idEstado")
					.setParameter("idEstado",
							listadoEstadosSelecionadas.get(i).getId())
					.getResultList();

			List<Entidad_configuracion> Ec = entityManager
					.createQuery(
							"select distinct ent "
									+ "from Entidad_configuracion ent "
									+ "inner join ent.servicioInEntidads servInEnt inner join servInEnt.usuarios u with u.id=:idUser "
									+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
									// + "where ent.perteneceARhio = true "
									+ "and (ent.eliminado = null or ent.eliminado = false) and ent.estado.id =:idEstado "
									+ "order by ent.nombre")
					.setParameter("idEstado",
							listadoEstadosSelecionadas.get(i).getId())
					.setParameter("idUser", user.getId()/*
														 * userTools.getUser().getId
														 * ()
														 */).getResultList();

			for (int h = 0; h < Ec.size(); h++) {
				for (int j = 0; j < E.size(); j++) {
					if (!E.get(j).getNombre().equals(null)
							&& Ec.get(h).getNombre()
									.equals(E.get(j).getNombre()))
						listadoEntidades.add(E.get(j));
				}
			}
		}
		Collections.sort(listadoEntidades, new Comparator<Entidad_ensayo>() {
			@Override
			public int compare(Entidad_ensayo o1, Entidad_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getNombre().compareTo(o2.getNombre());
			}
		});

		validationEntidades();
	}

	public void validationEntidades() {

		boolean entidadAsignada = false;

		for (int j = 0; j < listadoEntidadesSeleccionadas.size(); j++) {
			for (int i = 0; i < listadoEntidades.size(); i++) {
				if (listadoEntidades.get(i).equals(
						listadoEntidadesSeleccionadas.get(j))) {
					listadoEntidades.remove(i);
					i--;
					entidadAsignada = true;
					break;
				}
			}
			if (entidadAsignada == false) {
				listadoEntidadesSeleccionadas.remove(j);
				j--;
			} else
				entidadAsignada = false;
		}

		estudios();

	}

	public void estudios() {
		listadoEstudios.clear();
		Usuario user = (Usuario) Component.getInstance("user");

		for (int i = 0; i < listadoEntidadesSeleccionadas.size(); i++) {
			List<Estudio_ensayo> E = entityManager
					.createQuery(
							"select distinct ue.estudioEntidad.estudio from UsuarioEstudio_ensayo ue where ue.estudioEntidad.entidad.id =:idEntidad "
									+ "and ue.estudioEntidad.estudio.eliminado != True and ue.estudioEntidad.estudio.estadoEstudio.codigo > 2 and ue.usuario.id=:idUser")
					.setParameter("idEntidad",
							listadoEntidadesSeleccionadas.get(i).getId())
					.setParameter("idUser", user.getId()).getResultList();

			for (int j = 0; j < E.size(); j++) {
				if (!E.get(j).getNombre().equals(null))
					listadoEstudios.add(E.get(j));
			}

		}
		Collections.sort(listadoEstudios, new Comparator<Estudio_ensayo>() {
			@Override
			public int compare(Estudio_ensayo o1, Estudio_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getNombre().compareTo(o2.getNombre());
			}
		});

		validationEstudios();
	}

	public void validationEstudios() {

		boolean estudioAsignado = false;
		for (int j = 0; j < listadoEstudiosSeleccionados.size(); j++) {
			for (int i = 0; i < listadoEstudios.size(); i++) {
				if (listadoEstudios.get(i).equals(
						listadoEstudiosSeleccionados.get(j))) {
					listadoEstudios.remove(i);
					i--;
					estudioAsignado = true;
					break;
				}
			}
			if (estudioAsignado == false) {
				listadoEstudiosSeleccionados.remove(j);
				j--;
			} else
				estudioAsignado = false;
		}

		grupos_Sujetos();

	}

	public void grupos_Sujetos() {
		listadoGrupos.clear();
		// List<EstudioEntidad_ensayo> listadoEstudioEntidad = new
		// ArrayList<EstudioEntidad_ensayo>();
		for (int i = 0; i < listadoEstudiosSeleccionados.size(); i++) {
			List<GrupoSujetos_ensayo> G = entityManager
					.createQuery(
							"select distinct ee.estudio.grupoSujetoses from EstudioEntidad_ensayo ee where ee.estudio.id =:idEstudio ")
					.setParameter("idEstudio",
							listadoEstudiosSeleccionados.get(i).getId())
					.getResultList();
			for (int j = 0; j < G.size(); j++) {
				if (!G.get(j).getEliminado())
					listadoGrupos.add(G.get(j));
			}
			// listadoGrupos.addAll(G);

		}
		Collections.sort(listadoGrupos, new Comparator<GrupoSujetos_ensayo>() {

			@Override
			public int compare(GrupoSujetos_ensayo o1, GrupoSujetos_ensayo o2) {
				// TODO Auto-generated method stub
				return o1.getNombreGrupo().compareTo(o2.getNombreGrupo());
			}

		});

		validationGrupoSujetos();
	}

	public void validationGrupoSujetos() {

		boolean grupoSujetosAsignado = false;
		for (int j = 0; j < listadoGruposSeleccionados.size(); j++) {
			for (int i = 0; i < listadoGrupos.size(); i++) {
				if (listadoGrupos.get(i).equals(
						listadoGruposSeleccionados.get(j))) {
					listadoGrupos.remove(i);
					i--;
					grupoSujetosAsignado = true;
					break;
				}
			}
			if (grupoSujetosAsignado == false) {
				listadoGruposSeleccionados.remove(j);
				j--;
			} else
				grupoSujetosAsignado = false;
		}

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

	public void reporteMonitoreo() {
		sujetoslist=new ArrayList<SujetoEstadisticaReporte>();
		style=-1;
		boolean haySujetos=false;
		Integer totalMomMoniNoIniciado = 0;
		Integer totalMomMoniIniciado = 0;
		Integer totalMomMoniCompletado = 0;
		Integer totalNotasNuevas = 0;
		Integer totalNotasSinResolver = 0;
		Integer totalNotasResueltas = 0;
		Integer totalNotasCerradas = 0;
		Integer totalSujetos=0;
		
		Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
		for (int j = 0; j < listaEstudioEntidad.size(); j++) {
			if (listaEstudioEntidad.get(j).getNombre().equals(this.estudio)) {
				estudioEnsayo = listaEstudioEntidad.get(j);
				break;
			}
		}
		haySujetos=false;
		if (fechaInicio != null && fechaFin != null &&! this.estudio.equals("")) {

			List<EstudioEntidad_ensayo> listaEntidadEst; //estudio seleccionado
			listaEntidadEst = (List<EstudioEntidad_ensayo>) entityManager
					.createQuery(
							"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false")
					.setParameter("idEst", estudioEnsayo.getId())
					.getResultList();

			for (int z = 0; z < listaEntidadEst.size(); z++) {

				List<GrupoSujetos_ensayo> grupos = (List<GrupoSujetos_ensayo>) entityManager.createQuery("select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
						.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
				
				for (int i = 0; i < grupos.size(); i++) { //en cada uno de los grupos extraigo los sujetos
					
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager
							.createQuery("select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())
							.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
							.setParameter("idGrupo", grupos.get(i).getId())
							.setParameter("fechaInicio", fechaInicio)
							.setParameter("fechaFin", fechaFin).getResultList();
					
					totalSujetos+=sujetosDelGrupo.size();
					
					if(!sujetosDelGrupo.isEmpty()){
						haySujetos=true;
						for(int k=0;k<sujetosDelGrupo.size();k++){
							
							Integer momMonNoIniciados=0;
							Integer momMonIniciados=0;
							Integer momMomCompletados=0;
							Integer notasNuevas=0;
							Integer notasSinResolver=0;
							Integer notasResueltas=0;
							Integer notasCerradas=0;
							
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							/* Se extraen los momentos de seguimientos del sujeto
							 * con los parametros espesificos
							 */
							List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
									.createQuery("select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.eliminado = false and momento.fechaInicio<=:fechaFin")
									.setParameter("idSujeto", sujeto.getId())
									.setParameter("fechaFin", fechaFin)
									.getResultList();
							
							
							for (int l=0;l<momentosSujeto.size();l++){
								
								MomentoSeguimientoEspecifico_ensayo momento = momentosSujeto
										.get(l);

								if (momento
										.getEstadoMonitoreo()
										.getCodigo() == 1)
									momMonIniciados++;
								else if (momento
										.getEstadoMonitoreo()
										.getCodigo() == 3)
									momMomCompletados++;
								else if (momento
										.getEstadoMonitoreo()
										.getCodigo() == 2)
									momMonNoIniciados++;
								
								List<CrdEspecifico_ensayo> crdMomento = entityManager
										.createQuery("select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
										.setParameter("idMomento", momento.getId())
										.getResultList();
								
								for (int m = 0; m < crdMomento.size(); m++) {
									CrdEspecifico_ensayo crd = crdMomento.get(m);
									List<Nota_ensayo> notas = entityManager
											.createQuery("select nota from Nota_ensayo nota where nota.crdEspecifico.id =:idCRD and nota.notaSitio = false and nota.eliminado = false and nota.notaPadre = null")
											.setParameter("idCRD", crd.getId())
											.getResultList();
									
									
									for(Nota_ensayo n: notas){
										if(n.getEstadoNota().getCodigo()==1){
											notasNuevas++;
										}
										
										if(n.getEstadoNota().getCodigo()==1 || n.getEstadoNota().getCodigo()==2){
											notasSinResolver++;
										}
										if(n.getEstadoNota().getCodigo()==3){
											notasResueltas++;
										}
										if(n.getEstadoNota().getCodigo()==4){
											notasCerradas++;
										}
									}																
									
								}
								
							}
							
							SujetoEstadisticaReporte sujetoToAdd=new SujetoEstadisticaReporte(sujeto.getCodigoPaciente(), sujeto.getEntidad().getNombre(), momMonNoIniciados, momMonIniciados, momMomCompletados, notasNuevas, notasSinResolver, notasResueltas, notasCerradas,this.style.toString());
							sujetoslist.add(sujetoToAdd);
							totalMomMoniCompletado+=momMomCompletados;
							totalMomMoniIniciado+=momMonIniciados;
							totalMomMoniNoIniciado+=momMonNoIniciados;
							totalNotasCerradas+=notasCerradas;
							totalNotasNuevas+=notasNuevas;
							totalNotasResueltas+=notasResueltas;
							totalNotasSinResolver+=notasSinResolver;						
							
						}				
					}
				}							
			}
			
			this.style=1;
			SujetoEstadisticaReporte sujetoToList=new SujetoEstadisticaReporte("Total: ", String.valueOf(listaEntidadEst.size()), totalMomMoniNoIniciado, totalMomMoniIniciado, totalMomMoniCompletado, totalNotasNuevas, totalNotasSinResolver, totalNotasResueltas, totalNotasCerradas, this.style.toString());
			sujetoslist.add(sujetoToList);
			
			if(haySujetos){
				reporteMonitoreo=new HashMap();
				reporteMonitoreo.put("estudio",this.estudio);
				reporteMonitoreo.put("nombreEstudio", "Estudio:");
				reporteMonitoreo.put("totalSujetos","Total de sujetos:");
				reporteMonitoreo.put("ptotalsujetos",totalSujetos);
				reporteMonitoreo.put("nombreSujeto","Nombre del sujeto");
				reporteMonitoreo.put("entidad","Entidad");
				reporteMonitoreo.put("monitoreoMomento","Estado de monitoreo");
				reporteMonitoreo.put("momentosNoIniciados","No iniciados");
				reporteMonitoreo.put("momentosIniciados","Iniciados");
				reporteMonitoreo.put("momentosCompletados","Completados");
				reporteMonitoreo.put("Notas","Estado de notas");
				reporteMonitoreo.put("notasNuevas","Nuevas");
				reporteMonitoreo.put("notasSinResolver","Sin resolver");
				reporteMonitoreo.put("notasResueltas","Resueltas");
				reporteMonitoreo.put("notasCerradas","Cerradas");
			
				nombreReport = reportManager.ExportReport(
						"reportEstadisticaMonitoreoEstudio", reporteMonitoreo,
						sujetoslist, FileType.HTML_FILE);
				flag = true;
				flag2 = false;
			} else {
				flag = false;
				flag2 = true;
			}				
		} else {
			flag = false;
			flag2 = true;
		}
		
		this.estudio="";
		this.fechaInicio=null;
		this.fechaFin=null;
	}

	/*public void reporteMonitoreoo() {

		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[1];

		r[0] = validations.requeridoList(this.listadoNacionSelecionadas,
				"ListShuttleNacion", facesMessages);

		naciones.clear();
		haySujetos = false;

		if (fechaFin != null && fechaInicio != null && !r[0]) {
			for (int n = 0; n < listadoNacionSelecionadas.size(); n++) {
				long totalMINacion = 0;
				long totalMCNacion = 0;
				long totalNSRNacion = 0;
				long totalMNoINacion = 0;
				List<Provincia> estados = new ArrayList<Provincia>();
				if (listadoEstadosSelecionadas.isEmpty())
					listadoEstadosSelecionadas = listadoEstados;

				for (int e = 0; e < listadoEstadosSelecionadas.size(); e++) {
					long totalMIEstado = 0;
					long totalMCEstado = 0;
					long totalNSREstado = 0;
					long totalMNoIEstado = 0;
					if (listadoEstadosSelecionadas
							.get(e)
							.getNacion()
							.getCodigo()
							.equals(listadoNacionSelecionadas.get(n)
									.getCodigo())) {
						List<Entidad> entidades = new ArrayList<Entidad>();

						if (listadoEntidades.isEmpty()) {
							for (int i = 0; i < listadoEstadosSelecionadas
									.size(); i++) {
								List<Entidad_ensayo> E = entityManager
										.createQuery(
												"select ee.entidad from EstudioEntidad_ensayo ee where ee.entidad.estado.id =:idEstado")
										.setParameter(
												"idEstado",
												listadoEstadosSelecionadas.get(
														i).getId())
										.getResultList();
								for (int j = 0; j < E.size(); j++) {
									if (!E.get(j).getNombre().equals(null))
										listadoEntidades.add(E.get(j));
								}

							}
						}
						if (listadoEntidadesSeleccionadas.isEmpty())
							listadoEntidadesSeleccionadas = listadoEntidades;

						for (int t = 0; t < listadoEntidadesSeleccionadas
								.size(); t++) {
							long totalMIEntidad = 0;
							long totalMCEntidad = 0;
							long totalNSREntidad = 0;
							long totalMNoIEntidad = 0;
							if (listadoEntidadesSeleccionadas.get(t)
									.getEstado().getId() == listadoEstadosSelecionadas
									.get(e).getId()) {
								List<Ensayo> estudios = new ArrayList<Ensayo>();

								if (listadoEstudios.isEmpty()
										&& listadoEntidadesSeleccionadas.get(t)
												.getTipoEntidad().getId() == 7) {
									for (int i = 0; i < listadoEntidadesSeleccionadas
											.size(); i++) {
										List<Estudio_ensayo> E = entityManager
												.createQuery(
														"select estudio from Estudio_ensayo estudio where estudio.entidad.id =:idEntidad and estudio.eliminado != True")
												.setParameter(
														"idEntidad",
														listadoEntidadesSeleccionadas
																.get(i).getId())
												.getResultList();
										listadoEstudios.addAll(E);
									}
									// listadoEstudiosSeleccionados=listadoEstudios;
								}

								else if (listadoEstudios.isEmpty()) {
									for (int i = 0; i < listadoEntidadesSeleccionadas
											.size(); i++) {
										List<Estudio_ensayo> E = entityManager
												.createQuery(
														"select ee.estudio from EstudioEntidad_ensayo ee where ee.estudio.entidad.id =:idEntidad and ee.estudio.eliminado != True")
												.setParameter(
														"idEntidad",
														listadoEntidadesSeleccionadas
																.get(i).getId())
												.getResultList();
										listadoEstudios.addAll(E);
									}
								}
								if (listadoEstudiosSeleccionados.isEmpty())
									listadoEstudiosSeleccionados = listadoEstudios;

								for (int s = 0; s < listadoEstudiosSeleccionados
										.size(); s++) {
									long totalMIEstudio = 0;
									long totalMCEstudio = 0;
									long totalNSREstudio = 0;
									long totalMNoIEstudio = 0;
									// if
									// (listadoEstudiosSeleccionados.get(s).getEntidad().getId()
									// ==
									// listadoEntidadesSeleccionadas.get(t).getId())
									{
										List<GrupoSujeto> grupoSujetos = new ArrayList<GrupoSujeto>();

										if (listadoGrupos.isEmpty()
												&& listadoEntidadesSeleccionadas
														.get(t)
														.getTipoEntidad()
														.getId() == 7) {
											for (int i = 0; i < listadoEstudiosSeleccionados
													.size(); i++) {
												List<GrupoSujetos_ensayo> G = entityManager
														.createQuery(
																"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado != True and grupo.estudio.id =:idEstudio")
														.setParameter(
																"idEstudio",
																listadoEstudiosSeleccionados
																		.get(i)
																		.getId())
														.getResultList();
												for (int j = 0; j < G.size(); j++) {
													listadoGrupos.add(G.get(j));
												}

											}
											// listadoGruposSeleccionados=listadoGrupos;
										}

										else if (listadoGrupos.isEmpty()) {
											for (int i = 0; i < listadoEstudiosSeleccionados
													.size(); i++) {
												List<GrupoSujetos_ensayo> G = entityManager
														.createQuery(
																"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado != True and grupo.estudio.id =:idEstudio")
														.setParameter(
																"idEstudio",
																listadoEstudiosSeleccionados
																		.get(i)
																		.getId())
														.getResultList();

												for (int j = 0; j < G.size(); j++) {
													listadoGrupos.add(G.get(j));
												}

											}
										}
										if (listadoGruposSeleccionados
												.isEmpty())
											listadoGruposSeleccionados = listadoGrupos;

										for (int g = 0; g < listadoGruposSeleccionados
												.size(); g++) {
											long totalMomentosIniciados = 0;
											long totalMomentosCompletados = 0;
											long totalNotasSinResolver = 0;
											long totalMomentosNoIni = 0;
											if (listadoGruposSeleccionados
													.get(g).getEstudio()
													.getId() == listadoEstudiosSeleccionados
													.get(s).getId()) {
												GrupoSujetos_ensayo grupo = listadoGruposSeleccionados
														.get(g);
												List<Sujeto_ensayo> sujetosDelGrupo = new ArrayList<Sujeto_ensayo>();

												if (listadoEntidadesSeleccionadas
														.get(t)
														.getTipoEntidad()
														.getId() == 7) {
													sujetosDelGrupo = entityManager
															.createQuery(
																	"select distinct sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin ")
															.setParameter(
																	"idEstudio",
																	listadoEstudiosSeleccionados
																			.get(s)
																			.getId())
															.setParameter(
																	"idGrupo",
																	grupo.getId())
															.setParameter(
																	"fechaInicio",
																	fechaInicio)
															.setParameter(
																	"fechaFin",
																	fechaFin)
															.getResultList();
												}

												else {
													sujetosDelGrupo = entityManager
															.createQuery(
																	"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.entidad.id =:idEntidad and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.eliminado = false  and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin ")
															.setParameter(
																	"idEntidad",
																	listadoEntidadesSeleccionadas
																			.get(t)
																			.getId())
															.setParameter(
																	"idEstudio",
																	listadoEstudiosSeleccionados
																			.get(s)
																			.getId())
															.setParameter(
																	"idGrupo",
																	grupo.getId())
															.setParameter(
																	"fechaInicio",
																	fechaInicio)
															.setParameter(
																	"fechaFin",
																	fechaFin)
															.getResultList();
												}

												List<Sujeto> sujetos = new ArrayList<Sujeto>();
												if (!sujetosDelGrupo.isEmpty()) {
													haySujetos = true;
													for (int k = 0; k < sujetosDelGrupo
															.size(); k++) {
														if (sujetosDelGrupo
																.get(k)
																.getGrupoSujetos()
																.getId() == listadoGruposSeleccionados
																.get(g).getId()) {
															long momentosNoIni = 0;
															long momentosIniciados = 0;
															long momentosCompletados = 0;
															long notasSinResolver = 0;

															Sujeto_ensayo sujeto = sujetosDelGrupo
																	.get(k);

															List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto = entityManager
																	.createQuery(
																			"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:idSujeto and momento.eliminado = false and momento.fechaInicio<=:fechaFin")
																	.setParameter(
																			"idSujeto",
																			sujeto.getId())
																	.setParameter(
																			"fechaFin",
																			fechaFin)
																	.getResultList();
															for (int l = 0; l < momentosSujeto 
																	.size(); l++) {
																MomentoSeguimientoEspecifico_ensayo momento = momentosSujeto
																		.get(l);

																if (momento
																		.getEstadoMonitoreo()
																		.getCodigo() == 1)
																	momentosIniciados++;
																else if (momento
																		.getEstadoMonitoreo()
																		.getCodigo() == 3)
																	momentosCompletados++;
																else if (momento
																		.getEstadoMonitoreo()
																		.getCodigo() == 2)
																	momentosNoIni++;

																List<CrdEspecifico_ensayo> crdMomento = entityManager
																		.createQuery(
																				"select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.eliminado = false")
																		.setParameter(
																				"idMomento",
																				momento.getId())
																		.getResultList();
																
																for (int m = 0; m < crdMomento
																		.size(); m++) {
																	CrdEspecifico_ensayo crd = crdMomento
																			.get(m);
																	List<Nota_ensayo> notas = entityManager
																			.createQuery(
																					"select nota from Nota_ensayo nota where nota.crdEspecifico.id =:idCRD and nota.notaSitio = false and nota.eliminado = false and nota.notaPadre = null")
																			.setParameter(
																					"idCRD",
																					crd.getId())
																			.getResultList();
																	
																																	
																	for (int i = 0; i < notas
																			.size(); i++) {
																		if (notas
																				.get(i)
																				.getEstadoNota()
																				.getCodigo() == 1
																				|| notas.get(
																						i)
																						.getEstadoNota()
																						.getCodigo() == 2) {
																			notasSinResolver++;
																		}
																	}																
																	
																}

															}
															long var = 0;
															Sujeto sujetoEstadisticas = new Sujeto(
																	sujeto.getCodigoPaciente(),
																	momentosIniciados,
																	momentosCompletados,
																	notasSinResolver,
																	momentosNoIni,
																	var);
															sujetos.add(sujetoEstadisticas);
															totalMomentosIniciados += momentosIniciados;
															totalMomentosCompletados += momentosCompletados;
															totalNotasSinResolver += notasSinResolver;
															totalMomentosNoIni += momentosNoIni;

														}
													}
												}

												if (!sujetos.isEmpty()) {
													long var = 0;
													GrupoSujeto grupoNuevo = new GrupoSujeto(
															grupo.getNombreGrupo(),
															sujetos,
															totalMomentosIniciados,
															totalMomentosCompletados,
															totalNotasSinResolver,
															totalMomentosNoIni,
															var);
													grupoSujetos
															.add(grupoNuevo);
													totalMIEstudio += totalMomentosIniciados;
													totalMCEstudio += totalMomentosCompletados;
													totalNSREstudio += totalNotasSinResolver;
													totalMNoIEstudio += totalMomentosNoIni;

												}

											}

										}
										if (!grupoSujetos.isEmpty()) {
											long var = 0;
											Ensayo ensayo = new Ensayo(
													listadoEstudiosSeleccionados
															.get(s).getNombre(),
													grupoSujetos,
													totalMIEstudio,
													totalMCEstudio,
													totalNSREstudio,
													totalMNoIEstudio, var);
											estudios.add(ensayo);
											totalMIEntidad += totalMIEstudio;
											totalMCEntidad += totalMCEstudio;
											totalNSREntidad += totalNSREstudio;
											totalMNoIEntidad += totalMNoIEstudio;
										}

									}

								}
								if (!estudios.isEmpty()) {
									long var = 0;
									Entidad entidad = new Entidad(
											listadoEntidadesSeleccionadas
													.get(t).getNombre(),
											estudios, totalMIEntidad,
											totalMCEntidad, totalNSREntidad,
											totalMNoIEntidad, var);
									entidades.add(entidad);
									totalMIEstado += totalMIEntidad;
									totalMCEstado += totalMCEntidad;
									totalNSREstado += totalNSREntidad;
									totalMNoIEstado += totalMNoIEntidad;
								}

							}

						}
						if (!entidades.isEmpty()) {
							long var = 0;
							Provincia provincia = new Provincia(
									listadoEstadosSelecionadas.get(e)
											.getValor(), entidades,
									totalMIEstado, totalMCEstado,
									totalNSREstado, totalMNoIEstado, var);
							estados.add(provincia);
							totalMINacion += totalMIEstado;
							totalMCNacion += totalMCEstado;
							totalNSRNacion += totalNSREstado;
							totalMNoINacion += totalMNoIEstado;
						}

					}

				}
				if (totalMINacion == 0 && totalMINacion == 0
						&& totalNSRNacion == 00 && totalMNoINacion == 0)
					break;
				if (estados != null && haySujetos) {
					long var = 0;
					Pais pais = new Pais(listadoNacionSelecionadas.get(n)
							.getValor(), estados, totalMINacion, totalMCNacion,
							totalNSRNacion, totalMNoINacion, var);
					naciones.add(pais);
				}

			}
			if (haySujetos) {
				reporteMonitoreo = new HashMap();

				reporteMonitoreo.put("pais", SeamResourceBundle.getBundle().getString("pais"));
				reporteMonitoreo.put("sujetos", SeamResourceBundle.getBundle().getString("sujetos"));
				reporteMonitoreo.put("provincia", SeamResourceBundle.getBundle().getString("provincia"));
				reporteMonitoreo.put("entidad", SeamResourceBundle.getBundle().getString("entidad"));
				reporteMonitoreo.put("estudio", SeamResourceBundle.getBundle().getString("estudio"));
				reporteMonitoreo.put("grupo", SeamResourceBundle.getBundle().getString("grupo"));
				reporteMonitoreo.put("momentosNoIniciados", SeamResourceBundle.getBundle().getString("momentosNoIniciados"));
				reporteMonitoreo.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteMonitoreo.put("momentosIniciados", SeamResourceBundle.getBundle().getString("momentosIniciados"));
				reporteMonitoreo.put("momentosCompletados", SeamResourceBundle.getBundle().getString("momentosCompletados"));
				reporteMonitoreo.put("notasSinResolver", SeamResourceBundle.getBundle().getString("notasSinResolver"));
				reporteMonitoreo.put("totalGrupo", SeamResourceBundle.getBundle().getString("totalGrupo"));
				reporteMonitoreo.put("totalEstudio", SeamResourceBundle.getBundle().getString("totalEstudio"));
				reporteMonitoreo.put("totalEntidad", SeamResourceBundle.getBundle().getString("totalEntidad"));
				reporteMonitoreo.put("totalEstado", SeamResourceBundle.getBundle().getString("totalEstado"));
				reporteMonitoreo.put("totalNacion", SeamResourceBundle.getBundle().getString("totalNacion"));

				nombreReport = reportManager.ExportReport("reportEstadisticaMonitoreoEstudio", reporteMonitoreo,naciones, FileType.HTML_FILE);
				flag = true;
				flag2 = false;
			} else {
				flag = false;
				flag2 = true;
				noResult = SeamResourceBundle.getBundle()
						.getString("noResult3");
			}
		} else {
			flag = false;
			flag2 = true;
		}
		

	}*/

	public void exportReportToFileFormat() {
		if(this.fileformatToExport != null && !this.fileformatToExport.isEmpty()){

		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadisticaMonitoreoEstudio", reporteMonitoreo,
					sujetoslist, FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadisticaMonitoreoEstudio", reporteMonitoreo,
					sujetoslist, FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport(
					"reportEstadisticaMonitoreoEstudio", reporteMonitoreo,
					sujetoslist, FileType.EXCEL_FILE);
		}
		}
		this.fileformatToExport = null;

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
		if(fileformatToExport == null || fileformatToExport.isEmpty() || fileformatToExport.trim().equals(seleccione))
			fileformatToExport = null;
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
	
	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

}
