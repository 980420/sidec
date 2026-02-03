package gehos.configuracion.clinicaldata.camas.porservicios.treebuilders;

import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.CamaWrapper;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.DepartamentoWrapper;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.ServicioWrapper;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;
import gehos.configuracion.management.entity.Cama_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
@Name("camasPorServiciosTreeBuilder")
public class CamasTreeBuilder {

	@In
	private EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	@SuppressWarnings("rawtypes")
	private TreeNode treeData;
	@SuppressWarnings("rawtypes")
	private TreeNode selectedNode;

	@SuppressWarnings("rawtypes")
	public void updateNode(TreeNode node) {
		if (node.getData() instanceof EntidadWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Entidad_configuracion.class,
					(((ITreeData) (node.getData())).getId())));
		} else if (node.getData() instanceof UbicacionWrapper) {
			((ITreeData) (node.getData())).setValue(entityManager.find(
					Ubicacion_configuracion.class,
					(((ITreeData) (node.getData())).getId())));
		}
		boolean loading = collapseOrExpand(node, true);
		collapseOrExpand(node, loading);
	}

	@Create
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadData() {
		// if (this.treeData == null) {
		treeData = new TreeNodeImpl();

		/**
		 * @author yurien 27/03/2014
		 * Se cambia la restriccion para que busque las entidades
		 * que pertenecen al anillo configurado
		 * **/
		List<Entidad_configuracion> entidades = entityManager
				.createQuery(
						"from Entidad_configuracion ent "
						        + "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//						        + "where ent.perteneceARhio = true "
								+ "and (ent.eliminado = null or ent.eliminado = false) "
								+ "order by ent.nombre").getResultList();
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
		// }
	}

	private Hashtable<Long, Entidad_configuracion> entidadesHashTable;

	@SuppressWarnings("unchecked")
	public void checkEntityListChanged() {
		if (this.treeData != null) {
			/**
			 * @author yurien 27/03/2014
			 * Se cambia la restriccion para que busque las entidades
			 * que pertenecen al anillo configurado
			 * **/
			List<Entidad_configuracion> entidades = entityManager
					.createQuery(
							"from Entidad_configuracion ent "
									 + "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//								        + "where ent.perteneceARhio = true "
									+ "and (ent.eliminado = null or ent.eliminado = false) "
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean collapseOrExpand(TreeNode selected, boolean putLoadingNode) {
		if (((ITreeData) selected.getData()).isExpanded()) {
			prune(selected);
			TreeNode loadingNode = new TreeNodeImpl();
			loadingNode.setData("...");
			selected.addChild("...", loadingNode);
			((ITreeData) selected.getData()).setExpanded(false);
		}

		else {
			if (selected.getData() instanceof EntidadWrapper) {
				return expandEntidad(selected);
			} else if (selected.getData() instanceof DepartamentoWrapper) {
				return expandDepartamento(selected);
			} else if (selected.getData() instanceof ServicioWrapper) {
				return expandServicio(selected);
			}
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean expandServicio(TreeNode selected) {
		ServicioInEntidad_configuracion servicio = ((ServicioWrapper) selected
				.getData()).getValue();
		selected.removeChild("...");

		List<ServicioInEntidad_configuracion> servicios = entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
								+ "and serv.servicio.servicio.id = :servid order by serv.servicio.nombre")
				.setParameter("servid", servicio.getServicio().getId())
				.setParameter("entId", servicio.getEntidad().getId())
				.getResultList();
		List<Cama_configuracion> camas = entityManager
				.createQuery(
						"from Cama_configuracion cama where cama.servicioInEntidadByIdServicio.id = :idServ")
				.setParameter("idServ", servicio.getId()).getResultList();
		Collections.sort(camas, new Comparator<Cama_configuracion>() {
			public int compare(Cama_configuracion arg0, Cama_configuracion arg1) {
				String id0 = arg0.getTipoCama().getCodigo()
						+ arg0.getDescripcion();
				String id1 = arg1.getTipoCama().getCodigo()
						+ arg1.getDescripcion();
				return id0.compareTo(id1);
			}
		});

		for (int i = 0; i < servicios.size(); i++) {
			TreeNode servicioNode = new TreeNodeImpl();
			ServicioWrapper w = new ServicioWrapper(servicios.get(i), false,
					servicio.getEntidad().getId());
			servicioNode.setData(w);

			Long servicioshijosCount = (Long) entityManager
					.createQuery(
							"select count(*) from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
									+ "and serv.servicio.servicio.id = :servid")
					.setParameter("servid",
							servicios.get(i).getServicio().getId())
					.setParameter("entId", servicio.getEntidad().getId())
					.getSingleResult();
			List<Cama_configuracion> camasList = new ArrayList<Cama_configuracion>(
					servicios.get(i).getCamasForIdServicio());

			if (servicioshijosCount > 0 || camasList.size() > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				servicioNode.addChild("...", loadingNode);
			}

			selected.addChild(w.hashCode(), servicioNode);
		}
		for (int i = 0; i < camas.size(); i++) {
			TreeNode servicioNode = new TreeNodeImpl();
			CamaWrapper w = new CamaWrapper(camas.get(i), false, servicio
					.getEntidad().getId());
			servicioNode.setData(w);
			selected.addChild(w.hashCode(), servicioNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean expandDepartamento(TreeNode selected) {
		DepartamentoWrapper value = ((DepartamentoWrapper) selected.getData());
		selected.removeChild("...");

		DepartamentoInEntidad_configuracion departamento = entityManager.find(
				DepartamentoInEntidad_configuracion.class, value.getValue()
						.getId());
		List<ServicioInEntidad_configuracion> servicios = entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
								+ "and serv.servicio.departamento.id = :depid and serv.servicio.servicio = null "
								+ "order by serv.servicio.nombre")
				.setParameter("depid", departamento.getDepartamento().getId())
				.setParameter("entId", departamento.getEntidad().getId())
				.getResultList();

		for (int i = 0; i < servicios.size(); i++) {
			TreeNode servicioNode = new TreeNodeImpl();
			ServicioWrapper w = new ServicioWrapper(servicios.get(i), false,
					value.getEntidadID());
			servicioNode.setData(w);

			Long servicioshijosCount = (Long) entityManager
					.createQuery(
							"select count(*) from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
									+ "and serv.servicio.servicio.id = :servid")
					.setParameter("servid",
							servicios.get(i).getServicio().getId())
					.setParameter("entId",
							servicios.get(i).getEntidad().getId())
					.getSingleResult();
			List<Cama_configuracion> camasList = new ArrayList<Cama_configuracion>(
					servicios.get(i).getCamasForIdServicio());

			if (servicioshijosCount > 0 || camasList.size() > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				servicioNode.addChild("...", loadingNode);
			}
			selected.addChild(w.hashCode(), servicioNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean expandEntidad(TreeNode selected) {
		EntidadWrapper value = ((EntidadWrapper) selected.getData());
		selected.removeChild("...");

		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, value.getValue().getId());

		List<DepartamentoInEntidad_configuracion> departamentos = entityManager
				.createQuery(
						"from DepartamentoInEntidad_configuracion dep where dep.departamento.esClinico = true "
								+ "and dep.entidad.id=:entid order by dep.departamento.nombre")
				.setParameter("entid", entidad.getId()).getResultList();

		Collections.sort(departamentos,
				new Comparator<DepartamentoInEntidad_configuracion>() {
					public int compare(
							DepartamentoInEntidad_configuracion arg0,
							DepartamentoInEntidad_configuracion arg1) {
						return arg0.getDepartamento().getNombre()
								.compareTo(arg1.getDepartamento().getNombre());
					}
				});

		for (int i = 0; i < departamentos.size(); i++) {
			TreeNode departamentoNode = new TreeNodeImpl();
			DepartamentoWrapper w = new DepartamentoWrapper(
					departamentos.get(i), false, value.getEntidadID());
			departamentoNode.setData(w);

			Long serviciosCount = (Long) entityManager
					.createQuery(
							"select count(*) from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
									+ "and serv.servicio.departamento.id = :depid and serv.servicio.servicio = null")
					.setParameter("depid",
							departamentos.get(i).getDepartamento().getId())
					.setParameter("entId",
							departamentos.get(i).getEntidad().getId())
					.getSingleResult();

			if (serviciosCount.intValue() > 0) {
				TreeNode loadingNode = new TreeNodeImpl();
				loadingNode.setData("...");
				departamentoNode.addChild("...", loadingNode);
			}
			selected.addChild(w.hashCode(), departamentoNode);
		}

		((ITreeData) selected.getData()).setExpanded(true);
		return false;
	}

	public void OnNodeCollapseExpand(org.richfaces.event.NodeExpandedEvent event) {
		HtmlTree tree = (HtmlTree) event.getSource();
		this.selectedNode = tree.getTreeNode();
		collapseOrExpand(this.selectedNode, true);
	}

	@SuppressWarnings({ "rawtypes" })
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

	@SuppressWarnings({ "rawtypes" })
	public TreeNode getTreeData() {
		return treeData;
	}

	@SuppressWarnings({ "rawtypes" })
	public void setTreeData(TreeNode treeData) {
		this.treeData = treeData;
	}

}
