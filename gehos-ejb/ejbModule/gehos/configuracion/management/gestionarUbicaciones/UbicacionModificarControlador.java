package gehos.configuracion.management.gestionarUbicaciones;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionID;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.TipoUbicacion_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Name("ubicacionModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class UbicacionModificarControlador {

	@In
	IBitacora bitacora;
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	LocaleSelector localeSelector;
	@In
	RulesParser rulesParser;

	private Long ubicacionId;
	private String tipoUbicacionSeleccionada;
	private String identificador;
	private String entidadSeleccionada;
	private Entidad_configuracion entidad;// Entidad seleccionada
	private String fatherLocationSelected;
	private Ubicacion_configuracion fatherLocation;// father ubication
	private Ubicacion_configuracion ubicacion;

	// other functions
	private Long cid = -1l;
	private int error;
	private boolean errorLoadData;
	RuleBase ruleBase;
	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;
	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;

	// Methods-----------------------------------------------------
	/*
	 * @Begin(flushMode = FlushModeType.MANUAL, join = true) public void
	 * setUbicacionId(Long ubicacionId) { errorLoadData = false;
	 * this.ubicacionId = ubicacionId; ubicacion = new
	 * Ubicacion_configuracion();
	 * 
	 * try { ubicacion = (Ubicacion_configuracion) entityManager.createQuery(
	 * "select u from Ubicacion_configuracion u where u.id =:id " +
	 * "and (u.eliminado = false or u.eliminado = null)") .setParameter("id",
	 * ubicacionId) .getSingleResult();
	 * 
	 * // load data entidadSeleccionada = ubicacion.getEntidad().getNombre();
	 * tipoUbicacionSeleccionada =
	 * ubicacion.getTipoUbicacion().getDescripcion(); identificador =
	 * ubicacion.getIdentificador();
	 * 
	 * cargarUbicaciones(); loadData();
	 * 
	 * if (this.ubicacion.getUbicacion() == null) { this.fatherLocation = new
	 * Ubicacion_configuracion(); fatherLocationSelected = ""; } else {
	 * this.fatherLocation = this.ubicacion.getUbicacion();
	 * fatherLocationSelected =
	 * getUbicationIdfromRules(this.fatherLocation.getId()); }
	 * 
	 * if (cid == -1) { cid =
	 * bitacora.registrarInicioDeAccion("Modificando ubicacion");
	 * ubicacion.setCid(cid); }
	 * 
	 * } catch (NoResultException e) { errorLoadData = true; } catch (Exception
	 * e) { errorLoadData = true; } }
	 */

	// return the list of entities
	@SuppressWarnings("unchecked")
	public List<String> listadoEntidades() {
		try {
			return entityManager
					.createQuery(
							"select ent.nombre from Entidad_configuracion ent "
									+ "where (ent.eliminado = false or ent.eliminado = null) and ent.perteneceARhio = true "
									+ "order by ent.nombre").getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return the list of ubication type
	@SuppressWarnings("unchecked")
	public List<String> listadoTipoUbicacion() {
		try {
			return entityManager
					.createQuery(
							"select tipo.descripcion from TipoUbicacion_configuracion tipo "
									+ "where (tipo.eliminado = false or tipo.eliminado = null) "
									+ "order by tipo.descripcion")
					.getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	/*
	 * select the Entity where be the new ubication. The value of selected
	 * entity are pased directly to the variable
	 */
	public void selectEntidadLocation() {
		this.fatherLocation = new Ubicacion_configuracion();
		this.fatherLocationSelected = "";
	}

	// select the father ubication
	public void putNewLocation(UbicacionWrapper ubic) {
		this.fatherLocation = ubic.getValue();
		this.fatherLocationSelected = this
				.getUbicationIdfromRules(this.fatherLocation.getId());
	}

	// return the ubication selected taking into account the bussines ruler
	public String getUbicationIdfromRules(Long ubicationId) {
		result = new UbicacionID();
		jerarquia = new ArrayList<Ubicacion_configuracion>();
		if (ruleBase == null)
			ruleBase = this.getParser();
		try {
			String id = this.getIdfromRules(ubicationId, ruleBase);
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

	// execute whene a node collapse or expand
	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		this.selectedNode = tree.getTreeNode();
		collapseOrExpand(this.selectedNode, true);
	}

	@SuppressWarnings("unchecked")
	private boolean collapseOrExpand(TreeNode selected, boolean putLoadingNode) {
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			if (putLoadingNode) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				selected.addChild("...", loadingNode);
			}
			((ITreeData) selected.getData()).setExpanded(false);
		}

		else {
			if (selected.getData() instanceof UbicacionWrapper) {
				return expandUbicacion(selected);
			} else if (selected.getData() instanceof EntidadWrapper) {
				return expandEntidad(selected);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void prune(TreeNode selected) {
		ArrayList<Integer> hashcodes = new ArrayList<Integer>();
		for (Iterator iterator = selected.getChildren(); iterator.hasNext();) {
			java.util.Map.Entry obj = (java.util.Map.Entry) iterator.next();
			TreeNode node = (TreeNode) obj.getValue();
			prune(node);
			hashcodes.add(node.getData().hashCode());
		}
		for (int i = 0; i < hashcodes.size(); i++) {
			selected.removeChild(hashcodes.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	private boolean expandUbicacion(TreeNode selected) {
		UbicacionWrapper value = ((UbicacionWrapper) selected.getData());
		selected.removeChild("...");

		List<Ubicacion_configuracion> ubicaciones = entityManager
				.createQuery(
						"from Ubicacion_configuracion f where f.ubicacion.id = :pid "
								+ "and (f.eliminado = false or f.eliminado = null) "
								+ "and (f.ubicacion.eliminado = false or f.ubicacion.eliminado = null)")
				.setParameter("pid", value.getValue().getId()).getResultList();

		for (int i = 0; i < ubicaciones.size(); i++) {
			Long childCount = (Long) entityManager
					.createQuery(
							"select count(*) from Ubicacion_configuracion f where f.ubicacion.id = :pid "
									+ "and (f.eliminado = false or f.eliminado = null) "
									+ "and (f.ubicacion.eliminado = false or f.ubicacion.eliminado = null)")
					.setParameter("pid", ubicaciones.get(i).getId())
					.getSingleResult();
			if (childCount > 0) {
				TreeNode categoriaNode = new TreeNodeImpl();
				UbicacionWrapper w = new UbicacionWrapper(ubicaciones.get(i),
						false);
				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);

				selected.addChild(w.hashCode(), categoriaNode);
			} else {
				TreeNode funcionalidadNode = new TreeNodeImpl();
				UbicacionWrapper w = new UbicacionWrapper(ubicaciones.get(i),
						false);
				funcionalidadNode.setData(w);

				selected.addChild(w.hashCode(), funcionalidadNode);
			}

		}
		((ITreeData) selected.getData()).setExpanded(true);
		return ubicaciones.size() > 0;
	}

	@SuppressWarnings("unchecked")
	private boolean expandEntidad(TreeNode selected) {
		EntidadWrapper value = ((EntidadWrapper) selected.getData());
		selected.removeChild("...");

		List<Ubicacion_configuracion> ubicaciones = entityManager
				.createQuery(
						"from Ubicacion_configuracion u where u.entidad.id = :pid and u.ubicacion = null "
								+ "and (u.eliminado = false or u.eliminado = null)")
				.setParameter("pid", value.getValue().getId()).getResultList();

		for (int i = 0; i < ubicaciones.size(); i++) {
			if (ubicaciones.get(i).getUbicacions().size() > 0) {
				TreeNode categoriaNode = new TreeNodeImpl();
				UbicacionWrapper w = new UbicacionWrapper(ubicaciones.get(i),
						false);
				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);

				selected.addChild(w.hashCode(), categoriaNode);
			} else {
				TreeNode funcionalidadNode = new TreeNodeImpl();
				UbicacionWrapper w = new UbicacionWrapper(ubicaciones.get(i),
						false);
				funcionalidadNode.setData(w);

				selected.addChild(w.hashCode(), funcionalidadNode);
			}
		}
		((ITreeData) selected.getData()).setExpanded(true);
		return ubicaciones.size() > 0;
	}

	// load the selected entity in the ComboBox
	public void cargarUbicaciones() {
		try {
			Long entidadid = null;
			if (entidad != null)
				entidadid = entidad.getId();
			entidad = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.nombre =:entidadNombre "
									+ "and (e.eliminado = false or e.eliminado = null)")
					.setParameter("entidadNombre", entidadSeleccionada)
					.getSingleResult();
			loadData();
			if (entidadid != null && !entidadid.equals(entidad.getId()))
				selectEntidadLocation();
		} catch (NoResultException e) {

		}
	}

	// lodad tree data (The necessary data to create the TreeView)
	@SuppressWarnings("unchecked")
	public void loadData() {
		treeData = new TreeNodeImpl();

		List<Ubicacion_configuracion> ubicaciones = entityManager
				.createQuery(
						"from Ubicacion_configuracion u where u.entidad.id = :pid and u.ubicacion = null "
								+ "and (u.eliminado = false or u.eliminado = null) "
								+ "and (u.entidad.eliminado = false or u.entidad.eliminado = null)")
				.setParameter("pid", entidad.getId()).getResultList();

		for (int i = 0; i < ubicaciones.size(); i++) {
			if (ubicaciones.get(i).getUbicacions().size() > 0) {
				TreeNode categoriaNode = new TreeNodeImpl();
				UbicacionWrapper w = new UbicacionWrapper(ubicaciones.get(i),
						false);
				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);

				treeData.addChild(w.hashCode(), categoriaNode);
			} else {
				TreeNode funcionalidadNode = new TreeNodeImpl();
				UbicacionWrapper w = new UbicacionWrapper(ubicaciones.get(i),
						false);
				funcionalidadNode.setData(w);

				treeData.addChild(w.hashCode(), funcionalidadNode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void modificar() {
		error = 0;
		try {
			// cargando datos
			TipoUbicacion_configuracion tipoUbicacion = (TipoUbicacion_configuracion) entityManager
					.createQuery(
							"select t from TipoUbicacion_configuracion t where t.descripcion =:tipoUbicacionDescripcion "
									+ "and (t.eliminado = false or t.eliminado = null)")
					.setParameter("tipoUbicacionDescripcion",
							tipoUbicacionSeleccionada).getSingleResult();

			// validacion existencia
			List<Ubicacion_configuracion> lu = new ArrayList<Ubicacion_configuracion>();
			if (this.fatherLocation.getIdentificador() == null) {
				lu = entityManager
						.createQuery(
								"select u from Ubicacion_configuracion u "
										+ "where u.identificador =:identificadorU "
										+ "and u.ubicacion = null "
										+ "and u.entidad.id =:entidadId "
										+ "and u.tipoUbicacion.id =:tipoUbicacionId "
										+ "and u.id <>:ubicacionId "
										+ "and (u.eliminado = false or u.eliminado = null) "
										+ "and (u.entidad.eliminado = false or u.entidad.eliminado = null) "
										+ "and (u.tipoUbicacion.eliminado = false or u.tipoUbicacion.eliminado = null)")
						.setParameter("identificadorU",
								this.identificador.trim())
						.setParameter("entidadId", entidad.getId())
						.setParameter("tipoUbicacionId", tipoUbicacion.getId())
						.setParameter("ubicacionId", this.ubicacion.getId())
						.getResultList();
			} else {
				lu = entityManager
						.createQuery(
								"select u from Ubicacion_configuracion u "
										+ "where u.identificador =:identificadorU "
										+ "and u.ubicacion.id =:ubicacionPadreId "
										+ "and u.entidad.id =:entidadId "
										+ "and u.tipoUbicacion.id =:tipoUbicacionId "
										+ "and u.id <>:ubicacionId "
										+ "and (u.eliminado = false or u.eliminado = null) "
										+ "and (u.entidad.eliminado = false or u.entidad.eliminado = null) "
										+ "and (u.tipoUbicacion.eliminado = false or u.tipoUbicacion.eliminado = null)")
						.setParameter("ubicacionPadreId",
								this.fatherLocation.getId())
						.setParameter("identificadorU", this.identificador)
						.setParameter("entidadId", entidad.getId())
						.setParameter("tipoUbicacionId", tipoUbicacion.getId())
						.setParameter("ubicacionId", this.ubicacion.getId())
						.getResultList();
			}
			if (lu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("error",
						Severity.ERROR, "entidadExistente");
				error = 1;
			}

			// asignando datos
			this.ubicacion.setIdentificador(identificador.trim());
			this.ubicacion.setEliminado(false);
			this.ubicacion.setTipoUbicacion(tipoUbicacion);
			this.ubicacion.setEntidad(entidad);
			if (this.fatherLocation.getIdentificador() != null)
				this.ubicacion.setUbicacion(this.fatherLocation);
			else
				this.ubicacion.setUbicacion(null);

			// persistiendo
			entityManager.persist(this.ubicacion);
			entityManager.flush();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR,
					"Una ubicación no puede ser hija de ella misma.");
		}
	}

	// Properties -------------------------------------------------
	public String getTipoUbicacionSeleccionada() {
		return tipoUbicacionSeleccionada;
	}

	public void setTipoUbicacionSeleccionada(String tipoUbicacionSeleccionada) {
		this.tipoUbicacionSeleccionada = tipoUbicacionSeleccionada;
	}

	public String getEntidadSeleccionada() {
		return entidadSeleccionada;
	}

	public void setEntidadSeleccionada(String entidadSeleccionada) {
		this.entidadSeleccionada = entidadSeleccionada;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public String getFatherLocationSelected() {
		return fatherLocationSelected;
	}

	public void setFatherLocationSelected(String fatherLocationSelected) {
		this.fatherLocationSelected = fatherLocationSelected;
	}

	public Ubicacion_configuracion getFatherLocation() {
		return fatherLocation;
	}

	public void setFatherLocation(Ubicacion_configuracion fatherLocation) {
		this.fatherLocation = fatherLocation;
	}

	public Ubicacion_configuracion getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(Ubicacion_configuracion ubicacion) {
		this.ubicacion = ubicacion;
	}

	@SuppressWarnings("unchecked")
	public TreeNode getTreeData() {
		return treeData;
	}

	@SuppressWarnings("unchecked")
	public void setTreeData(TreeNode treeData) {
		this.treeData = treeData;
	}

	@SuppressWarnings("unchecked")
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}

	public Long getUbicacionId() {
		return ubicacionId;
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setUbicacionId(Long ubicacionId) {
		if (this.ubicacionId == null || !this.ubicacionId.equals(ubicacionId)) {
			errorLoadData = false;
			this.ubicacionId = ubicacionId;
			// ubicacion = new Ubicacion_configuracion();
			ubicacion = entityManager.find(Ubicacion_configuracion.class,
					ubicacionId);

			try {
				identificador = ubicacion.getIdentificador();
				tipoUbicacionSeleccionada = ubicacion.getTipoUbicacion()
						.getDescripcion();
				// load data
				entidadSeleccionada = ubicacion.getEntidad().getNombre();

				ubicacion = (Ubicacion_configuracion) entityManager
						.createQuery(
								"select u from Ubicacion_configuracion u where u.id =:id "
										+ "and (u.eliminado = false or u.eliminado = null)")
						.setParameter("id", ubicacionId).getSingleResult();

				cargarUbicaciones();
				loadData();

				if (this.ubicacion.getUbicacion() == null) {
					this.fatherLocation = new Ubicacion_configuracion();
					fatherLocationSelected = "";
				} else {
					this.fatherLocation = this.ubicacion.getUbicacion();
					fatherLocationSelected = getUbicationIdfromRules(this.fatherLocation
							.getId());
				}

				if (cid == -1) {
					cid = bitacora
							.registrarInicioDeAccion("Modificando ubicacion");
					ubicacion.setCid(cid);
				}

			} catch (NoResultException e) {
				errorLoadData = true;
			} catch (Exception e) {
				errorLoadData = true;
			}
		}
	}
}
