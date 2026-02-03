//CU 2 Adicionar sujeto
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstadoSujeto_ensayo;
import gehos.ensayo.entity.EstadoTratamiento_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearSujetoPesquisaje")
@Scope(ScopeType.CONVERSATION)
public class CrearSujetoPesquisaje {

	@In
	private IActiveModule activeModule;
	@In
	private Usuario user;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;

	Entidad_ensayo entidadEnsayo;

	// Cronnogrma Especifico by Eiler
	private MomentoSeguimientoGeneral_ensayo listadoMomentosGeneral;

	// Valores seleccionados de nomencladores--------------------------

	protected String grupo = "";
	protected String estado = "";
	private Usuario_ensayo usuario = new Usuario_ensayo();
	protected String inicialesP;
	protected String hora;
	protected String minutos;
	protected String am_pm;
	protected Long numeroInclucion;
	protected String inicialesCentro;
	protected String inicialesCentroPromotor;

	// Nomencladores-------------------------------------------------------

	protected List<GrupoSujetos_ensayo> listarGrupo;
	protected List<EstadoInclusion_ensayo> listarEstados;

	protected @In
	EntityManager entityManager;
	protected @In
	IBitacora bitacora;
	protected @In(create = true)
	FacesMessages facesMessages;

	private long cid = -1;

	protected String seleccione;

	// variables que definen si los campos son obligatorios o no
	protected boolean inicialesRequired;
	protected boolean numInclusionRequired;

	protected boolean fechaInclusionRequired;

	protected boolean grupoRequired;
	protected boolean estadoRequired;
	protected boolean inicialesCentroRequired;

	protected String pagAnterior;

	protected Entidad_ensayo entidaEns = new Entidad_ensayo();

	protected Sujeto_ensayo sujeto;
	private Long idSujeto;

	private boolean error;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		this.inicialesRequired = true;
		this.numInclusionRequired = true;
		this.fechaInclusionRequired = false;
		this.grupoRequired = true;
		this.estadoRequired = false;
		this.inicialesCentroRequired = false;
		if (seguridadEstudio.VerificarActivo()) {
			listarGrupo();
		}

		listarEstados();

		this.inicialesCentro = this.getHospitalActivo();
		this.inicialesCentroPromotor = this.getHospitalPromotor();

