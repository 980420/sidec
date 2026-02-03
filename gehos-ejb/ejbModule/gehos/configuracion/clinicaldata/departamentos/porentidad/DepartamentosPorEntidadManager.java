package gehos.configuracion.clinicaldata.departamentos.porentidad;

import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.DepartamentosPorEntidadTreeBuilder;
import gehos.configuracion.clinicaldata.departamentos.porentidad.treebuilders.model.ITreeData;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.TreeNode;

@Name("departamentosPorEntidadManager")
public class DepartamentosPorEntidadManager {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	@In(required = false, create = true, value = "departamentosPorEntidadTreeBuilder")
	DepartamentosPorEntidadTreeBuilder treeBuilder;

	private ITreeData selectedItem;
	@SuppressWarnings( { "unchecked", "unused" })
	private TreeNode selectedTreenode;

	public HashMap<String, DepartamentoInEntidad_configuracion> departmentExistence = new HashMap<String, DepartamentoInEntidad_configuracion>();
	public HashMap<String, ServicioInEntidad_configuracion> servicesExistence = new HashMap<String, ServicioInEntidad_configuracion>();
	public HashMap<String, EspecialidadInEntidad_configuracion> especialtiesExistence = new HashMap<String, EspecialidadInEntidad_configuracion>();

	@Create
	public void constructor() {
		//System.out.println("HASHCODE DEL BEAN: " + this.hashCode());
		// departmentExistence.clear(); servicesExistence.clear();
		// especialtiesExistence.clear();
	}

	public Boolean servicioTieneConsulta(Long entidadId, Long servicioId) {
		String key = entidadId.toString() + "-" + servicioId.toString();
		if (servicesExistence.get(key).getTieneConsultaExterna() == null
				|| servicesExistence.get(key).getTieneConsultaExterna() == true)
			return true;
		else
			return false;
	}

	public Boolean servicioTieneHospitalizacion(Long entidadId,
			Long servicioId) {
		String key = entidadId.toString() + "-" + servicioId.toString();
		if (servicesExistence.get(key).getTieneHospitalizacion() == null
				|| servicesExistence.get(key).getTieneHospitalizacion() == true)
			return true;
		else
			return false;
	}

	private Long hospitalizacionServicioIdToChange;
	private Long hospitalizacionServicioEntityIdToChange;

	public void cambiarTieneHospitalizacionAServicio() {
		String key = hospitalizacionServicioEntityIdToChange.toString() + "-"
				+ hospitalizacionServicioIdToChange.toString();
		ServicioInEntidad_configuracion funct = entityManager.find(
				ServicioInEntidad_configuracion.class, servicesExistence.get(
						key).getId());
		if (funct.getTieneHospitalizacion() == null
				|| funct.getTieneHospitalizacion() == true)
			funct.setTieneHospitalizacion(false);
		else
			funct.setTieneHospitalizacion(true);
		entityManager.persist(funct);
		entityManager.flush();
		servicesExistence.remove(key);
		servicesExistence.put(key, funct);
	}

	private Long consultaServicioIdToChange;
	private Long consultaServicioEntityIdToChange;

	public void cambiarTieneConsultaAServicio() {
		String key = consultaServicioEntityIdToChange.toString() + "-"
				+ consultaServicioIdToChange.toString();
		ServicioInEntidad_configuracion funct = entityManager.find(
				ServicioInEntidad_configuracion.class, servicesExistence.get(
						key).getId());
		if (funct.getTieneConsultaExterna() == null
				|| funct.getTieneConsultaExterna() == true)
			funct.setTieneConsultaExterna(false);
		else
			funct.setTieneConsultaExterna(true);
		entityManager.persist(funct);
		entityManager.flush();
		servicesExistence.remove(key);
		servicesExistence.put(key, funct);
	}

	public Boolean especialtyVisibility(Long entidadId, Long especialtyId) {
		String key = entidadId.toString() + "-" + especialtyId.toString();
		if (especialtiesExistence.get(key).getEliminado() == null
				|| especialtiesExistence.get(key).getEliminado() == false)
			return true;
		else
			return false;
	}

	public Boolean serviceVisibility(Long entidadId, Long servicioId) {
		String key = entidadId.toString() + "-" + servicioId.toString();
		if (servicesExistence.get(key).getEliminado() == null
				|| servicesExistence.get(key).getEliminado() == false)
			return true;
		else
			return false;
	}

	public Boolean departmentVisibility(Long entidadId, Long departmentId) {
		String key = entidadId.toString() + "-" + departmentId.toString();
		if (departmentExistence.get(key).getEliminado() == null
				|| departmentExistence.get(key).getEliminado() == false)
			return true;
		else
			return false;
	}

