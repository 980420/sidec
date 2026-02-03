package gehos.configuracion.management.gestionarCama;

import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.SESSION)
@Name("ubicacionesChooserTreeBuilderControler")
public class UbicacionesTreeBuilder {

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;
	private int selectedEntity = -1;

	@SuppressWarnings("unchecked")
	public void updateNode(TreeNode node) {
		if (node.getData() instanceof EntidadWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Entidad_configuracion.class,
					(((ITreeData) (node.getData())).getId())));
		} else if (node.getData() instanceof UbicacionWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Ubicacion_configuracion.class, (((ITreeData) (node
							.getData())).getId())));
		}
		boolean loading = collapseOrExpand(node, true);
		collapseOrExpand(node, loading);
	}

	// @Begin(join = true)
	public boolean putSelectedEntity(int entidadId) {
		if (this.selectedEntity != entidadId) {
			this.selectedEntity = entidadId;
			this.loadData();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		treeData = new TreeNodeImpl();

		List<Ubicacion_configuracion> ubicaciones = entityManager
				.createQuery(
						"from Ubicacion_configuracion u where u.entidad.id = :pid and u.ubicacion = null")
				.setParameter("pid", selectedEntity).getResultList();

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
	private boolean expandEntidad(TreeNode selected) {
		EntidadWrapper value = ((EntidadWrapper) selected.getData());
		selected.removeChild("...");

		List<Ubicacion_configuracion> ubicaciones = entityManager
				.createQuery(
						"from Ubicacion_configuracion u where u.entidad.id = :pid and u.ubicacion = null")
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

	@SuppressWarnings("unchecked")
	private boolean expandUbicacion(TreeNode selected) {
		UbicacionWrapper value = ((UbicacionWrapper) selected.getData());
		selected.removeChild("...");

		List<Ubicacion_configuracion> ubicaciones = entityManager.createQuery(
				"from Ubicacion_configuracion f where f.ubicacion.id = :pid")
				.setParameter("pid", value.getValue().getId()).getResultList();

		for (int i = 0; i < ubicaciones.size(); i++) {
			Long childCount = (Long) entityManager
					.createQuery(
							"select count(*) from Ubicacion_configuracion f where f.ubicacion.id = :pid")
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

	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		this.selectedNode = tree.getTreeNode();
		collapseOrExpand(this.selectedNode, true);
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

	public Integer getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(Integer selectedEntity) {
		this.selectedEntity = selectedEntity;
	}

}
