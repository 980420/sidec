package gehos.configuracion.clinicaldata.departamentos.porentidad;

import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.DepartamentosPorEntidadTreeBuilder;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.DepartamentoWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.EntidadWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.EspecialidadWrapper;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ITreeData;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ServicioWrapper;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.TreeNode;

@Name("departamentosPorEntidadManagerBulkStyle")
public class DepartamentosPorEntidadManagerBulkStyle {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	@In(required = false, create = true, value = "departamentosPorEntidadTreeBuilder")
	DepartamentosPorEntidadTreeBuilder treeBuilder;

	private ITreeData selectedItem;
	@SuppressWarnings({ "unused", "rawtypes" })
	private TreeNode selectedTreenode;

	@Create
	public void constructor() {
	}

	public void subir() {
		if (this.selectedItem instanceof EntidadWrapper) {
			this.selectedItem = null;
		} else if (this.selectedItem instanceof DepartamentoWrapper) {
			Entidad_configuracion entidad = entityManager.find(
					Entidad_configuracion.class,
					((DepartamentoWrapper) (this.selectedItem)).getEntidadID());
			this.selectedItem = new EntidadWrapper(entidad, false,
					entidad.getId());
		} else if (this.selectedItem instanceof ServicioWrapper) {
			Departamento_configuracion dep = entityManager.find(
					Departamento_configuracion.class,
					((ServicioWrapper) (this.selectedItem)).getValue()
							.getDepartamento().getId());
			this.selectedItem = new DepartamentoWrapper(dep, false,
					((ServicioWrapper) (this.selectedItem)).getEntidadID());
		}
	}

	public void aplicarCambios() {
		if (this.selectedItem.toString().equals("entidad")) {
			for (DepartamentoWrapper wrapper : visibleDepartments) {
				if (!wrapper.getExistia() && wrapper.getExiste()) {
					this.departamentIdToAdd = wrapper.getId();
					this.departamentEntityIdToAdd = wrapper.getEntidadID();
					this.adicionarDepartamentoAEntidad();
				} else if (wrapper.getExistia() && !wrapper.getExiste()) {
					this.departamentIdToRemove = wrapper.getId();
					this.departamentEntityIdToRemove = wrapper.getEntidadID();
					this.eliminarDepartamentoAEntidad();
				}
			}
		}
		if (this.selectedItem.toString().equals("departamento")
				|| this.selectedItem.toString().equals("servicio")) {
			for (ServicioWrapper wrapper : visibleServices) {
				if (!wrapper.getExistia() && wrapper.getExiste()) {
					this.serviceIdToAdd = wrapper.getId();
					this.serviceEntityIdToAdd = wrapper.getEntidadID();
					this.adicionarServicioAEntidad();
				} else if (wrapper.getExistia() && !wrapper.getExiste()) {
					this.serviceIdToRemove = wrapper.getId();
					this.serviceEntityIdToRemove = wrapper.getEntidadID();
					this.eliminarServicioAEntidad();
				} else if (wrapper.getExistia() && wrapper.getExiste()) {
					if (!wrapper.getTeniaConsultaExterna()
							&& wrapper.getTieneConsultaExterna()) {
						cambiarTieneConsultaAServicio(wrapper.getId(),
								wrapper.getEntidadID(), true);
					}
					if (wrapper.getTeniaConsultaExterna()
							&& !wrapper.getTieneConsultaExterna()) {
						cambiarTieneConsultaAServicio(wrapper.getId(),
								wrapper.getEntidadID(), false);
					}
					if (!wrapper.getTeniaHospitalizacion()
							&& wrapper.getTieneHospitalizacion()) {
						cambiarTieneHospitalizacionAServicio(wrapper.getId(),
								wrapper.getEntidadID(), true);
					}
					if (wrapper.getTeniaHospitalizacion()
							&& !wrapper.getTieneHospitalizacion()) {
						cambiarTieneHospitalizacionAServicio(wrapper.getId(),
								wrapper.getEntidadID(), false);
					}
					if (!wrapper.getTeniaEmergencias()
							&& wrapper.getTieneEmergencias()) {
						cambiarTieneEmergenciaAServicio(wrapper.getId(),
								wrapper.getEntidadID(), true);
					}
					if (wrapper.getTeniaEmergencias()
							&& !wrapper.getTieneEmergencias()) {
						cambiarTieneEmergenciaAServicio(wrapper.getId(),
								wrapper.getEntidadID(), false);
					}

				}
			}
		}
		if (this.selectedItem.toString().equals("servicio")) {
			for (EspecialidadWrapper wrapper : visibleEspecialties) {
				if (!wrapper.getExistia() && wrapper.getExiste()) {
					this.especialtyIdToAdd = wrapper.getId();
					this.especialtyEntityIdToAdd = wrapper.getEntidadID();
					this.adicionarEspecialidadAEntidad();
				} else if (wrapper.getExistia() && !wrapper.getExiste()) {
					this.especialtyIdToRemove = wrapper.getId();
					this.especialtyEntityIdToRemove = wrapper.getEntidadID();
					this.eliminarEspecialidadAEntidad();
				}
			}
		}
	}

