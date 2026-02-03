package gehos.autorizacion.management.entities;

import gehos.autenticacion.entity.Usuario;
import gehos.autorizacion.entity.Funcionalidad_permissions;
import gehos.autorizacion.entity.MenuItem;
import gehos.autorizacion.entity.UserEntityPermission;
import gehos.autorizacion.entity.UserMenuitemPermission;
import gehos.autorizacion.entity.Usuario_permissions;
import gehos.autorizacion.management.entities.tree.UsuariosPorEntidadTreeBuilder;
import gehos.autorizacion.management.logical.LogicPermission;
import gehos.autorizacion.management.logical.LogicPermissionResolver;
import gehos.autorizacion.management.logical.LogicPermissionResolverDataLoader;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.modulos.porentidad.treebuilders.model.EntidadWrapper;
import gehos.configuracion.management.entity.Entidad_configuracion;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.theme.Theme;

@Name("usuariosPorEntidadManager")
public class UsuariosPorEntidadManager {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In("logicPermissionResolver")
	LogicPermissionResolver permissionResolver;
	@In("logicPermissionResolverDataLoader")
	LogicPermissionResolverDataLoader dataLoader;

	@In(required = false, create = true, value = "usuariosPorEntidadTreeBuilder")
	UsuariosPorEntidadTreeBuilder treeBuilder;

	private Usuario_permissions selectedUser;

	ServletContext context;

	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;

	public boolean usuarioVeModulo(Funcionalidad modulo) {
		if (modulo == null)
			return false;
		Usuario user = entityManager.find(Usuario.class, selectedUser.getId());
		return permissionResolver.userCanSeeThisFunctionality(user, modulo,
				modulo, false);
	}

	Long moduloIdAAdicionar;

	public void adicionarModuloAUsuario() {

		Funcionalidad_permissions modulo = (Funcionalidad_permissions) entityManager
				.find(Funcionalidad_permissions.class, moduloIdAAdicionar);

		if (!dataLoader.getPermissionsTable().containsKey(moduloIdAAdicionar)) {
			MenuItem menuitem = new MenuItem();
			menuitem.setFuncionalidad(modulo);
			UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
			userMenuitemPermission.setMenuItem(menuitem);
			Usuario_permissions userp = (Usuario_permissions) entityManager
					.createQuery(
							"from Usuario_permissions u "
									+ "where u.username = :username")
					.setParameter("username", selectedUser.getUsername())
					.getSingleResult();
			userMenuitemPermission.setUsuario(userp);
			userMenuitemPermission.setModulo(modulo);
			userMenuitemPermission.setAllowed(true);
			menuitem.getUserMenuitemPermissions().add(userMenuitemPermission);
			entityManager.persist(menuitem);
			entityManager.persist(userMenuitemPermission);
			entityManager.flush();

			LogicPermission p = new LogicPermission();
			p.getPermittedUsersHashMap().put(modulo.getNombre(),
					new ConcurrentHashMap<String, Boolean>());
			p.getPermittedUsersHashMap().get(modulo.getNombre()).put(
					selectedUser.getUsername(), true);
			dataLoader.getPermissionsTable().put(moduloIdAAdicionar, p);
		} else {
			LogicPermission p = dataLoader.getPermissionsTable().get(
					moduloIdAAdicionar);
			if (!p.getPermittedUsersHashMap().containsKey(modulo.getNombre()))
				p.getPermittedUsersHashMap().put(modulo.getNombre(),
						new ConcurrentHashMap<String, Boolean>());
			if (!p.getPermittedUsersHashMap().containsKey(
					selectedUser.getUsername())
					&& !p.getRestrictedUsersHashMap().containsKey(
							selectedUser.getUsername())) {
				MenuItem menuItem = (MenuItem) entityManager.createQuery(
						"from MenuItem r " + "where r.id = :functionalityId")
						.setParameter("functionalityId", moduloIdAAdicionar)
						.getSingleResult();
				UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
				userMenuitemPermission.setMenuItem(menuItem);
				Usuario_permissions userp = (Usuario_permissions) entityManager
						.createQuery(
								"from Usuario_permissions u "
										+ "where u.username = :username")
						.setParameter("username", selectedUser.getUsername())
						.getSingleResult();
				userMenuitemPermission.setUsuario(userp);
				userMenuitemPermission.setModulo(modulo);
				userMenuitemPermission.setAllowed(true);
				menuItem.getUserMenuitemPermissions().add(
						userMenuitemPermission);
				entityManager.persist(userMenuitemPermission);
				entityManager.flush();

				p.getPermittedUsersHashMap().get(modulo.getNombre()).put(
						selectedUser.getUsername(), true);
			} else
				;
			// facesMessages.add(new
			// FacesMessage("Regla para el usuario ya adicionada."));
		}
	}

	Long moduloIdAEliminar;

