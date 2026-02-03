//CU 1 Buscar sujeto
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoSujeto_ensayo;
import gehos.ensayo.entity.EstadoTratamiento_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Variable_ensayo;
import gehos.ensayo.session.common.auto.SujetoList_ensayo;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.hibernate.mapping.Array;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("sujetoCustomListRe")
@Scope(ScopeType.CONVERSATION)
public class SujetoCustomListRe extends SujetoList_ensayo {

	private static final String EJBQL = "select sujeto from Sujeto_ensayo sujeto "
			+ "where sujeto.eliminado = false and sujeto.grupoSujetos.estudio = #{sujetoCustomListRe.seguridadEstudio.getEstudioEntidadActivo().getEstudio()} "
			+ "and sujeto.id in(select distinct re.idSujeto from ReReporteexpedito_ensayo re)";

	private static final String[] RESTRICTIONS = {
			"lower(sujeto.codigoPaciente) like concat('%',concat(lower(#{sujetoCustomListRe.sujeto.codigoPaciente.trim()}),'%'))",
			"lower(sujeto.grupoSujetos.nombreGrupo) like concat(lower(#{sujetoCustomListRe.nombeGrup.trim()}),'%')",
			"lower(sujeto.estadoInclusion.nombre) like concat(lower(#{sujetoCustomListRe.estadoI.trim()}),'%')",
			"lower(sujeto.estadoTratamiento.nombre) like concat(lower(#{sujetoCustomListRe.estadoT.trim()}),'%')",			
			"#{sujetoCustomListRe.idSujeto} <> sujeto.id" };

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Map<Long, List<MomentoSeguimientoEspecifico_ensayo>> listaFechasMomentos;
	MomentoSeguimientoEspecifico_ensayo momentoAnterior;
	MomentoSeguimientoEspecifico_ensayo momentoPosterior;
	Sujeto_ensayo suj = new Sujeto_ensayo();
	private boolean existResultados = true;
	Calendar calenadrio = Calendar.getInstance();
	private String estadoI;
	private String estadoM;
	private String estadoT;
	private String nombeGrup;
	private Date fechaInicioA;
	private Date fechaInicioP;

	@In
	private Usuario user;

	protected List<EstadoInclusion_ensayo> listarEstados;

	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private boolean seleccionar = false;
	private boolean modificar = false;
	private boolean ver = false;
	private int pagina;
	private boolean crear = false;
	private boolean gestionar = false;
	private boolean interrumpir = false;

	private Sujeto_ensayo sujeto = new Sujeto_ensayo();

	private Long idSujeto, ideliminarSuj, idhabilitado;

	

	@Override
	public List<Sujeto_ensayo> getResultList() {

		if (!this
				.getHospitalActivo()
				.getTipoEntidad()
				.getValor()
				.equals(SeamResourceBundle.getBundle().getString(
						"bioTecnologica"))) {

			String tempEJB = "select sujeto from Sujeto_ensayo sujeto "
					+ "where sujeto.eliminado = false and sujeto.estadoInclusion.codigo = 4 and sujeto.grupoSujetos.estudio = #{sujetoCustomListRe.seguridadEstudio.getEstudioEntidadActivo().getEstudio()} "
					+ "and sujeto.entidad = #{sujetoCustomListRe.getHospitalActivo()} and sujeto.id in(select distinct re.idSujeto from ReReporteexpedito_ensayo re)";
			this.setEjbql(tempEJB);
		} else {
			this.setEjbql(EJBQL);
		}

		return super.getResultList();

	}

