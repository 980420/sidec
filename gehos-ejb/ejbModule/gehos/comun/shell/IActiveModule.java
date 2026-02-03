package gehos.comun.shell;

import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.util.List;

import javax.ejb.Remove;

import org.jboss.seam.annotations.Destroy;

public interface IActiveModule {

	@Remove
	@Destroy
	public void destructor();

	public abstract void loadCurrentUserMenu();

	public abstract List<Funcionalidad> loadCurrentUserMenu(String module);

	public abstract List<Funcionalidad> activeModuleChildren();

	public abstract void setActiveModuleName(String activeModuleName);

	public abstract String activeSubModulePhysicalName();

	// @BypassInterceptors
	public abstract void setActiveModuleName(String activeModuleName,
			boolean showmenu);

	public abstract void loadUserFavorites();

	public abstract void expandOrCollapseCategory();

	public abstract void expandCategory(String categoryToModify);

	public abstract List<Funcionalidad> getModuleMenu();

	public abstract void setModuleMenu(List<Funcionalidad> moduleMenu);

	public abstract List<Funcionalidad> getUserfavorites();

	public abstract void setUserfavorites(List<Funcionalidad> userfavorites);

	public abstract boolean isShowMenu();

	public abstract void setShowMenu(boolean showMenu);

	public abstract Funcionalidad getActiveModule();

	public abstract void setActiveModule(Funcionalidad activeModule);

	public abstract String getActiveModuleName();

	public abstract String getCategoryToModify();

	public abstract void setCategoryToModify(String categoryToModify);

	@SuppressWarnings("unchecked")
	public abstract java.util.Hashtable getCategoriesExpanded();

	@SuppressWarnings("unchecked")
	public abstract void setCategoriesExpanded(
			java.util.Hashtable categoriesExpanded);
	public Boolean getBrothers();
	public void setBrothers(Boolean brothers);
	public void detectBrothers(Long f);

}