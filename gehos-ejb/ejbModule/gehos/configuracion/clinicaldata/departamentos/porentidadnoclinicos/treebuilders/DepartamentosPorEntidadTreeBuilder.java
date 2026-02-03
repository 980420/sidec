package gehos.configuracion.clinicaldata.departamentos.porentidadnoclinicos.treebuilders;

import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.DepartamentoWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.EspecialidadWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ServicioWrapper;

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
@Name("departamentosNoClinicosPorEntidadTreeBuilder")
public class DepartamentosPorEntidadTreeBuilder {

	@In
	private EntityManager entityManager;
	@In
	private FacesMessages facesMessages;

	@SuppressWarnings("rawtypes")
	private TreeNode treeData;
	@SuppressWarnings("rawtypes")
	private TreeNode selectedNode;

	@SuppressWarnings("rawtypes")
	public void updateNode(TreeNode node) {
		collapseOrExpand(node);
		collapseOrExpand(node);
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
			 * Se agrega la restriccion para que muestre las entidades 
			 * que pertenecen al anillo actual configurado para la instancia del his
			 * **/
			List<Entidad_configuracion> entidades = entityManager
					.createQuery(
							"from Entidad_configuracion ent where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} order by ent.nombre")
//							"from Entidad_configuracion ent where ent.perteneceARhio = true order by ent.nombre")
					.getResultList();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadData() {
		treeData = new TreeNodeImpl();

		/**
		 * @author yurien 27/03/2014
		 * Se agrega la restriccion para que muestre las entidades 
		 * que pertenecen al anillo actual configurado para la instancia del his
		 * **/
		List<Entidad_configuracion> entidades = entityManager
				.createQuery(
						"from Entidad_configuracion ent where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} order by ent.nombre")
//						"from Entidad_configuracion ent where ent.perteneceARhio = true order by ent.nombre")
				.getResultList();
		entidadesHashTable = new Hashtable<Long, Entidad_configuracion>();
		for (Entidad_configuracion entidad_configuracion : entidades) {
			entidadesHashTable.put(entidad_configuracion.getId(),
					entidad_configuracion);
		}

		for (int i = 0; i < entidades.size(); i++) {
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");

			TreeNode entidadNode = new TreeNodeImpl();
			entidadNode.setData(new EntidadWrapper(entidades.get(i), false,
					entidades.get(i).getId()));
			entidadNode.addChild("...", loadingNode);

			treeData.addChild(entidades.get(i), entidadNode);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void collapseOrExpand(TreeNode selected) {
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			selected.addChild("...", loadingNode);
			((ITreeData) selected.getData()).setExpanded(false);
		}

		else {
			if (selected.getData() instanceof EntidadWrapper) {
				expandEntidad(selected);
			} else if (selected.getData() instanceof DepartamentoWrapper) {
				expandDepartamento(selected);
			} else if (selected.getData() instanceof ServicioWrapper) {
				expandServicio(selected);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void expandServicio(TreeNode selected) {
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

			// if (servicios.get(i).getServicios().size() > 0
			// || servicios.get(i).getEspecialidads().size() > 0) {
			if (servicioshijosCount > 0 || especialidadeshijosCount > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				servicioNode.addChild("...", loadingNode);
			}

			selected.addChild(w.hashCode(), servicioNode);
		}
		for (int i = 0; i < especialidades.size(); i++) {
			TreeNode especialidadNode = new TreeNodeImpl();
			EspecialidadWrapper w = new EspecialidadWrapper(
					especialidades.get(i), false, value.getEntidadID());
			especialidadNode.setData(w);
			selected.addChild(w.hashCode(), especialidadNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void expandDepartamento(TreeNode selected) {
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
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void expandEntidad(TreeNode selected) {
		EntidadWrapper value = ((EntidadWrapper) selected.getData());
		selected.removeChild("...");

		List<Departamento_configuracion> departamentos = entityManager
				.createQuery(
						"from Departamento_configuracion dep where dep.esClinico != true order by dep.nombre")
				.getResultList();

		for (int i = 0; i < departamentos.size(); i++) {
			TreeNode departamentoNode = new TreeNodeImpl();
			DepartamentoWrapper w = new DepartamentoWrapper(
					departamentos.get(i), false, value.getEntidadID());
			departamentoNode.setData(w);

			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			departamentoNode.addChild("...", loadingNode);

			selected.addChild(w.hashCode(), departamentoNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
	}

	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		this.selectedNode = tree.getTreeNode();
		collapseOrExpand(this.selectedNode);
	}

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	public TreeNode getTreeData() {
		return treeData;
	}

	@SuppressWarnings("rawtypes")
	public void setTreeData(TreeNode treeData) {
		this.treeData = treeData;
	}

}
