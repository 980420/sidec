package gehos.comun.shell;

import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.UserTools;
import gehos.autorizacion.management.logical.LogicPermissionResolver;
import gehos.comun.anillo.AnilloHisConfig;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.ensayo.ensayo_disenno.gestionarEstudio.verEstudioControlador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.mq.il.uil2.msgs.GetIDMsg;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Scope(ScopeType.SESSION)
@Name("modSelectorController")
@Stateful
public class ModSelectorController implements IModSelectorController {

	@PersistenceContext(unitName = "gehos")
	EntityManager entityManager;
	@In
	IActiveModule activeModule;
	private static Integer globalEntitiesReloads = 0;
	private Integer entitiesReloads = 0;
	private List<Serializable> entities;

	private List<Funcionalidad> modules;
	@In("logicPermissionResolver")
	LogicPermissionResolver permissionResolver;
    @In  
    UserTools userTools;
	private Long selectedEntityId = null;
	private Long selectedModuleTypeId = null;
	private Funcionalidad selectedModule = null;
	private Entidad_configuracion selectedEntity = null;
	private Boolean hasChanged = true;
	
		
	@Remove
	@Destroy
	public void destructor() {
	}

	@Create
	public void constructor() {
		this.entitiesReloads = globalEntitiesReloads;
	}

	public String subir() {
		if (this.selectedModuleTypeId != null) {
			this.setSelectedEntityId(this.selectedEntityId);
			return "/modCommons/modSelector/selector.xhtml";
		}
		if (this.selectedModuleTypeId == null) {
			return "/modCommons/entitySelector/selector.xhtml";
		}
		return "/modCommons/modSelector/selector.xhtml";
	}

	public void selectEntity(Long entityId) {
		// activeModule.setActiveModuleName("alas_his", false);
		if (!(this.selectedEntityId == entityId))
			hasChanged = true;
		this.selectedEntityId = entityId;
	}

	private Funcionalidad moduloConfiguracion;

	public static void reloadsEntities() {
		globalEntitiesReloads++;
				
	}

	@SuppressWarnings("unchecked")
	@Factory(value = "entities", scope = ScopeType.PAGE)
	public List<Serializable> entitiesFactory() {
		if (entities == null || entitiesReloads < globalEntitiesReloads) {
			this.entitiesReloads = globalEntitiesReloads;
			entities = new ArrayList<Serializable>();
			
			/**
			 * @author yurien 27/03/2014
			 * Se agrega la nueva restriccion para que muestre las entidades 
			 * que pertenecen al anillo configurado
			 * **/
			Usuario user = (Usuario)Component.getInstance("user");
			entities.addAll(entityManager
							.createQuery(
									"select distinct ent " +
									"from Entidad_configuracion ent " +
									"inner join ent.servicioInEntidads servInEnt inner join servInEnt.usuarios u with u.id=:idUser "
									        + "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//											+ "where ent.perteneceARhio = true "
											+ "and (ent.eliminado = null or ent.eliminado = false) "
											+ "order by ent.nombre")
							.setParameter("idUser", user.getId()/*userTools.getUser().getId()*/).getResultList());
			moduloConfiguracion = (Funcionalidad) entityManager.createQuery(
					"from Funcionalidad mod where mod.nombre='configuracion'")
					.getSingleResult();
			entities.add(moduloConfiguracion);

		}

		return this.entities;
	}

	public void selectDefaultEntity(List<Serializable> entities) {
		if (entities != null && entities.size() > 0
				&& entities.get(0) instanceof Entidad_configuracion) {
			selectedEntityId = ((Entidad_configuracion) entities.get(0))
					.getId();
		}
	}

	public void selectDefaultEntity() {
		if (entities != null && entities.size() > 0
				&& entities.get(0) instanceof Entidad_configuracion) {
			selectedEntityId = ((Entidad_configuracion) entities.get(0))
					.getId();
		}
	}

