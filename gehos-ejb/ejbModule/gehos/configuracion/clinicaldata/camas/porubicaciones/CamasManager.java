package gehos.configuracion.clinicaldata.camas.porubicaciones;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.camas.porubicaciones.treebuilders.CamasTreeBuilder;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionID;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;
import gehos.configuracion.management.entity.Cama_configuracion;
import gehos.configuracion.management.entity.CategoriaCama_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EstadoCama_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoCama_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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

@Name("camasManager")
@Scope(ScopeType.CONVERSATION)
public class CamasManager {

	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;
	@In
	FacesMessages facesMessages;

	@In(required = false, create = true, value = "camasTreeBuilder")
	CamasTreeBuilder treeBuilder;

	private ITreeData selectedItem;

	@SuppressWarnings({ "rawtypes" })
	private TreeNode selectedTreenode;

	@In
	RulesParser rulesParser;
	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;

	private String camasDistributionChanges;

	public void saveCamasDistribution() {
		String[] changes = this.camasDistributionChanges.split("-");
		for (int i = 0; i < changes.length; i++) {
			String change = changes[i];
			if (!change.isEmpty()) {
				String[] changeParts = change.split(",");
				Long id = Long.parseLong(changeParts[0].trim());
				Integer top = Integer.parseInt(changeParts[1].trim());
				Integer left = Integer.parseInt(changeParts[2].trim());
				Cama_configuracion cama = entityManager.find(
						Cama_configuracion.class, id);
				cama.setX(left);
				cama.setY(top);
				entityManager.persist(cama);
			}
		}
		entityManager.flush();
	}

	private Long camaId;
	private Integer camaX;
	private Integer camaY;

	public void changeCamaPosition() {
		Cama_configuracion cama = entityManager.find(Cama_configuracion.class,
				this.camaId);
		cama.setX(this.camaX);
		cama.setY(this.camaY);
		entityManager.persist(cama);
		entityManager.flush();
	}

