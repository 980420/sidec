package gehos.configuracion.management.gestionarTipoEstablecimiento;

import java.util.List;

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

@Name("tipoEstablecimientoCrear_custom")
@Scope(ScopeType.CONVERSATION)
public class TipoEstablecimientoCrear_custom {

	TipoEstablecimientoSalud_configuracion tEstablecimiento = new TipoEstablecimientoSalud_configuracion();
	private String valor = "";
	private boolean gestionarTipoEstablecimiento;
	private String tipoEstablecimiento = "crear";

	// otras funcionalidades
	private String estadoFuncionalidad = "crear";
	private String message = "";

	FacesMessage facesMessage;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	//METODOS--------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {

	}

	//CREAR, MODIFICAR, ELIMINAR-------------------------------------------------
	// Crear tipo establecimiento
	@SuppressWarnings("unchecked")
	public void crear() {
		tEstablecimiento = new TipoEstablecimientoSalud_configuracion();
		tEstablecimiento.setValor(valor);
		tEstablecimiento.setEliminado(false);
		List<TipoEstablecimientoSalud_configuracion> le = entityManager
				.createQuery(
						"select te from TipoEstablecimientoSalud_configuracion te where te.valor =:valorTipoEstablecimiento")
				.setParameter("valorTipoEstablecimiento", valor)
				.getResultList();
		if (le.size() == 0) {
			entityManager.persist(tEstablecimiento);
			entityManager.flush();
			estadoFuncionalidad = "ver";
		} else
			facesMessages.addToControlFromResourceBundle(
					"tipoEstableciminetoValor", Severity.ERROR,
					"Tipo de establecimiento repetido");

	}

	// modificar establecimiento
	@SuppressWarnings("unchecked")
	public void modificar() {
		tEstablecimiento.setValor(valor);
		tEstablecimiento.setEliminado(false);
		List<TipoEstablecimientoSalud_configuracion> le = entityManager
				.createQuery(
						"select te from TipoEstablecimientoSalud_configuracion te where te.valor =:valorTipoEstablecimiento and te.id<>:tipoEstablecimientoId")
				.setParameter("valorTipoEstablecimiento", valor).setParameter(
						"tipoEstablecimientoId", tEstablecimiento.getId())
				.getResultList();
		if (le.size() == 0) {
			entityManager.persist(tEstablecimiento);
			entityManager.flush();
			estadoFuncionalidad = "ver";
		} else
			facesMessages.addToControlFromResourceBundle(
					"tipoEstableciminetoValorModificar", Severity.ERROR,
					"Tipo de establecimiento repetido");
	}

	// eliminar establecimiento
	public void eliminar() {
		try {
			entityManager.remove(tEstablecimiento);
			entityManager.flush();
		} catch (Exception e) {
			facesMessages
					.addToControlFromResourceBundle("btnSi", Severity.ERROR,
							"El establecimiento está siendo usado por lo tanto no puede ser eliminado.");
		}
		valor = "";
	}	
	
	//VALIDACIONES-----------------------------------------------------------------
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	@SuppressWarnings("unchecked")
	public void validarTipoEstablecimientoNombreCrear(FacesContext context,
			UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"formGestionarTipoEntidad:buttonCrearTipoEntidad")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789_]+\\s*)+$"))
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));

			List<TipoEstablecimientoSalud_configuracion> le = entityManager
					.createQuery(
							"select te from TipoEstablecimientoSalud_configuracion te where te.valor =:valorTipoEstablecimiento")
					.setParameter("valorTipoEstablecimiento", value)
					.getResultList();
			if (le.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("establecimientoRepetido"));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void validarTipoEstablecimientoNombreModificar(FacesContext context,
			UIComponent component, Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"formGestionarTipoEntidad:buttonModificarTipoEstablecimiento")) {

			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789_]+\\s*)+$"))
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));

			List<TipoEstablecimientoSalud_configuracion> le = entityManager
					.createQuery(
							"select te from TipoEstablecimientoSalud_configuracion te where te.valor =:valorTipoEstablecimiento and te.id<>:tipoEstablecimientoId")
					.setParameter("valorTipoEstablecimiento", value)
					.setParameter("tipoEstablecimientoId",
							tEstablecimiento.getId()).getResultList();
			if (le.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("establecimientoRepetido"));
			}
		}
	}

	//Validar gestionar tipo entidad
	public void validadTipoEntidad() {
		if (tipoEstablecimiento.equals("<Otro>"))
			gestionarTipoEstablecimiento = true;
		else
			gestionarTipoEstablecimiento = false;
	}
	
	public void limpiarCampos() {
		valor = "";
		estadoFuncionalidad = "crear";
	}
	
	
	//PROPIEDADES---------------------------------------------------------------
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public boolean isGestionarTipoEstablecimiento() {
		return gestionarTipoEstablecimiento;
	}

	public void setGestionarTipoEstablecimiento(
			boolean gestionarTipoEstablecimiento) {
		this.gestionarTipoEstablecimiento = gestionarTipoEstablecimiento;
	}

	public String getTipoEstablecimiento() {
		return tipoEstablecimiento;
	}

	public void setTipoEstablecimiento(String tipoEstablecimiento) {
		this.tipoEstablecimiento = tipoEstablecimiento;
	}

	public TipoEstablecimientoSalud_configuracion getTEstablecimiento() {
		return tEstablecimiento;
	}

	public void setTEstablecimiento(
			TipoEstablecimientoSalud_configuracion establecimiento) {
		tEstablecimiento = establecimiento;
	}

	public String getEstadoFuncionalidad() {
		return estadoFuncionalidad;
	}

	public void setEstadoFuncionalidad(String estadoFuncionalidad) {
		this.estadoFuncionalidad = estadoFuncionalidad;
	}

}