	@Factory(value = "modules", scope = ScopeType.PAGE)
	public List<Funcionalidad> modulesFactory() {
		if (modules == null) {
			modules = this.grandsonModules(this.activeModule
					.getActiveModuleName());
		}
		return this.modules;
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> grandsonModules(String moduleName) {
		List<Funcionalidad> result = new ArrayList<Funcionalidad>();
		if (selectedEntityId != null && selectedModuleTypeId == null) {
			List<Funcionalidad> tiposModulosExistentesParaEntidad = entityManager
					.createQuery(
							"select distinct func.funcionalidadPadre.funcionalidadPadre "
									+ "from Funcionalidad func where func.esModulo=true "
									+ "and func.moduloFisico=true and func.entidad.id=:entId "
									+ "and (func.eliminado = false or func.eliminado = null) "
									+ "and func.funcionalidadPadre.funcionalidadPadre.id != -1 "
									+ "and (func.funcionalidadPadre.funcionalidadPadre.eliminado = null or func.funcionalidadPadre.funcionalidadPadre.eliminado = false) "
									+ "order by func.funcionalidadPadre.funcionalidadPadre.label")
					.setParameter("entId", this.selectedEntityId)
					.getResultList();
			for (int i = 0; i < tiposModulosExistentesParaEntidad.size(); i++) {
				List<Funcionalidad> instanciasModulo = entityManager
						.createQuery(
								"from Funcionalidad func where func.esModulo=true "
										+ "and func.moduloFisico=true "
										+ "and (func.eliminado = false or func.eliminado = null) "
										+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modId "
										+ "and func.entidad.id=:entId")
						.setParameter(
								"modId",
								tiposModulosExistentesParaEntidad.get(i)
										.getId()).setParameter("entId",
								this.selectedEntityId).getResultList();
				if (instanciasModulo.size() > 1) {
					int conpermisocount = 0;
					Funcionalidad unicaconpermiso = null;
					for (Funcionalidad funcionalidad : instanciasModulo) {
						if (permissionResolver
								.currentUserCanSeeThisFunctionality(
										funcionalidad, funcionalidad, false)) {
							conpermisocount++;
							unicaconpermiso = funcionalidad;
						}
					}
					if (conpermisocount == 1)
						result.add(unicaconpermiso);
					else if (conpermisocount > 1)
						result.add(tiposModulosExistentesParaEntidad.get(i));
				} else if (instanciasModulo.size() == 1) {
					if (permissionResolver.currentUserCanSeeThisFunctionality(
							instanciasModulo.get(0), instanciasModulo.get(0),
							false)) {
						result.add(instanciasModulo.get(0));
					}
				}
			}
			Funcionalidad configuracion = (Funcionalidad) entityManager
					.createQuery(
							"from Funcionalidad f where f.nombre='configuracion'")
					.getSingleResult();
			result.add(configuracion);
		} else if (selectedEntityId != null && selectedModuleTypeId != null) {
			List<Funcionalidad> instanciasModulo = entityManager
					.createQuery(
							"from Funcionalidad func where func.esModulo=true "
									+ "and func.moduloFisico=true "
									+ "and (func.eliminado = false or func.eliminado = null) "
									+ "and func.funcionalidadPadre.funcionalidadPadre.id = :modId "
									+ "and func.entidad.id=:entId")
					.setParameter("modId", this.selectedModuleTypeId)
					.setParameter("entId", this.selectedEntityId)
					.getResultList();
			for (Funcionalidad funcionalidad : instanciasModulo) {
				if (permissionResolver.currentUserCanSeeThisFunctionality(
						funcionalidad, funcionalidad, false)) {
					result.add(funcionalidad);
				}
			}
		}
		return result;
	}

	public Long getSelectedEntityId() {
		System.out.println("CAMBIO "+selectedEntityId);
		verEstudioControlador.idEntidad = selectedEntityId;
		return selectedEntityId;
	}
	
	

	public void setSelectedEntityId(Long selectedEntityId) {

		if (this.selectedEntityId != null
				&& (!(this.selectedEntityId==selectedEntityId)))
			hasChanged = true;

		activeModule.setActiveModuleName("XAVIA_his", false);

		this.selectedEntityId = selectedEntityId;
		this.selectedEntity = entityManager.find(Entidad_configuracion.class,
				this.selectedEntityId);
		this.selectedModuleTypeId = null;
		this.modules = null;
	}

	public Long getSelectedModuleTypeId() {
		return selectedModuleTypeId;
	}

	
	public void setSelectedModuleTypeId(Long selectedModuleTypeId) {
		try{
			
			
			this.selectedModuleTypeId = selectedModuleTypeId;
			this.selectedModule = entityManager.find(Funcionalidad.class,
					this.selectedModuleTypeId);
			this.modules = null;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Funcionalidad getSelectedModule() {
		return selectedModule;
	}

	public void setSelectedModule(Funcionalidad selectedModule) {
		this.selectedModule = selectedModule;
	}

	public Entidad_configuracion getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(Entidad_configuracion selectedEntity) {
		this.selectedEntity = selectedEntity;
	}

	public Funcionalidad getModuloConfiguracion() {
		return moduloConfiguracion;
	}

	public void setModuloConfiguracion(Funcionalidad moduloConfiguracion) {
		this.moduloConfiguracion = moduloConfiguracion;
	}

	public String getSelectedEntityShortName() {
		if (selectedEntity.getNombre().length() <= 18)
			return selectedEntity.getNombre();
		else
			return selectedEntity.getNombre().substring(0, 16) + "...";
	}

	public String getSelectedEntityShortNameTitle() {
		if (selectedEntity.getNombre()
				.equals(this.getSelectedEntityShortName()))
			return "";
		else
			return selectedEntity.getNombre();
	}

	public Boolean getHasChanged() {
		return hasChanged;
	}

	public void setHasChanged(Boolean hasChanged) {
		this.hasChanged = hasChanged;
	}

	public List<Serializable> entitiesList() {
		return entities;
	}
	
	
	
	
}
