package gehos.configuracion.management.gestionarActividadesInvestigacionDocencia;

import gehos.configuracion.management.entity.ActividadInvestigacionDocencia_configuracion;
import gehos.configuracion.management.entity.TipoActividadInvestigacionDocencia_configuracion;

import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaCrearControlador;
import gehos.configuracion.management.gestionarEntidadesSistema.EntidadSistemaModificarControlador;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

@Name("actividadInvestigacionDocenciaBuscarControlador")
@Scope(ScopeType.CONVERSATION)
public class ActividadInvestigacionDocenciaBuscarControlador {

	// listar y seleccion
	private ListControlerActividades actividadListControler = new ListControlerActividades(
			new ArrayList<ActividadInvestigacionDocencia_configuracion>());
	private List<ActividadInvestigacionDocencia_configuracion> actividadList = new ArrayList<ActividadInvestigacionDocencia_configuracion>();
	private List<ActividadInvestigacionDocencia_configuracion> actividadListSelect = new ArrayList<ActividadInvestigacionDocencia_configuracion>();

	// crear, modificr, eliminar
	private ActividadInvestigacionDocencia_configuracion actividad = new ActividadInvestigacionDocencia_configuracion();
	private String actividadValor = "";
	private String actividadDescripcion = "";
	private String actividadTipo = "";

	// otras funcionalidades
	private boolean gestionarActividad;
	private String from = "";
	private String estadoFuncionalidad = "";// crear, modificar, ver

	private Long actividadSeleccionadaId = -1l;
	private Long entidadId;

	private int posActividad;
	private int salida = 1;
	private String critB1 = "";

	private String message;

	FacesMessage facesMessage;

