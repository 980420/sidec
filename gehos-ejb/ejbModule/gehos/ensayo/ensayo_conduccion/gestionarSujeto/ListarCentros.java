//Listar centros por estudio de sujeto
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.session.common.auto.EstudioEntidadList_ensayo;

import java.util.Arrays;
import java.util.Calendar;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("listarCentros")
@Scope(ScopeType.CONVERSATION)
public class ListarCentros extends EstudioEntidadList_ensayo {

	private static final String EJBQL = "select estudioEntidad from EstudioEntidad_ensayo estudioEntidad "
			+ "where estudioEntidad.eliminado = false and estudioEntidad.estudio = #{listarCentros.ObtenerSujeto().grupoSujetos.estudio} and estudioEntidad.entidad != #{listarCentros.EntidadActiva()}";

	private static final String[] RESTRICTIONS = {
			"lower(estudioEntidad.entidad.nombre) like concat(lower(#{listarCentros.nombreEstudioCentro.trim()}),'%')",
			"#{listarCentros.idCentro} <> estudioEntidad.id" };

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	protected @In IBitacora bitacora;
	@In
	private IActiveModule activeModule;
	@In
	Usuario user;

	private boolean existResultados = true;

	protected boolean causaRequired = true;
	private String causaGuardar = "";
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle()
			.getString("caracteresEspeciales");

	public String getCausaGuardar() {
		return causaGuardar;
	}

	public void setCausaGuardar(String causaGuardar) {
		this.causaGuardar = causaGuardar;
	}

	private Object causa = null;

	public Object getCausa() {
		return causa;
	}

	public void setCausa(Object causa) {
		this.causa = causa;
	}

	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private int pagina;

	private String nombreEstudioCentro = "";
	private EstudioEntidad_ensayo estudEnt = new EstudioEntidad_ensayo();
	private Long idSujeto, ideliminarSuj, idCentro;

	public ListarCentros() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("estudioEntidad.id desc");
	}

	public Sujeto_ensayo ObtenerSujeto() {
		return entityManager.find(Sujeto_ensayo.class, this.idSujeto);
	}

	public Entidad_ensayo EntidadActiva() {
		return entityManager.find(Entidad_ensayo.class, this.activeModule
				.getActiveModule().getEntidad().getId());
	}

	public String validarCampo() {
		if (causa != null) {
			causaGuardar = causa.toString();
			int longitud = causa.toString().length();
			boolean noExtranno = causa.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$");
			if (causa != null && noExtranno && longitud <= 250) {
				causa = null;
				return "Richfaces.showModalPanel('mpAdvertenciaCambiarCentro')";
			}
		}
		return "";
	}

	public void buscar() {
		this.refresh();
		this.setFirstResult(0);
		this.getResultList();
		this.existResultados = (this.getResultCount() != 0);
		setOrder("estudioEntidad.id desc");
	}

	public void SeleccionarInstanciaCentro(long id) {
		this.setIdCentro(id);
		estudEnt = entityManager.find(EstudioEntidad_ensayo.class,
				this.idCentro);
	}

	// Cambiar sujeto de centro
	public void CambiarSujetoCentro() {
		Calendar cal=Calendar.getInstance();
		
		
		Sujeto_ensayo sujeto = entityManager.find(Sujeto_ensayo.class,
				this.idSujeto);
		sujeto.setEntidad(estudEnt.getEntidad());
		sujeto.setInicialesCentro(estudEnt.getEntidad().getFax());
		entityManager.persist(sujeto);
		long cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitCrearCausa"));
		Causa_ensayo causa = new Causa_ensayo();
		causa.setCid(cid);
		causa.setDescripcion(this.causaGuardar);
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
				user.getId());
		causa.setUsuario(usuario);
		causa.setEstudio(seguridadEstudio.getEstudioActivo());
		causa.setSujeto(sujeto);
		causa.setFecha(cal.getTime());
		causa.setTipoCausa("Creando causa de cambiar sujeto");
		entityManager.persist(causa);
		entityManager.flush();

	}

	public String NombreCentro() {
		String nombre = this.idCentro != null ? entityManager
				.find(EstudioEntidad_ensayo.class, this.idCentro).getEntidad()
				.getNombre() : "";
		return nombre;
	}

	public Long getideliminarSuj() {
		return ideliminarSuj;
	}

	public void setideliminarSuj(Long ideliminarSuj) {
		this.ideliminarSuj = ideliminarSuj;
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

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public Long getIdCentro() {
		return idCentro;
	}

	public void setIdCentro(Long idCentro) {
		this.idCentro = idCentro;
	}

	public String getNombreEstudioCentro() {
		return nombreEstudioCentro;
	}

	public void setNombreEstudioCentro(String nombreEstudioCentro) {
		this.nombreEstudioCentro = nombreEstudioCentro;
	}

	public EstudioEntidad_ensayo getEstudEnt() {
		return estudEnt;
	}

	public void setEstudEnt(EstudioEntidad_ensayo estudEnt) {
		this.estudEnt = estudEnt;
	}

	public boolean isCausaRequired() {
		return causaRequired;
	}

	public void setCausaRequired(boolean causaRequired) {
		this.causaRequired = causaRequired;
	}
}
