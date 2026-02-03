package gehos.comun.modulos.porentidad.treebuilders;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.modulos.porentidad.treebuilders.model.EntidadWrapper;
import gehos.comun.modulos.porentidad.treebuilders.model.ITreeData;
import gehos.comun.modulos.porentidad.treebuilders.model.ModuloWrapper;
import gehos.configuracion.management.entity.Entidad_configuracion;

import java.util.ArrayList;
import java.util.Hashtable;
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
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.SESSION)
@Name("modulosPorEntidadTreeBuilder")
public class ModulosPorEntidadTreeBuilder {

	@In
	private EntityManager entityManager;
	@In
	private FacesMessages facesMessages;

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
		} else if (node.getData() instanceof ModuloWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Funcionalidad.class, (((ITreeData) (node.getData()))
							.getId())));
		}
		boolean loading = collapseOrExpand(node, true);
		collapseOrExpand(node, loading);
	}

	private Hashtable<Long, Entidad_configuracion> entidadesHashTable;

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void ifNotPostBackLoadData() {
		if (this.treeData == null) {
			loadData();
		}
	}

	@SuppressWarnings("unchecked")
	public void checkEntityListChanged() {
		if (this.treeData != null) {
			
			/**
			 * @author yurien 27/03/2014
			 * Se cambia la restriccion para que muestre solo las entidades que pertenecen al anillo configurado
			 * **/
			List<Entidad_configuracion> entidades = entityManager.createQuery(
					"from Entidad_configuracion ent "
							+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//					     + "where ent.perteneceARhio = true "
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

	@SuppressWarnings("unchecked")
	private void loadData() {
		treeData = new TreeNodeImpl();

		/**
		 * @author yurien 27/03/2014
		 * Se cambia la restriccion para que muestre solo las entidades que pertenecen al anillo configurado
		 * **/
		List<Entidad_configuracion> entidades = entityManager.createQuery(
				"from Entidad_configuracion ent "
						+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//				        + "where ent.perteneceARhio = true "
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
	private boolean collapseOrExpand(TreeNode selected, boolean putLoadingNode) {
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			if (putLoadingNode) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				selected.addChild("...", loadingNode);
			}
			((ITreeData) selected.getData()).setExpanded(false);
		} else {
			if (selected.getData() instanceof EntidadWrapper) {
				return expandEntidad(selected);
			} else if (selected.getData() instanceof ModuloWrapper) {
				return expandModulo(selected);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean expandModulo(TreeNode selected) {
		ModuloWrapper value = ((ModuloWrapper) selected.getData());
		selected.removeChild("...");

		List<Funcionalidad> modulosFisicos = entityManager
				.createQuery(
						"from Funcionalidad func where func.esModulo=true "
								+ "and func.moduloFisico=true and func.entidad.id=:entId "
								+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modPadreId "
								+ "order by func.label").setParameter("entId",
						value.getEntidadID()).setParameter("modPadreId",
						value.getId()).getResultList();

		for (int i = 0; i < modulosFisicos.size(); i++) {
			TreeNode moduloNode = new TreeNodeImpl();
			ModuloWrapper w = new ModuloWrapper(modulosFisicos.get(i), false,
					value.getEntidadID());
			moduloNode.setData(w);
			w.setModuloUnico(false);
			w.setType("modulo-fisico");
			selected.addChild(w.hashCode(), moduloNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return modulosFisicos.size() > 0;
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
			Long numeroInstanciasModulo = (Long) entityManager
					.createQuery(
							"select count(*) from Funcionalidad func where func.esModulo=true "
									+ "and func.moduloFisico=true "
									+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modId "
									+ "and func.entidad.id=:entId")
					.setParameter("modId",
							tiposModulosExistentesPorEntidad.get(i).getId())
					.setParameter("entId", value.getId()).getSingleResult();
			ModuloWrapper w;
			if (numeroInstanciasModulo > 1) {
				w = new ModuloWrapper(tiposModulosExistentesPorEntidad.get(i),
						false, value.getEntidadID());
				w.setModuloUnico(false);
				w.setType("modulo-padre");
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				moduloNode.addChild("...", loadingNode);
			} else {
				Funcionalidad modUnicoFisico = (Funcionalidad) entityManager
						.createQuery(
								"from Funcionalidad func "
										+ "where func.esModulo=true and func.moduloFisico=true "
										+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modId "
										+ "and func.entidad.id=:entId ")
						.setParameter("modId",
								tiposModulosExistentesPorEntidad.get(i).getId())
						.setParameter("entId", value.getId()).getSingleResult();
				w = new ModuloWrapper(modUnicoFisico, false, value
						.getEntidadID());
				w.setModuloUnico(true);
				w.setType("modulo-fisico");
			}
			moduloNode.setData(w);
			selected.addChild(w.hashCode(), moduloNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return tiposModulosExistentesPorEntidad.size() > 0;
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