	@SuppressWarnings("unchecked")
	public List<Cama_configuracion> camasPorUbicacion() {
		List<Cama_configuracion> camas = new ArrayList<Cama_configuracion>();
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			camas = entityManager
					.createQuery(
							"from Cama_configuracion c where c.ubicacion.id = :pid")
					.setParameter("pid", ubicacion.getId()).getResultList();
		}
		return camas;
	}

	private Cama_configuracion camaToDelete;

	private Cama_configuracion camaToModify;

	public void putCamaToDelete(Cama_configuracion cama) {
		this.camaToDelete = cama;
	}

	public void putCamaToDelete(Long camaId) {
		this.camaToDelete = entityManager
				.find(Cama_configuracion.class, camaId);
	}

	public void putCamaToModify(Cama_configuracion cama) {
		this.camaToModify = entityManager.merge(cama);
		this.servicioEnModificacion = cama.getServicioInEntidadByIdServicio()
				.getServicio().getNombre();
		this.tipoCamaEnModificacion = cama.getTipoCama().getValor();
	}

	public void eliminarCamaSeleccionada() {
		try {
			camaToDelete = entityManager.find(Cama_configuracion.class,
					camaToDelete.getId());
			entityManager.remove(camaToDelete);
			entityManager.flush();
			if (selectedTreenode != null)
				this.treeBuilder.updateNode(selectedTreenode);
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "error_delete_cama");
		}
	}

	private Hashtable<Long, Boolean> camasModificadas = new Hashtable<Long, Boolean>();

	public boolean esCamaModificada(Long camaId) {
		if (camasModificadas.containsKey(camaId)) {
			return true;
		}
		return false;
	}

	public boolean cleanCamasModificadas() {
		this.camasModificadas.clear();
		return false;
	}

	private String servicioEnModificacion = "";
	private String tipoCamaEnModificacion = "";

	public void modifyCama() {
		long cid = bitacora.registrarInicioDeAccion("Modificando cama..");
		if (!this.camaToModify.getTipoCama().getValor()
				.equals(tipoCamaEnModificacion)) {
			TipoCama_configuracion tipoCama = (TipoCama_configuracion) entityManager
					.createQuery(
							"from TipoCama_configuracion t where t.valor = :tipoCama")
					.setParameter("tipoCama", tipoCamaEnModificacion)
					.getSingleResult();
			this.camaToModify.setTipoCama(tipoCama);
		}
		if (!this.camaToModify.getServicioInEntidadByIdServicio().getServicio()
				.getNombre().equals(servicioEnModificacion)) {
			Ubicacion_configuracion ubicacion = entityManager.find(
					Ubicacion_configuracion.class,
					((UbicacionWrapper) this.selectedItem).getValue().getId());

			Set<ServicioInEntidad_configuracion> list = ubicacion
					.getServicioInEntidads();
			ServicioInEntidad_configuracion servicio = null;
			for (ServicioInEntidad_configuracion serv : list) {
				if (serv.getServicio().getNombre()
						.equals(servicioEnModificacion)) {
					servicio = serv;
					break;
				}
			}
			this.camaToModify.setServicioInEntidadByIdServicio(servicio);
		}
		camaToModify.setCid(cid);
		entityManager.persist(camaToModify);
		entityManager.flush();
		camasModificadas.put(camaToModify.getId(), true);
		if (selectedTreenode != null)
			this.treeBuilder.updateNode(selectedTreenode);
	}

	private String servicioSeleccionado = "";
	private String tipoSeleccionado = "";
	private int cantidadACrear = 1;

	@SuppressWarnings("unchecked")
	public void crearCamas() {
		Ubicacion_configuracion ubicacion = entityManager.find(
				Ubicacion_configuracion.class,
				((UbicacionWrapper) this.selectedItem).getValue().getId());

		List<Cama_configuracion> camas = entityManager
				.createQuery(
						"from Cama_configuracion c where c.ubicacion.id = :pid")
				.setParameter("pid", ubicacion.getId()).getResultList();

		HashMap<String, Boolean> matrix = new HashMap<String, Boolean>();
		for (Cama_configuracion cama : camas) {
			String key = cama.getX() + "-" + cama.getY();
			matrix.put(key, true);
		}

		TipoCama_configuracion tipoCama = (TipoCama_configuracion) entityManager
				.createQuery(
						"from TipoCama_configuracion t where t.valor = :tipoCama")
				.setParameter("tipoCama", tipoSeleccionado).getSingleResult();
		CategoriaCama_configuracion categoria = (CategoriaCama_configuracion) entityManager
				.createQuery(
						"from CategoriaCama_configuracion c where c.valor = :cat")
				.setParameter("cat", "Ordinaria").getSingleResult();
		EstadoCama_configuracion estado = (EstadoCama_configuracion) entityManager
				.createQuery(
						"from EstadoCama_configuracion e where e.codigo = :est")
				.setParameter("est", "desocupada").getSingleResult();

		Set<ServicioInEntidad_configuracion> list = ubicacion
				.getServicioInEntidads();
		ServicioInEntidad_configuracion servicio = null;
		for (ServicioInEntidad_configuracion serv : list) {
			if (serv.getServicio().getNombre().equals(servicioSeleccionado)) {
				servicio = serv;
				break;
			}
		}

		Integer last_x = 0;
		Integer last_y = 0;
		for (int i = 0; i < cantidadACrear; i++) {
			Cama_configuracion cama = new Cama_configuracion();
			cama.setTipoCama(tipoCama);
			cama.setDescripcion("nueva");
			cama.setUbicacion(ubicacion);

			calculateCamaCoordenates(cama, matrix, last_x, last_y);

			cama.setCategoriaCama(categoria);
			cama.setEstadoCama(estado);
			cama.setServicioInEntidadByIdServicio(servicio);
			ubicacion.getCamas().add(cama);
			entityManager.persist(cama);
			camasModificadas.put(cama.getId(), true);
		}
		entityManager.persist(ubicacion);
		entityManager.flush();

		if (selectedTreenode != null)
			this.treeBuilder.updateNode(selectedTreenode);
	}

	private void calculateCamaCoordenates(Cama_configuracion cama,
			HashMap<String, Boolean> matrix, int last_x, int last_y) {
		int y = last_y;
		boolean coordenatesFound = false;
		while (y < 7 && !coordenatesFound) {
			int x = last_x;
			while (x < 8 && !coordenatesFound) {
				String key = x + "-" + y;
				if (!matrix.containsKey(key)) {
					matrix.put(key, true);
					cama.setX(x);
					last_x = x;
					cama.setY(y);
					last_y = y;
					coordenatesFound = true;
				}
				x++;
			}
			y++;
			last_x = 0;
		}
		if (!coordenatesFound) {
			cama.setX(0);
			cama.setY(0);
		}
	}

	public String onComplete(String modalName) {
		if (this.tipoSeleccionado.isEmpty()
				|| this.servicioSeleccionado.isEmpty())
			return "return false;";
		else {
			this.tipoSeleccionado = "";
			this.servicioSeleccionado = "";
			this.cantidadACrear = 1;
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> serviciosDeLaUbicacionSeleccionada() {
		List<String> result = new ArrayList<String>();
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			List<ServicioInEntidad_configuracion> list = entityManager
					.createQuery(
							"select u.servicioInEntidads from Ubicacion_configuracion u where u.id = :ubid")
					.setParameter("ubid", ubicacion.getId()).getResultList();
			for (ServicioInEntidad_configuracion servicioInEntidad : list) {
				result.add(servicioInEntidad.getServicio().getNombre());
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<String> tiposDeCamas() {
		return entityManager.createQuery(
				"select t.valor from TipoCama_configuracion t").getResultList();
	}

	public boolean showMapa() {
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			Long camas = (Long) entityManager
					.createQuery(
							"select count(*) from Cama_configuracion c where c.ubicacion.id = :pid")
					.setParameter("pid", ubicacion.getId()).getSingleResult();
			return camas > 0;
		}
		return false;
	}

	public void subir() {
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			if (ubicacion.getUbicacion() != null) {
				this.selectedItem = new UbicacionWrapper(
						ubicacion.getUbicacion(), false);
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
	public List<Ubicacion_configuracion> ubicacionesHijasDeUbicacionSeleccionada() {
		List<Ubicacion_configuracion> result = new ArrayList<Ubicacion_configuracion>();
		if (this.selectedItem instanceof UbicacionWrapper) {
			Ubicacion_configuracion ubicacion = ((UbicacionWrapper) this.selectedItem)
					.getValue();
			result = entityManager
					.createQuery(
							"from Ubicacion_configuracion u where u.ubicacion.id = :pid")
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
			result = entityManager
					.createQuery(
							"from Ubicacion_configuracion u where u.entidad.id = :pid and u.ubicacion = null")
					.setParameter("pid", entidad.getId()).getResultList();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Entidad_configuracion> entidades() {
		/**
		 * @author yurien 27/03/2014
		 * Se cambia la restriccion para que busque las entidades
		 * que pertenecen al anillo configurado
		 * **/
		return entityManager
				.createQuery(
						"from Entidad_configuracion ent "
								+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//						        + "where ent.perteneceARhio = true "
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
		// Long id = field.getLong(ubication);
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
		if (param instanceof Ubicacion_configuracion)
			this.selectedItem = new UbicacionWrapper(
					(Ubicacion_configuracion) param, false);
	}

	@SuppressWarnings({ "rawtypes" })
	public void setSelectedItem(ITreeData funct, TreeNode node) {
		this.selectedItem = funct;
		this.selectedTreenode = node;
	}

	@SuppressWarnings({ "rawtypes" })
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

		Ubicacion_configuracion ubicDestino = (Ubicacion_configuracion) (((ITreeData) droppedInNode
				.getData()).getValue());
		Ubicacion_configuracion ubicacion = entityManager.find(
				Ubicacion_configuracion.class, ubicDestino.getId());

		Cama_configuracion camaDragged = (Cama_configuracion) (((ITreeData) draggedNode
				.getData()).getValue());
		camaDragged = entityManager.find(Cama_configuracion.class,
				camaDragged.getId());

		camaDragged.setUbicacion(ubicacion);
		ubicacion.getCamas().add(camaDragged);

		entityManager.persist(ubicacion);
		entityManager.persist(camaDragged);
		entityManager.flush();

		this.treeBuilder.updateNode(droppedInNode);
		if (draggedNode.getParent() != null)
			this.treeBuilder.updateNode(draggedNode.getParent());
	}

	public CamasTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(CamasTreeBuilder treeBuilder) {
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

	public Long getCamaId() {
		return camaId;
	}

	public void setCamaId(Long camaId) {
		this.camaId = camaId;
	}

	public Integer getCamaX() {
		return camaX;
	}

	public void setCamaX(Integer camaX) {
		this.camaX = camaX;
	}

	public Integer getCamaY() {
		return camaY;
	}

	public void setCamaY(Integer camaY) {
		this.camaY = camaY;
	}

	public String getCamasDistributionChanges() {
		return camasDistributionChanges;
	}

	public void setCamasDistributionChanges(String camasDistributionChanges) {
		this.camasDistributionChanges = camasDistributionChanges;
	}

	public String getServicioSeleccionado() {
		return servicioSeleccionado;
	}

	public void setServicioSeleccionado(String servicioSeleccionado) {
		this.servicioSeleccionado = servicioSeleccionado;
	}

	public Cama_configuracion getCamaToModify() {
		return camaToModify;
	}

	public void setCamaToModify(Cama_configuracion camaToModify) {
		this.camaToModify = camaToModify;
	}

	public String getServicioEnModificacion() {
		return servicioEnModificacion;
	}

	public void setServicioEnModificacion(String servicioEnModificacion) {
		this.servicioEnModificacion = servicioEnModificacion;
	}

	public String getTipoCamaEnModificacion() {
		return tipoCamaEnModificacion;
	}

	public void setTipoCamaEnModificacion(String tipoCamaEnModificacion) {
		this.tipoCamaEnModificacion = tipoCamaEnModificacion;
	}

}
