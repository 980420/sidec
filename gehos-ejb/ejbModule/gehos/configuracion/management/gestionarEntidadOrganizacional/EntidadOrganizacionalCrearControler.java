package gehos.configuracion.management.gestionarEntidadOrganizacional;

import java.util.List;

import gehos.configuracion.management.entity.EntidadOrganizacional_configuracion;
import gehos.configuracion.management.entity.TipoEstablecimientoSalud_configuracion;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("entidadOrganizacionalCrearControler")
@Scope(ScopeType.CONVERSATION)
public class EntidadOrganizacionalCrearControler {

	EntidadOrganizacional_configuracion entidadOrganizacional = new EntidadOrganizacional_configuracion();

	// otras funcionalidades
	private String estadoFuncionalidad = "crear";
	private String message = "";

	FacesMessage facesMessage;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	// METODOS--------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {

	}

	public void limpiarCampos() {
		entidadOrganizacional = new EntidadOrganizacional_configuracion();
		estadoFuncionalidad = "crear";
	}

	// CREAR, MODIFICAR,
	// ELIMINAR-------------------------------------------------
	// Crear tipo establecimiento
	public void crear() {
		entityManager.persist(entidadOrganizacional);
		entityManager.flush();
		estadoFuncionalidad = "ver";
	}

	// modificar establecimiento
	public void modificar() {
		entityManager.merge(entidadOrganizacional);
		entityManager.flush();
		estadoFuncionalidad = "ver";
	}

	// eliminar establecimiento
	public void eliminar() {
		try {
			entityManager.remove(entidadOrganizacional);
			entityManager.flush();
		} catch (Exception e) {
			facesMessages
					.addToControlFromResourceBundle(
							"btnSi",
							Severity.ERROR,
							"La entidad organizacional está siendo usado por lo tanto no puede ser eliminado.");
		}
	}

	// VALIDACIONES-----------------------------------------------------------------
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	@SuppressWarnings("unchecked")
	public void validarEntidadOrganizacionalCrear(FacesContext context,
			UIComponent component, Object value) {
		if (context
				.getExternalContext()
				.getRequestParameterMap()
				.containsKey(
						"formGestionarEntidadOrganizacional:buttonCrearEntidadOrganizacional")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$"))
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			
		

			List<EntidadOrganizacional_configuracion> le = entityManager
					.createQuery(
							"select e from EntidadOrganizacional_configuracion e where e.valor =:entidadOrganizacionalValor")
					.setParameter("entidadOrganizacionalValor", value)
					.getResultList();
			if (le.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("entidadOrganizacionalRepetida"));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void validarEntidadOrganizacionalModificar(FacesContext context,
			UIComponent component, Object value) {
		if (context
				.getExternalContext()
				.getRequestParameterMap()
				.containsKey(
						"formGestionarEntidadOrganizacional:buttonModificarEntidadOrganizacional")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$"))
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
		

			List<TipoEstablecimientoSalud_configuracion> le = entityManager
					.createQuery(
							"select e from EntidadOrganizacional_configuracion e where e.valor =:entidadOrganizacionalValor and e.id<>:entidadOrganizacionalId")
					.setParameter("entidadOrganizacionalValor", value)
					.setParameter("entidadOrganizacionalId",
							entidadOrganizacional.getId()).getResultList();
			if (le.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("entidadOrganizacionalRepetida"));
			}
		}
	}

	// PROPIEDADES---------------------------------------------------------------
	public EntidadOrganizacional_configuracion getEntidadOrganizacional() {
		return entidadOrganizacional;
	}

	public void setEntidadOrganizacional(
			EntidadOrganizacional_configuracion entidadOrganizacional) {
		this.entidadOrganizacional = entidadOrganizacional;
	}

	public String getEstadoFuncionalidad() {
		return estadoFuncionalidad;
	}

	public void setEstadoFuncionalidad(String estadoFuncionalidad) {
		this.estadoFuncionalidad = estadoFuncionalidad;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
