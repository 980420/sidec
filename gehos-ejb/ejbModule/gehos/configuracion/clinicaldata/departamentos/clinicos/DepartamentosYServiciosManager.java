package gehos.configuracion.clinicaldata.departamentos.clinicos;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.clinicaldata.departamentos.clinicos.treebuilders.DepartamentosYServiciosTreeBuilder;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.DepartamentoWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.EspecialidadWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ServicioWrapper;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.DropEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeRowKey;

@Name("departamentosYServiciosManager")
public class DepartamentosYServiciosManager {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In IBitacora bitacora;

	@In(required = false, create = true, value = "departamentosYServiciosTreeBuilder")
	DepartamentosYServiciosTreeBuilder treeBuilder;

	private ITreeData selectedItem;
	@SuppressWarnings({ "unchecked" })
	private TreeNode selectedTreenode;

	@Create
	public void constructor() {

	}

	public String onModificarDepComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputIdent").size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onSalvarDepComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputIdentEdit").size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onAddServADepComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputServIdent").size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onAddServADepEditComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputServIdentEdit")
				.size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onAddServAServComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputServAServIdent")
				.size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onAddEspAServComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputEspIdent").size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public String onEditEspAServComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputEspIdentEdit")
				.size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	public void subir() {
		if (this.selectedItem instanceof DepartamentoWrapper) {
			this.selectedItem = null;
		} else if (this.selectedItem instanceof ServicioWrapper) {
			if (((ServicioWrapper) this.selectedItem).getValue().getServicio() == null) {
				Departamento_configuracion departamento = entityManager.find(
						Departamento_configuracion.class,
						((ServicioWrapper) this.selectedItem).getValue()
								.getDepartamento().getId());
				this.selectedItem = new DepartamentoWrapper(departamento,
						false, null);
			} else {
				Servicio_configuracion servicio = entityManager.find(
						Servicio_configuracion.class,
						((ServicioWrapper) this.selectedItem).getValue()
								.getServicio().getId());
				this.selectedItem = new ServicioWrapper(servicio, false, null);
			}
		} else if (this.selectedItem instanceof EspecialidadWrapper) {
			Servicio_configuracion serv = entityManager.find(
					Servicio_configuracion.class,
					((EspecialidadWrapper) this.selectedItem).getValue()
							.getServicio().getId());
			this.selectedItem = new ServicioWrapper(serv, false, null);
		}
	}

	public void changeDepartmentVisibility(Long depId) {
		Departamento_configuracion department = entityManager.find(
				Departamento_configuracion.class, depId);
		if (department.getEliminado() == null
				|| department.getEliminado() == false)
			department.setEliminado(true);
		else
			department.setEliminado(false);
		department.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad")));
		entityManager.persist(department);
		entityManager.flush();
	}

	public void changeServiceVisibility(Long serviceId) {
		Servicio_configuracion servicio = entityManager.find(
				Servicio_configuracion.class, serviceId);
		if (servicio.getEliminado() == null || servicio.getEliminado() == false)
			servicio.setEliminado(true);
		else
			servicio.setEliminado(false);		
		servicio.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidadServicio")));
		entityManager.persist(servicio);
		entityManager.flush();
	}

	public void changeEspecialtyVisibility(Long espId) {
		Especialidad_configuracion especialidad = entityManager.find(
				Especialidad_configuracion.class, espId);
		if (especialidad.getEliminado() == null
				|| especialidad.getEliminado() == false)
			especialidad.setEliminado(true);
		else
			especialidad.setEliminado(false);
		especialidad.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidadEspecialidad")));
		entityManager.persist(especialidad);
		entityManager.flush();
	}

	private Especialidad_configuracion especialidad_en_edicion;

	public void editarEspecialidadSeleccionada() {
		especialidad_en_edicion = (Especialidad_configuracion) entityManager
				.find(Especialidad_configuracion.class,
						this.selectedItem.getId());
	}

	public void salvarEspecialidad() {
		especialidad_en_edicion = entityManager.merge(especialidad_en_edicion);
		especialidad_en_edicion.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificarEspecialidad")));
		
		entityManager.persist(especialidad_en_edicion);
		entityManager.flush();
		this.selectedItem.setValue(especialidad_en_edicion);
		if (this.selectedTreenode != null)
			this.treeBuilder.updateNode(this.selectedTreenode);
	}

	private Servicio_configuracion servicio_en_edicion;

	public void editarServicioSeleccionado() {
		servicio_en_edicion = (Servicio_configuracion) entityManager.find(
				Servicio_configuracion.class, this.selectedItem.getId());
	}

	public void salvarServicio() {
		servicio_en_edicion = entityManager.merge(servicio_en_edicion);
		servicio_en_edicion.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificarServicio")));
		entityManager.persist(servicio_en_edicion);
		entityManager.flush();
		this.selectedItem.setValue(servicio_en_edicion);
		if (this.selectedTreenode != null)
			this.treeBuilder.updateNode(this.selectedTreenode);
	}

	private Departamento_configuracion departamento_en_edicion;

	public void editarDepartamentoSeleccionado() {
		departamento_en_edicion = (Departamento_configuracion) entityManager
				.find(Departamento_configuracion.class,
						this.selectedItem.getId());
	}

	public void salvarDepartamento() {
		// validar();
		// if ((facesMessages.getCurrentMessagesForControl("inputIdentEdit")
		// .size() == 0)) {
		departamento_en_edicion = entityManager.merge(departamento_en_edicion);
		departamento_en_edicion.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar")));
		entityManager.persist(departamento_en_edicion);
		entityManager.flush();
		this.selectedItem.setValue(departamento_en_edicion);
		if (this.selectedTreenode != null)
			this.treeBuilder.updateNode(this.selectedTreenode);
		// }
	}

	private void validar() {
		String value = departamento_en_edicion.getNombre();
		if (value.isEmpty()) {
			facesMessages.addToControlFromResourceBundle("inputIdentEdit",
					Severity.ERROR, "Valor requerido");
		}
		if (!value.toString().matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("inputIdentEdit",
					Severity.ERROR, "caracteresIncorrectos");
		}

		if (value.toString().length() > 25) {
			facesMessages.addToControlFromResourceBundle("inputIdentEdit",
					Severity.ERROR, "caracteresIncorrectos");
		}
	}

	private String departamentoIdToRemove;

	public void eliminarDepartamento() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));

			Departamento_configuracion departamento = entityManager.find(
					Departamento_configuracion.class,
					Long.parseLong(this.departamentoIdToRemove));
			entityManager.remove(departamento);
			entityManager.flush();
			this.treeBuilder.loadData();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_tipo_departamento");
		}
	}

	private String servicioIdToRemove;

	public void eliminarServicio() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminarServicio"));

			Servicio_configuracion servicio = entityManager.find(
					Servicio_configuracion.class,
					Long.parseLong(this.servicioIdToRemove));
			entityManager.remove(servicio);
			entityManager.flush();
			if (this.selectedTreenode != null)
				this.treeBuilder.updateNode(this.selectedTreenode);
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_tipo_servicio");
		}
	}

	private String especialidadIdToRemove;

	public void eliminarEspecialidad() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminarEspecialidad"));

			Especialidad_configuracion especialidad = entityManager.find(
					Especialidad_configuracion.class,
					Long.parseLong(this.especialidadIdToRemove));
			entityManager.remove(especialidad);
			entityManager.flush();
			if (this.selectedTreenode != null)
				this.treeBuilder.updateNode(this.selectedTreenode);
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_tipo_servicio");
		}
	}

	private String servicioADepartamentoNombre;

	public Boolean clearServicioADepartamentoNombre() {
		servicioADepartamentoNombre = "";
		return false;
	}

	public void adicionarServicioADepartamento() {
		try {
			Servicio_configuracion servicio_configuracion = new Servicio_configuracion();
			servicio_configuracion.setNombre(servicioADepartamentoNombre);
			Departamento_configuracion departamento = entityManager
					.find(Departamento_configuracion.class,
							this.selectedItem.getId());
			servicio_configuracion.setDepartamento(departamento);
			servicio_configuracion.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrearServicio")));
			
			entityManager.persist(servicio_configuracion);
			entityManager.flush();
			if (selectedTreenode != null
					&& (selectedTreenode.getData() instanceof DepartamentoWrapper)
			/* && ((ITreeData) selectedTreenode.getData()).isExpanded() */) {
				this.treeBuilder.updateNode(this.selectedTreenode);
			}
			servicioADepartamentoNombre = "";
		} catch (EntityExistsException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "servicio_existente");
			servicioADepartamentoNombre = "";
		}
	}

	private String servicioAServicio;

	public Boolean clearServicioAServicio() {
		servicioAServicio = "";
		return false;
	}

	public void adicionarServicioAServicio() {
		try {
			Servicio_configuracion servicio_configuracion = new Servicio_configuracion();
			servicio_configuracion.setNombre(servicioAServicio);
			Servicio_configuracion servicio_padre = entityManager.find(
					Servicio_configuracion.class, this.selectedItem.getId());
			servicio_configuracion.setServicio(servicio_padre);
			servicio_configuracion.setDepartamento(servicio_padre
					.getDepartamento());
			servicio_configuracion.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrearServicio")));
			
			entityManager.persist(servicio_configuracion);
			entityManager.flush();
			if (selectedTreenode != null
					&& (selectedTreenode.getData() instanceof ServicioWrapper)
			/* && ((ITreeData) selectedTreenode.getData()).isExpanded() */) {
				this.treeBuilder.updateNode(this.selectedTreenode);
			}
			servicioAServicio = "";
		} catch (EntityExistsException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "servicio_existente");
			servicioAServicio = "";
		}
	}

	private String especialidadAServicio;

	public Boolean clearEspecialidadAServicio() {
		especialidadAServicio = "";
		return false;
	}

	public void adicionarEspecialidadAServicio() {
		try {
			Especialidad_configuracion especialidad_configuracion = new Especialidad_configuracion();
			especialidad_configuracion.setNombre(especialidadAServicio);
			Servicio_configuracion servicio_padre = entityManager.find(
					Servicio_configuracion.class, this.selectedItem.getId());
			especialidad_configuracion.setServicio(servicio_padre);
			// servicio_configuracion.setDepartamento(servicio_padre.getDepartamento());
			
			especialidad_configuracion.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrearEspecialidad")));
			entityManager.persist(especialidad_configuracion);
			entityManager.flush();
			if (selectedTreenode != null
					&& (selectedTreenode.getData() instanceof ServicioWrapper)
			/* && ((ITreeData) selectedTreenode.getData()).isExpanded() */) {
				this.treeBuilder.updateNode(this.selectedTreenode);
			}
			especialidadAServicio = "";
		} catch (EntityExistsException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "especialidad_existente");
			especialidadAServicio = "";
		}
	}

	private String nuevoDepartamento;

	public Boolean clearNuevoDepartamento() {
		nuevoDepartamento = "";
		return false;
	}

	public void adicionarDepartamentoClinico() {
		try {
			Departamento_configuracion departamento_clinico = new Departamento_configuracion();
			departamento_clinico.setEsClinico(true);
			departamento_clinico.setNombre(nuevoDepartamento);
			departamento_clinico.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear")));
			entityManager.persist(departamento_clinico);
			entityManager.flush();
			if (selectedTreenode == null) {
				this.treeBuilder.loadData();
			}
		} catch (EntityExistsException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "departamento_existente");
			nuevoDepartamento = "";
		}
	}

	@SuppressWarnings("unchecked")
	public List<Departamento_configuracion> departments() {
		return entityManager
				.createQuery(
						"from Departamento_configuracion dep where dep.esClinico = true order by dep.nombre")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Servicio_configuracion> services(Long departmentId) {
		List<Servicio_configuracion> result = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.departamento.id = :depId and serv.servicio = null order by serv.nombre")
				.setParameter("depId", departmentId).getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Servicio_configuracion> servicesOfServices(Long serviceId) {
		List<Servicio_configuracion> result = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.servicio.id = :servId "
								+ "and serv.departamento.esClinico = true order by serv.nombre")
				.setParameter("servId", serviceId).getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Especialidad_configuracion> especialties(Long serviceId) {
		List<Especialidad_configuracion> result = entityManager
				.createQuery(
						"from Especialidad_configuracion esp where esp.servicio.id = :servId order by esp.nombre")
				.setParameter("servId", serviceId).getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public void processDrop(DropEvent dropEvent) {
		UITreeNode destNode = (dropEvent.getSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getSource() : null;
		UITree destTree = destNode != null ? destNode.getUITree() : null;
		TreeRowKey dropNodeKey = (dropEvent.getDropValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDropValue() : null;
		TreeNode droppedInNode = dropNodeKey != null ? destTree
				.getTreeNode(dropNodeKey) : null;

		UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getDraggableSource() : null;
		UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
		TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDragValue() : null;
		TreeNode draggedNode = dragNodeKey != null ? srcTree
				.getTreeNode(dragNodeKey) : null;

		ITreeData destino = (ITreeData) droppedInNode.getData();
		ITreeData dragged = ((ITreeData) draggedNode.getData());

		if (destino instanceof DepartamentoWrapper) {
			Servicio_configuracion servicio = entityManager
					.merge(((ServicioWrapper) dragged).getValue());
			Departamento_configuracion departamento = entityManager
					.merge(((DepartamentoWrapper) destino).getValue());
			servicio.setDepartamento(departamento);
			servicio.setServicio(null);
			entityManager.persist(servicio);
		}
		if (destino instanceof ServicioWrapper) {
			if (dragged instanceof ServicioWrapper) {
				Servicio_configuracion servicio = entityManager
						.merge(((ServicioWrapper) dragged).getValue());
				Servicio_configuracion servicio_destino = entityManager
						.merge(((ServicioWrapper) destino).getValue());
				servicio.setServicio(servicio_destino);
				servicio.setDepartamento(servicio_destino.getDepartamento());
				entityManager.persist(servicio);
			}
			if (dragged instanceof EspecialidadWrapper) {
				Especialidad_configuracion especialidad = entityManager
						.merge(((EspecialidadWrapper) dragged).getValue());
				Servicio_configuracion servicio_destino = entityManager
						.merge(((ServicioWrapper) destino).getValue());
				especialidad.setServicio(servicio_destino);
				entityManager.persist(especialidad);
			}
		}

		try {
			entityManager.flush();

			this.treeBuilder.updateNode(droppedInNode);
			if (draggedNode.getParent() != null)
				this.treeBuilder.updateNode(draggedNode.getParent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem(ITreeData selectedItem, TreeNode node) {
		this.selectedItem = selectedItem;
		this.selectedTreenode = node;
	}

	public void setSelectedItem(Long departId) {
		Departamento_configuracion departamento = entityManager.find(
				Departamento_configuracion.class, departId);
		this.selectedItem = new DepartamentoWrapper(departamento, false, null);
	}

	public void setSelectedItem(Long servId, Integer dummy) {
		Servicio_configuracion departamento = entityManager.find(
				Servicio_configuracion.class, servId);
		this.selectedItem = new ServicioWrapper(departamento, false, null);
	}

	public void setSelectedItem(Long espId, Integer dummy, Integer dummy2) {
		Especialidad_configuracion esp = entityManager.find(
				Especialidad_configuracion.class, espId);
		this.selectedItem = new EspecialidadWrapper(esp, false, null);
	}

	public DepartamentosYServiciosTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(DepartamentosYServiciosTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public ITreeData getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ITreeData selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getServicioADepartamentoNombre() {
		return servicioADepartamentoNombre;
	}

	public void setServicioADepartamentoNombre(
			String servicioADepartamentoNombre) {
		this.servicioADepartamentoNombre = servicioADepartamentoNombre;
	}

	public String getServicioIdToRemove() {
		return servicioIdToRemove;
	}

	public void setServicioIdToRemove(String servicioIdToRemove) {
		this.servicioIdToRemove = servicioIdToRemove;
	}

	public String getServicioAServicio() {
		return servicioAServicio;
	}

	public void setServicioAServicio(String servicioAServicio) {
		this.servicioAServicio = servicioAServicio;
	}

	public String getEspecialidadAServicio() {
		return especialidadAServicio;
	}

	public void setEspecialidadAServicio(String especialidadAServicio) {
		this.especialidadAServicio = especialidadAServicio;
	}

	public String getEspecialidadIdToRemove() {
		return especialidadIdToRemove;
	}

	public void setEspecialidadIdToRemove(String especialidadIdToRemove) {
		this.especialidadIdToRemove = especialidadIdToRemove;
	}

	public String getNuevoDepartamento() {
		return nuevoDepartamento;
	}

	public void setNuevoDepartamento(String nuevoDepartamento) {
		this.nuevoDepartamento = nuevoDepartamento;
	}

	public String getDepartamentoIdToRemove() {
		return departamentoIdToRemove;
	}

	public void setDepartamentoIdToRemove(String departamentoIdToRemove) {
		this.departamentoIdToRemove = departamentoIdToRemove;
	}

	public Departamento_configuracion getDepartamento_en_edicion() {
		return departamento_en_edicion;
	}

	public void setDepartamento_en_edicion(
			Departamento_configuracion departamento_en_edicion) {
		this.departamento_en_edicion = departamento_en_edicion;
	}

	public Servicio_configuracion getServicio_en_edicion() {
		return servicio_en_edicion;
	}

	public void setServicio_en_edicion(
			Servicio_configuracion servicio_en_edicion) {
		this.servicio_en_edicion = servicio_en_edicion;
	}

	public Especialidad_configuracion getEspecialidad_en_edicion() {
		return especialidad_en_edicion;
	}

	public void setEspecialidad_en_edicion(
			Especialidad_configuracion especialidad_en_edicion) {
		this.especialidad_en_edicion = especialidad_en_edicion;
	}

}