		this.sujeto = new Sujeto_ensayo();
	}

	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	@SuppressWarnings("unchecked")
	public void listarGrupo() {
		listarGrupo = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select g from GrupoSujetos_ensayo g where g.estudio=:estud and g.habilitado=true and g.eliminado=false")
				.setParameter("estud",
						seguridadEstudio.getEstudioEntidadActivo().getEstudio())
				.getResultList();
	}

	public List<String> listarGrupo1() {
		List<String> nombregrupo = new ArrayList<String>();
		try {
			for (int i = 0; i < listarGrupo.size(); i++) {
				if (listarGrupo.get(i).getNombreGrupo()
						.equals("Grupo Pesquisaje")) {
					this.grupo = listarGrupo.get(i).getNombreGrupo();
				}
				nombregrupo.add(listarGrupo.get(i).getNombreGrupo());
			}

		} catch (Exception e) {

		}
		return nombregrupo;
	}

	@SuppressWarnings("unchecked")
	public void listarEstados() {
		listarEstados = (List<EstadoInclusion_ensayo>) entityManager
				.createQuery("select e from EstadoInclusion_ensayo e")
				.getResultList();

	}

	public List<String> listarEstados1() {
		List<String> nombreEst = new ArrayList<String>();
		for (int i = 0; i < listarEstados.size(); i++) {
			if (listarEstados
					.get(i)
					.getNombre()
					.equals(SeamResourceBundle.getBundle().getString(
							"evaluacion"))) {
				this.estado = listarEstados.get(i).getNombre();
			}
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}

	public GrupoSujetos_ensayo GrupoxNombre() {
		GrupoSujetos_ensayo grup = new GrupoSujetos_ensayo();
		for (int i = 0; i < listarGrupo.size(); i++) {
			if (listarGrupo.get(i).getNombreGrupo().equals("Grupo Pesquisaje")) {
				grup = listarGrupo.get(i);
			}
		}

		return grup;
	}

	public EstadoInclusion_ensayo EstadoxNombre() {
		EstadoInclusion_ensayo estad = new EstadoInclusion_ensayo();
		for (int i = 0; i < listarEstados.size(); i++) {
			if (listarEstados
					.get(i)
					.getNombre()
					.equals(SeamResourceBundle.getBundle().getString(
							"evaluacion"))) {
				estad = listarEstados.get(i);
			}

		}

		return estad;
	}

	@Transactional
	public void persistir() {
			if (this.cid == -1) {
				this.cid = this.bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("creandoSujeto"));
			}
			Calendar cal = Calendar.getInstance();
			this.usuario = (Usuario_ensayo) entityManager.find(
					Usuario_ensayo.class, user.getId());
			this.sujeto.setCid(this.cid);
			this.sujeto.setInicialesPaciente(inicialesP);
			if((!hora.isEmpty() && (minutos.isEmpty() || am_pm.isEmpty()))){
				error = true;
				this.facesMessages
				.addToControlFromResourceBundle(
						"horaInclusion",
						Severity.ERROR,
						"Introduzca los valores restantes");
				return;
			}
			if(!hora.isEmpty() && !minutos.isEmpty() && !am_pm.isEmpty()){
				this.sujeto.setNombre(hora + ":" + minutos + ":00 " + am_pm.replace("1", ""));
			}
			EstadoTratamiento_ensayo estadoTratam = (EstadoTratamiento_ensayo) (entityManager
					.createQuery("select est from EstadoTratamiento_ensayo est where est.nombre =:eva ")
					.setParameter("eva", SeamResourceBundle.getBundle()
							.getString("evaluacion"))).getSingleResult();
			this.sujeto.setGrupoSujetos(GrupoxNombre());
			this.sujeto.setEstadoTratamiento(estadoTratam);
			this.sujeto.setEstadoInclusion(EstadoxNombre());
			this.sujeto.setInicialesCentro(inicialesCentro);
			this.sujeto.setCronogramaEspecifico(false);
			this.sujeto.setFechaCreacion(cal.getTime());
			this.sujeto.setEntidad(entidadEnsayo);
			this.sujeto.setEliminado(false);
			this.sujeto.setUsuario(usuario);

			EstadoSujeto_ensayo estadosSuje = (EstadoSujeto_ensayo) entityManager
					.createQuery(
							"select est from EstadoSujeto_ensayo est where est.nombre='Habilitado'")
					.getSingleResult();

			this.sujeto.setEstadoSujeto(estadosSuje);
			this.sujeto.setCodigoPaciente(inicialesCentroPromotor + "_" + this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getIdentificador() + "_" + inicialesCentro + "_" + inicialesP);

			entityManager.persist(this.sujeto);
			entityManager.flush();
			idSujeto = sujeto.getId();
			// Busco el cronograma que le corresponde al sujeto.
			Cronograma_ensayo cronograma_General = (Cronograma_ensayo) entityManager
					.createQuery(
							"select c from Cronograma_ensayo c where c.grupoSujetos.id=:id")
					.setParameter("id", sujeto.getGrupoSujetos().getId())
					.getSingleResult();
			// Aqui tengo el listado de los momento de seguimientos general

			listadoMomentosGeneral = (MomentoSeguimientoGeneral_ensayo) entityManager
					.createQuery(
							"select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id=:id and msg.programado=True and msg.nombre='Pesquisaje' and msg.eliminado = false")
					.setParameter("id", cronograma_General.getId())
					.getSingleResult();

			// List<String> listaDiasPlanificados =picarCadena(ms);
			long idEstadoSeguimiento = 2;
			// EstadoMomentoSeguimiento_ensayo
			// estadoMomento=(EstadoMomentoSeguimiento_ensayo)
			// entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.id=:id").setParameter("id",
			// idEstadoSeguimiento).getSingleResult();
			// EstadoMonitoreo_ensayo estadoMonitoreo=(EstadoMonitoreo_ensayo)
			// entityManager.createQuery("select e from EstadoMonitoreo_ensayo e where e.id=:id").setParameter("id",
			// idEstadoSeguimiento).getSingleResult();
			EstadoMomentoSeguimiento_ensayo estadoMomento = entityManager.find(
					EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimiento);
			EstadoMonitoreo_ensayo estadoMonitoreo = entityManager.find(
					EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);

			@SuppressWarnings("unchecked")
			List<HojaCrd_ensayo> listaHojas = entityManager
					.createQuery(
							"select momentoGhojaCRD.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoGhojaCRD where momentoGhojaCRD.momentoSeguimientoGeneral.id=:id and momentoGhojaCRD.eliminado = 'false'")
					.setParameter("id", listadoMomentosGeneral.getId())
					.getResultList();
			// List<HojaCrd_ensayo>
			// listaHojas=entityManager.createQuery("select crd from HojaCrd_ensayo crd where crd.momentoSeguimientoGeneral.id=:id").setParameter("id",
			// listadoMomentosGeneral.getId()).getResultList();
			MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo();

			momentoEspecifico.setCid(bitacora
					.registrarInicioDeAccion(SeamResourceBundle.getBundle()
							.getString("momentoCrear")));
			momentoEspecifico.setDia(Integer.valueOf(listadoMomentosGeneral
					.getDiasEvaluacion()));
			momentoEspecifico.setEliminado(false);
			momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomento);
			momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo);

			momentoEspecifico.setFechaCreacion(sujeto.getFechaCreacion());
			momentoEspecifico.setFechaInicio(sujeto.getFechaCreacion());
			cal.add(Calendar.DATE, listadoMomentosGeneral.getTiempoLlenado());
			momentoEspecifico.setUsuario(usuario);
			momentoEspecifico.setFechaFin(cal.getTime());
			momentoEspecifico
					.setMomentoSeguimientoGeneral(listadoMomentosGeneral);
			momentoEspecifico.setSujeto(sujeto);
			entityManager.persist(momentoEspecifico);
			entityManager.flush();

			String noIniciada = "No iniciada";
			EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) entityManager
					.createQuery(
							"select e from EstadoHojaCrd_ensayo e where e.nombre=:noIniciada")
					.setParameter("noIniciada", noIniciada).getSingleResult();

			for (int j2 = 0; j2 < listaHojas.size(); j2++) {
				CrdEspecifico_ensayo crdEsp = new CrdEspecifico_ensayo();
				crdEsp.setCid(bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("hojaCrear")));
				crdEsp.setEliminado(false);
				crdEsp.setEstadoHojaCrd(listaHojas.get(j2).getEstadoHojaCrd());
				crdEsp.setEstadoMonitoreo(estadoMonitoreo);
				crdEsp.setHojaCrd(listaHojas.get(j2));
				crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
				crdEsp.setEstadoHojaCrd(estadoNoIniciada);
				entityManager.persist(crdEsp);
				entityManager.flush();
			}
	}

	public String getHospitalActivo() {
		entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());
		String nom = "";
		nom = entidadEnsayo.getFax();
		return nom;
	}

	public String getHospitalPromotor() {

		@SuppressWarnings("unchecked")
        List<Entidad_ensayo> entidadEnsayo = (List<Entidad_ensayo>) entityManager
                .createQuery(
                        "select e.entidad from EstudioEntidad_ensayo e where e.entidad.tipoEntidad.valor=:Biotec and e.estudio=:EstudioActivo")
                .setParameter(
                        "EstudioActivo",
                        this.seguridadEstudio.getEstudioEntidadActivo()
                                .getEstudio())
                .setParameter(
                        "Biotec", SeamResourceBundle.getBundle().getString(
                                "bioTecnologica")).getResultList();
        String nom = "";
        nom = entidadEnsayo.get(0).getFax();
		return nom;
	}

	public Entidad_ensayo getEntidadEnsayo() {
		return entidadEnsayo;
	}

	public void setEntidadEnsayo(Entidad_ensayo entidadEnsayo) {
		this.entidadEnsayo = entidadEnsayo;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<GrupoSujetos_ensayo> getListarGrupo() {
		return listarGrupo;
	}

	public void setListarGrupo(List<GrupoSujetos_ensayo> listarGrupo) {
		this.listarGrupo = listarGrupo;
	}

	public List<EstadoInclusion_ensayo> getListarEstados() {
		return listarEstados;
	}

	public void setListarEstados(List<EstadoInclusion_ensayo> listarEstados) {
		this.listarEstados = listarEstados;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getSeleccione() {
		return seleccione;
	}

	public void setSeleccione(String seleccione) {
		this.seleccione = seleccione;
	}

	public boolean isInicialesRequired() {
		return inicialesRequired;
	}

	public void setInicialesRequired(boolean inicialesRequired) {
		this.inicialesRequired = inicialesRequired;
	}

	public boolean isNumInclusionRequired() {
		return numInclusionRequired;
	}

	public void setNumInclusionRequired(boolean numInclusionRequired) {
		this.numInclusionRequired = numInclusionRequired;
	}

	public boolean isFechaInclusionRequired() {
		return fechaInclusionRequired;
	}

	public void setFechaInclusionRequired(boolean fechaInclusionRequired) {
		this.fechaInclusionRequired = fechaInclusionRequired;
	}

	public boolean isGrupoRequired() {
		return grupoRequired;
	}

	public void setGrupoRequired(boolean grupoRequired) {
		this.grupoRequired = grupoRequired;
	}

	public boolean isEstadoRequired() {
		return estadoRequired;
	}

	public void setEstadoRequired(boolean estadoRequired) {
		this.estadoRequired = estadoRequired;
	}

	public boolean isInicialesCentroRequired() {
		return inicialesCentroRequired;
	}

	public void setInicialesCentroRequired(boolean inicialesCentroRequired) {
		this.inicialesCentroRequired = inicialesCentroRequired;
	}

	public String getPagAnterior() {
		return pagAnterior;
	}

	public void setPagAnterior(String pagAnterior) {
		this.pagAnterior = pagAnterior;
	}

	public String getInicialesP() {
		return inicialesP;
	}

	public void setInicialesP(String inicialesP) {
		this.inicialesP = inicialesP;
	}

	public Long getNumeroInclucion() {
		return numeroInclucion;
	}

	public void setNumeroInclucion(Long numeroInclucion) {
		this.numeroInclucion = numeroInclucion;
	}

	public String getInicialesCentro() {
		return inicialesCentro;
	}

	public void setInicialesCentro(String inicialesCentro) {
		this.inicialesCentro = inicialesCentro;
	}

	public Entidad_ensayo getEntidaEns() {
		return entidaEns;
	}

	public void setEntidaEns(Entidad_ensayo entidaEns) {
		this.entidaEns = entidaEns;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getInicialesCentroPromotor() {
		return inicialesCentroPromotor;
	}

	public void setInicialesCentroPromotor(String inicialesCentroPromotor) {
		this.inicialesCentroPromotor = inicialesCentroPromotor;
	}

	public String getMinutos() {
		return minutos;
	}

	public void setMinutos(String minutos) {
		this.minutos = minutos;
	}

	public String getAm_pm() {
		return am_pm;
	}

	public void setAm_pm(String am_pm) {
		this.am_pm = am_pm;
	}

}