	public void changeEspecialtyVisibility(Long entidadId,
			Long especialtyId) {
		String key = entidadId.toString() + "-" + especialtyId.toString();
		EspecialidadInEntidad_configuracion funct = entityManager.find(
				EspecialidadInEntidad_configuracion.class,
				especialtiesExistence.get(key).getId());
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		entityManager.persist(funct);
		entityManager.flush();
		especialtiesExistence.remove(key);
		especialtiesExistence.put(key, funct);
	}

	public void changeServiceVisibility(Long entidadId, Long servicioId) {
		String key = entidadId.toString() + "-" + servicioId.toString();
		ServicioInEntidad_configuracion funct = entityManager.find(
				ServicioInEntidad_configuracion.class, servicesExistence.get(
						key).getId());
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		entityManager.persist(funct);
		entityManager.flush();
		servicesExistence.remove(key);
		servicesExistence.put(key, funct);
	}

	public void changeDepartmentVisibility(Long entidadId,
			Long departmetId) {
		String key = entidadId.toString() + "-" + departmetId.toString();
		DepartamentoInEntidad_configuracion funct = entityManager
				.merge(departmentExistence.get(key));
		if (funct.getEliminado() == null || funct.getEliminado() == false)
			funct.setEliminado(true);
		else
			funct.setEliminado(false);
		entityManager.persist(funct);
		entityManager.flush();
		departmentExistence.remove(key);
		departmentExistence.put(key, funct);
	}

