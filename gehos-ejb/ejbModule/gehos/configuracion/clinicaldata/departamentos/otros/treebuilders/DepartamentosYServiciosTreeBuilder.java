package gehos.configuracion.clinicaldata.departamentos.otros.treebuilders;

import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.DepartamentoWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.EspecialidadWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ServicioWrapper;

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
@Name("departamentosYServiciosNoClinicosTreeBuilder")
public class DepartamentosYServiciosTreeBuilder {

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	private TreeNode treeData;
	@SuppressWarnings("unchecked")
	private TreeNode selectedNode;

	@SuppressWarnings("unchecked")
	public void updateNode(TreeNode node) {
		if (node.getData() instanceof DepartamentoWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Departamento_configuracion.class, (((ITreeData) (node
							.getData())).getId())));
		} else if (node.getData() instanceof ServicioWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Servicio_configuracion.class,
					(((ITreeData) (node.getData())).getId())));
		} else if (node.getData() instanceof EspecialidadWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Especialidad_configuracion.class, (((ITreeData) (node
							.getData())).getId())));
		}
		boolean loading = collapseOrExpand(node, true);
		collapseOrExpand(node, loading);
	}

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void constructor() {
	}

	private boolean administrarClinicos = true;

	@SuppressWarnings("unchecked")
	public void loadData() {
		treeData = new TreeNodeImpl();
		List<Departamento_configuracion> departamentos = entityManager
				.createQuery(
						"from Departamento_configuracion dep "
								+ "where dep.esClinico = :admClinicos order by dep.nombre")
				.setParameter("admClinicos", this.administrarClinicos)
				.getResultList();
		for (int i = 0; i < departamentos.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode departNode = new TreeNodeImpl();
			departNode.setData(new DepartamentoWrapper(departamentos.get(i),
					false, new Long(departamentos.get(i).getId())));
			departNode.addChild("...", loadingNode);

			treeData.addChild(departamentos.get(i), departNode);
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
			if (selected.getData() instanceof DepartamentoWrapper) {
				return expandDepartamento(selected);
			} else if (selected.getData() instanceof ServicioWrapper) {
				return expandServicio(selected);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean expandServicio(TreeNode selected) {
		ServicioWrapper value = ((ServicioWrapper) selected.getData());
		selected.removeChild("...");

		List<Servicio_configuracion> servicios = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.servicio.id = :servid order by serv.nombre")
				.setParameter("servid", value.getValue().getId())
				.getResultList();
		List<Especialidad_configuracion> especialidades = entityManager
				.createQuery(
						"from Especialidad_configuracion esp where esp.servicio.id = :servid order by esp.nombre")
				.setParameter("servid", value.getValue().getId())
				.getResultList();

		for (int i = 0; i < servicios.size(); i++) {
			TreeNode servicioNode = new TreeNodeImpl();
			ServicioWrapper w = new ServicioWrapper(servicios.get(i), false,
					value.getEntidadID());
			servicioNode.setData(w);

			Long servicioshijosCount = (Long) entityManager
					.createQuery(
							"select count(*) from Servicio_configuracion serv where serv.servicio.id = :servPad")
					.setParameter("servPad", servicios.get(i).getId())
					.getSingleResult();
			Long especialidadeshijosCount = (Long) entityManager
					.createQuery(
							"select count(*) from Especialidad_configuracion esp where esp.servicio.id = :servPad")
					.setParameter("servPad", servicios.get(i).getId())
					.getSingleResult();

			if (servicioshijosCount > 0 || especialidadeshijosCount > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				servicioNode.addChild("...", loadingNode);
			}

			selected.addChild(w.hashCode(), servicioNode);
		}
		for (int i = 0; i < especialidades.size(); i++) {
			TreeNode especialidadNode = new TreeNodeImpl();
			EspecialidadWrapper w = new EspecialidadWrapper(especialidades
					.get(i), false, value.getEntidadID());
			especialidadNode.setData(w);
			selected.addChild(w.hashCode(), especialidadNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return (servicios.size() + especialidades.size() > 0);
	}

	@SuppressWarnings("unchecked")
	private boolean expandDepartamento(TreeNode selected) {
		DepartamentoWrapper value = ((DepartamentoWrapper) selected.getData());
		selected.removeChild("...");

		List<Servicio_configuracion> servicios = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.departamento.id = :depid and serv.servicio = null order by serv.nombre")
				.setParameter("depid", value.getValue().getId())
				.getResultList();

		for (int i = 0; i < servicios.size(); i++) {
			TreeNode servicioNode = new TreeNodeImpl();
			ServicioWrapper w = new ServicioWrapper(servicios.get(i), false,
					value.getEntidadID());
			servicioNode.setData(w);
			if (servicios.get(i).getServicios().size() > 0
					|| servicios.get(i).getEspecialidads().size() > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				servicioNode.addChild("...", loadingNode);
			}
			selected.addChild(w.hashCode(), servicioNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return servicios.size() > 0;
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

	public boolean isAdministrarClinicos() {
		return administrarClinicos;
	}

	public void setAdministrarClinicos(boolean administrarClinicos) {
		if (this.treeData == null
				|| this.administrarClinicos != administrarClinicos) {
			this.administrarClinicos = administrarClinicos;
			this.loadData();
		}
	}

}