	public SujetoCustomListRe() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("sujeto.numeroInclucion asc");
		this.ver = true;
		this.modificar = true;
		this.interrumpir = true;
		this.crear = true;
		this.gestionar = true;
	}

	public Role_ensayo DevolverRol() {
		Role_ensayo rol = new Role_ensayo();
		rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}
	
	public boolean pesquisajeHabilitado(){
		boolean habilitado = false;
		GrupoSujetos_ensayo grupo = new GrupoSujetos_ensayo();
		try {
			grupo = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select g from GrupoSujetos_ensayo g where g.estudio=:estud and g.nombreGrupo = 'Grupo Pesquisaje'")
					.setParameter("estud",
							seguridadEstudio.getEstudioEntidadActivo().getEstudio())
					.getSingleResult();
		} catch (Exception e) {
			grupo = null;
		}
		
		
		if(grupo != null && grupo.getHabilitado()){
			habilitado = true;
		}
		
		return habilitado;
	}

	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}

	@SuppressWarnings("unchecked")
	public String estadoMonitoreo(Long idSujeto) {
		List<MomentoSeguimientoEspecifico_ensayo> listarMomentos = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
				.createQuery(
						"select mom from MomentoSeguimientoEspecifico_ensayo mom where mom.sujeto.id=:idSuj")
				.setParameter("idSuj", idSujeto).getResultList();

		boolean iniciado = false;

		int contCompletado = 0;
		for (int i = 0; i < listarMomentos.size(); i++) {
			if (listarMomentos.get(i).getEstadoMonitoreo().getCodigo() == 1) {
				iniciado = true;
				break;
			} else if (listarMomentos.get(i).getEstadoMonitoreo().getCodigo() == 3) {
				contCompletado++;
			}
		}

		if (iniciado
				|| (contCompletado != 0 && contCompletado < listarMomentos
						.size())) {
			return "Iniciado";
		} else if (contCompletado == listarMomentos.size()) {
			return "Completado";
		} else {
			return "No iniciado";
		}

	}	
		

	@SuppressWarnings("unchecked")
	public MomentoSeguimientoEspecifico_ensayo obtenerfechaMSAnterior(Long id) {
		momentoAnterior = null;
		try {
			Calendar cal = Calendar.getInstance();
			// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			List<MomentoSeguimientoEspecifico_ensayo> listaMomentosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
					.createQuery(
							"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.fechaInicio <:fecha and momento.eliminado = false and momento.momentoSeguimientoGeneral.programado = TRUE and (momento.estadoMomentoSeguimiento.codigo = 3 or momento.estadoMomentoSeguimiento.codigo = 5) ORDER BY momento.fechaInicio DESC")
							.setParameter("fecha", cal.getTime())
							.setParameter("id", id).getResultList();
			
			momentoAnterior = listaMomentosxSujeto.get(0);
		} catch (Exception e) {
			return null;
		}
		return momentoAnterior;
	}

	@SuppressWarnings("unchecked")
	public MomentoSeguimientoEspecifico_ensayo obtenerfechaProximoMS(Long id) {
		
		try {
			momentoPosterior = null;
			List<MomentoSeguimientoEspecifico_ensayo> listaMomentosAtrasadosxSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// la consulta devuelve los momentos de seguimiento no iniciados y
			// atrasados no iniciados por sujeto.

			listaMomentosAtrasadosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
					.createQuery(
							"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.fechaInicio <:fecha and momento.eliminado = false and momento.momentoSeguimientoGeneral.programado = TRUE and (momento.estadoMomentoSeguimiento.codigo = 4) and momento.id IN (select e.momentoSeguimientoEspecifico.id from CrdEspecifico_ensayo e where e.estadoHojaCrd.codigo = 2) ORDER BY momento.fechaInicio ASC")
							.setParameter("fecha", calenadrio.getTime())
							.setParameter("id", id).getResultList();
			//List<MomentoSeguimientoEspecifico_ensayo> listaMayoresAtrasados = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			if (listaMomentosAtrasadosxSujeto.size() > 0) {
				momentoPosterior = listaMomentosAtrasadosxSujeto.get(0);
			} else {
				List<MomentoSeguimientoEspecifico_ensayo> listaMomentosNoIniciadosxSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
				listaMomentosNoIniciadosxSujeto = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager
						.createQuery(
								"select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id =:id and momento.fechaInicio <:fecha and momento.eliminado = false and momento.momentoSeguimientoGeneral.programado = TRUE and (momento.estadoMomentoSeguimiento.codigo = 2) ORDER BY momento.fechaInicio ASC")
								.setParameter("fecha", calenadrio.getTime())
								.setParameter("id", id).getResultList();
				momentoPosterior = listaMomentosNoIniciadosxSujeto.get(0);
			}
		} catch (Exception e) {
			return null;
		}
		return momentoPosterior;
	}

	@SuppressWarnings("unchecked")
	public boolean momentoAtrasadoNoIniciado(
			MomentoSeguimientoEspecifico_ensayo momeSegui) {
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();

		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(
				"select e from CrdEspecifico_ensayo e where e.momentoSeguimientoEspecifico.id=:IdMomento").setParameter("IdMomento", momeSegui.getId()).getResultList();
		boolean completa = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if (listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 2) {
				completa = false;
				break;
			}
		}
		return completa;

	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoI() {
		listarEstados = (List<EstadoInclusion_ensayo>) entityManager
				.createQuery("select e from EstadoInclusion_ensayo e")
				.getResultList();
		List<String> nombreEst = new ArrayList<String>();
		nombreEst.add("<Seleccione>");
		for (int i = 0; i < listarEstados.size(); i++) {
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoT() {
		List<String> nombreEstados = new ArrayList<String>();
		nombreEstados.add("<Seleccione>");
		List<EstadoTratamiento_ensayo> nombreEst = (List<EstadoTratamiento_ensayo>) entityManager
				.createQuery("select e from EstadoTratamiento_ensayo e")
				.getResultList();
		for (int i = 0; i < nombreEst.size(); i++) {
			nombreEstados.add(nombreEst.get(i).getNombre());
		}

		return nombreEstados;
	}

	@SuppressWarnings("unchecked")
	public List<String> listEstadoM() {
		List<String> nombreEst = (List<String>) entityManager.createQuery(
				"select e.nombre from EstadoMonitoreo_ensayo e")
				.getResultList();
		nombreEst.add(0, "<Seleccione>");
		return nombreEst;
	}

	@SuppressWarnings("unchecked")
	public List<String> listNombreG() {
		List<GrupoSujetos_ensayo> listarGrupo = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select g from GrupoSujetos_ensayo g where g.estudio=:estud")
				.setParameter("estud",
						seguridadEstudio.getEstudioEntidadActivo().getEstudio())
				.getResultList();
		List<String> nombreG = new ArrayList<String>();
		nombreG.add("<Seleccione>");
		for (int i = 0; i < listarGrupo.size(); i++) {
			nombreG.add(listarGrupo.get(i).getNombreGrupo());
		}
		return nombreG;
	}

	public void buscar() {
		this.refresh();
		this.setFirstResult(0);
		this.getResultList();
		this.existResultados = (this.getResultCount() != 0);
		setOrder("sujeto.id desc");
	}

	public void SeleccionarInstanciaSuj(long id) {
		this.setideliminarSuj(id);
	}

	public void SeleccionarDeshabilitarSuj(long id) {
		this.setIdhabilitado(id);
	}

	// CU 9 Eliminar sujeto
	@SuppressWarnings("unchecked")
	public void EliminarInstanciaSuj() {

		List<Sujeto_ensayo> sujEliminar = entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id =:id and suj.eliminado = false")
				.setParameter("id", ideliminarSuj).getResultList();
		Sujeto_ensayo nuevo = sujEliminar.get(0);
		nuevo.setEliminado(true);
		entityManager.persist(nuevo);
		entityManager.flush();

	}

	// CU 5 Deshabilitar sujeto && CU 6 Restaurar sujeto
	@SuppressWarnings("unchecked")
	public void DeshabilitarInstanciaSuj() {
		EstadoSujeto_ensayo habil = new EstadoSujeto_ensayo();
		EstadoSujeto_ensayo deshabil = new EstadoSujeto_ensayo();

		List<Sujeto_ensayo> sujDeshabilitar = entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id =:id and suj.eliminado = false")
				.setParameter("id", idhabilitado).getResultList();
		Sujeto_ensayo nuevo = sujDeshabilitar.get(0);
		List<EstadoSujeto_ensayo> estadosSuje = entityManager.createQuery(
				"select estados from EstadoSujeto_ensayo estados")
				.getResultList();
		for (int i = 0; i < estadosSuje.size(); i++) {
			if (estadosSuje.get(i).getNombre().equals("Habilitado")) {
				habil = estadosSuje.get(i);
			} else if (estadosSuje.get(i).getNombre().equals("Deshabilitado")) {
				deshabil = estadosSuje.get(i);
			}
		}
		if (nuevo.getEstadoSujeto().getNombre().equals("Habilitado")) {
			nuevo.setEstadoSujeto(deshabil);
		} else {
			nuevo.setEstadoSujeto(habil);
		}
		entityManager.persist(nuevo);
		entityManager.flush();
	}

	public void cambiarBusqueda() {
		if (this.displayBA.equals("display:none"))
			this.displayBA = "display:block";
		else
			this.displayBA = "display:none";

		if (this.displayBN.equals("display:none"))
			this.displayBN = "display:block";
		else
			this.displayBN = "display:none";
	}

	public Long getideliminarSuj() {
		return ideliminarSuj;
	}

	public void setideliminarSuj(Long ideliminarSuj) {
		this.ideliminarSuj = ideliminarSuj;
	}

	public Long getIdhabilitado() {
		return idhabilitado;
	}

	public void setIdhabilitado(Long idhabilitado) {
		this.idhabilitado = idhabilitado;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public Date getFechaInicioP() {
		return fechaInicioP;
	}

	public void setFechaInicioP(Date fechaInicioP) {
		this.fechaInicioP = fechaInicioP;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public String getDisplayBA() {
		return displayBA;
	}

	public void setDisplayBA(String displayBA) {
		this.displayBA = displayBA;
	}

	public String getDisplayBN() {
		return displayBN;
	}

	public void setDisplayBN(String displayBN) {
		this.displayBN = displayBN;
	}

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
	}

	public boolean isSeleccionar() {
		return seleccionar;
	}

	public void setSeleccionar(boolean seleccionar) {
		this.seleccionar = seleccionar;
	}

	public int getPagina() {
		if (this.getNextFirstResult() != 0)
			return this.getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getResultCount() / 10) + 1;
		if (this.pagina > 0) {
			if (getResultCount() % 10 != 0) {
				if (pagina <= num)
					this.setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

	public boolean isModificar() {
		return modificar;
	}

	public boolean isInterrumpir() {
		return interrumpir;
	}

	public void setInterrumpir(boolean interrumpir) {
		this.interrumpir = interrumpir;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public boolean isVer() {
		return ver;
	}

	public void setVer(boolean ver) {
		this.ver = ver;
	}

	public boolean isCrear() {
		return crear;
	}

	public void setCrear(boolean crear) {
		this.crear = crear;
	}

	public String getEstadoI() {
		return estadoI;
	}

	public void setEstadoI(String estadoI) {
		this.estadoI = estadoI;
		if (this.estadoI.equals("<Seleccione>")) {
			this.estadoI = "";
		}
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public String getNombeGrup() {
		return nombeGrup;
	}

	public void setNombeGrup(String nombeGrup) {
		this.nombeGrup = nombeGrup;
		if (this.nombeGrup.equals("<Seleccione>")) {
			this.nombeGrup = "";
		}
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public String getEstadoT() {
		return estadoT;
	}

	public void setEstadoT(String estadoT) {
		this.estadoT = estadoT;
		if (this.estadoT.equals("<Seleccione>")) {
			this.estadoT = "";
		}
	}

	public String getEstadoM() {
		return estadoM;
	}

	public void setEstadoM(String estadoM) {
		this.estadoM = estadoM;
		if (this.estadoM.equals("<Seleccione>")) {
			this.estadoM = "";
		}
	}

	public Date getfechaInicioA() {
		return fechaInicioA;
	}

	public void setfechaInicioA(Date fechaInicioA) {
		this.fechaInicioA = fechaInicioA;
	}

	public MomentoSeguimientoEspecifico_ensayo getMomentoAnterior() {
		return momentoAnterior;
	}

	public void setMomentoAnterior(
			MomentoSeguimientoEspecifico_ensayo momentoAnterior) {
		this.momentoAnterior = momentoAnterior;
	}

	public MomentoSeguimientoEspecifico_ensayo getMomentoPosterior() {
		return momentoPosterior;
	}

	public void setMomentoPosterior(
			MomentoSeguimientoEspecifico_ensayo momentoPosterior) {
		this.momentoPosterior = momentoPosterior;
	}

	public Map<Long, List<MomentoSeguimientoEspecifico_ensayo>> getListaFechasMomentos() {
		return listaFechasMomentos;
	}

	public void setListaFechasMomentos(
			Map<Long, List<MomentoSeguimientoEspecifico_ensayo>> listaFechasMomentos) {
		this.listaFechasMomentos = listaFechasMomentos;
	}

	public Calendar getCalenadrio() {
		return calenadrio;
	}

	public void setCalenadrio(Calendar calenadrio) {
		this.calenadrio = calenadrio;
	}

}
