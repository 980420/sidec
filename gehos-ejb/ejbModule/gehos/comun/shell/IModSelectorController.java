package gehos.comun.shell;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.configuracion.management.entity.Entidad_configuracion;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remove;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;

public interface IModSelectorController {

	@Create
	public void constructor();

	@Remove
	@Destroy
	public void destructor();

	public abstract String subir();

	public abstract void selectEntity(Long entityId);


	public abstract List<Serializable> entitiesFactory();


	public abstract List<Funcionalidad> modulesFactory();

	public abstract List<Funcionalidad> grandsonModules(String moduleName);

	public abstract Long getSelectedEntityId();

	public abstract void setSelectedEntityId(Long selectedEntityId);

	public abstract Long getSelectedModuleTypeId();

	public abstract void setSelectedModuleTypeId(Long selectedModuleTypeId);

	public abstract Funcionalidad getSelectedModule();

	public abstract void setSelectedModule(Funcionalidad selectedModule);

	public abstract Entidad_configuracion getSelectedEntity();

	public abstract void setSelectedEntity(Entidad_configuracion selectedEntity);

	public String getSelectedEntityShortName();

	public String getSelectedEntityShortNameTitle();

	public Boolean getHasChanged();

	public void setHasChanged(Boolean hasChanged);

	public List<Serializable> entitiesList();

	public void selectDefaultEntity(List<Serializable> entities);

	public void selectDefaultEntity();

}