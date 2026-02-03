package gehos.autorizacion.treebuilders.logical;

import gehos.autorizacion.treebuilders.logical.model.CategoriaWrapper;
import gehos.autorizacion.treebuilders.logical.model.EntidadWrapper;
import gehos.autorizacion.treebuilders.logical.model.FuncionalidadWrapper;
import gehos.autorizacion.treebuilders.logical.model.ITreeData;
import gehos.autorizacion.treebuilders.logical.model.ModuloWrapper;
import gehos.comun.funcionalidades.entity.Funcionalidad;

import gehos.comun.shell.IActiveModule;
import gehos.configuracion.management.entity.Entidad_configuracion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.SESSION)
@Name("functionalitiesController")
public class FunctionalitiesController {

	@In
	private EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	@SuppressWarnings("unchecked")
	private TreeNode treeData;

	private HtmlTree htmlTree;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;

	@In
	IActiveModule activeModule;

	@Create
	public void buildBitacoraTree() {
		if (this.treeData == null) {
			loadData();
		}
	}

	@SuppressWarnings("unchecked")
	public void checkEntityListChanged() {
		if (this.treeData != null) {
			
			/**
			 * @author yurien 28/03/2014
			 * Se agrega la nueva restriccion para que muestre las entidades 
			 * que pertenecen al anillo configurado
			 * **/
			List<Entidad_configuracion> entidades = entityManager.createQuery(
					"from Entidad_configuracion ent "
							 + "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//								+ "where ent.perteneceARhio = true "
							+ "order by ent.nombre").getResultList();
			if (entidades.size() != entidadesHashTable.size()) {
				this.loadData();
				facesMessages.addToControl("error",
						"El listado de entidades ha sido modificado.");
				return;
			}
			for (Entidad_configuracion entidad_configuracion : entidades) {
				if (!entidadesHashTable.containsKey(entidad_configuracion
						.getId())) {
					this.loadData();
					facesMessages.addToControl("error",
							"El listado de entidades ha sido modificado.");
					return;
				}
			}
		}
	}

	private Hashtable<Long, Entidad_configuracion> entidadesHashTable;

	@SuppressWarnings("unchecked")
	private void loadData() {
		treeData = new TreeNodeImpl();

		/**
		 * @author yurien 28/03/2014
		 * Se agrega la nueva restriccion para que muestre las entidades 
		 * que pertenecen al anillo configurado
		 * **/
		List<Entidad_configuracion> entidades = entityManager.createQuery(
				"from Entidad_configuracion ent "
						+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//						+ "where ent.perteneceARhio = true "
						+ "order by ent.nombre").getResultList();
		entidadesHashTable = new Hashtable<Long, Entidad_configuracion>();
		for (Entidad_configuracion entidad_configuracion : entidades) {
			entidadesHashTable.put(entidad_configuracion.getId(),
					entidad_configuracion);
		}

		for (int i = 0; i < entidades.size(); i++) {
			TreeNode entidadNode = new TreeNodeImpl();
			entidadNode.setData(new EntidadWrapper(entidades.get(i), false,
					entidades.get(i).getId()));

			Long numeroModulos = (Long) entityManager
					.createQuery(
							"select count(*) "
									+ "from Funcionalidad func where func.esModulo=true "
									+ "and func.moduloFisico=true and func.entidad.id=:entId "
									+ "and func.funcionalidadPadre.funcionalidadPadre.id != -1 ")
					.setParameter("entId", entidades.get(i).getId())
					.getSingleResult();

			if (numeroModulos > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				entidadNode.addChild("...", loadingNode);
			}
			treeData.addChild(entidades.get(i), entidadNode);
		}
	}