	@In(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaModificarControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaModificarControlador entidadSistemaModificarControlador;

	@In(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	@Out(required = false, value = "entidadSistemaCrearControlador", scope = ScopeType.CONVERSATION)
	EntidadSistemaCrearControlador entidadSistemaCrearControlador;

	@In
	EntityManager entityManager;

	@In
	FacesMessages facesMessages;

	@SuppressWarnings("unchecked")
	public List<String> tiposActividad() {
		return entityManager
				.createQuery(
						"select t.valor "
								+ "from TipoActividadInvestigacionDocencia_configuracion t")
				.getResultList();
	}

	// METODOS--------------------------------------------------------------
	// Cargar los datos de la entidad
	@SuppressWarnings("unchecked")
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		actividadList = entityManager
				.createQuery(
						"select a from ActividadInvestigacionDocencia_configuracion a "
								+ "where a.eliminado = false and a not in "
								+ "(select a from ActividadInvestigacionDocencia_configuracion a "
								+ "join a.entidads ent where ent.id =:entidadId and a.eliminado = false) "
								+ "order by a.id desc")
				.setParameter("entidadId", this.entidadId).getResultList();

		if (from.equals("modificar")) {
			// elimina las actividades que ya estan asignados a una entidad
			for (int i = 0; i < entidadSistemaModificarControlador
					.getActividadInvestigacionDocenciaList().size(); i++)
				for (int j = 0; j < actividadList.size(); j++) {
					{
						if (entidadSistemaModificarControlador
								.getActividadInvestigacionDocenciaList().get(i)
								.getId().equals(actividadList.get(j).getId())) {
							actividadList.remove(j);
							break;
						}
					}
				}
		} else {
			// elimina las actividades que ya estan asignados a una entidad
			for (int i = 0; i < entidadSistemaCrearControlador
					.getActividadInvestigacionDocenciaList().size(); i++)
				for (int j = 0; j < actividadList.size(); j++) {
					{
						if (entidadSistemaCrearControlador
								.getActividadInvestigacionDocenciaList().get(i)
								.getId().equals(actividadList.get(j).getId())) {
							actividadList.remove(j);
							break;
						}
					}
				}
		}

		actividadListControler.setElementos(actividadList);

		if (salida == 1) {
			actividadListSelect = new ArrayList<ActividadInvestigacionDocencia_configuracion>();
			actividadListControler.setFirstResult(0);
			actividad = new ActividadInvestigacionDocencia_configuracion();
		}
		salida = 0;
	}

	// realizar busqueda
	public void buscar() {
		actividadListControler.setFirstResult(0);
		this.actividadListControler.setValor(this.critB1);
	}

	// cancela la busqueda
	public void cancelarBusqueda() {
		this.critB1 = "";
		actividadListControler.setValor("");
	}

	// CREAR MODIFICAR
	// ELIMINAR----------------------------------------------------------
	// selecciona tipo entidad para eliminar
	public void seleccionar(Long actividadId) {
		actividad = (ActividadInvestigacionDocencia_configuracion) entityManager
				.createQuery(
						"select a from ActividadInvestigacionDocencia_configuracion a where a.id =:actividadId")
				.setParameter("actividadId", actividadId).getSingleResult();
		actividadValor = actividad.getValor();
		actividadDescripcion = actividad.getDescripcion();
		actividadTipo = actividad.getTipoActividadInvestigacionDocencia()
				.getValor();
		estadoFuncionalidad = "modificar";
	}

	// limpia los campos despues de una operacion (crear, modificar, eliminar)
	public void limpiarCampos() {
		actividad = new ActividadInvestigacionDocencia_configuracion();
		actividadValor = "";
		actividadDescripcion = "";
		estadoFuncionalidad = "crear";
	}

	public void activa() {
		actividad.setActiva(!actividad.isActiva());
	}

	public String onAdicionarCatComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("actividadCrear").size() > 0
				|| facesMessages.getCurrentMessagesForControl(
						"actividadCrearDesc").size() > 0
				|| facesMessages.getCurrentMessagesForControl("subtiposCombo")
						.size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	// crear actividad
	@SuppressWarnings("unchecked")
	public void crear() {
		try {
			List<ActividadInvestigacionDocencia_configuracion> la = new ArrayList<ActividadInvestigacionDocencia_configuracion>();
			if (estadoFuncionalidad.equals("modificar")) {
				la = entityManager
						.createQuery(
								"select a from ActividadInvestigacionDocencia_configuracion a where a.valor =:actividadValor and a.id<>:actividadId")
						.setParameter("actividadValor", actividadValor.trim())
						.setParameter("actividadId", actividad.getId())
						.getResultList();
			} else {
				la = entityManager
						.createQuery(
								"select a from ActividadInvestigacionDocencia_configuracion a where a.valor =:actividadValor")
						.setParameter("actividadValor", actividadValor.trim())
						.getResultList();
			}

			if (la.size() > 0) {
				facesMessages.addToControlFromResourceBundle("buttonAceptar",
						Severity.ERROR,
						"La actividad ha sido creada previamente.");
				return;
			}
			TipoActividadInvestigacionDocencia_configuracion tipo = (TipoActividadInvestigacionDocencia_configuracion) entityManager
					.createQuery(
							"select a from TipoActividadInvestigacionDocencia_configuracion a where a.valor = :val")
					.setParameter("val", this.actividadTipo).getSingleResult();
			this.actividad.setTipoActividadInvestigacionDocencia(tipo);
			tipo.getActividadInvestigacionDocencias().add(this.actividad);
			this.actividad.setCid(-1L);
			this.actividad.setValor(actividadValor);
			this.actividad.setDescripcion(actividadDescripcion);
			this.actividad.setEliminado(false);
			entityManager.persist(tipo);
			entityManager.persist(actividad);
			entityManager.flush();
			this.actividadTipo = "";
			this.actividadValor = "";
			this.actividadDescripcion = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// eliminar establecimiento
	@SuppressWarnings("unchecked")
	public void eliminar() {
		try {
			entityManager.remove(actividad);
			entityManager.flush();
			if (actividadListControler.getElementos().contains(actividad)) {
				actividadListControler.getElementos().remove(actividad);
				actividadListSelect.remove(actividad);
			}
			actividadList = entityManager
					.createQuery(
							"select a from ActividadInvestigacionDocencia_configuracion a "
									+ "where a.eliminado = false and a not in "
									+ "(select a from ActividadInvestigacionDocencia_configuracion a "
									+ "join a.entidads ent where ent.id =:entidadId and a.eliminado = false) "
									+ "order by a.id desc")
					.setParameter("entidadId", this.entidadId).getResultList();

			if (from.equals("modificar")) {
				// elimina los departamentos que ya estan asignados a una
				// entidad
				for (int i = 0; i < entidadSistemaModificarControlador
						.getActividadInvestigacionDocenciaList().size(); i++)
					for (int j = 0; j < actividadList.size(); j++) {
						{
							if (entidadSistemaModificarControlador
									.getActividadInvestigacionDocenciaList()
									.get(i).getId()
									.equals(actividadList.get(j).getId())) {
								actividadList.remove(j);
								actividadListControler.remove(j);
								break;
							}
						}
					}
			} else {
				// elimina los departamentos que ya estan asignados a una
				// entidad
				for (int i = 0; i < entidadSistemaCrearControlador
						.getActividadInvestigacionDocenciaList().size(); i++)
					for (int j = 0; j < actividadList.size(); j++) {
						{
							if (entidadSistemaCrearControlador
									.getActividadInvestigacionDocenciaList()
									.get(i).getId()
									.equals(actividadList.get(j).getId())) {
								actividadList.remove(j);
								actividadListControler.remove(j);
								break;
							}
						}
					}
			}

			this.actividadListControler.setValor(this.critB1);

			if (actividadListControler.getResultList().size() == 0
					&& actividadListControler.getFirstResult() != 0)
				actividadListControler.setFirstResult(actividadListControler
						.getFirstResult()
						- actividadListControler.getMaxResults());

		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("msjEliminar"));

		}
		limpiarCampos();
	}

	// asignar actividad a la entidad
	public void asignarActividad() {
		if (existeActividad(this.actividadSeleccionadaId))
			actividadListSelect.remove(posActividad);
		else {
			ActividadInvestigacionDocencia_configuracion est = entityManager
					.find(ActividadInvestigacionDocencia_configuracion.class,
							this.actividadSeleccionadaId);
			actividadListSelect.add(est);
		}

	}

	// verificar que la entidad tenga asignado un establecimiento
	public boolean existeActividad(Long actividadSeleccionadaId) {
		for (int i = 0; i < actividadListSelect.size(); i++) {
			if (actividadSeleccionadaId.equals(actividadListSelect.get(i)
					.getId())) {
				posActividad = i;
				return true;
			}
		}
		return false;
	}

	// asignar departamentos a la entidad
	public void asignar() {
		if (from.equals("modificar")) {
			entidadSistemaModificarControlador
					.getActividadInvestigacionDocenciaList().addAll(
							actividadListSelect);
			actividadList.removeAll(actividadListSelect);
			entidadSistemaModificarControlador
					.getActividadInvestigacionDocencia_controler()
					.setElementos(
							entidadSistemaModificarControlador
									.getActividadInvestigacionDocenciaList());
		} else {
			entidadSistemaCrearControlador
					.getActividadInvestigacionDocenciaList().addAll(
							actividadListSelect);
			actividadList.removeAll(actividadListSelect);
			entidadSistemaCrearControlador
					.getActividadInvestigacionDocencia_controler()
					.setElementos(
							entidadSistemaCrearControlador
									.getActividadInvestigacionDocenciaList());
		}
		cancelar();
	}

	// cancelar asignacion de departamentos a la entidad
	public void cancelar() {
		salida = 1;
		actividadListControler = new ListControlerActividades();
		this.critB1 = "";
	}

	// PROPIEDADES-----------------------------------------------------------------
	public void setSalida(int salida) {
		this.salida = salida;
		cancelar();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Long getEntidadId() {
		return entidadId;
	}

	public void setEntidadId(Long entidadId) {

		this.entidadId = entidadId;
	}

	public int getSalida() {
		return salida;
	}

	public EntidadSistemaModificarControlador getentidadSistemaModificarControlador() {
		return entidadSistemaModificarControlador;
	}

	public void setentidadSistemaModificarControlador(
			EntidadSistemaModificarControlador entidadSistemaModificarControlador) {
		this.entidadSistemaModificarControlador = entidadSistemaModificarControlador;
	}

	public EntidadSistemaCrearControlador getentidadSistemaCrearControlador() {
		return entidadSistemaCrearControlador;
	}

	public void setentidadSistemaCrearControlador(
			EntidadSistemaCrearControlador entidadSistemaCrearControlador) {
		this.entidadSistemaCrearControlador = entidadSistemaCrearControlador;
	}

	public String getEstadoFuncionalidad() {
		return estadoFuncionalidad;
	}

	public void setEstadoFuncionalidad(String estadoFuncionalidad) {
		this.estadoFuncionalidad = estadoFuncionalidad;
	}

	public ListControlerActividades getActividadListControler() {
		return actividadListControler;
	}

	public void setActividadListControler(
			ListControlerActividades actividadListControler) {
		this.actividadListControler = actividadListControler;
	}

	public List<ActividadInvestigacionDocencia_configuracion> getActividadList() {
		return actividadList;
	}

	public void setActividadList(
			List<ActividadInvestigacionDocencia_configuracion> actividadList) {
		this.actividadList = actividadList;
	}

	public List<ActividadInvestigacionDocencia_configuracion> getActividadListSelect() {
		return actividadListSelect;
	}

	public void setActividadListSelect(
			List<ActividadInvestigacionDocencia_configuracion> actividadListSelect) {
		this.actividadListSelect = actividadListSelect;
	}

	public ActividadInvestigacionDocencia_configuracion getActividad() {
		return actividad;
	}

	public void setActividad(
			ActividadInvestigacionDocencia_configuracion actividad) {
		this.actividad = actividad;
	}

	public boolean isGestionarActividad() {
		return gestionarActividad;
	}

	public void setGestionarActividad(boolean gestionarActividad) {
		this.gestionarActividad = gestionarActividad;
	}

	public int getPosActividad() {
		return posActividad;
	}

	public void setPosActividad(int posActividad) {
		this.posActividad = posActividad;
	}

	public String getCritB1() {
		return critB1;
	}

	public void setCritB1(String critB1) {
		this.critB1 = critB1;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getActividadSeleccionadaId() {
		return actividadSeleccionadaId;
	}

	public void setActividadSeleccionadaId(Long actividadSeleccionadaId) {
		this.actividadSeleccionadaId = actividadSeleccionadaId;
	}

	public String getActividadValor() {
		return actividadValor;
	}

	public void setActividadValor(String actividadValor) {
		this.actividadValor = actividadValor;
	}

	public String getActividadDescripcion() {
		return actividadDescripcion;
	}

	public void setActividadDescripcion(String actividadDescripcion) {
		this.actividadDescripcion = actividadDescripcion;
	}

	public String getActividadTipo() {
		return actividadTipo;
	}

	public void setActividadTipo(String actividadTipo) {
		this.actividadTipo = actividadTipo;
	}

}
