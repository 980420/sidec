package gehos.comun.shell;

import gehos.autenticacion.entity.Usuario;
import gehos.autorizacion.management.logical.LogicPermissionResolver;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.funcionalidades.entity.User_comun;
import gehos.comun.funcionalidades.entity.custom.Favoritos;
import gehos.comun.funcionalidades.entity.custom.FavoritosId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

@Name("funSelectorController")
public class FunSelectorController {

	private Long idFuncionalidad;
	private String listaAncestros;

	private Funcionalidad funcionalidad;
	private List<Funcionalidad> ancestros;
	private boolean showForm = false;

	@In
	EntityManager entityManager;
	@In("logicPermissionResolver")
	LogicPermissionResolver permissionResolver;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION, required = false)
	IActiveModule activeModule;
	@In
	Usuario user;
	@In(create = true)
	MenuBuilder menuBuilder;

	public Funcionalidad iconsContainer(Funcionalidad fun) {
		while (!fun.getContenedorIconos())
			fun = fun.getFuncionalidadPadre();
		return fun;
	}

	public String processUrl(String url) {
		if (url.indexOf("codebase") < 0)
			return url;
		List<String> resultList = Arrays.asList(activeModule.getActiveModule()
				.getUrl().split("/"));
		int modules = resultList.indexOf("modules");
		String substitude = resultList.get(modules) + "/"
				+ resultList.get(modules + 1);
		String result = url.replace("codebase", substitude);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List SearchFuncionalidad(Object arg) {
		String key = (String) arg;
		List<Funcionalidad> searchResult = new ArrayList<Funcionalidad>();
		List<Funcionalidad> parents = this.activeModule.getModuleMenu();
		findRecursively(key, parents, searchResult);
		return searchResult;
	}

	public void findRecursively(String key,
			java.util.Collection<Funcionalidad> list,
			List<Funcionalidad> searchResult) {
		if (key == null)
			return;
		for (Iterator<Funcionalidad> iterator = list.iterator(); iterator
				.hasNext();) {
			Funcionalidad f = (Funcionalidad) iterator.next();
			if (f.getLabel().toLowerCase().indexOf(key.toLowerCase()) >= 0) {
				searchResult.add(f);
			}
			findRecursively(key, f.getFuncionalidadesHijas(), searchResult);
		}
	}

	private Long selectedFuncionality;

	public String redirectToSelectedFuncionality() {
		Funcionalidad f = entityManager.find(Funcionalidad.class,
				selectedFuncionality);
		if (f.getUrl().indexOf("selector") >= 0)
			return f.getUrl() + "?idFuncionalidad=" + f.getId()
					+ "&idancestors=" + f.getId() + "a";
		return f.getUrl();
	}

	public MenuBuilder getmenuBuilder() {
		return menuBuilder;
	}

	public void setmenuBuilder(MenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
	}

	private List<Funcionalidad> children;

	@Factory(value = "children", scope = ScopeType.PAGE)
	public List<Funcionalidad> childrenFactory() {
		if (children == null) {
			children = this.childrenFunction();
		}
		return children;
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> childrenFunction() {
		if (this.funcionalidad != null) {
			List<Funcionalidad> list = new ArrayList<Funcionalidad>();
			List<Funcionalidad> list1 = entityManager
					.createQuery(
							"from Funcionalidad f where f.funcionalidadPadre.id = :pid and f.esModulo = false and (f.eliminado = null or f.eliminado = false) order by f.orden")
					.setParameter("pid", this.funcionalidad.getId())
					.getResultList();
			for (Funcionalidad funcionalidad : list1) {
				if (permissionResolver.currentUserCanSeeThisFunctionality(
						funcionalidad, activeModule.getActiveModule(), true)) {
					list.add(funcionalidad);
				}
			}
			if (this.funcionalidad.getEsModulo() == true) {
				Funcionalidad fun = this.funcionalidad.getFuncionalidadPadre();
				while (fun != null) {
					List<Funcionalidad> list2 = entityManager
							.createQuery(
									"from Funcionalidad f where f.funcionalidadPadre.id = :pid and f.esModulo = false and (f.eliminado = null or f.eliminado = false) order by f.orden")
							.setParameter("pid", fun.getId()).getResultList();
					for (Funcionalidad funcionalidad : list2) {
						if (permissionResolver
								.currentUserCanSeeThisFunctionality(
										funcionalidad, activeModule
												.getActiveModule(), true)) {
							list.add(funcionalidad);
						}
					}
					// list.addAll(list2);
					fun = fun.getFuncionalidadPadre();
				}
			}
			/*
			 * Collections.sort(list, new Comparator<Funcionalidad>() { public
			 * int compare(Funcionalidad arg0, Funcionalidad arg1) { return
			 * arg0.getOrden() - arg1.getOrden(); } });
			 */
			return list;
		}
		return new ArrayList<Funcionalidad>();
	}

	public String destination(Funcionalidad f) {
		return f.getUrl();
	}

	public IActiveModule getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	@SuppressWarnings("unchecked")
	public List<Funcionalidad> userFuntions(String username) {
		Long ent = null;
		String script = "select f from Funcionalidad f "
				+ "where f.id in (select fv.id.idFuncionalidad from Favoritos fv "
				+ "where fv.id.idModulo = :idmod "
				+ "and fv.id.idUser = :iduser and fv.entidad = null) "
				+ "and (f.esModulo = false or f.esModulo = null)";
		List<Funcionalidad> fun = null;
		if (this.activeModule.getActiveModule().getEntidad() != null) {
			ent = this.activeModule.getActiveModule().getEntidad().getId();
			script = "select f from Funcionalidad f "
					+ "where f.id in (select fv.id.idFuncionalidad from Favoritos fv "
					+ "where fv.id.idModulo = :idmod "
					+ "and fv.id.idUser = :iduser and fv.entidad.id = :idF) "
					+ "and (f.esModulo = false or f.esModulo = null)";
			fun = entityManager.createQuery(script).setParameter("iduser",
					this.user.getId()).setParameter("idmod",
					this.activeModule.getActiveModule().getId()).setParameter(
					"idF", ent).getResultList();
		} else {
			fun = entityManager.createQuery(script).setParameter("iduser",
					this.user.getId()).setParameter("idmod",
					this.activeModule.getActiveModule().getId())
					.getResultList();
		}
		return fun;
	}

	public String favorites() {
		String aux = activeModule.getActiveModule().getUrl();
		String[] aux2 = aux.split("/");
		aux = aux2[1];

		return "/" + aux + "/funSelector/favorites.gehos";
	}

	public String baseMod() {
		String aux = activeModule.getActiveModule().getUrl();
		String[] aux2 = aux.split("/");
		aux = aux2[1];

		return "/" + aux;
	}

	public String checkQueryStringAndInitialize() {
		try {
			this.funcionalidad = entityManager.find(Funcionalidad.class,
					this.idFuncionalidad);
			if (this.funcionalidad == null)
				return "problems";

			this.ancestros = new ArrayList<Funcionalidad>();
			String regexp = "^((0|([1-9][0-9]*))a)*$";
			Boolean match = Pattern.matches(regexp, this.listaAncestros);

			if (match) {
				String[] ids = this.listaAncestros.split("a");
				for (int i = 0; i < ids.length; i++) {
					try {
						Funcionalidad aux = entityManager.find(
								Funcionalidad.class, Long.parseLong(ids[i]));
						if (aux != null)
							this.ancestros.add(aux);
						else
							return "problems";
					} catch (Exception e) {
						return "problems";
					}
				}
			} else
				return "problems";
			this.showForm = true;
			return "ok";
		} catch (Exception e) {
			return "problems";
		}
	}

	public Long getIdFuncionalidad() {
		return idFuncionalidad;
	}

	public void setIdFuncionalidad(Long idFunction) {
		/*
		 * Funcionalidad fun = entityManager.find(Funcionalidad.class,
		 * idFunction); if(fun != null){ while(fun.getEsModulo() != true) fun =
		 * fun.getFuncionalidadPadre();
		 * activeModule.setActiveModuleName(fun.getNombre()); }
		 */
		this.idFuncionalidad = idFunction;
		this.funcionalidad = entityManager.find(Funcionalidad.class,
				this.idFuncionalidad);
		activeModule.expandCategory(Long.toString(idFunction));
	}

	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private Long funcIdToAdd;

	public void addFavorites() {
		if (this.moduleName != null && this.moduleName != "")
			activeModule.setActiveModuleName(this.moduleName, true);
		try {
			Favoritos fInUser = new Favoritos();
			FavoritosId funcionalidadInUserId = new FavoritosId();

			funcionalidadInUserId.setIdUser(this.user.getId());
			fInUser.setUsuario(entityManager.find(User_comun.class, this.user
					.getId()));

			funcionalidadInUserId.setIdFuncionalidad(funcIdToAdd);
			fInUser.setFuncionalidadByIdFuncionalidad(entityManager.find(
					Funcionalidad.class, funcIdToAdd));

			funcionalidadInUserId.setIdModulo(this.activeModule
					.getActiveModule().getId());
			fInUser.setFuncionalidadByIdModulo(this.activeModule
					.getActiveModule());

			fInUser
					.setEntidad(this.activeModule.getActiveModule()
							.getEntidad());

			fInUser.setId(funcionalidadInUserId);

			entityManager.persist(fInUser);
			entityManager.flush();

			/*
			 * try { Session session = (Session)entityManager.getDelegate();
			 * SessionFactory sessionFactory = session.getSessionFactory();
			 * sessionFactory.evict(Funcionalidad.class, f.getId()); } catch
			 * (Exception e) { System.out.print(e.toString()); }
			 */

			this.activeModule.loadUserFavorites();
			facesMessages
					.add("La funcionalidad ha sido agregada a sus favoritos");
		} catch (Exception e) {
			facesMessages.add("ha ocurrido un error");
			System.out.print(e.toString());
		}
	}

	private Long funcIdToRemove;

	public void removeFavorites() {
		if (this.moduleName != null && this.moduleName != "")
			activeModule.setActiveModuleName(this.moduleName, true);
		try {
			Favoritos fInUser = null;
			if (this.activeModule.getActiveModule().getEntidad() != null) {
				fInUser = (Favoritos) entityManager.createQuery(
						"from Favoritos f "
								+ "where f.id.idFuncionalidad = :idfunct "
								+ "and f.id.idModulo = :idmodulo "
								+ "and f.id.idUser = :iduser "
								+ "and f.entidad.id = :ident").setParameter(
						"idfunct", funcIdToRemove).setParameter("idmodulo",
						this.activeModule.getActiveModule().getId())
						.setParameter("iduser", this.user.getId())
						.setParameter(
								"ident",
								this.activeModule.getActiveModule()
										.getEntidad().getId())
						.getSingleResult();
			} else {
				
				System.out.println("f.id.idFuncionalidad = "+funcIdToRemove);
				System.out.println("f.id.idModulo = " + this.activeModule.getActiveModule().getId());
				System.out.println("f.id.idUser = " + this.user.getId());
				System.out.println("f.entidad  tiene que ser nula");
				
				fInUser = (Favoritos) entityManager.createQuery(
						"from Favoritos f "
								+ "where f.id.idFuncionalidad = :idfunct "
								+ "and f.id.idModulo = :idmodulo "
								+ "and f.id.idUser = :iduser "
								+ "and f.entidad = null").setParameter(
						"idfunct", funcIdToRemove).setParameter("idmodulo",
						this.activeModule.getActiveModule().getId())
						.setParameter("iduser", this.user.getId())
						.getSingleResult();
			}

			entityManager.remove(fInUser);
			entityManager.flush();

			this.activeModule.loadUserFavorites();

			facesMessages
					.add("La funcionalidad ha sido removida de sus favoritos");
		} catch (Exception e) {
			facesMessages.add("ha ocurrido un error");
			System.out.print(e.toString());
		}
	}

	public Boolean isNotAlreadyAFavorite(Funcionalidad f, String username) {
		
		if (f == null)
			return true;
		List<Funcionalidad> favs = this.activeModule.getUserfavorites();
		/**@author yurien
		 * 26/06/2014
		 * Se chequea que los favoritos no sean nulos
		 * puesto que se lanzaba un NullPointerException
		 * **/
		if (favs == null || favs.size() == 0)
			return true;
		
		
		return (Collections.binarySearch(this.activeModule.getUserfavorites(),
				f, new Comparator<Funcionalidad>() {
					public int compare(Funcionalidad arg0, Funcionalidad arg1) {
						return new Long(arg0.getId()).compareTo(new Long(arg1.getId())) ;
					}
				}) < 0);
		
	}

	public String takeMyAncestors(Long idf) {
		String result = "";
		for (int i = 0; i < this.ancestros.size(); i++) {
			if (this.ancestros.get(i).getId() == idf) {
				result += this.ancestros.get(i).getId() + "a";
				break;
			}
			result += this.ancestros.get(i).getId() + "a";
		}
		return result;
	}

	public String getListaAncestros() {
		return listaAncestros;
	}

	public void setListaAncestros(String idancestors) {
		this.listaAncestros = idancestors;
	}

	public Boolean isLast(Long idfun) {
		if (this.ancestros != null
				&& this.ancestros.size() > 0
				&& this.ancestros.get(this.ancestros.size() - 1).getId() == idfun)
			return true;
		return false;

	}

	public List<Funcionalidad> getAncestros() {
		return ancestros;
	}

	public void setAncestros(List<Funcionalidad> ancestros) {
		this.ancestros = ancestros;
	}

	public boolean isShowForm() {
		return showForm;
	}

	public void setShowForm(boolean showForm) {
		this.showForm = showForm;
	}

	public Long getFuncIdToAdd() {
		return funcIdToAdd;
	}

	public void setFuncIdToAdd(Long funcIdToAdd) {
		this.funcIdToAdd = funcIdToAdd;
	}

	public Long getSelectedFuncionality() {
		return selectedFuncionality;
	}

	public void setSelectedFuncionality(Long selectedFuncionality) {
		this.selectedFuncionality = selectedFuncionality;
	}

	public Long getFuncIdToRemove() {
		return funcIdToRemove;
	}

	public void setFuncIdToRemove(Long funcIdToRemove) {
		this.funcIdToRemove = funcIdToRemove;
	}

}