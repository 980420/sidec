package gehos.configuracion.management.gestionarAsignacionesFinancieras;


import java.util.List;

import gehos.configuracion.management.entity.AsignacionFinancieraHospital_configuracion;
import gehos.configuracion.management.entity.EntidadOrganizacional_configuracion;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaCrearControlador;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaModificarControlador;

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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;

@Name("asignacionFinancieraModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class AsignacionFinancieraModificarControlador {

	private AsignacionFinancieraHospital_configuracion asignacionFinanciera = new AsignacionFinancieraHospital_configuracion();
	private String entidadOrganizacionalSeleccionada = "";
	private String cantidad = "";

	// otras funcionalidades
	private boolean crearEntidadOrganizacional = false;
	private String from = "";
	private int pos = -1;
	private String message = "";

	@In(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaModificarControlador entidadSistemaModificarControlador;

	@In(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaCrearControlador entidadSistemaCrearControlador;

	@In
	EntityManager entityManager;

	FacesMessage facesMessage;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	// METODOS------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setFrom(String from) {
		this.from = from;
		if (from.equals("crear")) {

			asignacionFinanciera = entidadSistemaCrearControlador
					.getAsingacionFinanciera();
			entidadOrganizacionalSeleccionada = asignacionFinanciera
					.getEntidadOrganizacional().getValor();
			pos = entidadSistemaCrearControlador.getAsignacionFinancieraList()
					.indexOf(asignacionFinanciera);
			cantidad = new Integer(asignacionFinanciera.getCantidad()).toString();
		} else {
			asignacionFinanciera = entidadSistemaModificarControlador
					.getAsignacionFinanciera();
			entidadOrganizacionalSeleccionada = asignacionFinanciera
					.getEntidadOrganizacional().getValor();
			pos = entidadSistemaModificarControlador
					.getAsignacionFinancieraList()
					.indexOf(asignacionFinanciera);
			cantidad = new Integer(asignacionFinanciera.getCantidad()).toString();
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> entidadOrganizacionalList() {
		return entityManager.createQuery(
				"select e.valor from EntidadOrganizacional_configuracion e order by e.valor")
				.getResultList();
	}

	public void nuevaEntidadOrganizacional() {
		if (entidadOrganizacionalSeleccionada.equals("<Otro>")) {
			crearEntidadOrganizacional = true;
		} else
			crearEntidadOrganizacional = false;
	}

	public String modificar() {
		EntidadOrganizacional_configuracion entidadOrganizacional = (EntidadOrganizacional_configuracion) entityManager
				.createQuery(
						"select e from EntidadOrganizacional_configuracion e "
								+ "where e.valor=:entidadOrganizacionalValor")
				.setParameter("entidadOrganizacionalValor",
						entidadOrganizacionalSeleccionada).getSingleResult();

		asignacionFinanciera.setCantidad(new Integer(this.cantidad));
		asignacionFinanciera.setEntidadOrganizacional(entidadOrganizacional);

		if (from.equals("crear")) {
			entidadSistemaCrearControlador.getAsignacionFinancieraList().set(
					pos, asignacionFinanciera);
		} else {
			entidadSistemaModificarControlador.getAsignacionFinancieraList()
					.set(pos, asignacionFinanciera);
		}

		cantidad = "";
		cancelar();
		return "gotodetails";
	}

	public void cancelar() {
		asignacionFinanciera = new AsignacionFinancieraHospital_configuracion();
		entidadOrganizacionalSeleccionada = "";
		crearEntidadOrganizacional = false;
	}
	
	// PROPIEDADES-------------------------------------------------------------
	public AsignacionFinancieraHospital_configuracion getAsignacionFinanciera() {
		return asignacionFinanciera;
	}

	public void setAsignacionFinanciera(
			AsignacionFinancieraHospital_configuracion asignacionFinanciera) {
		this.asignacionFinanciera = asignacionFinanciera;
	}

	public String getEntidadOrganizacionalSeleccionada() {
		return entidadOrganizacionalSeleccionada;
	}

	public void setEntidadOrganizacionalSeleccionada(
			String entidadOrganizacionalSeleccionada) {
		this.entidadOrganizacionalSeleccionada = entidadOrganizacionalSeleccionada;
	}

	public boolean isCrearEntidadOrganizacional() {
		return crearEntidadOrganizacional;
	}

	public void setCrearEntidadOrganizacional(boolean crearEntidadOrganizacional) {
		this.crearEntidadOrganizacional = crearEntidadOrganizacional;
	}

	public String getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

}