	public void eliminarModuloAUsuario() {
		Funcionalidad_permissions modulo = (Funcionalidad_permissions) entityManager
				.find(Funcionalidad_permissions.class, moduloIdAEliminar);

		if (!dataLoader.getPermissionsTable().containsKey(moduloIdAEliminar)) {
			MenuItem menuitem = new MenuItem();
			menuitem.setFuncionalidad(modulo);
			UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
			userMenuitemPermission.setMenuItem(menuitem);
			Usuario_permissions userp = (Usuario_permissions) entityManager
					.createQuery(
							"from Usuario_permissions u "
									+ "where u.username = :username")
					.setParameter("username", selectedUser.getUsername())
					.getSingleResult();
			userMenuitemPermission.setUsuario(userp);
			userMenuitemPermission.setModulo(modulo);
			userMenuitemPermission.setAllowed(false);
			menuitem.getUserMenuitemPermissions().add(userMenuitemPermission);
			entityManager.persist(menuitem);
			entityManager.persist(userMenuitemPermission);
			entityManager.flush();

			LogicPermission p = new LogicPermission();
			p.getRestrictedUsersHashMap().put(modulo.getNombre(),
					new ConcurrentHashMap<String, Boolean>());
			p.getRestrictedUsersHashMap().get(modulo.getNombre()).put(
					selectedUser.getUsername(), false);
			dataLoader.getPermissionsTable().put(moduloIdAEliminar, p);
		} else {
			LogicPermission p = dataLoader.getPermissionsTable().get(
					moduloIdAEliminar);
			if (!p.getRestrictedUsersHashMap().containsKey(modulo.getNombre()))
				p.getRestrictedUsersHashMap().put(modulo.getNombre(),
						new ConcurrentHashMap<String, Boolean>());
			if (!p.getRestrictedUsersHashMap().containsKey(
					selectedUser.getUsername())
					&& !p.getPermittedUsersHashMap().containsKey(
							selectedUser.getUsername())) {
				MenuItem r = (MenuItem) entityManager.createQuery(
						"from MenuItem r " + "where r.id = :functionalityId")
						.setParameter("functionalityId", moduloIdAEliminar)
						.getSingleResult();
				UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
				userMenuitemPermission.setMenuItem(r);
				Usuario_permissions userp = (Usuario_permissions) entityManager
						.createQuery(
								"from Usuario_permissions u "
										+ "where u.username = :username")
						.setParameter("username", selectedUser.getUsername())
						.getSingleResult();
				userMenuitemPermission.setUsuario(userp);
				userMenuitemPermission.setModulo(modulo);
				userMenuitemPermission.setAllowed(false);
				r.getUserMenuitemPermissions().add(userMenuitemPermission);
				entityManager.persist(userMenuitemPermission);
				entityManager.flush();

				p.getRestrictedUsersHashMap().get(modulo.getNombre()).put(
						selectedUser.getUsername(), false);
			} else
				;
		}

	}

	@SuppressWarnings("unchecked")
	public boolean usuarioVeEntidad(Long entidadId) {
		if (this.selectedUser == null)
			return false;
		List<UserEntityPermission> list = entityManager
				.createQuery(
						"select u from UserEntityPermission u "
								+ "where u.entidad.id = :entid and u.usuario.id = :userid")
				.setParameter("entid", entidadId).setParameter("userid",
						this.selectedUser.getId()).getResultList();
		return list.size() > 0;
	}

	Long entidadIdAAdicionar;

	public void adicionarEntidadAUsuario() {
		UserEntityPermission u = new UserEntityPermission();
		u.setUsuario(this.selectedUser);
		Entidad_configuracion entidad = entityManager.find(
				Entidad_configuracion.class, entidadIdAAdicionar);
		u.setEntidad(entidad);
		entityManager.persist(u);
		entityManager.flush();
	}

	Long entidadIdAEliminar;

	public void eliminarEntidadAUsuario() {
		UserEntityPermission entityPermission = (UserEntityPermission) entityManager
				.createQuery(
						"select u from UserEntityPermission u "
								+ "where u.entidad.id = :entid and u.usuario.id = :userid")
				.setParameter("entid", entidadIdAEliminar).setParameter(
						"userid", this.selectedUser.getId()).getSingleResult();
		entityManager.remove(entityPermission);
		entityManager.flush();
	}

	public void setSelectedUser(Usuario_permissions user) {
		this.selectedUser = user;
	}

	private Long userId;

	public void setSelectedUser() {
		this.selectedUser = entityManager.find(Usuario_permissions.class,
				userId);
	}

	public String entidadIcon(Entidad_configuracion node) {
		EntidadWrapper wrapper = new EntidadWrapper(node, false, node.getId());
		return entidadIcon(wrapper);
	}

	public String entidadIcon(EntidadWrapper node) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modCommon/entidades_logos/"
				+ theme.getTheme().get("name") + "/"
				+ theme.getTheme().get("color") + "/"
				+ node.getValue().getLogo();

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modCommon/entidades_logos/"
					+ theme.getTheme().get("name") + "/"
					+ theme.getTheme().get("color") + "/generic.png";
	}

	@Create
	public void constructor() {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		context = (ServletContext) aFacesContext.getExternalContext()
				.getContext();
	}

	public UsuariosPorEntidadTreeBuilder getTreeBuilder() {
		return treeBuilder;
	}

	public void setTreeBuilder(UsuariosPorEntidadTreeBuilder treeBuilder) {
		this.treeBuilder = treeBuilder;
	}

	public Usuario_permissions getSelectedUser() {
		return selectedUser;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEntidadIdAAdicionar() {
		return entidadIdAAdicionar;
	}

	public void setEntidadIdAAdicionar(Long entidadIdAAdicionar) {
		this.entidadIdAAdicionar = entidadIdAAdicionar;
	}

	public Long getEntidadIdAEliminar() {
		return entidadIdAEliminar;
	}

	public void setEntidadIdAEliminar(Long entidadIdAEliminar) {
		this.entidadIdAEliminar = entidadIdAEliminar;
	}

}
