package gehos.configuracion.clinicaldata.camas.porservicios;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.CamasTreeBuilder;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.DepartamentoWrapper;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.camas.porservicios.treebuilders.model.ServicioWrapper;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionID;
import gehos.configuracion.clinicaldata.ubicaciones.treebuilders.model.UbicacionWrapper;
import gehos.configuracion.management.entity.Cama_configuracion;
import gehos.configuracion.management.entity.CategoriaCama_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EstadoCama_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoCama_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

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

@Name("camasPorServiciosManager")
@Scope(ScopeType.CONVERSATION)
public class CamasManager {

	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;
	@In
	FacesMessages facesMessages;

	@In(required = false, create = true, value = "camasPorServiciosTreeBuilder")
	CamasTreeBuilder treeBuilder;

	private ITreeData selectedItem;

	@SuppressWarnings({ "rawtypes" })
	private TreeNode selectedTreenode;

	@In
	RulesParser rulesParser;
	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;
	RuleBase ruleBase;

	public String onAdicionarCamaComplete(String modalName) {
		if (facesMessages.getCurrentMessagesForControl("descinput").size() > 0)
			return "return false;";
		else {
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	private Ubicacion_configuracion newLocation;

	public void putNewLocation(UbicacionWrapper ubic) {
		this.newLocation = ubic.getValue();
		this.ubicacionEnModificacion = this
				.getUbicationIdfromRules(this.newLocation.getId());
	}

	public String camaLocation(Cama_configuracion cama) {
		if (cama == null)
			return "-";
		if (cama.getUbicacion() == null)
			return "-";
		return this.getUbicationIdfromRules(cama.getUbicacion().getId());
	}

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

	@SuppressWarnings("unchecked")
	public List<Cama_configuracion> camasDeServicioSeleccionado() {
		List<Cama_configuracion> camas = new ArrayList<Cama_configuracion>();
		if (selectedItem instanceof ServicioWrapper) {
			ServicioInEntidad_configuracion servicio = ((ServicioWrapper) this.selectedItem)
					.getValue();
			camas = entityManager
					.createQuery(
							"from Cama_configuracion cama where cama.servicioInEntidadByIdServicio.id = :idServ")
					.setParameter("idServ", servicio.getId()).getResultList();
			Collections.sort(camas, new Comparator<Cama_configuracion>() {
				public int compare(Cama_configuracion arg0,
						Cama_configuracion arg1) {
					String id0 = arg0.getTipoCama().getCodigo()
							+ arg0.getDescripcion();
					String id1 = arg1.getTipoCama().getCodigo()
							+ arg1.getDescripcion();
					return id0.compareTo(id1);
				}
			});
		}
		return camas;
	}

	@SuppressWarnings("unchecked")
	public List<ServicioInEntidad_configuracion> serviciosDeServicioSeleccionado() {
		List<ServicioInEntidad_configuracion> servicios = new ArrayList<ServicioInEntidad_configuracion>();
		if (selectedItem instanceof ServicioWrapper) {
			ServicioInEntidad_configuracion servicio = ((ServicioWrapper) this.selectedItem)
					.getValue();
			servicios = entityManager
					.createQuery(
							"from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
									+ "and serv.servicio.servicio.id = :servid order by serv.servicio.nombre")
					.setParameter("servid", servicio.getServicio().getId())
					.setParameter("entId", servicio.getEntidad().getId())
					.getResultList();
		}
		return servicios;
	}

	@SuppressWarnings("unchecked")
	public List<ServicioInEntidad_configuracion> serviciosDeDepartamentoSeleccionado() {
		List<ServicioInEntidad_configuracion> servicios = new ArrayList<ServicioInEntidad_configuracion>();
		if (selectedItem instanceof DepartamentoWrapper) {
			DepartamentoInEntidad_configuracion departamento = ((DepartamentoWrapper) this.selectedItem)
					.getValue();
			servicios = entityManager
					.createQuery(
							"from ServicioInEntidad_configuracion serv where serv.entidad.id = :entId "
									+ "and serv.servicio.departamento.id = :depid and serv.servicio.servicio = null "
									+ "order by serv.servicio.nombre")
					.setParameter("depid",
							departamento.getDepartamento().getId())
					.setParameter("entId", departamento.getEntidad().getId())
					.getResultList();
		}
		return servicios;
	}

	@SuppressWarnings("unchecked")
	public List<DepartamentoInEntidad_configuracion> departamentosDeEntidadSeleccionada() {
		List<DepartamentoInEntidad_configuracion> departamentos = null;
		if (this.selectedItem instanceof EntidadWrapper) {
			Entidad_configuracion entidad = entityManager.find(
					Entidad_configuracion.class,
					((EntidadWrapper) this.selectedItem).getValue().getId());

			departamentos = entityManager
					.createQuery(
							"from DepartamentoInEntidad_configuracion dep where dep.departamento.esClinico = true "
									+ "and dep.entidad.id=:entid order by dep.departamento.nombre")
					.setParameter("entid", entidad.getId()).getResultList();

			// departamentos.addAll(departamentos);
			Collections.sort(departamentos,
					new Comparator<DepartamentoInEntidad_configuracion>() {
						public int compare(
								DepartamentoInEntidad_configuracion arg0,
								DepartamentoInEntidad_configuracion arg1) {
							return arg0
									.getDepartamento()
									.getNombre()
									.compareTo(
											arg1.getDepartamento().getNombre());
						}
					});
		}
		return departamentos;
	}

	private Cama_configuracion camaToDelete;

	private Cama_configuracion camaToModify;

	public void putCamaToDelete(Cama_configuracion cama) {
		this.camaToDelete = cama;
	}

	public void putCamaToModify(Cama_configuracion cama) {
		this.camaToModify = entityManager.merge(cama);
		this.servicioEnModificacion = cama.getServicioInEntidadByIdServicio()
				.getServicio().getNombre();
		this.tipoCamaEnModificacion = cama.getTipoCama().getValor();
		if (cama.getUbicacion() != null) {
			this.ubicacionEnModificacion = this.camaLocation(cama);
		} else {
			this.ubicacionEnModificacion = "-";
		}
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
	private String ubicacionEnModificacion = "";

	public void modifyCama() {
		long cid = bitacora.registrarInicioDeAccion("Modificando cama..");
		this.camaToModify = entityManager.merge(this.camaToModify);
		if (!this.camaToModify.getTipoCama().getValor()
				.equals(tipoCamaEnModificacion)) {
			TipoCama_configuracion tipoCama = (TipoCama_configuracion) entityManager
					.createQuery(
							"from TipoCama_configuracion t where t.valor = :tipoCama")
					.setParameter("tipoCama", tipoCamaEnModificacion)
					.getSingleResult();
			this.camaToModify.setTipoCama(tipoCama);
		}
		if (this.newLocation != null
				&& (this.camaToModify.getUbicacion() == null || (this.newLocation
						.getId() != this.camaToModify.getUbicacion().getId()))) {
			Ubicacion_configuracion ubicacion = entityManager.find(
					Ubicacion_configuracion.class, this.newLocation.getId());
			this.camaToModify.setUbicacion(ubicacion);
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

	public void crearCamas() {
		ServicioInEntidad_configuracion svcio = entityManager.find(
				ServicioInEntidad_configuracion.class,
				((ServicioWrapper) this.selectedItem).getValue().getId());

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

		for (int i = 0; i < cantidadACrear; i++) {
			Cama_configuracion cama = new Cama_configuracion();
			cama.setTipoCama(tipoCama);
			cama.setDescripcion("nueva");
			// cama.setUbicacion(svcio);
			cama.setX(0);
			cama.setY(0);
			cama.setCategoriaCama(categoria);
			cama.setEstadoCama(estado);
			cama.setServicioInEntidadByIdServicio(svcio);
			svcio.getCamasForIdServicio().add(cama);
			entityManager.persist(cama);
			camasModificadas.put(cama.getId(), true);
		}
		entityManager.persist(svcio);
		entityManager.flush();

		if (selectedTreenode != null)
			this.treeBuilder.updateNode(selectedTreenode);
	}

	public String onComplete(String modalName) {
		if (this.tipoSeleccionado.isEmpty())
			return "return false;";
		else {
			this.tipoSeleccionado = "";
			this.servicioSeleccionado = "";
			this.cantidadACrear = 1;
			return "javascript:Richfaces.hideModalPanel('" + modalName + "');";
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> tiposDeCamas() {
		return entityManager.createQuery(
				"select t.valor from TipoCama_configuracion t").getResultList();
	}

	public void subir() {
		if (this.selectedItem instanceof ServicioWrapper) {
			ServicioInEntidad_configuracion servicio = ((ServicioWrapper) this.selectedItem)
					.getValue();
			if (servicio.getServicio().getServicio() == null) {
				DepartamentoInEntidad_configuracion departamento = (DepartamentoInEntidad_configuracion) entityManager
						.createQuery(
								"from DepartamentoInEntidad_configuracion dep "
										+ "where dep.departamento.id = :depid and dep.entidad.id = :entid")
						.setParameter(
								"depid",
								servicio.getServicio().getDepartamento()
										.getId())
						.setParameter("entid", servicio.getEntidad().getId())
						.getSingleResult();
				this.selectedItem = new DepartamentoWrapper(departamento,
						false, servicio.getEntidad().getId());
			} else {
				ServicioInEntidad_configuracion servicioPadre = (ServicioInEntidad_configuracion) entityManager
						.createQuery(
								"from ServicioInEntidad_configuracion svr "
										+ "where svr.servicio.id = :servid and svr.entidad.id = :entid")
						.setParameter("servid",
								servicio.getServicio().getServicio().getId())
						.setParameter("entid", servicio.getEntidad().getId())
						.getSingleResult();
				this.selectedItem = new ServicioWrapper(servicioPadre, false,
						servicio.getEntidad().getId());
			}
		} else if (this.selectedItem instanceof DepartamentoWrapper) {
			DepartamentoInEntidad_configuracion departamento = ((DepartamentoWrapper) this.selectedItem)
					.getValue();
			this.selectedItem = new EntidadWrapper(departamento.getEntidad(),
					false, departamento.getEntidad().getId());
		} else if (this.selectedItem instanceof EntidadWrapper) {
			this.selectedItem = null;
		}
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
//							        + "where ent.perteneceARhio = true "
								+ "and (ent.eliminado = null or ent.eliminado = false) "
								+ "order by ent.nombre").getResultList();
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

	public ITreeData getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object param) {
		if (param instanceof Entidad_configuracion)
			this.selectedItem = new EntidadWrapper(
					(Entidad_configuracion) param, false,
					((Entidad_configuracion) param).getId());
		else if (param instanceof DepartamentoInEntidad_configuracion) {
			DepartamentoInEntidad_configuracion dep = (DepartamentoInEntidad_configuracion) param;
			this.selectedItem = new DepartamentoWrapper(dep, false, dep
					.getEntidad().getId());
		} else if (param instanceof ServicioInEntidad_configuracion) {
			ServicioInEntidad_configuracion dep = (ServicioInEntidad_configuracion) param;
			this.selectedItem = new ServicioWrapper(dep, false, dep
					.getEntidad().getId());
		}
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

		ServicioInEntidad_configuracion servDestino = (ServicioInEntidad_configuracion) (((ITreeData) droppedInNode
				.getData()).getValue());
		ServicioInEntidad_configuracion servicio = entityManager.find(
				ServicioInEntidad_configuracion.class, servDestino.getId());

		Cama_configuracion camaDragged = (Cama_configuracion) (((ITreeData) draggedNode
				.getData()).getValue());
		camaDragged = entityManager.find(Cama_configuracion.class,
				camaDragged.getId());

		camaDragged.setServicioInEntidadByIdServicio(servicio);
		servicio.getCamasForIdServicio().add(camaDragged);

		entityManager.persist(servicio);
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

	public String getUbicacionEnModificacion() {
		return ubicacionEnModificacion;
	}

	public void setUbicacionEnModificacion(String ubicacionEnModificacion) {
		this.ubicacionEnModificacion = ubicacionEnModificacion;
	}

}
