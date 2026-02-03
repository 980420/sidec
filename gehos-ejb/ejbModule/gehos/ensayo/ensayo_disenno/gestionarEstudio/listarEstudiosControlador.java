package gehos.ensayo.ensayo_disenno.gestionarEstudio;

import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.Cie_ensayo;
import gehos.ensayo.entity.EAjusteTemporal_ensayo;
import gehos.ensayo.entity.EAleatorizacion_ensayo;
import gehos.ensayo.entity.EAsignacion_ensayo;
import gehos.ensayo.entity.EControl_ensayo;
import gehos.ensayo.entity.EEnfermedadCie_ensayo;
import gehos.ensayo.entity.EEnmascaramiento_ensayo;
import gehos.ensayo.entity.EFaseEstudio_ensayo;
import gehos.ensayo.entity.EIntervencion_ensayo;
import gehos.ensayo.entity.EProposito_ensayo;
import gehos.ensayo.entity.EPuntoFinal_ensayo;
import gehos.ensayo.entity.ESeleccion_ensayo;
import gehos.ensayo.entity.ESexo_ensayo;
import gehos.ensayo.entity.ETipoIntervencion_ensayo;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.UsuarioEstudio_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.session.common.auto.EstudioList_ensayo;
import gehos.ensayo.session.custom.CieConsList_custom;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.swing.plaf.basic.ComboPopup;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("listarEstudiosControlador")
@Scope(ScopeType.CONVERSATION)
public class listarEstudiosControlador extends EstudioList_ensayo {

	private String nombres;
	private String apellido1;
	private String apellido2;
	private boolean existResultados = true;
	private String cedula;
	private String EFaseEstudio;
	private String estadoEstudio;
	private Date fechacreacion;
	private String entidad;
	private List<String> lisEntidad;
	private List<String> lisfase;
	private List<String> lisEstados;
	private boolean busquedaTipo = false;
	private boolean openSimpleTogglePanel = true;
	private boolean fechaInterrupcionRequired = true;
	private boolean causaRequerido = true;
	private Object causa = null;
	private String causaGuardar = "";

	@In
	Usuario user;
	@In
	EntityManager entityManager;

	private static final String EJBQL = "select distinct e from EstudioEntidad_ensayo estudioEntidad "
			+ " JOIN estudioEntidad.estudio e "
			+ " JOIN estudioEntidad.entidad entidadE "
			+ " where e.eliminado=false "
			+ " AND e.entidad.id= #{permisosSeleccionarEstudio.activeModule.getActiveModule().getEntidad().getId()}"
			+ " AND entidadE.id = #{listarEstudiosControlador.activeModule.getActiveModule().getEntidad().getId()}"
			+ " AND estudioEntidad.id in (select u.estudioEntidad.id from UsuarioEstudio_ensayo u"
			+ " where u.usuario.id = #{listarEstudiosControlador.user.id} and u.eliminado <> true "
			+ "and u.role.eliminado <> true and u.estudioEntidad.eliminado <> true "
			+ "and e.estadoEstudio.codigo <> 1 and e.estadoEstudio.codigo <> 2)";

	@In
	FacesMessages facesMessages;

	@In
	LocaleSelector localeSelector;

	FacesMessage facesMessage;
	private String message = "";
	@In
	IBitacora bitacora;
	@In
	SeguridadEstudio seguridadEstudio;

	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	private Long estudyId;
	// busqueda avanzada
	private static final String[] RESTRICTIONSA = {
			"lower(e.nombre) like concat('%',concat(lower(#{listarEstudiosControlador.estudio.nombre}),'%'))",
			"lower(e.investigadorPrincipal) like concat('%',concat(lower(#{listarEstudiosControlador.estudio.investigadorPrincipal}),'%'))",
			"lower(e.EFaseEstudio.nombre) like concat (lower(#{listarEstudiosControlador.EFaseEstudio}), '%')",
			"lower(e.estadoEstudio.nombre) like concat (lower(#{listarEstudiosControlador.estadoEstudio}), '%')", };

