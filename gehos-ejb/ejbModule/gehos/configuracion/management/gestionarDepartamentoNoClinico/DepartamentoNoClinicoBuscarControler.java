package gehos.configuracion.management.gestionarDepartamentoNoClinico;

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
import org.jboss.seam.transaction.Transaction;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("departamentoNoClinicoBuscarControler")
public class DepartamentoNoClinicoBuscarControler extends
		EntityQuery<Departamento_configuracion> {

	private static final String EJBQL = "select departamento from Departamento_configuracion departamento where departamento.esClinico = false";

	private static final String[] RESTRICTIONS = { "lower(departamento.nombre) like concat(lower(#{departamentoNoClinicoBuscarControler.nombre.trim()}),'%')",
	// "#{departamentoNoClinicoBuscarControler.departamentoId} <> departamento.id"
	};

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	// search criteria
	private String nombre = "";

	private Departamento_configuracion departamento = new Departamento_configuracion();

	// other functions
	private Long departamentoId = -1l;
	private Long cid = -1l;
	private boolean state = true;
	private Parameters parametros = new Parameters();

	private int error = 0;

	// Methods ----------------------------------------------------
	public DepartamentoNoClinicoBuscarControler() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("departamento.id desc");
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}

	// search
	public void buscar() {
		setFirstResult(0);
		cid=bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitBuscar"));
	}

	// change the state of search simpleTugglePanel (open/closed)
	public void cambiarEstadoSimpleTogglePanel() {
		state = !state;
	}

	// selec clinic departament to removal
	public String seleccionar() throws IllegalStateException,
			SecurityException, SystemException {
		try {
			@SuppressWarnings("unused")
			Departamento_configuracion depConcu = (Departamento_configuracion) entityManager
					.createQuery(
							"select departamento from Departamento_configuracion departamento "
									+ "where departamento.esClinico = false "
									+ "and departamento.id = :id")
					.setParameter("id", this.departamentoId).getSingleResult();

			return "good";
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("userSearch",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
		}
		return "null";
	}

	// selecciona dep para modificar

	public void seleccionarModificar() {
		try {
			error = 0;
			@SuppressWarnings("unused")
			Departamento_configuracion depConcu = (Departamento_configuracion) entityManager
					.createQuery(
							"select departamento from Departamento_configuracion departamento "
									+ "where departamento.esClinico = false "
									+ "and departamento.id = :id")
					.setParameter("id", this.departamentoId).getSingleResult();

		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch",
					Severity.ERROR, "eliminado");
			error = 1;
		}

	}

	public void seleccionarVer() throws IllegalStateException,
			SecurityException, SystemException {
		try {
			error = 0;
			@SuppressWarnings("unused")
			Departamento_configuracion depConcu = (Departamento_configuracion) entityManager
					.createQuery(
							"select departamento from Departamento_configuracion departamento "
									+ "where departamento.esClinico = false "
									+ "and departamento.id = :id")
					.setParameter("id", this.departamentoId).getSingleResult();

		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch",
					Severity.ERROR, "eliminado");
			error = 1;
		}
	}

	// remove clinic departament
	public void eliminar() {
		try {
			cid=bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));

			Departamento_configuracion aux = (Departamento_configuracion) entityManager
					.createQuery(
							"select departamento from Departamento_configuracion departamento "
									+ "where departamento.esClinico = false "
									+ "and departamento.id = :id")
					.setParameter("id", this.departamentoId).getSingleResult();

			this.departamentoId = -1l;
			entityManager.remove(aux);
			entityManager.flush();

			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());

		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "msjEliminar");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "msjEliminar");
		}
	}

	// change visibility
	public void cambiarVisibilidad(Long id) {
		try {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));

			Departamento_configuracion aux = (Departamento_configuracion) entityManager
					.createQuery(
							"select departamento from Departamento_configuracion departamento "
									+ "where departamento.esClinico = false "
									+ "and departamento.id = :id")
					.setParameter("id", id).getSingleResult();
			if (aux.getEliminado() == null) {
				aux.setEliminado(true);
			} else {
				aux.setEliminado(!aux.getEliminado());
			}
			aux.setCid(cid);
			entityManager.merge(aux);
			entityManager.flush();
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch",
					Severity.ERROR, "eliminado");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("userSearch",
					Severity.ERROR, "errorInesperado");
		}
	}

	// Properties -------------------------------------------------------
	public Departamento_configuracion getDepartamento() {
		return departamento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = parametros.decodec(nombre);
	}

	public void setDepartamento(Departamento_configuracion departamento) {
		this.departamento = departamento;
	}

	public Long getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Long departamentoId) {
		this.departamentoId = departamentoId;
	}

	public Parameters getParametros() {
		return parametros;
	}

	public void setParametros(Parameters parametros) {
		this.parametros = parametros;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}