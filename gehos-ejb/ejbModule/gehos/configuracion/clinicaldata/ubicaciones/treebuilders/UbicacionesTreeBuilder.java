package gehos.configuracion.clinicaldata.ubicaciones.treebuilders;

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
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.SESSION)
@Name("ubicacionesTreeBuilder")
public class UbicacionesTreeBuilder {

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;

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

	@Create
	@SuppressWarnings("unchecked")
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void ifNotPostBackLoadData() {
		if (this.treeData == null) {
			treeData = new TreeNodeImpl();
			List<Entidad_configuracion> entidades = new ArrayList<Entidad_configuracion>();

			/**
			 * @author yurien 27/03/2014
			 * Se cambia la restriccion para que muestre solo las entidades que pertenecen al anillo configurado
			 * **/
			List<Entidad_configuracion> entidadesAsociadas = entityManager
					.createQuery(
							"from Entidad_configuracion ent "
							+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//							+ "where ent.perteneceARhio = true "
									+ "and (ent.eliminado = null or ent.eliminado = false) "
									+ "order by ent.nombre").getResultList();
			entidades.addAll(entidadesAsociadas);

			for (int i = 0; i < entidades.size(); i++) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");

				TreeNode entidadNode = new TreeNodeImpl();
				entidadNode
						.setData(new EntidadWrapper(entidades.get(i), false));
				entidadNode.addChild("...", loadingNode);

				treeData.addChild(entidades.get(i), entidadNode);
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
						"from Ubicacion_configuracion u where u.entidad.id = :pid and u.ubicacion = null order by u.id")
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
				"from Ubicacion_configuracion f where f.ubicacion.id = :pid order by f.id")
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

}