	// busqueda simple
	private static final String[] RESTRICTIONSS = { "lower(e.nombre) like concat('%',concat(lower(#{listarEstudiosControlador.estudio.nombre}),'%'))", };

	Estudio_ensayo estudio = new Estudio_ensayo();

	private static final String CARACTERES_ESPECIALES = SeamResourceBundle
			.getBundle().getString("caracteresEspeciales");

	// Date fechabloqueo;

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
		fecha = new Date();
		fecha = Calendar.getInstance().getTime();
		setDescripcion("");
	}

	public void limpiar() {
		setDescripcion("");
	}

	public listarEstudiosControlador() {

		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("e.id desc");
	}

	public void Buscar() {
		this.setFirstResult(0);
	}

	public void Lista() {
		this.apellido1 = "";
		this.apellido2 = "";
		this.cedula = "";
		this.fechacreacion = null;
		this.nombres = "";
		this.setFirstResult(0);
	}

	public Date getFechacreacion() {
		return fechacreacion;
	}

	public void setFechacreacion(Date fechacreacion) {
		this.fechacreacion = fechacreacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	private int pagina;

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

	/**
	 * @return the lisEntidad
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLisEntidad() {
		if (this.lisEntidad == null || this.lisEntidad.size() == 0) {
			this.lisEntidad = (List<String>) entityManager.createQuery(
					"select e.nombre from Entidad_ensayo e "
							+ "where e.eliminado = FALSE").getResultList();
			this.lisEntidad.add(0,
					SeamResourceBundle.getBundle().getString("seleccione"));
		}
		return lisEntidad;
	}

	/**
	 * @param lisEntidad
	 *            the lisEntidad to set
	 */
	public void setLisEntidad(List<String> lisEntidad) {
		this.lisEntidad = lisEntidad;
	}

	// cargar el estado de la restriccion a partir del tipo de busqueda
	public void setBusquedaTipo(boolean busquedaTipo) {
		this.busquedaTipo = busquedaTipo;
		// setFirstResult(0);
		String[] aux = (busquedaTipo) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	// dado el tipo de busqueda carga la restriccion
	public void busqueda(boolean avan) {
		setFirstResult(0);
		String[] aux = (avan) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
		this.existResultados = (this.getResultCount() != 0);
	}

	// cambia de tipo de busqueda (avanzada o normal)
	public void cambiar(boolean a) {
		this.busquedaTipo = a;
	}

	/**
	 * @return the lisfase
	 */
	public List<String> getLisfase() {
		if (this.lisfase == null || this.lisfase.size() == 0) {
			this.lisfase = (List<String>) entityManager.createQuery(
					"select e.nombre from EFaseEstudio_ensayo e "
							+ "where e.eliminado = FALSE").getResultList();
			this.lisfase.add(0,
					SeamResourceBundle.getBundle().getString("seleccione"));
		}
		return lisfase;
	}

	/**
	 * @param lisfase
	 *            the lisfase to set
	 */
	public void setLisfase(List<String> lisfase) {
		this.lisfase = lisfase;
	}

	/**
	 * @return the lisEstados
	 */
	public List<String> getLisEstados() {
		if (this.lisEstados == null || this.lisEstados.size() == 0) {
			this.lisEstados = (List<String>) entityManager.createQuery(
					"select e.nombre from EstadoEstudio_ensayo e ")
					.getResultList();
			this.lisEstados.add(0,
					SeamResourceBundle.getBundle().getString("seleccione"));
		}
		return lisEstados;
	}

	/**
	 * @param lisEstados
	 *            the lisEstados to set
	 */
	public void setLisEstados(List<String> lisEstados) {
		this.lisEstados = lisEstados;
	}

	/**
	 * @return the eFaseEstudio
	 */
	public String getEFaseEstudio() {
		return EFaseEstudio;
	}

	/**
	 * @param eFaseEstudio
	 *            the eFaseEstudio to set
	 */
	public void setEFaseEstudio(String eFaseEstudio) {
		EFaseEstudio = eFaseEstudio;
		if (EFaseEstudio.equals("<Seleccione>"))
			EFaseEstudio = null;
	}

	/**
	 * @return the estadoEstudio
	 */
	public String getEstadoEstudio() {
		return estadoEstudio;
	}

	/**
	 * @param estadoEstudio
	 *            the estadoEstudio to set
	 */
	public void setEstadoEstudio(String estadoEstudio) {
		this.estadoEstudio = estadoEstudio;
		if (this.estadoEstudio.equals("<Seleccione>"))
			this.estadoEstudio = null;
	}

	/**
	 * @return the entidad
	 */
	public String getEntidad() {
		return entidad;
	}

	/**
	 * @param entidad
	 *            the entidad to set
	 */
	public void setEntidad(String entidad) {
		this.entidad = entidad;
		if (entidad.equals("<Seleccione>"))
			this.entidad = "";
	}

	String desde = "";

	/**
	 * Metodos para bloquear el estudio
	 * 
	 * @author tania
	 */

	@SuppressWarnings("unchecked")
	public boolean UsuarioPromotor(long estudio) {

		String promotor = "ecProm";
		// necesito saber si el usuario conectado tiene el rol de promotor
		List<String> roles = this.entityManager
				.createQuery(
						"Select uee from UsuarioEstudio_ensayo uee JOIN uee.usuario ueeU "
								+ "JOIN uee.role ueeRole "
								+ "JOIN uee.estudioEntidad estEnt JOIN estEnt.estudio est JOIN estEnt.entidad ent JOIN est.estadoEstudio estado"
								+ " where " + "ueeU.id = :idUsuario "
								+ "and est.id = :idestudio "
								+ "and ent.id =:identidad "
								+ "and uee.eliminado <> true "
								+ "and ueeRole.eliminado <> true "
								+ "and ueeRole.codigo = :promotor "
								+ "and estado.codigo =3")
				.setParameter("idestudio", estudio)
				.setParameter("idUsuario", this.user.getId())
				.setParameter(
						"identidad",
						this.activeModule.getActiveModule().getEntidad()
								.getId()).setParameter("promotor", promotor)
				.getResultList();

		return (!roles.isEmpty());

	}

	@SuppressWarnings("unchecked")
	public boolean UsuarioPromotorDes(long estudio) {

		String promotor = "ecProm";
		// necesito saber si el usuario conectado tiene el rol de promotor
		List<String> roles = this.entityManager
				.createQuery(
						"Select uee from UsuarioEstudio_ensayo uee JOIN uee.usuario ueeU "
								+ "JOIN uee.role ueeRole "
								+ "JOIN uee.estudioEntidad estEnt JOIN estEnt.estudio est JOIN estEnt.entidad ent JOIN est.estadoEstudio estado"
								+ " where " + "ueeU.id = :idUsuario "
								+ "and est.id = :idestudio "
								+ "and ent.id =:identidad "
								+ "and uee.eliminado <> true "
								+ "and ueeRole.eliminado <> true "
								+ "and ueeRole.codigo = :promotor "
								+ "and estado.codigo =6")
				.setParameter("idestudio", estudio)
				.setParameter("idUsuario", this.user.getId())
				.setParameter(
						"identidad",
						this.activeModule.getActiveModule().getEntidad()
								.getId()).setParameter("promotor", promotor)
				.getResultList();

		return (!roles.isEmpty());

	}

	private String descripcion = "";

	public void bloquearEstudio() {

		Causa_ensayo causa = new Causa_ensayo();
		causa.setDescripcion(descripcion);
		causa.setFecha(getFecha());
		Estudio_ensayo est = (Estudio_ensayo) entityManager
				.createQuery(
						"select est from Estudio_ensayo est where est.id = :estudio")
				.setParameter("estudio", estudyId).getSingleResult();
		causa.setEstudio(est);
		Usuario_ensayo us = entityManager.find(Usuario_ensayo.class,
				user.getId());
		causa.setUsuario(us);
		causa.setTipoCausa(SeamResourceBundle.getBundle().getString(
				"prm_tipocausa_ensClin"));
		EstadoEstudio_ensayo estado = (EstadoEstudio_ensayo) entityManager
				.createQuery(
						"select estado from EstadoEstudio_ensayo estado where estado.codigo = 6")
				.getSingleResult();
		est.setEstadoEstudio(estado);
		causa.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("prm_bitbloqEstudio_ens")));
		entityManager.persist(causa);
		entityManager.persist(est);
		entityManager.flush();
	}
	
	public void desbloquearEstudio(){
		Causa_ensayo causa = new Causa_ensayo();
		causa.setDescripcion(descripcion);
		causa.setFecha(getFecha());
		Estudio_ensayo est = (Estudio_ensayo) entityManager
				.createQuery(
						"select est from Estudio_ensayo est where est.id = :estudio")
				.setParameter("estudio", estudyId).getSingleResult();
		causa.setEstudio(est);
		Usuario_ensayo us = entityManager.find(Usuario_ensayo.class,
				user.getId());
		causa.setUsuario(us);
		causa.setTipoCausa(SeamResourceBundle.getBundle().getString(
				"prm_tipocausa_ensClin"));
		EstadoEstudio_ensayo estado = (EstadoEstudio_ensayo) entityManager
				.createQuery(
						"select estado from EstadoEstudio_ensayo estado where estado.codigo = 3")
				.getSingleResult();
		est.setEstadoEstudio(estado);
		causa.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("prm_bitbloqEstudio_ens")));
		entityManager.persist(causa);
		entityManager.persist(est);
		entityManager.flush();
	}

	
	

	public String validarCampo() {
		if (!descripcion.equals("")) {
			return "Richfaces.showModalPanel('confirmar_bloquear_estudio')";
		}
		return "";
	}

	/**
	 * Validaciones
	 * 
	 */
	// mensajes en los campos (azterizcos rojos)
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	// mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message", Severity.ERROR,
				mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}

	public void textnumber250(FacesContext context, UIComponent component,

	Object value) {

		if (!value.toString().matches(
				"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES
						+ "\u00BF?!.,0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}

	}

	/* Get and set */
	public Long getEstudyId() {
		return estudyId;
	}

	public void setEstudyId(Long estudyId) {
		this.estudyId = estudyId;
	}

	public IActiveModule getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/*
	 * public Date getFechabloqueo() { return fechabloqueo; }
	 * 
	 * public void setFechabloqueo(Date fechabloqueo) { this.fechabloqueo =
	 * fechabloqueo; }
	 */

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	private Date fecha;

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public boolean isOpenSimpleTogglePanel() {
		return openSimpleTogglePanel;
	}

	public void setOpenSimpleTogglePanel(boolean openSimpleTogglePanel) {
		this.openSimpleTogglePanel = openSimpleTogglePanel;
	}

	public boolean isBusquedaTipo() {
		return busquedaTipo;
	}

	public boolean isFechaInterrupcionRequired() {
		return fechaInterrupcionRequired;
	}

	public void setFechaInterrupcionRequired(boolean fechaInterrupcionRequired) {
		this.fechaInterrupcionRequired = fechaInterrupcionRequired;
	}

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
	}

	public boolean isCausaRequerido() {
		return causaRequerido;
	}

	public void setCausaRequerido(boolean causaRequerido) {
		this.causaRequerido = causaRequerido;
	}

	public Object getCausa() {
		return causa;
	}

	public void setCausa(Object causa) {
		this.causa = causa;
	}

	public String getCausaGuardar() {
		return causaGuardar;
	}

	public void setCausaGuardar(String causaGuardar) {
		this.causaGuardar = causaGuardar;
	}

}