	@SuppressWarnings("unchecked")
	public void updateNode() {
		TreeNode node = this.selectedNode;
		if (node.getData() instanceof EntidadWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Entidad_configuracion.class,
					(((ITreeData) (node.getData())).getId())));
		} else if (node.getData() instanceof ModuloWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Funcionalidad.class, (((ITreeData) (node.getData()))
							.getId())));
		}
		boolean loading = collapseOrExpand(node, true);
		collapseOrExpand(node, loading);
	}

	@SuppressWarnings("unchecked")
	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		TreeNode selected = tree.getTreeNode();
		collapseOrExpand(selected, true);
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
			if (selected.getData() instanceof EntidadWrapper) {
				return expandEntidad(selected);
			} else if (selected.getData() instanceof ModuloWrapper) {
				return expandModulo(selected);
			} else if (selected.getData() instanceof CategoriaWrapper) {
				return expandCategoria(selected);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean expandEntidad(TreeNode selected) {
		EntidadWrapper value = ((EntidadWrapper) selected.getData());
		selected.removeChild("...");

		List<Funcionalidad> tiposModulosExistentesPorEntidad = entityManager
				.createQuery(
						"select distinct func.funcionalidadPadre.funcionalidadPadre "
								+ "from Funcionalidad func where func.esModulo=true "
								+ "and func.moduloFisico=true and func.entidad.id=:entId "
								+ "and func.funcionalidadPadre.funcionalidadPadre.id != -1 "
								+ "order by func.funcionalidadPadre.funcionalidadPadre.label")
				.setParameter("entId", value.getId()).getResultList();

		for (int i = 0; i < tiposModulosExistentesPorEntidad.size(); i++) {
			TreeNode moduloNode = new TreeNodeImpl();
			ModuloWrapper w = new ModuloWrapper(
					tiposModulosExistentesPorEntidad.get(i), false, null, value
							.getEntidadID());

			List<ITreeData> path = new ArrayList<ITreeData>();
			path.add(w);
			w.setRootToNodePath(path);

			w.setModuloUnico(false);
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			moduloNode.addChild("...", loadingNode);

			moduloNode.setData(w);
			selected.addChild(w.hashCode(), moduloNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return tiposModulosExistentesPorEntidad.size() > 0;
	}

	public Funcionalidad iconsModuleContainer(Funcionalidad fun) {
		// while(! (fun.getEsModulo() == true && fun.getModuloFisico() ==
		// false))
		while (!fun.getContenedorIconos())
			fun = fun.getFuncionalidadPadre();
		return fun;
	}

	@SuppressWarnings("unchecked")
	private boolean expandCategoria(TreeNode selected) {
		CategoriaWrapper value = ((CategoriaWrapper) selected.getData());
		selected.removeChild("...");

		// List<Funcionalidad> funcionalidades = new
		// ArrayList<Funcionalidad>(value.getValue().getFuncionalidadesHijas());
		List<Funcionalidad> funcionalidades = entityManager
				.createQuery(
						"from Funcionalidad f "
								+ "where f.esModulo = false and f.funcionalidadPadre.id = :idcat")
				.setParameter("idcat", value.getValue().getId())
				.getResultList();

		for (int i = 0; i < funcionalidades.size(); i++) {
			if (funcionalidades.get(i).getUrl().indexOf("selector") != -1) {
				TreeNode categoriaNode = new TreeNodeImpl();
				CategoriaWrapper w = new CategoriaWrapper(funcionalidades
						.get(i), false, null, value.getModuleFatherName());
				List<ITreeData> path = new ArrayList<ITreeData>(value
						.getRootToNodePath());
				path.add(w);
				w.setRootToNodePath(path);

				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);

				selected.addChild(w.hashCode(), categoriaNode);
			} else {
				TreeNode funcionalidadNode = new TreeNodeImpl();
				FuncionalidadWrapper w = new FuncionalidadWrapper(
						funcionalidades.get(i), false, null, value
								.getModuleFatherName());
				List<ITreeData> path = new ArrayList<ITreeData>(value
						.getRootToNodePath());
				path.add(w);
				w.setRootToNodePath(path);

				funcionalidadNode.setData(w);

				selected.addChild(w.hashCode(), funcionalidadNode);
			}

		}
		((ITreeData) selected.getData()).setExpanded(true);
		return funcionalidades.size() > 0;
	}

	@SuppressWarnings("unchecked")
	private boolean expandModulo(TreeNode selected) {
		ModuloWrapper value = ((ModuloWrapper) selected.getData());
		selected.removeChild("...");

		List<Funcionalidad> modulosHijos = new ArrayList<Funcionalidad>();
		if (value.getValue().getModuloFisico() == false) {
			if (value.getValue().getFuncionalidadPadre()
					.getFuncionalidadPadre().getId() == -1) {
				modulosHijos = entityManager
						.createQuery(
								"from Funcionalidad f "
										+ "where f.esModulo = true and f.funcionalidadPadre.id = :idmodulo")
						.setParameter("idmodulo", value.getValue().getId())
						.getResultList();
			} else {
				modulosHijos = entityManager
						.createQuery(
								"from Funcionalidad f "
										+ "where f.esModulo = true and f.funcionalidadPadre.id = :idmodulo "
										+ "and f.entidad.id = :identidad")
						.setParameter("idmodulo", value.getValue().getId())
						.setParameter("identidad", value.getEntidadID())
						.getResultList();
			}
			for (int i = 0; i < modulosHijos.size(); i++) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");

				TreeNode moduloNode = new TreeNodeImpl();
				ModuloWrapper mw = new ModuloWrapper(modulosHijos.get(i),
						false, null, value.getEntidadID());
				List<ITreeData> path = new ArrayList<ITreeData>(value
						.getRootToNodePath());
				path.add(mw);
				mw.setRootToNodePath(path);
				// mw.setModuleFatherName(modulos.get(i).getNombre());
				moduloNode.setData(mw);
				moduloNode.addChild("...", loadingNode);

				selected.addChild(mw.hashCode(), moduloNode);
			}
		}

		List<Funcionalidad> funcionalidades = activeModule
				.loadCurrentUserMenu(value.getValue().getNombre()/* , false */);

		for (int i = 0; i < funcionalidades.size(); i++) {
			if (funcionalidades.get(i).getUrl().indexOf("selector") != -1) {
				TreeNode categoriaNode = new TreeNodeImpl();
				CategoriaWrapper w = new CategoriaWrapper(funcionalidades
						.get(i), false, null, value.getValue().getNombre());
				List<ITreeData> path = new ArrayList<ITreeData>(value
						.getRootToNodePath());
				path.add(w);
				w.setRootToNodePath(path);

				categoriaNode.setData(w);

				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				categoriaNode.addChild("...", loadingNode);

				selected.addChild(w.hashCode(), categoriaNode);
			} else {
				TreeNode funcionalidadNode = new TreeNodeImpl();
				FuncionalidadWrapper w = new FuncionalidadWrapper(
						funcionalidades.get(i), false, null, value.getValue()
								.getNombre());
				List<ITreeData> path = new ArrayList<ITreeData>(value
						.getRootToNodePath());
				path.add(w);
				w.setRootToNodePath(path);

				funcionalidadNode.setData(w);

				selected.addChild(w.hashCode(), funcionalidadNode);
			}

		}
		((ITreeData) selected.getData()).setExpanded(true);
		return modulosHijos.size() + funcionalidades.size() > 0;
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
	public TreeNode getTreeData() {
		return treeData;
	}

	@SuppressWarnings("unchecked")
	public void setTreeData(TreeNode treeData) {
		this.treeData = treeData;
	}

	public HtmlTree getHtmlTree() {
		return htmlTree;
	}

	public void setHtmlTree(HtmlTree htmlTree) {
		this.htmlTree = htmlTree;
	}

	@SuppressWarnings("unchecked")
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

}
