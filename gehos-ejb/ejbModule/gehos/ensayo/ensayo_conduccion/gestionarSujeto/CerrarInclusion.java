//Listar centros por estudio de sujeto
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoEstudio_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
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
@Name("cerrarInclusion")
@Scope(ScopeType.CONVERSATION)
public class CerrarInclusion {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	protected @In IBitacora bitacora;
	@In
	private IActiveModule activeModule;

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

	private String nombreEstudioCentro = "";
	private EstudioEntidad_ensayo estudEnt = new EstudioEntidad_ensayo();
	private Long idSujeto, ideliminarSuj, idCentro;


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
				return "Richfaces.showModalPanel('mpAdvertenciaCerrarInclusion')";
			}
		}
		return "";
	}
	
	public void SeleccionarInstanciaCentro(long id) {
		this.setIdCentro(id);
		estudEnt = entityManager.find(EstudioEntidad_ensayo.class,
				this.idCentro);
	}

	// Cambiar estado del estudio
	public void CambiarSujetoCentro() {
		Calendar cal=Calendar.getInstance();
		
		
		Estudio_ensayo estudioActivo = seguridadEstudio.EstudioActivo();
		EstadoEstudio_ensayo estadoEstudio = (EstadoEstudio_ensayo)entityManager
				.createQuery(
						"select momento from EstadoEstudio_ensayo momento where momento.codigo = 8")
				.getSingleResult();
		estudioActivo.setEstadoEstudio(estadoEstudio);
		entityManager.persist(estudioActivo);
		long cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("bitCrearCausa"));
		Causa_ensayo causa = new Causa_ensayo();
		causa.setCid(cid);
		causa.setDescripcion(this.causaGuardar);
		//causa.setSujeto(sujeto);
		causa.setFecha(cal.getTime());
		causa.setTipoCausa("Creando causa de reanudar inclusion");
		entityManager.persist(causa);
		entityManager.flush();

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

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
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
