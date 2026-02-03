package gehos.configuracion.clinicaldata.ubicaciones.management;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.UbicacionesTreeBuilder;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.entity.TipoUbicacion_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.EntityManager;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.DropEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeRowKey;

@Name("ubicacionesManager")
@Scope(ScopeType.CONVERSATION)
public class UbicacionesManager {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	@In(required = false, create = true, value = "ubicacionesTreeBuilder")
	UbicacionesTreeBuilder treeBuilder;

	private ITreeData selectedItem;

	@SuppressWarnings( { "rawtypes" })
	private TreeNode selectedTreenode;

	@In
	RulesParser rulesParser;
	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;

	public String onAdicionarUbicComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("inputIdent").size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	private void llenarServiciosExistentes(Ubicacion_configuracion ubicacion) {
		serviciosExistentes.clear();
		// ubicacion = entityManager.merge(ubicacion);
		@SuppressWarnings("unchecked")
		List<ServicioInEntidad_configuracion> servs = entityManager
				.createQuery(
						"select u.servicioInEntidads from Ubicacion_configuracion u where u.id = :pid")
				.setParameter("pid", ubicacion.getId()).getResultList();
		for (ServicioInEntidad_configuracion serv : servs) {
			serviciosExistentes.put(serv.getId(), serv);
		}
	}

	public void adicionarServiciosAUbicacionSelecc() {
		Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
				.getValue();
		List<ServicioInEntidad_configuracion> servs = new ArrayList<ServicioInEntidad_configuracion>(
				serviciosSeleccionados.values());
		for (ServicioInEntidad_configuracion serv : servs) {
			// ubicacion.getServicioInEntidads().add(serv);
			serv.getUbicacions().add(ubicacion);
			entityManager.persist(serv);
		}
		// entityManager.persist(ubicacion);
		entityManager.flush();
		limpiarSeleccion();
		llenarServiciosExistentes(ubicacion);
	}

	public String departamentoDadoServicio(Servicio_configuracion serv) {
		while (serv.getDepartamento() == null)
			serv = serv.getServicio();
		return serv.getDepartamento().getNombre();
	}

