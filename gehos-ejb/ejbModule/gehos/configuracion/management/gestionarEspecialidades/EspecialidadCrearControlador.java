package gehos.configuracion.management.gestionarEspecialidades;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
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
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.List;

@Name("especialidadCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class EspecialidadCrearControlador {

	// especialidades
	private String nombre = "";
	private String codigo = "";
	private String servicioSeleccionado = "";
	private boolean quirurgica = false;
	Especialidad_configuracion especialidad = new Especialidad_configuracion();
	private Long cid;
	Events events;

	// validaciones
	private Boolean especialidadExiste = false;
	private Boolean codigoExiste = false;

	@In
	EntityManager entityManager;

	FacesMessage facesMessage;
	private String message = "";

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;
	// METODOS------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.cid = bitacora
		.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));

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
		List<Departamento_configuracion> e = entityManager.createQuery(
				"select e from Especialidad_configuracion e "
						+ "where e.nombre =:nombre").setParameter("nombre",
				this.nombre.trim()).getResultList();

		if (e.size() != 0) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "especialidad_existe"); 
			return true;
		}
		return false;
	}

	@Transactional
	public String crear() {
		try {
			if (validacion())
				return "fail";

			especialidad.setNombre(nombre.trim());
			especialidad.setCodigo(codigo.trim());
			especialidad.setQuirurgica(quirurgica);

			Servicio_configuracion s = (Servicio_configuracion) entityManager
					.createQuery(
							"select s from Servicio_configuracion s where s.nombre =:nombre")
					.setParameter("nombre", servicioSeleccionado)
					.getSingleResult();

			especialidad.setServicio(s);
			especialidad.setEliminado(false);
			especialidad.setCid(cid);
			entityManager.persist(especialidad);
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
		} catch (Exception exc) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "especialidad_existe"); 
			return null;
		}
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

			List<Departamento_configuracion> e1 = entityManager.createQuery(
					"select e from Especialidad_configuracion e "
							+ "where e.codigo =:codigo").setParameter("codigo",
					value.toString().trim()).getResultList();

			if (e1.size() != 0) {
				validatorManagerExeption(SeamResourceBundle.getBundle()
						.getString("valorExistente"));
			}
		}
	}

	// PROPIEDADES-------------------------------------------------------------
	public String getNombre() {
		return nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getServicioSeleccionado() {
		return servicioSeleccionado;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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

	public Especialidad_configuracion getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad_configuracion especialidad) {
		this.especialidad = especialidad;
	}

}
