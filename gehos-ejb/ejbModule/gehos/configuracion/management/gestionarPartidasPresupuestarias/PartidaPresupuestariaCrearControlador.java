package gehos.configuracion.management.gestionarPartidasPresupuestarias;

import java.util.ArrayList;
import java.util.List;

import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.EntidadOrganizacional_configuracion;
import gehos.configuracion.management.entity.PartidaPresupuestariaHospital_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaCrearControlador;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaModificarControlador;
import gehos.configuracion.management.gestionarServiciosClinicos.SubCartegorias;

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

@Name("partidaPresupuestariaCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class PartidaPresupuestariaCrearControlador {

	private PartidaPresupuestariaHospital_configuracion partidaPresupuestaria = new PartidaPresupuestariaHospital_configuracion();
	private String entidadOrganizacionalSeleccionada = "";

	// departamento
	private List<SubCartegorias> subCategorias = new ArrayList<SubCartegorias>();
	private String categoria = "";
	private String departamentoSeleccionado = "";
	private String subCateogoriaSeleccionada = "";
	private String categoriaSeleccionada = "";// servicio no fisico hijo de un
	private String cantidad = "";

	// servicios
	private String servicioFisicoSeleccionado = "";

	// otras funcionalidades
	private boolean crearEntidadOrganizacional = false;
	private String from = "";
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
	public void begin() {
		partidaPresupuestaria = new PartidaPresupuestariaHospital_configuracion();
		entidadOrganizacionalSeleccionada = "";
	}

	// devuelve la lista de departamentos
	@SuppressWarnings("unchecked")
	public List<String> departamentos() {
		if (from.equals("crear")) {
			return new ArrayList<String>();
		} else {
			return entityManager
					.createQuery(
							"select d.departamento.nombre from DepartamentoInEntidad_configuracion d where d.entidad.id = :idp")
					.setParameter(
							"idp",
							entidadSistemaModificarControlador.getEntidad()
									.getId()).getResultList();
		}
		// return entityManager
		// .createQuery(
		// "select dep.nombre from Departamento_configuracion dep where dep.esClinico = true order by dep.nombre")
		// .getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> serviciosFisicos() {
		if (from.equals("crear")) {
			return new ArrayList<String>();
		} else {
			return entityManager
					.createQuery(
							"select s.servicio.nombre from ServicioInEntidad_configuracion s "
									+ "where s.entidad.id = :id and s.servicio.departamento.nombre = :departamento ")
					.setParameter("departamento", this.departamentoSeleccionado)
					.setParameter(
							"id",
							entidadSistemaModificarControlador.getEntidad()
									.getId()).getResultList();
		}
		// return entityManager
		// .createQuery(
		// "select s.nombre from Servicio_configuracion s where s.departamento.nombre =:departamento and s.eliminado = false order by s.nombre")
		// .setParameter("departamento", this.departamentoSeleccionado)
		// .getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> entidadOrganizacionalList() {
		return entityManager
				.createQuery(
						"select e.valor from EntidadOrganizacional_configuracion e order by e.valor")
				.getResultList();
	}

	public void nuevaEntidadOrganizacional() {
		if (entidadOrganizacionalSeleccionada.equals("<Otro>")) {
			crearEntidadOrganizacional = true;
		} else
			crearEntidadOrganizacional = false;
	}

	public String crear() {
		EntidadOrganizacional_configuracion entidadOrganizacional = (EntidadOrganizacional_configuracion) entityManager
				.createQuery(
						"select e from EntidadOrganizacional_configuracion e "
								+ "where e.valor=:entidadOrganizacionalValor")
				.setParameter("entidadOrganizacionalValor",
						entidadOrganizacionalSeleccionada).getSingleResult();

		if (!departamentoSeleccionado.equals("")) {
			Departamento_configuracion d = (Departamento_configuracion) entityManager
					.createQuery(
							"select d from Departamento_configuracion d where d.nombre =:departamentoNombre")
					.setParameter("departamentoNombre",
							departamentoSeleccionado).getSingleResult();
			partidaPresupuestaria.setDepartamento(d);
		}

		if (!servicioFisicoSeleccionado.equals("")) {
			Servicio_configuracion s = (Servicio_configuracion) entityManager
					.createQuery(
							"select s from Servicio_configuracion s where s.nombre =:servicioNombre")
					.setParameter("servicioNombre", servicioFisicoSeleccionado)
					.getSingleResult();
			partidaPresupuestaria.setServicio(s);
		}

		partidaPresupuestaria.setEntidadOrganizacional(entidadOrganizacional);
		partidaPresupuestaria.setEliminado(false);
		partidaPresupuestaria.setCantidad(new Integer(this.cantidad));

		List<PartidaPresupuestariaHospital_configuracion> la = new ArrayList<PartidaPresupuestariaHospital_configuracion>();
		la.add(partidaPresupuestaria);
		if (from.equals("crear")) {

			entidadSistemaCrearControlador.getPartidaPresupuestariaList()
					.addAll(la);
			entidadSistemaCrearControlador
					.setPartidaPresupuestaria(partidaPresupuestaria);
			entidadSistemaCrearControlador
					.getPartidaPresupuestariaList_controler().setElementos(
							entidadSistemaCrearControlador
									.getPartidaPresupuestariaList());
		} else {
			entidadSistemaModificarControlador.getPartidaPresupuestariaList()
					.addAll(la);
			entidadSistemaModificarControlador
					.setPartidaPresupuestaria(partidaPresupuestaria);
			entidadSistemaModificarControlador
					.getPartidaPresupuestariaList_controler().setElementos(
							entidadSistemaModificarControlador
									.getPartidaPresupuestariaList());
		}

		cancelar();
		return "gotodetails";
	}

	public void cancelar() {
		cantidad = "";
		departamentoSeleccionado = "";
		servicioFisicoSeleccionado = "";
		partidaPresupuestaria = new PartidaPresupuestariaHospital_configuracion();
		entidadOrganizacionalSeleccionada = "";
		crearEntidadOrganizacional = false;
	}

	// PROPIEDADES-------------------------------------------------------------
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

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public PartidaPresupuestariaHospital_configuracion getPartidaPresupuestaria() {
		return partidaPresupuestaria;
	}

	public void setPartidaPresupuestaria(
			PartidaPresupuestariaHospital_configuracion partidaPresupuestaria) {
		this.partidaPresupuestaria = partidaPresupuestaria;
	}

	public String getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}

	public void setDepartamentoSeleccionado(String departamentoSeleccionado) {
		this.departamentoSeleccionado = departamentoSeleccionado;
	}

	public List<SubCartegorias> getSubCategorias() {
		return subCategorias;
	}

	public void setSubCategorias(List<SubCartegorias> subCategorias) {
		this.subCategorias = subCategorias;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getSubCateogoriaSeleccionada() {
		return subCateogoriaSeleccionada;
	}

	public void setSubCateogoriaSeleccionada(String subCateogoriaSeleccionada) {
		this.subCateogoriaSeleccionada = subCateogoriaSeleccionada;
	}

	public String getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(String categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public String getServicioFisicoSeleccionado() {
		return servicioFisicoSeleccionado;
	}

	public void setServicioFisicoSeleccionado(String servicioFisicoSeleccionado) {
		this.servicioFisicoSeleccionado = servicioFisicoSeleccionado;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

}