	public String currentEntity() {
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			while (ubicacion.getEntidad() == null)
				ubicacion = ubicacion.getUbicacion();
			return ubicacion.getEntidad().getNombre();
		}
		return "";
	}

	@SuppressWarnings( { "unchecked", "rawtypes" })
	public void limpiarSeleccion() {
		this.serviciosSeleccionados.clear();
		serviciosSeleccionadosList = new ListadoController<ServicioInEntidad_configuracion>(
				new ArrayList());
	}

	private Hashtable<Long, ServicioInEntidad_configuracion> serviciosSeleccionados = new Hashtable<Long, ServicioInEntidad_configuracion>();
	private Hashtable<Long, ServicioInEntidad_configuracion> serviciosExistentes = new Hashtable<Long, ServicioInEntidad_configuracion>();

	private Long serviceId;

	public void putOrDeleteInSelectedServices() {
		if (serviciosSeleccionados.containsKey(serviceId))
			serviciosSeleccionados.remove(serviceId);
		else {
			ServicioInEntidad_configuracion servicio = entityManager.find(
					ServicioInEntidad_configuracion.class, serviceId);
			serviciosSeleccionados.put(serviceId, servicio);
		}
		List<ServicioInEntidad_configuracion> list = new ArrayList<ServicioInEntidad_configuracion>(
				serviciosSeleccionados.values());
		serviciosSeleccionadosList = new ListadoController<ServicioInEntidad_configuracion>(
				list);
	}

	public void putOrDeleteInSelectedServices(Long serviceId) {
		if (serviciosSeleccionados.containsKey(serviceId))
			serviciosSeleccionados.remove(serviceId);
		else {
			ServicioInEntidad_configuracion servicio = entityManager.find(
					ServicioInEntidad_configuracion.class, serviceId);
			serviciosSeleccionados.put(serviceId, servicio);
		}
		List<ServicioInEntidad_configuracion> list = new ArrayList<ServicioInEntidad_configuracion>(
				serviciosSeleccionados.values());
		serviciosSeleccionadosList = new ListadoController<ServicioInEntidad_configuracion>(
				list);
	}

	@SuppressWarnings( { "unchecked", "rawtypes" })
	ListadoController<ServicioInEntidad_configuracion> serviciosSeleccionadosList = new ListadoController<ServicioInEntidad_configuracion>(
			new ArrayList());

	public ServicioInEntidad_configuracion[] serviciosSeleccionados() {
		ServicioInEntidad_configuracion[] result = serviciosSeleccionados
				.values()
				.toArray(
						new ServicioInEntidad_configuracion[serviciosSeleccionados
								.size()]);

		return result;
	}

	private Long servicioIdToDelete;
	private ServicioInEntidad_configuracion servicioToDelete;

	public void putServicioToDelete(ServicioInEntidad_configuracion servicio) {
		this.servicioToDelete = servicio;
	}

	public void putServicioToDelete(Long servicioId) {
		this.servicioIdToDelete = servicioId;
	}

	public void eliminarServicioSeleccionado() {
		Ubicacion_configuracion ubicacion = entityManager.find(
				Ubicacion_configuracion.class,
				((UbicacionWrapper) this.selectedItem).getValue().getId());
		ubicacion.getServicioInEntidads().remove(this.servicioToDelete);
		entityManager.persist(ubicacion);
		entityManager.flush();
	}

	public void eliminarServicioSeleccionadoById() {
		Ubicacion_configuracion ubicacion = entityManager.find(
				Ubicacion_configuracion.class,
				((UbicacionWrapper) this.selectedItem).getValue().getId());
		ubicacion = entityManager.merge(ubicacion);
		ServicioInEntidad_configuracion servicioInEntidad = entityManager.find(
				ServicioInEntidad_configuracion.class, servicioIdToDelete);
		servicioInEntidad = entityManager.merge(servicioInEntidad);
		ubicacion.getServicioInEntidads().remove(servicioInEntidad);
		servicioInEntidad.getUbicacions().remove(ubicacion);
		entityManager.persist(servicioInEntidad);
		entityManager.persist(ubicacion);
		entityManager.flush();
		llenarServiciosExistentes(ubicacion);
	}

	private Ubicacion_configuracion ubicacionToDelete;

	public void putUbicacionToDelete(Ubicacion_configuracion ubicacion) {
		this.ubicacionToDelete = ubicacion;
	}

	public void eliminarUbicacionSeleccionada() {
		try {
			entityManager.remove(this.ubicacionToDelete);
			entityManager.flush();
			if (selectedTreenode != null)
				this.treeBuilder.updateNode(selectedTreenode);
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "error_delete_ubicacion");
		}
	}

	private String tipoUbicacionToModify;
	private String identificadorUbicacionToModify;
	private Ubicacion_configuracion ubicacionToModify;

	public void putUbicacionToModify(Ubicacion_configuracion ubicacion) {
		this.ubicacionToModify = /*
								 * entityManager.find(
								 * Ubicacion_configuracion.class,
								 */ubicacion/* .getId()) */;
		this.tipoUbicacionToModify = this.ubicacionToModify.getTipoUbicacion()
				.getDescripcion();
		this.identificadorUbicacionToModify = this.ubicacionToModify
				.getIdentificador();
	}

	public void modificarUbicacion() {
		this.ubicacionToModify = entityManager.merge(this.ubicacionToModify);
		if (!ubicacionToModify.getTipoUbicacion().getDescripcion().equals(
				tipoUbicacionToModify)) {
			TipoUbicacion_configuracion tipoUbicacion = (TipoUbicacion_configuracion) entityManager
					.createQuery(
							"from TipoUbicacion_configuracion t where t.descripcion=:desc")
					.setParameter("desc", tipoUbicacionToModify)
					.getSingleResult();
			this.ubicacionToModify.setTipoUbicacion(tipoUbicacion);
		}
		entityManager.persist(ubicacionToModify);
		entityManager.flush();
		if (selectedTreenode != null)
			this.treeBuilder.updateNode(selectedTreenode);
	}

	private String tipoSeleccionado = "";
	private int cantidadACrear = 1;

	public void crearUbicaciones() {
		TipoUbicacion_configuracion tipoUbicacion = (TipoUbicacion_configuracion) entityManager
				.createQuery(
						"from TipoUbicacion_configuracion t where t.descripcion=:desc")
				.setParameter("desc", tipoSeleccionado).getSingleResult();
		Ubicacion_configuracion ubicacionPadre = null;
		Entidad_configuracion entidadPadre = null;
		if (this.selectedItem instanceof UbicacionWrapper) {
			ubicacionPadre = entityManager.find(Ubicacion_configuracion.class,
					((UbicacionWrapper) this.selectedItem).getValue().getId());
		}
		if (this.selectedItem instanceof EntidadWrapper) {
			entidadPadre = entityManager.find(Entidad_configuracion.class,
					((EntidadWrapper) this.selectedItem).getValue().getId());
		}
		Long cid = null;
		if (cantidadACrear > 0)
			cid = bitacora.registrarInicioDeAccion("Creando ubicaciones.");
		for (int i = 0; i < cantidadACrear; i++) {
			Ubicacion_configuracion ubicacion = new Ubicacion_configuracion();
			ubicacion.setTipoUbicacion(tipoUbicacion);
			ubicacion.setIdentificador("nueva");
			if (ubicacionPadre != null) {
				ubicacion.setUbicacion(ubicacionPadre);
			}
			if (entidadPadre != null) {
				ubicacion.setEntidad(entidadPadre);
			} else {
				ubicacion.setEntidad(ubicacionPadre.getEntidad());

			}
			ubicacion.setCid(cid);
			entityManager.persist(ubicacion);
		}
		entityManager.flush();
		if (selectedTreenode != null)
			this.treeBuilder.updateNode(selectedTreenode);
	}

	public String onComplete(String modalName) {
		if (this.tipoSeleccionado.isEmpty())
			return "return false;";
		else {
			this.tipoSeleccionado = "";
			this.cantidadACrear = 1;
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> tiposDeUbicacion() {
		return entityManager
				.createQuery(
						"select t.descripcion from TipoUbicacion_configuracion t order by t.descripcion")
				.getResultList();
	}

	public void subir() {
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			if (ubicacion.getUbicacion() != null) {
				this.selectedItem = new UbicacionWrapper(ubicacion
						.getUbicacion(), false);
			} else {
				Entidad_configuracion entidad = entityManager.find(
						Entidad_configuracion.class, ubicacion.getEntidad()
								.getId());
				this.selectedItem = new EntidadWrapper(entidad, false);
			}
		} else if (this.selectedItem instanceof EntidadWrapper) {
			this.selectedItem = null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ServicioInEntidad_configuracion> serviciosAsigAUbicacionSeleccionada() {
		List<ServicioInEntidad_configuracion> result = new ArrayList<ServicioInEntidad_configuracion>();
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			result = entityManager
					.createQuery(
							"select u.servicioInEntidads from Ubicacion_configuracion u where u.id = :pid")
					.setParameter("pid", ubicacion.getId()).getResultList();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Ubicacion_configuracion> ubicacionesHijasDeUbicacionSeleccionada() {
		List<Ubicacion_configuracion> result = new ArrayList<Ubicacion_configuracion>();
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			result = entityManager
					.createQuery(
							"from Ubicacion_configuracion u where u.ubicacion.id = :pid order by u.id")
					.setParameter("pid", ubicacion.getId()).getResultList();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Ubicacion_configuracion> ubicacionesDeEntidadSeleccionada() {
		List<Ubicacion_configuracion> result = new ArrayList<Ubicacion_configuracion>();
		if (this.selectedItem instanceof EntidadWrapper) {
			Entidad_configuracion entidad = ((EntidadWrapper) this.selectedItem)
					.getValue();
			result = entityManager.createQuery(
					"from Ubicacion_configuracion u where u.entidad.id = :pid "
							+ "and u.ubicacion = null order by u.id")
					.setParameter("pid", entidad.getId()).getResultList();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Entidad_configuracion> entidades() {

		/**
		 * @author yurien 27/03/2014 Se agrega la nueva restriccion para mostrar
		 *         las entidades que pertenecen al anillo configurado
		 * **/
		return entityManager
				.createQuery(
						"from Entidad_configuracion ent where "
								+ "ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
								// + "ent.perteneceARhio = true "
								+ "and (ent.eliminado = null or ent.eliminado = false) "
								+ "order by ent.nombre").getResultList();
	}

	public String getIdfromRules(UbicacionWrapper ubication) {
		result = new UbicacionID();
		jerarquia = new ArrayList<Ubicacion_configuracion>();

		RuleBase ruleBase = this.getParser();
		try {
			String id = this.getIdfromRules(ubication.getValue(), ruleBase);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getIdfromRules(Long id, RuleBase ruleBase) throws Exception {
		jerarquia = new ArrayList<Ubicacion_configuracion>();
		Ubicacion_configuracion temp = entityManager.find(
				Ubicacion_configuracion.class, id);
		while (temp != null) {
			jerarquia.add(0, temp);
			temp = temp.getUbicacion();
		}
		try {
			StatefulSession session = ruleBase.newStatefulSession();
			session.insert(result);
			session.insert(jerarquia);
			session.fireAllRules();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.getId();
	}

	public String getIdfromRules(Serializable ubication, RuleBase ruleBase)
			throws Exception {
		Field field = ubication.getClass().getDeclaredField("id");
		if (!Modifier.isPublic(field.getModifiers())) {
			field.setAccessible(true);
		}
		Number n = (Number) field.get(ubication);
		Long id = n.longValue();
		Ubicacion_configuracion temp = entityManager.find(
				Ubicacion_configuracion.class, id);
		while (temp != null) {
			jerarquia.add(0, temp);
			temp = temp.getUbicacion();
		}
		try {
			StatefulSession session = ruleBase.newStatefulSession();
			session.insert(result);
			session.insert(jerarquia);
			session.fireAllRules();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.getId();
	}

	public RuleBase getParser() {
		RuleBase ruleBase = null;
		try {
			ruleBase = rulesParser.readRule(
					"/configuracion/ubicaciones/id_generator.drl",
					RulesDirectoryBase.business_rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ruleBase;
	}

	public ITreeData getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object param) {
		if (param instanceof Entidad_configuracion)
			this.selectedItem = new EntidadWrapper(
					(Entidad_configuracion) param, false);
		if (param instanceof Ubicacion_configuracion) {
			this.selectedItem = new UbicacionWrapper(
					(Ubicacion_configuracion) param, false);
			llenarServiciosExistentes((Ubicacion_configuracion) param);
		}
	}

	@SuppressWarnings( { "rawtypes" })
	public void setSelectedItem(ITreeData funct, TreeNode node) {
		this.selectedItem = funct;
		if (this.selectedItem != null
				&& this.selectedItem.getValue() instanceof Ubicacion_configuracion)
			llenarServiciosExistentes((Ubicacion_configuracion) (this.selectedItem
					.getValue()));
		this.selectedTreenode = node;
	}

	@SuppressWarnings( { "rawtypes" })
	public void processDrop(DropEvent dropEvent) {
		UITreeNode destNode = (dropEvent.getSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getSource()
				: null;
		UITree destTree = destNode != null ? destNode.getUITree() : null;
		TreeRowKey dropNodeKey = (dropEvent.getDropValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDropValue()
				: null;
		TreeNode droppedInNode = dropNodeKey != null ? destTree
				.getTreeNode(dropNodeKey) : null;

		UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent
				.getDraggableSource()
				: null;
		UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
		TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent
				.getDragValue()
				: null;
		TreeNode draggedNode = dragNodeKey != null ? srcTree
				.getTreeNode(dragNodeKey) : null;

		if (droppedInNode.getData() instanceof UbicacionWrapper) {
			Ubicacion_configuracion funcDestino = (Ubicacion_configuracion) (((ITreeData) droppedInNode
					.getData()).getValue());
			funcDestino = entityManager.find(Ubicacion_configuracion.class,
					funcDestino.getId());

			Ubicacion_configuracion funcDragged = (Ubicacion_configuracion) (((ITreeData) draggedNode
					.getData()).getValue());
			funcDragged = entityManager.find(Ubicacion_configuracion.class,
					funcDragged.getId());

			if (funcDestino.getId() == funcDragged.getId()) {
				return;
			}

			funcDragged.setUbicacion(funcDestino);
			funcDestino.getUbicacions().add(funcDragged);

			entityManager.persist(funcDestino);
			entityManager.persist(funcDragged);
			entityManager.flush();
		}
		if (droppedInNode.getData() instanceof EntidadWrapper) {
			Entidad_configuracion entDestino = (Entidad_configuracion) (((ITreeData) droppedInNode
					.getData()).getValue());
			Entidad_configuracion hospDestino = entityManager.find(
					Entidad_configuracion.class, entDestino.getId());

			Ubicacion_configuracion funcDragged = (Ubicacion_configuracion) (((ITreeData) draggedNode
					.getData()).getValue());
			funcDragged = entityManager.find(Ubicacion_configuracion.class,
					funcDragged.getId());

			funcDragged.setEntidad(hospDestino);
			funcDragged.setUbicacion(null);
			hospDestino.getUbicacions().add(funcDragged);

			entityManager.persist(hospDestino);
			entityManager.persist(funcDragged);
			entityManager.flush();
		}

		this.treeBuilder.updateNode(droppedInNode);
		if (draggedNode.getParent() != null)
			this.treeBuilder.updateNode(draggedNode.getParent());
	}

	public UbicacionesTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(UbicacionesTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public String getTipoSeleccionado() {
		return tipoSeleccionado;
	}

	public void setTipoSeleccionado(String tipoSeleccionado) {
		this.tipoSeleccionado = tipoSeleccionado;
	}

	public int getCantidadACrear() {
		return cantidadACrear;
	}

	public void setCantidadACrear(int cantidadACrear) {
		this.cantidadACrear = cantidadACrear;
	}

	public Ubicacion_configuracion getUbicacionToModify() {
		return ubicacionToModify;
	}

	public void setUbicacionToModify(Ubicacion_configuracion ubicacionToModify) {
		this.ubicacionToModify = ubicacionToModify;
	}

	public String getTipoUbicacionToModify() {
		return tipoUbicacionToModify;
	}

	public void setTipoUbicacionToModify(String tipoUbicacionToModify) {
		this.tipoUbicacionToModify = tipoUbicacionToModify;
	}

	public String getIdentificadorUbicacionToModify() {
		return identificadorUbicacionToModify;
	}

	public void setIdentificadorUbicacionToModify(
			String identificadorUbicacionToModify) {
		this.identificadorUbicacionToModify = identificadorUbicacionToModify;
	}

	public Hashtable<Long, ServicioInEntidad_configuracion> getServiciosSeleccionados() {
		return serviciosSeleccionados;
	}

	public void setServiciosSeleccionados(
			Hashtable<Long, ServicioInEntidad_configuracion> serviciosSeleccionados) {
		this.serviciosSeleccionados = serviciosSeleccionados;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public ListadoController<ServicioInEntidad_configuracion> getServiciosSeleccionadosList() {
		return serviciosSeleccionadosList;
	}

	public void setServiciosSeleccionadosList(
			ListadoController<ServicioInEntidad_configuracion> serviciosSeleccionadosList) {
		this.serviciosSeleccionadosList = serviciosSeleccionadosList;
	}

	public Hashtable<Long, ServicioInEntidad_configuracion> getServiciosExistentes() {
		return serviciosExistentes;
	}

	public void setServiciosExistentes(
			Hashtable<Long, ServicioInEntidad_configuracion> serviciosExistentes) {
		this.serviciosExistentes = serviciosExistentes;
	}

}
