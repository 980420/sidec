package gehos.configuracion.management.gestionarCama;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
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

import javax.persistence.EntityManager;

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

@Name("camaCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class CamaCrearControlador {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	LocaleSelector localeSelector;
	@In
	RulesParser rulesParser;
	@In
	IBitacora bitacora;

	private String tipoSeleccionado;
	private String servicioSeleccionado;
	private String categoriaSeleccionada;
	private String estadoSeleccionado;
	private String descripcion;

	private String entidadSeleccionada;
	private Entidad_configuracion entidad = new Entidad_configuracion();

	private String locationSelected;
	private Ubicacion_configuracion location = new Ubicacion_configuracion();

	private List<String> servicios = new ArrayList<String>();
	private Cama_configuracion cama;

	// other functions
	private Long cid = -1l;
	private int error;
	RuleBase ruleBase;
	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;
	List<Ubicacion_configuracion> jerarquia;
	UbicacionID result = new UbicacionID();

	// Methods-------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void begin() {
		cama = new Cama_configuracion();
		cama.setEliminado(false);

		locationSelected = "";
		tipoSeleccionado = "";
		servicioSeleccionado = "";
		categoriaSeleccionada = "";
		estadoSeleccionado = "";
		descripcion = "";
		entidadSeleccionada = "";

		try {
			if (cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion("Creando ubicacion");
				cama.setCid(cid);
			}
		} catch (Exception e) {

		}
	}

	public List<String> serviciosDadaUbicacion() {
		List<String> result = new ArrayList<String>();
		if (this.location != null) {
			for (ServicioInEntidad_configuracion string : this.location
					.getServicioInEntidads()) {
				result.add(string.getServicio().getNombre());
			}
		}
		return result;
	}

	// return the list of entities that can be assigned beds
	@SuppressWarnings("unchecked")
	public List<String> listadoEntidades() {
		try {
			return entityManager
					.createQuery(
							"select ent.nombre from Entidad_configuracion ent "
									+ "where (ent.eliminado = false or ent.eliminado = null) "
									+ "and ent.perteneceARhio = true "
									+ "order by ent.nombre").getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of beds cathegory
	@SuppressWarnings("unchecked")
	public List<String> listadoCategorias() {
		try {
			return entityManager
					.createQuery(
							"select cat.valor from CategoriaCama_configuracion cat "
									+ "where (cat.eliminado = false or cat.eliminado = null) "
									+ "order by cat.valor").getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of beds state
	@SuppressWarnings("unchecked")
	public List<String> listadoEstados() {
		try {
			return entityManager
					.createQuery(
							"select est.valor from EstadoCama_configuracion est where "
									+ "(est.eliminado = false or est.eliminado = null) "
									+ "order by est.valor").getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of beds kind
	@SuppressWarnings("unchecked")
	public List<String> listadoTipo() {
		try {
			return entityManager
					.createQuery(
							"select tipo.valor from TipoCama_configuracion tipo "
									+ "where (tipo.eliminado = false or tipo.eliminado = null) "
									+ "order by tipo.valor").getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// return a list of services that acan be assigned beds
	@SuppressWarnings("unchecked")
	public List<String> listadoServicios() {
		try {
			return entityManager
					.createQuery(
							"select serv.nombre from ServicioInEntidad_configuracion serInEnt "
									+ "join serInEnt.entidad ent "
									+ "join serInEnt.servicio serv "
									+ "where ent.nombre =:nomEnt "
									+ "and (serInEnt.eliminado = false or serInEnt.eliminado = null) "
									+ "and (ent.eliminado = false or ent.eliminado = null) "
									+ "and (serv.eliminado = false or serv.eliminado = null) "
									+ "ordet by serv.nombre")
					.setParameter("nomEnt", this.entidadSeleccionada)
					.getResultList();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	// select the father ubication
	public void putNewLocation(UbicacionWrapper ubic) {
		this.location = ubic.getValue();
		this.locationSelected = this.getUbicationIdfromRules(this.location
				.getId());
	}

	// load a list of services by entity that show in a comboBox visual
	// component
	@SuppressWarnings("unchecked")
	public void serviciosPorEntidad() {
		this.servicios = entityManager
				.createQuery(
						"select serv.nombre from ServicioInEntidad_configuracion serInEnt "
								+ "join serInEnt.entidad ent "
								+ "join serInEnt.servicio serv "
								+ "where ent.nombre =:nomEnt "
								+ "and (serInEnt.eliminado = false or serInEnt.eliminado = null) "
								+ "and (ent.eliminado = false or ent.eliminado = null)"
								+ "and (serv.eliminado = false or serv.eliminado = null) "
								+ "order by serv.nombre")
				.setParameter("nomEnt", this.entidadSeleccionada)
				.getResultList();
		this.servicioSeleccionado = "";
		this.locationSelected = "";
		this.loadData();
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
								+ "and (u.eliminado = false or u.eliminado = null) "
								+ "and (u.entidad.eliminado = false or u.entidad.eliminado = null)")
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
			this.entidad = (Entidad_configuracion) entityManager
					.createQuery(
							"select ent from Entidad_configuracion ent where ent.nombre =:nombre "
									+ "and (ent.eliminado = false or ent.eliminado = null)")
					.setParameter("nombre", this.entidadSeleccionada)
					.getSingleResult();
			loadData();
		} catch (Exception e) {

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
				.setParameter("pid", this.entidad.getId()).getResultList();

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

	public void ubicacionVlaida2Camas() {
		/*
		 * La ubicacion seleccionada deberia de ser una habitacion. La cual debe
		 * de tenercomo hubicacion padre a una sala.Por lo tando pregunto si el
		 * padre de la ubicacion seleccionada es una sala
		 */
		if (!this.location.getUbicacion().getTipoUbicacion().getCodigo()
				.equals("S")) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "ubicacionInvalida");
		}
	}

	@SuppressWarnings("unchecked")
	public boolean validations() {
		// validando que se haya seleccionado una entidad
		if (this.locationSelected.equals("")) {
			this.error = 1;
			return true;
		}

		// validacion de existencia
		List<Cama_configuracion> l = entityManager
				.createQuery(
						"select c from Cama_configuracion c where c.descripcion = :descripcion "
								+ "and (c.id <> :id) "
								+ "and (c.eliminado = false or c.eliminado = null)")
				.setParameter("id", this.cama.getId())
				.setParameter("descripcion", this.descripcion).getResultList();

		// validacion de existencia
		/*
		 * NOTA: Se buscan camas con el mismo identificador, en la misma entidad
		 * y en la misma sala. Debido a que las camas dentro de una sala no
		 * deben de tener el mismo identificador y las salas a su vez dentro de
		 * una misma entidad no deben de tener el mismo identificador
		 */

		if (l.size() != 0) {
			for (int i = 0; i < l.size(); i++) {
				String a = this.getUbicationIdfromRules(l.get(i).getUbicacion()
						.getId());
				if (locationSelected.equals(a)) {
					error = 1;
					facesMessages.addToControlFromResourceBundle("error",
							Severity.ERROR, "entidadExistente");
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void crear() {
		error = 0;

		try {
			// validaciones
			if (validations())
				return;

			EstadoCama_configuracion estado = (EstadoCama_configuracion) entityManager
					.createQuery(
							"select estado from EstadoCama_configuracion estado "
									+ "where estado.valor=:valorSelecc "
									+ "and (estado.eliminado = false or estado.eliminado = null)")
					.setParameter("valorSelecc", this.estadoSeleccionado)
					.getSingleResult();

			CategoriaCama_configuracion categoriaCama = (CategoriaCama_configuracion) entityManager
					.createQuery(
							"select catg from CategoriaCama_configuracion catg "
									+ "where catg.valor=:valor "
									+ "and (catg.eliminado = false or catg.eliminado = null)")
					.setParameter("valor", this.categoriaSeleccionada)
					.getSingleResult();

			TipoCama_configuracion tipo = (TipoCama_configuracion) entityManager
					.createQuery(
							"select tipo from TipoCama_configuracion tipo "
									+ "where tipo.valor=:valor "
									+ "and (tipo.eliminado = false or tipo.eliminado = null)")
					.setParameter("valor", this.tipoSeleccionado)
					.getSingleResult();

			ServicioInEntidad_configuracion servicio = (ServicioInEntidad_configuracion) entityManager
					.createQuery(
							"select serInEnt from ServicioInEntidad_configuracion serInEnt "
									+ "join serInEnt.entidad ent "
									+ "join serInEnt.servicio serv "
									+ "where ent.nombre =:nombreEnt and serv.nombre=:nombreSer "
									+ "and (serInEnt.eliminado = false or serInEnt.eliminado = null) "
									+ "and (ent.eliminado = false or ent.eliminado = null) "
									+ "and (serv.eliminado = false or serv.eliminado = null)")
					.setParameter("nombreEnt", this.entidadSeleccionada)
					.setParameter("nombreSer", this.servicioSeleccionado)
					.getSingleResult();

			// validacion de existencia
			List<Cama_configuracion> l = entityManager
					.createQuery(
							"select c from Cama_configuracion c where c.descripcion = :descripcion "
									+ "and (c.eliminado = false or c.eliminado = null)")
					.setParameter("descripcion", this.descripcion)
					.getResultList();

			// validacion de existencia
			/*
			 * NOTA: Se buscan camas con el mismo identificador, en la misma
			 * entidad y en la misma sala. Debido a que las camas dentro de una
			 * sala no deben de tener el mismo identificador y las salas a su
			 * vez dentro de una misma entidad no deben de tener el mismo
			 * identificador
			 */

			if (l.size() != 0) {
				for (int i = 0; i < l.size(); i++) {
					String a = this.getUbicationIdfromRules(l.get(i)
							.getUbicacion().getId());
					if (locationSelected.equals(a)) {
						error = 1;
						facesMessages.addToControlFromResourceBundle("error",
								Severity.ERROR, "entidadExistente");
						return;
					}
				}
			}

			this.cama.setEliminado(false);
			this.cama.setDescripcion(descripcion.trim());
			this.cama.setUbicacion(location);
			this.cama.setCategoriaCama(categoriaCama);
			this.cama.setTipoCama(tipo);
			this.cama.setEstadoCama(estado);
			this.cama.setServicioInEntidadByIdServicio(servicio);

			// estableciendo la distribucion fisica de la cama en la sala
			this.cama.setX(0);
			this.cama.setY(0);

			entityManager.persist(this.cama);
			entityManager.flush();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "errorInesperado");
		}
	}

	// Important properties ---------------------------------------
	public void setEntidadSeleccionada(String entidadSeleccionada) {
		this.entidadSeleccionada = entidadSeleccionada;
		cargarUbicaciones();
	}

	// Properties -------------------------------------------------
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.trim();
	}

	public Cama_configuracion getCama() {
		return cama;
	}

	public void setCama(Cama_configuracion cama) {
		this.cama = cama;
	}

	public List<String> getServicios() {
		return servicios;
	}

	public void setServicios(List<String> servicios) {
		this.servicios = servicios;
	}

	@SuppressWarnings("unchecked")
	public TreeNode getTreeData() {
		return treeData;
	}

	@SuppressWarnings("unchecked")
	public void setTreeData(TreeNode treeData) {
		this.treeData = treeData;
	}

	public String getServicioSeleccionado() {
		return servicioSeleccionado;
	}

	public void setServicioSeleccionado(String servicioSeleccionado) {
		this.servicioSeleccionado = servicioSeleccionado;
	}

	public String getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(String categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public String getEstadoSeleccionado() {
		return estadoSeleccionado;
	}

	public void setEstadoSeleccionado(String estadoSeleccionado) {
		this.estadoSeleccionado = estadoSeleccionado;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
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

	public String getEntidadSeleccionada() {
		return entidadSeleccionada;
	}

	public String getTipoSeleccionado() {
		return tipoSeleccionado;
	}

	public void setTipoSeleccionado(String tipoSeleccionado) {
		this.tipoSeleccionado = tipoSeleccionado;
	}

	public String getLocationSelected() {
		return locationSelected;
	}

	public void setLocationSelected(String locationSelected) {
		this.locationSelected = locationSelected;
	}

	public Ubicacion_configuracion getLocation() {
		return location;
	}

	public void setLocation(Ubicacion_configuracion location) {
		this.location = location;
	}
}