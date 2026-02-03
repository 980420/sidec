package gehos.comun.shell;

import gehos.autenticacion.entity.Usuario;
import gehos.autorizacion.management.logical.LogicPermissionResolver;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.configuracion.management.entity.Entidad_configuracion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;

@AutoCreate
@Name(value = "activeModule")
@Scope(ScopeType.SESSION)
@Stateful
public class ActiveModule implements IActiveModule {

	@PersistenceContext(unitName = "gehos")
	private EntityManager em;

	LogicPermissionResolver permissionResolver;
	IBitacora bitacora;
	Usuario user;
	IModSelectorController modSelectorController;

	private void loadDependencies() {
		this.permissionResolver = (LogicPermissionResolver) Component
				.getInstance("logicPermissionResolver");
		this.bitacora = (IBitacora) Component.getInstance("bitacora");
		this.user = (Usuario) Component.getInstance("user");
		this.modSelectorController = (IModSelectorController) Component
				.getInstance("modSelectorController");
	}

	private String activeModuleName;
	private Funcionalidad activeModule;
	private boolean showMenu;
	private List<Funcionalidad> moduleMenu = new ArrayList<Funcionalidad>();
	private List<Funcionalidad> userfavorites;

	@Remove
	@Destroy
	public void destructor() {

	}

