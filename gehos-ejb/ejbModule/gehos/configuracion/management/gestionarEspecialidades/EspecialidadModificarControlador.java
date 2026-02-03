package gehos.configuracion.management.gestionarEspecialidades;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("especialidadModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class EspecialidadModificarControlador {

	// especialidades
	private String nombre = "";
	private String codigo = "";
	private String servicioSeleccionado = "";
	private Long especialidadId = -1l;
	private boolean quirurgica = false;
	Especialidad_configuracion especialidad = new Especialidad_configuracion();
	private Long cid = -1l;
	// validaciones
	private Boolean especialidadExiste = false;
	private Boolean codigoExiste = false;

	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	FacesMessage facesMessage;
	private String message = "";

	@In(create = true)
	FacesMessages facesMessages;
	@In IBitacora bitacora;

	// Metodos
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setEspecialidadId(Long especialidadId) {
		if (this.especialidadId == -1) {
			this.especialidadId = especialidadId;
			this.especialidad = entityManager.find(
					Especialidad_configuracion.class, this.especialidadId);
			this.nombre = this.especialidad.getNombre();
			this.codigo = this.especialidad.getCodigo();
			this.quirurgica = this.especialidad.isQuirurgica();
			this.servicioSeleccionado = this.especialidad.getServicio()
					.getNombre();
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));
			especialidad.setCid(cid);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> serviciosClinicos() {
		return entityManager
				.createQuery(
						"select s.nombre from Servicio_configuracion s "
								+ "where s.departamento.esClinico = true order by s.nombre")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public boolean validacion() {
		especialidadExiste = false;
		codigoExiste = false;

		List<Departamento_configuracion> e = entityManager.createQuery(
				"select e from Especialidad_configuracion e "
						+ "where e.nombre =:nombre and e.id <>:especialidadId")
				.setParameter("nombre", this.nombre.trim()).setParameter(
						"especialidadId", especialidad.getId()).getResultList();

		if (e.size() != 0) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"entidadExistente"));
			return true;
		}
		return false;
	}

	@End
	public String modificar() {
		if (validacion())
			return "fail";

		//Si era quirugica antes de actualizar
		Boolean eraQuirurgica = especialidad.isQuirurgica();
		
		especialidad.setNombre(nombre.trim());
		especialidad.setCodigo(codigo.trim());
		especialidad.setQuirurgica(quirurgica);

		Servicio_configuracion s = (Servicio_configuracion) entityManager
				.createQuery(
						"select s from Servicio_configuracion s where s.nombre =:nombre")
				.setParameter("nombre", servicioSeleccionado).getSingleResult();

		especialidad.setServicio(s);
		especialidad.setEliminado(false);

		entityManager.merge(especialidad);
		entityManager.flush();
		
		/**
		 * @author yurien
		 * **/
		// Se llama al observer que pone su servicio como quirurgico en caso
		// de q
		// tenga alguna especialidad quirurgica
		try {
			Events.instance().raiseEvent(
					"servicioQuirurgicoEventEspecialidadNoEntidad",
					especialidad);
		} catch (Exception e) {
			System.err.print("Error lanzando el evento servicioQuirurgicoEventEspecialidadNoEntidad");
		}

		return "gotodetails";
	}

	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	// Validacion de numeros (Entidades del sistema)
	@SuppressWarnings("unchecked")
	public void validateCodigo(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap().containsKey(
				"form:buttonAceptar")) {

			if (!value.toString().matches("^(\\s*[A-Za-z0-9/_]+\\s*)+$")) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("caracteresIncorrectos"));
			}

			if (value.toString().length() > 25) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres"));
			}

			List<Departamento_configuracion> e1 = entityManager
					.createQuery(
							"select e from Especialidad_configuracion e "
									+ "where e.codigo =:codigo and e.id <>:especialidadId")
					.setParameter("especialidadId", especialidad.getId())
					.setParameter("codigo", value.toString().trim()).getResultList();

			if (e1.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("valorExistente"));
			}
		}
	}

	// PROPIEDADES---------------------------------------------------------

	public String getNombre() {
		return nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getServicioSeleccionado() {
		return servicioSeleccionado;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setServicioSeleccionado(String servicioSeleccionado) {
		this.servicioSeleccionado = servicioSeleccionado;
	}

	public boolean isQuirurgica() {
		return quirurgica;
	}

	public void setQuirurgica(boolean quirurgica) {
		this.quirurgica = quirurgica;
	}

	public Boolean getEspecialidadExiste() {
		return especialidadExiste;
	}

	public void setEspecialidadExiste(Boolean especialidadExiste) {
		this.especialidadExiste = especialidadExiste;
	}

	public Boolean getCodigoExiste() {
		return codigoExiste;
	}

	public void setCodigoExiste(Boolean codigoExiste) {
		this.codigoExiste = codigoExiste;
	}

	public Long getEspecialidadId() {
		return especialidadId;
	}

	public Especialidad_configuracion getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad_configuracion especialidad) {
		this.especialidad = especialidad;
	}

}