	@SuppressWarnings("unchecked")
	public List<Departamento_configuracion> departments() {
		return entityManager
				.createQuery(
						"from Departamento_configuracion dep where dep.esClinico = true")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Servicio_configuracion> services(Long departmentId) {
		List<Servicio_configuracion> result = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.departamento.id = :depId")
				.setParameter("depId", departmentId).getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Servicio_configuracion> servicesOfServices(Long serviceId) {
		List<Servicio_configuracion> result = entityManager
				.createQuery(
						"from Servicio_configuracion serv where serv.servicio.id = :servId")
				.setParameter("servId", serviceId).getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Especialidad_configuracion> especialties(Long serviceId) {
		List<Especialidad_configuracion> result = entityManager
				.createQuery(
						"from Especialidad_configuracion esp where esp.servicio.id = :servId")
				.setParameter("servId", serviceId).getResultList();
		return result;
	}

	private Long especialtyIdToAdd;
	private Long especialtyEntityIdToAdd;
	private Long especialtyIdToRemove;
	private Long especialtyEntityIdToRemove;

	public void eliminarEspecialidadAEntidad() {
		try {
			EspecialidadInEntidad_configuracion especialidadInEntidad = (EspecialidadInEntidad_configuracion) entityManager
					.createQuery(
							"from EspecialidadInEntidad_configuracion esp where esp.especialidad.id = :espId and esp.entidad.id = :entId")
					.setParameter("espId", especialtyIdToRemove).setParameter(
							"entId", especialtyEntityIdToRemove)
					.getSingleResult();
			entityManager.remove(especialidadInEntidad);
			entityManager.flush();
			String key = especialtyEntityIdToRemove.toString() + "-"
					+ especialtyIdToRemove.toString();
			especialtiesExistence.put(key, null);
		} catch (ConstraintViolationException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_especialidad");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_especialidad");
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
		String key = especialtyEntityIdToAdd.toString() + "-"
				+ especialtyIdToAdd.toString();
		especialtiesExistence.put(key, especialidadInEntidad);
	}

	private Long serviceIdToAdd;
	private Long serviceEntityIdToAdd;
	private Long serviceIdToRemove;
	private Long serviceEntityIdToRemove;

	public void eliminarServicioAEntidad() {
		try {
			ServicioInEntidad_configuracion servicioInEntidad = (ServicioInEntidad_configuracion) entityManager
					.createQuery(
							"from ServicioInEntidad_configuracion serv where serv.servicio.id = :servId and serv.entidad.id = :entId")
					.setParameter("servId", serviceIdToRemove).setParameter(
							"entId", serviceEntityIdToRemove).getSingleResult();
			entityManager.remove(servicioInEntidad);
			entityManager.flush();
			String key = serviceEntityIdToRemove.toString() + "-"
					+ serviceIdToRemove.toString();
			servicesExistence.put(key, null);
		} catch (ConstraintViolationException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_servicio");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_servicio");
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
		entityManager.persist(servicioInEntidad);
		entityManager.flush();
		String key = serviceEntityIdToAdd.toString() + "-"
				+ serviceIdToAdd.toString();
		servicesExistence.put(key, servicioInEntidad);
	}

	private Long departamentIdToAdd;
	private Long departamentEntityIdToAdd;
	private Long departamentIdToRemove;
	private Long departamentEntityIdToRemove;

	public void eliminarDepartamentoAEntidad() {
		try {
			DepartamentoInEntidad_configuracion departamentoInEntidad = (DepartamentoInEntidad_configuracion) entityManager
					.createQuery(
							"from DepartamentoInEntidad_configuracion dep "
									+ "where dep.departamento.id = :depId and dep.entidad.id = :entId")
					.setParameter("depId", departamentIdToRemove).setParameter(
							"entId", departamentEntityIdToRemove)
					.getSingleResult();
			entityManager.remove(departamentoInEntidad);
			entityManager.flush();
			String key = departamentEntityIdToRemove.toString() + "-"
					+ departamentIdToRemove.toString();
			departmentExistence.put(key, null);
		} catch (ConstraintViolationException e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_departamento");
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error",
					Severity.ERROR, "conf_error_delete_departamento");
		}
	}

	public void adicionarDepartamentoAEntidad() {
		DepartamentoInEntidad_configuracion departamentoInEntidad = new DepartamentoInEntidad_configuracion();
		Departamento_configuracion departamento = entityManager.find(
				Departamento_configuracion.class, departamentIdToAdd);
		departamentoInEntidad.setDepartamento(departamento);
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, departamentEntityIdToAdd);
		departamentoInEntidad.setEntidad(entidad);
		entityManager.persist(departamentoInEntidad);
		entityManager.flush();
		String key = departamentEntityIdToAdd.toString() + "-"
				+ departamentIdToAdd.toString();
		departmentExistence.put(key, departamentoInEntidad);
	}

	@SuppressWarnings("unchecked")
	public Boolean entidadTieneDepartamento(Long entidadId,
			Long departamentoId) {
		String key = entidadId.toString() + "-" + departamentoId.toString();
		if (departmentExistence.containsKey(key)) {
			if (departmentExistence.get(key) == null)
				return false;
			else
				return true;
		}
		List<DepartamentoInEntidad_configuracion> result = entityManager
				.createQuery(
						"from DepartamentoInEntidad_configuracion dep "
								+ "where dep.departamento.id = :depId and dep.entidad.id = :entId")
				.setParameter("depId", departamentoId).setParameter("entId",
						entidadId).getResultList();
		if (result.size() > 0) {
			departmentExistence.put(key, result.get(0));
			return true;
		} else {
			departmentExistence.put(key, null);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean entidadTieneServicio(Long entidadId, Long servicioId) {
		String key = entidadId.toString() + "-" + servicioId.toString();
		if (servicesExistence.containsKey(key)) {
			if (servicesExistence.get(key) == null)
				return false;
			else
				return true;
		}
		List<ServicioInEntidad_configuracion> result = entityManager
				.createQuery(
						"from ServicioInEntidad_configuracion serv "
								+ "where serv.servicio.id = :servId and serv.entidad.id = :entId")
				.setParameter("servId", servicioId).setParameter("entId",
						entidadId).getResultList();
		if (result.size() > 0) {
			servicesExistence.put(key, result.get(0));
			return true;
		} else {
			servicesExistence.put(key, null);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean entidadTieneEspecialidad(Long entidadId,
			Long especialidadId) {
		String key = entidadId.toString() + "-" + especialidadId.toString();
		if (especialtiesExistence.containsKey(key)) {
			if (especialtiesExistence.get(key) == null)
				return false;
			else
				return true;
		}
		List<EspecialidadInEntidad_configuracion> result = entityManager
				.createQuery(
						"from EspecialidadInEntidad_configuracion esp "
								+ "where esp.especialidad.id = :espId and esp.entidad.id = :entId")
				.setParameter("espId", especialidadId).setParameter("entId",
						entidadId).getResultList();
		if (result.size() > 0) {
			especialtiesExistence.put(key, result.get(0));
			return true;
		} else {
			especialtiesExistence.put(key, null);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
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

	public void setDepartamentEntityIdToRemove(
			Long departamentEntityIdToRemove) {
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

	public Long getConsultaServicioIdToChange() {
		return consultaServicioIdToChange;
	}

	public void setConsultaServicioIdToChange(Long consultaServicioIdToChange) {
		this.consultaServicioIdToChange = consultaServicioIdToChange;
	}

	public Long getConsultaServicioEntityIdToChange() {
		return consultaServicioEntityIdToChange;
	}

	public void setConsultaServicioEntityIdToChange(
			Long consultaServicioEntityIdToChange) {
		this.consultaServicioEntityIdToChange = consultaServicioEntityIdToChange;
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

}
