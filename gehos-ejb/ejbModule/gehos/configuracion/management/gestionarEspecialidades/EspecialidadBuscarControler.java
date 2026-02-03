package gehos.configuracion.management.gestionarEspecialidades;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.Arrays;

import javax.persistence.EntityManager;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("especialidadBuscarControler")
public class EspecialidadBuscarControler extends
		EntityQuery<Departamento_configuracion> {

	private static final String EJBQL = "select especialidad from Especialidad_configuracion especialidad";

	private static final String[] RESTRICTIONS = {
			"lower(especialidad.nombre) like concat(lower(#{especialidadBuscarControler.nombre}),'%')",
			"lower(especialidad.codigo) like concat(lower(#{especialidadBuscarControler.codigo}),'%')",
			"especialidad.id <> #{especialidadBuscarControler.especialidadId}", };

	// departamento
	private Especialidad_configuracion especialidad = new Especialidad_configuracion();
	private String nombre;
	private String codigo;
	private Long especialidadId = -1l;
	private Parameters parameters = new Parameters();
	
	private Long cid = -1l;
	// otras funcionalidades
	private boolean openSimpleTogglePanel = true;

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;
	
	@In
	IBitacora bitacora;
	
	public EspecialidadBuscarControler() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("especialidad.id desc");
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		if (getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult() - getMaxResults());
	}

	public void cambiarVisibilidad(Long id) {
		Especialidad_configuracion aux = entityManager.find(
				Especialidad_configuracion.class, id);
		if (aux.getEliminado() == null) {
			aux.setEliminado(false);
		}
		else {
			aux.setEliminado(!aux.getEliminado());
		}
		aux.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad")));
		entityManager.merge(aux);
		entityManager.flush();
	}

	public void seleccionar(Long especialidadId) {
		this.especialidadId = especialidadId;
	}

	public void eliminar() {
		try {
			cid=bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));

			especialidad = entityManager.find(Especialidad_configuracion.class,
					this.especialidadId);
			this.especialidadId = -1l;
			entityManager.remove(especialidad);
			entityManager.flush();

			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());

		} catch (Exception e) {
			facesMessages
					.addToControlFromResourceBundle("btnSi", Severity.ERROR,
							"Esta especialidad esta en uso por lo tanto no puede ser eliminada.");
		}

	}

	public void cambiarEstadoSimpleTogglePanel() {
		openSimpleTogglePanel = !openSimpleTogglePanel;
	}

	public void buscar() {
		setFirstResult(0);
	}

	public void cancelar() {
		this.nombre = "";
		this.codigo = "";
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = parameters.decodec(nombre);
	}

	public boolean isOpenSimpleTogglePanel() {
		return openSimpleTogglePanel;
	}

	public void setOpenSimpleTogglePanel(boolean openSimpleTogglePanel) {
		this.openSimpleTogglePanel = openSimpleTogglePanel;
	}

	public Especialidad_configuracion getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad_configuracion especialidad) {
		this.especialidad = especialidad;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = parameters.decodec(codigo);
	}

	public Long getEspecialidadId() {
		return especialidadId;
	}

	public void setEspecialidadId(Long especialidadId) {
		this.especialidadId = especialidadId;
	}
}