	private Long hospitalizacionServicioIdToChange;
	private Long hospitalizacionServicioEntityIdToChange;

	public void cambiarTieneEmergenciaAServicio(Long servId, Long entidadId,
			boolean tiene) {
		ServicioInEntidad_configuracion funct = (ServicioInEntidad_configuracion) entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion dep "
								+ "where dep.entidad.id = :entId and dep.servicio.id = :depId")
				.setParameter("entId", entidadId).setParameter("depId", servId)
				.getSingleResult();
		funct.setTieneEmergencia(tiene);
		entityManager.persist(funct);
		entityManager.flush();
	}

	public void cambiarTieneHospitalizacionAServicio(Long servId,
			Long entidadId, boolean tiene) {
		ServicioInEntidad_configuracion funct = (ServicioInEntidad_configuracion) entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion dep "
								+ "where dep.entidad.id = :entId and dep.servicio.id = :depId")
				.setParameter("entId", entidadId).setParameter("depId", servId)
				.getSingleResult();
		funct.setTieneHospitalizacion(tiene);
		entityManager.persist(funct);
		entityManager.flush();
	}

	public void cambiarTieneConsultaAServicio(Long servId, Long entidadId,
			boolean tiene) {
		ServicioInEntidad_configuracion funct = (ServicioInEntidad_configuracion) entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion dep "
								+ "where dep.entidad.id = :entId and dep.servicio.id = :depId")
				.setParameter("entId", entidadId).setParameter("depId", servId)
				.getSingleResult();
		funct.setTieneConsultaExterna(tiene);
		entityManager.persist(funct);
		entityManager.flush();
	}

	public void changeEspecialtyVisibility(Long entidadId, Long especialtyId) {
		EspecialidadInEntidad_configuracion funct = (EspecialidadInEntidad_configuracion) entityManager
				.createQuery(
						"from EspecialidadInEntidad_configuracion dep where dep.entidad.id = :entId and dep.especialidad.id = :depId")
				.setParameter("entId", entidadId)
				.setParameter("depId", especialtyId).getSingleResult();
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		entityManager.persist(funct);
		entityManager.flush();
		for (EspecialidadWrapper wrapper : visibleEspecialties) {
			if (wrapper.getId() == especialtyId) {
				wrapper.setVisible(!funct.getEliminado());
				break;
			}
		}
		try {

			// Evento para poner el servicio quirurgico
			if (funct.getEliminado())
				Events.instance().raiseEvent(
						"servicioQuirurgicoEventRemoveEspec", funct);
			else
				Events.instance().raiseEvent(
						"servicioQuirurgicoEventCreateEspec", funct);
		} catch (Exception e) {
			System.err.print("Error lanzando los eventos servicioQuirurgicoEventCreateEspec o servicioQuirurgicoEventRemoveEspec ");

		}

	}

	public void changeServiceVisibility(Long entidadId, Long servicioId) {
		ServicioInEntidad_configuracion funct = (ServicioInEntidad_configuracion) entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion dep where dep.entidad.id = :entId and dep.servicio.id = :depId")
				.setParameter("entId", entidadId)
				.setParameter("depId", servicioId).getSingleResult();
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		entityManager.persist(funct);
		entityManager.flush();
		for (ServicioWrapper wrapper : visibleServices) {
			if (wrapper.getId() == servicioId) {
				wrapper.setVisible(!funct.getEliminado());
				break;
			}
		}
        
		try {

			// Evento para poner el servicio quirurgico
			if (!funct.getEliminado())
				Events.instance().raiseEvent(
						"servicioQuirurgicoEventCreateServicio", funct);
			
		} catch (Exception e) {
			System.err.print("Error lanzando el evento servicioQuirurgicoEventCreateServicio ");

		}

	}

	public void changeDepartmentVisibility(Long entidadId, Long departmetId) {
		DepartamentoInEntidad_configuracion funct = (DepartamentoInEntidad_configuracion) entityManager
				.createQuery(
						"from DepartamentoInEntidad_configuracion dep where dep.entidad.id = :entId and dep.departamento.id = :depId")
				.setParameter("entId", entidadId)
				.setParameter("depId", departmetId).getSingleResult();
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		entityManager.persist(funct);
		entityManager.flush();
		for (DepartamentoWrapper wrapper : visibleDepartments) {
			if (wrapper.getId() == departmetId) {
				wrapper.setVisible(!funct.getEliminado());
				break;
			}
		}
	
	}
	

	

	List<DepartamentoWrapper> visibleDepartments;

	@SuppressWarnings("unchecked")
	public List<DepartamentoWrapper> departments() {
		List<Departamento_configuracion> list = entityManager
				.createQuery(
						"from Departamento_configuracion dep where dep.esClinico = true order by dep.nombre")
				.getResultList();
		visibleDepartments = new ArrayList<DepartamentoWrapper>();
		for (Departamento_configuracion dep : list) {
			DepartamentoWrapper wrapper = new DepartamentoWrapper(dep, false,
					this.selectedItem.getEntidadID());
			DepartamentoInEntidad_configuracion depEnt = entidadTieneDepartamento(
					wrapper.getEntidadID(), dep.getId());
			wrapper.setExiste(depEnt != null);
			wrapper.setExistia(depEnt != null);
			wrapper.setVisible(depEnt == null ? false
					: (depEnt.getEliminado() == null || depEnt.getEliminado() == false));
			visibleDepartments.add(wrapper);
		}
		return visibleDepartments;
	}

	List<ServicioWrapper> visibleServices;

	@SuppressWarnings("unchecked")
	public List<ServicioWrapper> services(Long departmentId) {
		List<Servicio_configuracion> list = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.departamento.id = :depId and serv.servicio = null order by serv.nombre")
				.setParameter("depId", departmentId).getResultList();
		visibleServices = new ArrayList<ServicioWrapper>();
		for (Servicio_configuracion serv : list) {
			ServicioWrapper wrapper = new ServicioWrapper(serv, false,
					this.selectedItem.getEntidadID());

			ServicioInEntidad_configuracion servEnt = entidadTieneServicio(
					wrapper.getEntidadID(), serv.getId());
			wrapper.setTieneConsultaExterna((servEnt != null)
					&& (servEnt.getTieneConsultaExterna() == null || servEnt
							.getTieneConsultaExterna() == true));
			wrapper.setTeniaConsultaExterna(wrapper.getTieneConsultaExterna());
			wrapper.setTieneHospitalizacion((servEnt != null)
					&& (servEnt.getTieneHospitalizacion() == null || servEnt
							.getTieneHospitalizacion() == true));
			wrapper.setTeniaHospitalizacion(wrapper.getTieneHospitalizacion());
			wrapper.setExiste(servEnt != null);
			wrapper.setExistia(servEnt != null);
			wrapper.setVisible(servEnt == null ? false : (servEnt
					.getEliminado() == null || servEnt.getEliminado() == false));
			wrapper.setTieneEmergencias((servEnt != null)
					&& (servEnt.getTieneEmergencia() == null || servEnt
							.getTieneEmergencia() == true));
			wrapper.setTeniaEmergencias(wrapper.getTieneEmergencias());
			visibleServices.add(wrapper);
		}
		return visibleServices;
	}

	@SuppressWarnings("unchecked")
	public List<ServicioWrapper> servicesOfServices(Long serviceId) {
		List<Servicio_configuracion> list = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.servicio.id = :servId order by serv.nombre")
				.setParameter("servId", serviceId).getResultList();
		visibleServices = new ArrayList<ServicioWrapper>();
		for (Servicio_configuracion serv : list) {
			ServicioWrapper wrapper = new ServicioWrapper(serv, false,
					this.selectedItem.getEntidadID());
			ServicioInEntidad_configuracion servEnt = entidadTieneServicio(
					wrapper.getEntidadID(), serv.getId());
			wrapper.setTieneConsultaExterna((servEnt != null)
					&& (servEnt.getTieneConsultaExterna() == null || servEnt
							.getTieneConsultaExterna() == true));
			wrapper.setTeniaConsultaExterna(wrapper.getTieneConsultaExterna());
			wrapper.setTieneHospitalizacion((servEnt != null)
					&& (servEnt.getTieneHospitalizacion() == null || servEnt
							.getTieneHospitalizacion() == true));
			wrapper.setTeniaHospitalizacion(wrapper.getTieneHospitalizacion());
			wrapper.setTieneEmergencias((servEnt != null)
					&& (servEnt.getTieneEmergencia() == null || servEnt
							.getTieneEmergencia() == true));
			wrapper.setTeniaEmergencias(wrapper.getTieneEmergencias());
			wrapper.setExiste(servEnt != null);
			wrapper.setExistia(servEnt != null);
			wrapper.setVisible(servEnt == null ? false : (servEnt
					.getEliminado() == null || servEnt.getEliminado() == false));
			visibleServices.add(wrapper);
		}
		return visibleServices;
	}

	List<EspecialidadWrapper> visibleEspecialties;

	public List<EspecialidadWrapper> getVisibleEspecialties() {
		return visibleEspecialties;
	}

	public void setVisibleEspecialties(
			List<EspecialidadWrapper> visibleEspecialties) {
		this.visibleEspecialties = visibleEspecialties;
	}

	@SuppressWarnings("unchecked")
	public List<EspecialidadWrapper> especialties(Long serviceId) {
		List<Especialidad_configuracion> list = entityManager
				.createQuery(
						"from Especialidad_configuracion esp where esp.servicio.id = :servId order by esp.nombre")
				.setParameter("servId", serviceId).getResultList();
		visibleEspecialties = new ArrayList<EspecialidadWrapper>();
		for (Especialidad_configuracion esp : list) {
			EspecialidadWrapper wrapper = new EspecialidadWrapper(esp, false,
					this.selectedItem.getEntidadID());
			EspecialidadInEntidad_configuracion espEnt = entidadTieneEspecialidad(
					wrapper.getEntidadID(), esp.getId());
			wrapper.setExiste(espEnt != null);
			wrapper.setExistia(espEnt != null);
			wrapper.setVisible(espEnt == null ? false
					: (espEnt.getEliminado() == null || espEnt.getEliminado() == false));
			visibleEspecialties.add(wrapper);
		}
		return visibleEspecialties;
	}

	private Long especialtyIdToAdd;
	private Long especialtyEntityIdToAdd;
	private Long especialtyIdToRemove;
	private Long especialtyEntityIdToRemove;

	public void eliminarEspecialidadAEntidad() {
		String nombre = null;
		EspecialidadInEntidad_configuracion especialidadInEntidad;
		try {
			especialidadInEntidad = (EspecialidadInEntidad_configuracion) entityManager
					.createQuery(
							"from EspecialidadInEntidad_configuracion esp where esp.especialidad.id = :espId and esp.entidad.id = :entId")
					.setParameter("espId", especialtyIdToRemove)
					.setParameter("entId", especialtyEntityIdToRemove)
					.getSingleResult();
			nombre = especialidadInEntidad.getEspecialidad().getNombre();
			entityManager.remove(especialidadInEntidad);
			entityManager.flush();
			
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_especialidad", nombre);
		}
	}

	public void adicionarEspecialidadAEntidad() {
		EspecialidadInEntidad_configuracion especialidadInEntidad = new EspecialidadInEntidad_configuracion();
		Especialidad_configuracion especialidad = entityManager.find(
				Especialidad_configuracion.class, especialtyIdToAdd);
		especialidadInEntidad.setEspecialidad(especialidad);
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, especialtyEntityIdToAdd);
		especialidadInEntidad.setEntidad(entidad);
		entityManager.persist(especialidadInEntidad);
		entityManager.flush();
		
		//Para poner el servicio de la especialidad como quirurgico
		//en caso de que tenga alguna quirurgica
		try {

			Events.instance().raiseEvent("servicioQuirurgicoEventCreateEspec",
					especialidadInEntidad);
		} catch (Exception e) {
			System.err.print("Error lanzando el evento servicioQuirurgicoEventCreateEspec ");

		}
		
	}

	private Long serviceIdToAdd;
	private Long serviceEntityIdToAdd;
	private Long serviceIdToRemove;
	private Long serviceEntityIdToRemove;

	public void eliminarServicioAEntidad() {
		String nombreServicio = null;
		ServicioInEntidad_configuracion servicioInEntidad = null;
		try {
			servicioInEntidad = (ServicioInEntidad_configuracion) entityManager
					.createQuery(
							"from ServicioInEntidad_configuracion serv where serv.servicio.id = :servId and serv.entidad.id = :entId")
					.setParameter("servId", serviceIdToRemove)
					.setParameter("entId", serviceEntityIdToRemove)
					.getSingleResult();
			nombreServicio = servicioInEntidad.getServicio().getNombre();
			entityManager.remove(servicioInEntidad);
			entityManager.flush();
			
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_servicio",
					nombreServicio);
		}
	}

	public void adicionarServicioAEntidad() {
		ServicioInEntidad_configuracion servicioInEntidad = new ServicioInEntidad_configuracion();
		Servicio_configuracion servicio = entityManager.find(
				Servicio_configuracion.class, serviceIdToAdd);
		servicioInEntidad.setServicio(servicio);
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, serviceEntityIdToAdd);
		servicioInEntidad.setEntidad(entidad);
		servicioInEntidad.setTieneConsultaExterna(true);
		servicioInEntidad.setTieneHospitalizacion(true);
		
		/**
		 * @author yurien 10/06/2014 Cuando se asocia el servicio a la entidad no
		 *         se creaba el campo tiene emergencia por default en true,
		 *         provocando que apareciera marcado visualmente pero no en BD
		 * **/
		servicioInEntidad.setTieneEmergencia(true);
		entityManager.persist(servicioInEntidad);
		entityManager.flush();
		
		//cuando se agrega el servicio a la entidad si tiene alguna especialidad quirurgica
		//se hace el servicio quirurgico
		try {
			Events.instance().raiseEvent("servicioQuirurgicoEventCreateServicio", servicioInEntidad);
		} catch (Exception e) {
			System.err.print("Error lanzando el evento servicioQuirurgicoEventCreateServicio ");
		}
		

	}

	private Long departamentIdToAdd;
	private Long departamentEntityIdToAdd;
	private Long departamentIdToRemove;
	private Long departamentEntityIdToRemove;

	public void eliminarDepartamentoAEntidad() {
		String nombre = null;
		DepartamentoInEntidad_configuracion departamentoInEntidad;
		try {
			departamentoInEntidad = (DepartamentoInEntidad_configuracion) entityManager
					.createQuery(
							"from DepartamentoInEntidad_configuracion dep "
									+ "where dep.departamento.id = :depId and dep.entidad.id = :entId")
					.setParameter("depId", departamentIdToRemove)
					.setParameter("entId", departamentEntityIdToRemove)
					.getSingleResult();
			nombre = departamentoInEntidad.getDepartamento().getNombre();
			entityManager.remove(departamentoInEntidad);
			entityManager.flush();
			
//			Events.instance().raiseEvent("servicioQuirurgicoEventCreate", servicioInEntidadList(departamentoInEntidad.getDepartamento().getServicios()),Actions.REMOVE);
		} catch (ConstraintViolationException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_departamento");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_departamento", nombre);
		}
		
	}

	@Transactional
	public void adicionarDepartamentoAEntidad() {
		DepartamentoInEntidad_configuracion departamentoInEntidad = new DepartamentoInEntidad_configuracion();
		Departamento_configuracion departamento = entityManager.find(
				Departamento_configuracion.class, departamentIdToAdd);
		departamentoInEntidad.setDepartamento(departamento);
		Entidad_configuracion entidad = (Entidad_configuracion) entityManager
				.createQuery(
						"from Entidad_configuracion ent where ent.id = :entId")
				.setParameter("entId", departamentEntityIdToAdd)
				.getSingleResult();
		departamentoInEntidad.setEntidad(entidad);
		entityManager.persist(departamentoInEntidad);
		entityManager.flush();
		
//		Events.instance().raiseEvent("servicioQuirurgicoEventCreate", servicioInEntidadList(departamentoInEntidad.getDepartamento().getServicios()),Actions.CREATE);

	
	}

	@SuppressWarnings("unchecked")
	public DepartamentoInEntidad_configuracion entidadTieneDepartamento(
			Long entidadId, Long departamentoId) {
		List<DepartamentoInEntidad_configuracion> result = entityManager
				.createQuery(
						"from DepartamentoInEntidad_configuracion dep "
								+ "where dep.departamento.id = :depId and dep.entidad.id = :entId")
				.setParameter("depId", departamentoId)
				.setParameter("entId", entidadId).getResultList();
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ServicioInEntidad_configuracion entidadTieneServicio(Long entidadId,
			Long servicioId) {
		List<ServicioInEntidad_configuracion> result = entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion serv "
								+ "where serv.servicio.id = :servId and serv.entidad.id = :entId")
				.setParameter("servId", servicioId)
				.setParameter("entId", entidadId).getResultList();
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public EspecialidadInEntidad_configuracion entidadTieneEspecialidad(
			Long entidadId, Long especialidadId) {
		List<EspecialidadInEntidad_configuracion> result = entityManager
				.createQuery(
						"from EspecialidadInEntidad_configuracion esp "
								+ "where esp.especialidad.id = :espId and esp.entidad.id = :entId")
				.setParameter("espId", especialidadId)
				.setParameter("entId", entidadId).getResultList();
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public void setSelectedItem(Long entidadId) {
		Entidad_configuracion ent = entityManager.find(
				Entidad_configuracion.class, entidadId);
		this.selectedItem = new EntidadWrapper(ent, false, ent.getId());
	}

	public void setSelectedItem(Long depId, Long entidadId) {
		Departamento_configuracion dep = entityManager.find(
				Departamento_configuracion.class, depId);
		this.selectedItem = new DepartamentoWrapper(dep, false, entidadId);
	}

	public void setSelectedItem(Long servId, Long entidadId, Integer dummy) {
		Servicio_configuracion serv = entityManager.find(
				Servicio_configuracion.class, servId);
		this.selectedItem = new ServicioWrapper(serv, false, entidadId);
	}

	@SuppressWarnings("rawtypes")
	public void setSelectedItem(ITreeData selectedItem, TreeNode node) {
		this.selectedItem = selectedItem;
		this.selectedTreenode = node;
	}

	public DepartamentosPorEntidadTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(DepartamentosPorEntidadTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public Long getDepartamentIdToAdd() {
		return departamentIdToAdd;
	}

	public void setDepartamentIdToAdd(Long departamentIdToAdd) {
		this.departamentIdToAdd = departamentIdToAdd;
	}

	public Long getDepartamentEntityIdToAdd() {
		return departamentEntityIdToAdd;
	}

	public void setDepartamentEntityIdToAdd(Long departamentEntityIdToAdd) {
		this.departamentEntityIdToAdd = departamentEntityIdToAdd;
	}

	public Long getDepartamentIdToRemove() {
		return departamentIdToRemove;
	}

	public void setDepartamentIdToRemove(Long departamentIdToRemove) {
		this.departamentIdToRemove = departamentIdToRemove;
	}

	public Long getDepartamentEntityIdToRemove() {
		return departamentEntityIdToRemove;
	}

	public void setDepartamentEntityIdToRemove(Long departamentEntityIdToRemove) {
		this.departamentEntityIdToRemove = departamentEntityIdToRemove;
	}

	public Long getServiceIdToAdd() {
		return serviceIdToAdd;
	}

	public void setServiceIdToAdd(Long serviceIdToAdd) {
		this.serviceIdToAdd = serviceIdToAdd;
	}

	public Long getServiceEntityIdToAdd() {
		return serviceEntityIdToAdd;
	}

	public void setServiceEntityIdToAdd(Long serviceEntityIdToAdd) {
		this.serviceEntityIdToAdd = serviceEntityIdToAdd;
	}

	public Long getServiceIdToRemove() {
		return serviceIdToRemove;
	}

	public void setServiceIdToRemove(Long serviceIdToRemove) {
		this.serviceIdToRemove = serviceIdToRemove;
	}

	public Long getServiceEntityIdToRemove() {
		return serviceEntityIdToRemove;
	}

	public void setServiceEntityIdToRemove(Long serviceEntityIdToRemove) {
		this.serviceEntityIdToRemove = serviceEntityIdToRemove;
	}

	public Long getEspecialtyIdToAdd() {
		return especialtyIdToAdd;
	}

	public void setEspecialtyIdToAdd(Long especialtyIdToAdd) {
		this.especialtyIdToAdd = especialtyIdToAdd;
	}

	public Long getEspecialtyEntityIdToAdd() {
		return especialtyEntityIdToAdd;
	}

	public void setEspecialtyEntityIdToAdd(Long especialtyEntityIdToAdd) {
		this.especialtyEntityIdToAdd = especialtyEntityIdToAdd;
	}

	public Long getEspecialtyIdToRemove() {
		return especialtyIdToRemove;
	}

	public void setEspecialtyIdToRemove(Long especialtyIdToRemove) {
		this.especialtyIdToRemove = especialtyIdToRemove;
	}

	public Long getEspecialtyEntityIdToRemove() {
		return especialtyEntityIdToRemove;
	}

	public void setEspecialtyEntityIdToRemove(Long especialtyEntityIdToRemove) {
		this.especialtyEntityIdToRemove = especialtyEntityIdToRemove;
	}

	public ITreeData getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ITreeData selectedItem) {
		this.selectedItem = selectedItem;
	}

	public Long getHospitalizacionServicioIdToChange() {
		return hospitalizacionServicioIdToChange;
	}

	public void setHospitalizacionServicioIdToChange(
			Long hospitalizacionServicioIdToChange) {
		this.hospitalizacionServicioIdToChange = hospitalizacionServicioIdToChange;
	}

	public Long getHospitalizacionServicioEntityIdToChange() {
		return hospitalizacionServicioEntityIdToChange;
	}

	public void setHospitalizacionServicioEntityIdToChange(
			Long hospitalizacionServicioEntityIdToChange) {
		this.hospitalizacionServicioEntityIdToChange = hospitalizacionServicioEntityIdToChange;
	}

	public List<ServicioWrapper> getVisibleServices() {
		return visibleServices;
	}

	public void setVisibleServices(List<ServicioWrapper> visibleServices) {
		this.visibleServices = visibleServices;
	}

}