	@SuppressWarnings("unchecked")
	public void loadCurrentUserMenu() {
		this.moduleMenu = new ArrayList<Funcionalidad>();
		Funcionalidad currentModule = (Funcionalidad) em
				.createQuery("from Funcionalidad m " + "where m.nombre = :name")
				.setParameter("name", this.activeModuleName).getSingleResult();
		while (currentModule != null) {
			List<Funcionalidad> funcs = em
					.createQuery(
							"select distinct m from Funcionalidad m where "
									+ "(m.esModulo = null or m.esModulo = false) "
									+ "and m.funcionalidadPadre.nombre = :nombre "
									+ "order by m.orden")
					.setParameter("nombre", currentModule.getNombre())
					.getResultList();
			this.moduleMenu.addAll(0, funcs);
			currentModule = currentModule.getFuncionalidadPadre();
		}
		loadUserFavorites();
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> loadCurrentUserMenu(String moduleName) {
		List<Funcionalidad> result = new ArrayList<Funcionalidad>();
		Funcionalidad currentModule = (Funcionalidad) em
				.createQuery("from Funcionalidad m " + "where m.nombre = :name")
				.setParameter("name", moduleName).getSingleResult();
		while (currentModule != null) {
			List<Funcionalidad> funcs = em
					.createQuery(
							"select distinct m from Funcionalidad m where "
									+ "(m.esModulo = null or m.esModulo = false) "
									+ "and m.funcionalidadPadre.nombre = :nombre "
									+ "order by m.orden")
					.setParameter("nombre", currentModule.getNombre())
					.getResultList();
			result.addAll(0, funcs);
			currentModule = currentModule.getFuncionalidadPadre();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> activeModuleChildren() {
		return em
				.createQuery(
						"select m from Funcionalidad m where m.funcionalidadPadre.id = :id order by m.label")
				.setParameter("id", this.activeModule.getId()).getResultList();
	}

	public void setActiveModuleName(String activeModuleName) {
		this.setActiveModuleName(activeModuleName, true);
	}

	public String activeSubModulePhysicalName() {
		return this.activeModule.getNombre();
	}

	public void setActiveModuleName(String activeModuleName, boolean showmenu) {
		try {
			this.loadDependencies();
			this.showMenu = showmenu;
			if (this.activeModuleName == null
					|| !this.activeModuleName.equals(activeModuleName)) {
				try {
					activeModule = (Funcionalidad) em
							.createQuery(
									"select m from Funcionalidad m where m.nombre = :nombre")
							.setParameter("nombre", activeModuleName)
							.getSingleResult();
					// Events.instance().raiseEvent("moduleActivatedEvent",
					// activeModule);
				} catch (Exception e) {
					return;
				}
				try {
					bitacora.registrarModuloAccedido(activeModule.getId());
					detectBrothers(activeModule.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				this.activeModuleName = activeModuleName;
				this.moduleMenu = new ArrayList<Funcionalidad>();
				if (this.activeModule.getModuloFisico()
						&& !this.activeModule.getNombre().equals(
								"configuracion")) {
					Entidad_configuracion selectedEntity = em.find(
							Entidad_configuracion.class, this.activeModule
									.getEntidad().getId());
					this.modSelectorController
							.setSelectedEntity(selectedEntity);
				}
				/**
				 * @author yurien 27/06/2014 Se lanza el evento antes de cargar
				 *         las funcionalidades del menu.
				 *         Especificamente para emergencia ya que se escucha el
				 *         evento moduleActivatedEvent y se hace un tratamiento
				 *         diferenciado,propio de emergencia
				 * 
				 * **/
				Events.instance().raiseEvent("moduleActivatedEvent",
						activeModule);

				if (this.activeModule.getModuloFisico()
						&& this.activeModule.getActivo()) {
					this.loadCurrentUserMenu();

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void loadUserFavorites() {
		user = (Usuario) Component.getInstance("user");

		// Para que esto no de error cuando se abre dos tabs y se cierra la
		// session
		if (user != null) {
			this.userfavorites = em
					.createQuery(
							"select f from Funcionalidad f "
									+ "where f.id in (select fv.id.idFuncionalidad from Favoritos fv "
									+ "where fv.id.idModulo = :idmod "
									+ "and fv.id.idUser = :iduser) order by f.id")
					.setParameter("iduser", this.user.getId())
					.setParameter("idmod", this.activeModule.getId())
					.getResultList();
			System.out.print("user favorites loading complete!!");
		}
	}

	private String categoryToModify;
	@SuppressWarnings("unchecked")
	private java.util.Hashtable categoriesExpanded = new Hashtable();

	@SuppressWarnings("unchecked")
	public void expandOrCollapseCategory() {
		if (categoriesExpanded.containsKey(categoryToModify))
			categoriesExpanded.remove(categoryToModify);
		else
			categoriesExpanded.put(categoryToModify, true);
	}

	@SuppressWarnings("unchecked")
	public void expandCategory(String categoryToModify) {
		if (!categoriesExpanded.containsKey(categoryToModify))
			categoriesExpanded.put(categoryToModify, true);
	}

	public List<Funcionalidad> getModuleMenu() {
		if (this.isShowMenu())
			return moduleMenu;
		return new ArrayList<Funcionalidad>();
	}

	public void setModuleMenu(List<Funcionalidad> moduleMenu) {
		this.moduleMenu = moduleMenu;
	}

	public List<Funcionalidad> getUserfavorites() {
		return userfavorites;
	}

	public void setUserfavorites(List<Funcionalidad> userfavorites) {
		this.userfavorites = userfavorites;
	}

	public boolean isShowMenu() {
		return showMenu;
	}

	public void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
	}

	public Funcionalidad getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(Funcionalidad activeModule) {
		this.activeModule = activeModule;
	}

	public String getActiveModuleName() {
		return activeModuleName;
	}

	public String getCategoryToModify() {
		return categoryToModify;
	}

	public void setCategoryToModify(String categoryToModify) {
		this.categoryToModify = categoryToModify;
	}

	@SuppressWarnings("unchecked")
	public java.util.Hashtable getCategoriesExpanded() {
		return categoriesExpanded;
	}

	@SuppressWarnings("unchecked")
	public void setCategoriesExpanded(java.util.Hashtable categoriesExpanded) {
		this.categoriesExpanded = categoriesExpanded;
	}

	private Boolean brothers;

	public Boolean getBrothers() {
		return brothers;
	}

	public void setBrothers(Boolean brothers) {
		this.brothers = brothers;
	}

	public void detectBrothers(Long f) {
		if (em.find(Funcionalidad.class, f).getNombre().equals("configuracion"))
			brothers = false;
		else
			brothers = (Boolean) em
					.createQuery(
							"select case when count(h) = 0 then false else true end "
									+ "from Funcionalidad f "
									+ "join f.funcionalidadPadre.funcionalidadPadre.funcionalidadesHijas j "
									+ "join j.funcionalidadesHijas h "
									+ "where f.id=:idF  and h<>f and h.entidad=f.entidad and h.eliminado<> true and j.eliminado<>true ")
					.setParameter("idF", f).getSingleResult();

	}

}
