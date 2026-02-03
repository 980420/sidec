package gehos.autorizacion.management.logical;

import gehos.autorizacion.entity.Funcionalidad_permissions;
import gehos.autorizacion.entity.MenuItem;
import gehos.autorizacion.entity.RoleMenuitemPermission;
import gehos.autorizacion.entity.Role_permissions;
import gehos.autorizacion.entity.UserMenuitemPermission;
import gehos.autorizacion.entity.Usuario_permissions;
import gehos.autorizacion.treebuilders.logical.model.ITreeData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("logicPermissionManager")
public class LogicPermissionManager {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In("logicPermissionResolverDataLoader")
	LogicPermissionResolverDataLoader dataLoader;
	private ITreeData selectedFunctionality;

	public void setSelectedFunctionality(ITreeData func) {
		// Integer hash = this.hashCode();
		// System.out.println(hash);

		this.selectedFunctionality = func;
	}

	public Funcionalidad_permissions getAsFunctionality(Long id) {
		return entityManager.find(Funcionalidad_permissions.class, id);
	}

	public int percent(ITreeData functId) {
		return 99 - (3 * this.selectedFunctionality.getRootToNodePath()
				.indexOf(functId));
	}

	public List<ITreeData> functionalitiesPath() {
		List<ITreeData> result = new ArrayList<ITreeData>();
		if (this.selectedFunctionality != null) {
			List<ITreeData> funcIds = this.selectedFunctionality
					.getRootToNodePath();
			return funcIds;
		}
		return result;
	}

	// LISTAR PERMISOS

	public List<String> usersDeniedForFunctionality(ITreeData functId) {
		if (functId == null)
			return new ArrayList<String>();
		if (dataLoader.getPermissionsTable().containsKey(functId.getId()))
			if (dataLoader.getPermissionsTable().get(functId.getId())
					.getRestrictedUsersHashMap()
					.containsKey(functId.getModuleFatherName()))
				return new ArrayList<String>(dataLoader.getPermissionsTable()
						.get(functId.getId()).getRestrictedUsersHashMap()
						.get(functId.getModuleFatherName()).keySet());
		return new ArrayList<String>();
	}

	public List<String> usersAllowedForFunctionality(ITreeData functId) {
		if (functId == null)
			return new ArrayList<String>();
		if (dataLoader.getPermissionsTable().containsKey(functId.getId()))
			if (dataLoader.getPermissionsTable().get(functId.getId())
					.getPermittedUsersHashMap()
					.containsKey(functId.getModuleFatherName()))
				return new ArrayList<String>(dataLoader.getPermissionsTable()
						.get(functId.getId()).getPermittedUsersHashMap()
						.get(functId.getModuleFatherName()).keySet());
		return new ArrayList<String>();
	}

	public List<String> rolesDeniedForFunctionality(ITreeData functId) {
		if (functId == null)
			return new ArrayList<String>();
		if (dataLoader.getPermissionsTable().containsKey(functId.getId()))
			if (dataLoader.getPermissionsTable().get(functId.getId())
					.getRestrictedRolesHashMap()
					.containsKey(functId.getModuleFatherName()))
				return new ArrayList<String>(dataLoader.getPermissionsTable()
						.get(functId.getId()).getRestrictedRolesHashMap()
						.get(functId.getModuleFatherName()).keySet());
		return new ArrayList<String>();
	}

	public List<String> rolesAllowedForFunctionality(ITreeData functId) {
		if (functId == null)
			return new ArrayList<String>();
		if (dataLoader.getPermissionsTable().containsKey(functId.getId()))
			if (dataLoader.getPermissionsTable().get(functId.getId())
					.getPermittedRolesHashMap()
					.containsKey(functId.getModuleFatherName()))
				return new ArrayList<String>(dataLoader.getPermissionsTable()
						.get(functId.getId()).getPermittedRolesHashMap()
						.get(functId.getModuleFatherName()).keySet());
		return new ArrayList<String>();
	}

	// CREAR REGLAS

	private String selectedUser;
	private String selectedRole;

	public void provideAccessToUser() {
		// Integer hash = this.hashCode();
		// System.out.println(hash);
		try {
			if (this.selectedFunctionality == null || this.selectedUser == null)
				return;
			Funcionalidad_permissions modulo_permissions = (Funcionalidad_permissions) entityManager
					.createQuery(
							"from Funcionalidad_permissions m where m.nombre = :name")
					.setParameter("name",
							this.selectedFunctionality.getModuleFatherName())
					.getSingleResult();

			if (!dataLoader.getPermissionsTable().containsKey(
					this.selectedFunctionality.getId())) {
				MenuItem menuitem = new MenuItem();
				// menuitem.setFunctionalityId(this.selectedFunctionality.getId());
				menuitem.setFuncionalidad(entityManager.find(
						Funcionalidad_permissions.class,
						this.selectedFunctionality.getId()));
				UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
				userMenuitemPermission.setMenuItem(menuitem);
				Usuario_permissions userp = (Usuario_permissions) entityManager
						.createQuery(
								"from Usuario_permissions u "
										+ "where u.username = :username")
						.setParameter("username", selectedUser)
						.getSingleResult();
				userMenuitemPermission.setUsuario(userp);
				userMenuitemPermission.setModulo(modulo_permissions);
				userMenuitemPermission.setAllowed(true);
				menuitem.getUserMenuitemPermissions().add(
						userMenuitemPermission);
				entityManager.persist(menuitem);
				entityManager.persist(userMenuitemPermission);
				entityManager.flush();

				LogicPermission p = new LogicPermission();
				p.getPermittedUsersHashMap().put(
						modulo_permissions.getNombre(),
						new ConcurrentHashMap<String, Boolean>());
				p.getPermittedUsersHashMap()
						.get(modulo_permissions.getNombre())
						.put(selectedUser, true);
				dataLoader.getPermissionsTable().put(
						this.selectedFunctionality.getId(), p);
			} else {
				LogicPermission p = dataLoader.getPermissionsTable().get(
						this.selectedFunctionality.getId());
				if (!p.getPermittedUsersHashMap().containsKey(
						modulo_permissions.getNombre()))
					p.getPermittedUsersHashMap().put(
							modulo_permissions.getNombre(),
							new ConcurrentHashMap<String, Boolean>());
				if (!p.getPermittedUsersHashMap().containsKey(selectedUser)
						&& !p.getRestrictedUsersHashMap().containsKey(
								selectedUser)) {
					MenuItem menuItem = (MenuItem) entityManager
							.createQuery(
									"from MenuItem r "
											+ "where r.id = :functionalityId")
							.setParameter("functionalityId",
									this.selectedFunctionality.getId())
							.getSingleResult();
					UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
					userMenuitemPermission.setMenuItem(menuItem);
					Usuario_permissions userp = (Usuario_permissions) entityManager
							.createQuery(
									"from Usuario_permissions u "
											+ "where u.username = :username")
							.setParameter("username", selectedUser)
							.getSingleResult();
					userMenuitemPermission.setUsuario(userp);
					userMenuitemPermission.setModulo(modulo_permissions);
					userMenuitemPermission.setAllowed(true);
					menuItem.getUserMenuitemPermissions().add(
							userMenuitemPermission);
					entityManager.persist(userMenuitemPermission);
					entityManager.flush();

					p.getPermittedUsersHashMap()
							.get(modulo_permissions.getNombre())
							.put(selectedUser, true);
				} else
					facesMessages.addToControlFromResourceBundle("error", Severity.ERROR,
							"msg_regUsuario_modConfig");
			}
		} catch (EntityExistsException e) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR,
					"msg_regUsuario_modConfig");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void denyAccessToUser() {
		try {
			if (this.selectedFunctionality == null || this.selectedUser == null)
				return;
			Funcionalidad_permissions modulo_permissions = (Funcionalidad_permissions) entityManager
					.createQuery(
							"from Funcionalidad_permissions m where m.nombre = :name")
					.setParameter("name",
							this.selectedFunctionality.getModuleFatherName())
					.getSingleResult();
			if (!dataLoader.getPermissionsTable().containsKey(
					this.selectedFunctionality.getId())) {
				MenuItem menuitem = new MenuItem();
				// menuitem.setFunctionalityId(this.selectedFunctionality.getId());
				menuitem.setFuncionalidad(entityManager.find(
						Funcionalidad_permissions.class,
						this.selectedFunctionality.getId()));
				UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
				userMenuitemPermission.setMenuItem(menuitem);
				Usuario_permissions userp = (Usuario_permissions) entityManager
						.createQuery(
								"from Usuario_permissions u "
										+ "where u.username = :username")
						.setParameter("username", selectedUser)
						.getSingleResult();
				userMenuitemPermission.setUsuario(userp);
				userMenuitemPermission.setModulo(modulo_permissions);
				userMenuitemPermission.setAllowed(false);
				menuitem.getUserMenuitemPermissions().add(
						userMenuitemPermission);
				entityManager.persist(menuitem);
				entityManager.persist(userMenuitemPermission);
				entityManager.flush();

				LogicPermission p = new LogicPermission();
				p.getRestrictedUsersHashMap().put(
						modulo_permissions.getNombre(),
						new ConcurrentHashMap<String, Boolean>());
				p.getRestrictedUsersHashMap()
						.get(modulo_permissions.getNombre())
						.put(selectedUser, false);
				dataLoader.getPermissionsTable().put(
						this.selectedFunctionality.getId(), p);
			} else {
				LogicPermission p = dataLoader.getPermissionsTable().get(
						this.selectedFunctionality.getId());
				if (!p.getRestrictedUsersHashMap().containsKey(
						modulo_permissions.getNombre()))
					p.getRestrictedUsersHashMap().put(
							modulo_permissions.getNombre(),
							new ConcurrentHashMap<String, Boolean>());
				if (!p.getRestrictedUsersHashMap().containsKey(selectedUser)
						&& !p.getPermittedUsersHashMap().containsKey(
								selectedUser)) {
					MenuItem r = (MenuItem) entityManager
							.createQuery(
									"from MenuItem r "
											+ "where r.id = :functionalityId")
							.setParameter("functionalityId",
									this.selectedFunctionality.getId())
							.getSingleResult();
					UserMenuitemPermission userMenuitemPermission = new UserMenuitemPermission();
					userMenuitemPermission.setMenuItem(r);
					Usuario_permissions userp = (Usuario_permissions) entityManager
							.createQuery(
									"from Usuario_permissions u "
											+ "where u.username = :username")
							.setParameter("username", selectedUser)
							.getSingleResult();
					userMenuitemPermission.setUsuario(userp);
					userMenuitemPermission.setModulo(modulo_permissions);
					userMenuitemPermission.setAllowed(false);
					r.getUserMenuitemPermissions().add(userMenuitemPermission);
					entityManager.persist(userMenuitemPermission);
					entityManager.flush();

					p.getRestrictedUsersHashMap()
							.get(modulo_permissions.getNombre())
							.put(selectedUser, false);
				} else
					facesMessages.addToControl("error", Severity.ERROR,
							"msg_regUsuario_modConfig");
			}
		} catch (EntityExistsException e) {
			facesMessages.addToControl("error", Severity.ERROR,
					"msg_regUsuario_modConfig");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void provideAccessToRole() {
		try {
			if (this.selectedFunctionality == null || this.selectedRole == null)
				return;
			Funcionalidad_permissions modulo_permissions = (Funcionalidad_permissions) entityManager
					.createQuery(
							"from Funcionalidad_permissions m where m.nombre = :name")
					.setParameter("name",
							this.selectedFunctionality.getModuleFatherName())
					.getSingleResult();
			if (!dataLoader.getPermissionsTable().containsKey(
					this.selectedFunctionality.getId())) {
				MenuItem menuitem = new MenuItem();
				// menuitem.setFunctionalityId(this.selectedFunctionality.getId());
				menuitem.setFuncionalidad(entityManager.find(
						Funcionalidad_permissions.class,
						this.selectedFunctionality.getId()));
				RoleMenuitemPermission roleMenuitemPermission = new RoleMenuitemPermission();
				roleMenuitemPermission.setMenuItem(menuitem);
				Role_permissions rolep = (Role_permissions) entityManager
						.createQuery(
								"from Role_permissions r "
										+ "where r.name = :role")
						.setParameter("role", selectedRole).getSingleResult();
				roleMenuitemPermission.setRole(rolep);
				roleMenuitemPermission.setModulo(modulo_permissions);
				roleMenuitemPermission.setAllowed(true);
				menuitem.getRoleMenuitemPermissions().add(
						roleMenuitemPermission);
				entityManager.persist(menuitem);
				entityManager.persist(roleMenuitemPermission);
				entityManager.flush();

				LogicPermission p = new LogicPermission();
				p.getPermittedRolesHashMap().put(
						modulo_permissions.getNombre(),
						new ConcurrentHashMap<String, Boolean>());
				p.getPermittedRolesHashMap()
						.get(modulo_permissions.getNombre())
						.put(selectedRole, true);
				dataLoader.getPermissionsTable().put(
						this.selectedFunctionality.getId(), p);
			} else {
				LogicPermission p = dataLoader.getPermissionsTable().get(
						this.selectedFunctionality.getId());
				if (!p.getPermittedRolesHashMap().containsKey(
						modulo_permissions.getNombre()))
					p.getPermittedRolesHashMap().put(
							modulo_permissions.getNombre(),
							new ConcurrentHashMap<String, Boolean>());
				if (!p.getPermittedRolesHashMap().containsKey(selectedRole)
						&& !p.getRestrictedRolesHashMap().containsKey(
								selectedRole)) {
					MenuItem menuItem = (MenuItem) entityManager
							.createQuery(
									"from MenuItem r "
											+ "where r.id = :functionalityId")
							.setParameter("functionalityId",
									this.selectedFunctionality.getId())
							.getSingleResult();
					RoleMenuitemPermission roleMenuitemPermission = new RoleMenuitemPermission();
					roleMenuitemPermission.setMenuItem(menuItem);
					Role_permissions rolep = (Role_permissions) entityManager
							.createQuery(
									"from Role_permissions r "
											+ "where r.name = :role")
							.setParameter("role", selectedRole)
							.getSingleResult();
					roleMenuitemPermission.setRole(rolep);
					roleMenuitemPermission.setModulo(modulo_permissions);
					roleMenuitemPermission.setAllowed(true);
					menuItem.getRoleMenuitemPermissions().add(
							roleMenuitemPermission);
					entityManager.persist(roleMenuitemPermission);
					entityManager.flush();

					p.getPermittedRolesHashMap()
							.get(modulo_permissions.getNombre())
							.put(selectedRole, true);
				} else
					facesMessages.addToControlFromResourceBundle("error", Severity.ERROR,
							"msg_regRol_modConfig");
			}
		} catch (EntityExistsException e) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR,
					"msg_regRol_modConfig");

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void denyAccessToRole() {
		try {
			if (this.selectedFunctionality == null || this.selectedRole == null)
				return;
			Funcionalidad_permissions modulo_permissions = (Funcionalidad_permissions) entityManager
					.createQuery(
							"from Funcionalidad_permissions m where m.nombre = :name")
					.setParameter("name",
							this.selectedFunctionality.getModuleFatherName())
					.getSingleResult();
			if (!dataLoader.getPermissionsTable().containsKey(
					this.selectedFunctionality.getId())) {
				MenuItem menuItem = new MenuItem();
				// menuItem.setFunctionalityId(this.selectedFunctionality.getId());
				menuItem.setFuncionalidad(entityManager.find(
						Funcionalidad_permissions.class,
						this.selectedFunctionality.getId()));
				RoleMenuitemPermission roleMenuitemPermission = new RoleMenuitemPermission();
				roleMenuitemPermission.setMenuItem(menuItem);
				Role_permissions rolep = (Role_permissions) entityManager
						.createQuery(
								"from Role_permissions r "
										+ "where r.name = :role")
						.setParameter("role", selectedRole).getSingleResult();
				roleMenuitemPermission.setRole(rolep);
				roleMenuitemPermission.setModulo(modulo_permissions);
				roleMenuitemPermission.setAllowed(false);
				menuItem.getRoleMenuitemPermissions().add(
						roleMenuitemPermission);
				entityManager.persist(menuItem);
				entityManager.persist(roleMenuitemPermission);
				entityManager.flush();

				LogicPermission p = new LogicPermission();
				p.getRestrictedRolesHashMap().put(
						modulo_permissions.getNombre(),
						new ConcurrentHashMap<String, Boolean>());
				p.getRestrictedRolesHashMap()
						.get(modulo_permissions.getNombre())
						.put(selectedRole, false);
				dataLoader.getPermissionsTable().put(
						this.selectedFunctionality.getId(), p);
			} else {
				LogicPermission p = dataLoader.getPermissionsTable().get(
						this.selectedFunctionality.getId());
				if (!p.getRestrictedRolesHashMap().containsKey(
						modulo_permissions.getNombre()))
					p.getRestrictedRolesHashMap().put(
							modulo_permissions.getNombre(),
							new ConcurrentHashMap<String, Boolean>());
				if (!p.getRestrictedRolesHashMap().containsKey(selectedRole)
						&& !p.getPermittedRolesHashMap().containsKey(
								selectedRole)) {
					MenuItem menuItem = (MenuItem) entityManager
							.createQuery(
									"from MenuItem r "
											+ "where r.id = :functionalityId")
							.setParameter("functionalityId",
									this.selectedFunctionality.getId())
							.getSingleResult();
					RoleMenuitemPermission roleMenuitemPermission = new RoleMenuitemPermission();
					roleMenuitemPermission.setMenuItem(menuItem);
					Role_permissions rolep = (Role_permissions) entityManager
							.createQuery(
									"from Role_permissions r "
											+ "where r.name = :role")
							.setParameter("role", selectedRole)
							.getSingleResult();
					roleMenuitemPermission.setRole(rolep);
					roleMenuitemPermission.setModulo(modulo_permissions);
					roleMenuitemPermission.setAllowed(false);
					menuItem.getRoleMenuitemPermissions().add(
							roleMenuitemPermission);
					entityManager.persist(roleMenuitemPermission);
					entityManager.flush();

					p.getRestrictedRolesHashMap()
							.get(modulo_permissions.getNombre())
							.put(selectedRole, false);
				} else
					facesMessages.addToControl("error", Severity.ERROR,
							"msg_regRol_modConfig");
			}
		} catch (EntityExistsException e) {
			facesMessages.addToControl("error", Severity.ERROR,
					"msg_regRol_modConfig");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	// ELIMINAR REGLAS

	public void deletedRestrictedUser(ITreeData functId, String username) {
		try {
			MenuItem menuItem = (MenuItem) entityManager
					.createQuery("from MenuItem r where r.id = :funcid")
					.setParameter("funcid", functId.getId()).getSingleResult();
			Usuario_permissions user = (Usuario_permissions) entityManager
					.createQuery(
							"from Usuario_permissions u where u.username = :username")
					.setParameter("username", username).getSingleResult();
			UserMenuitemPermission permission = (UserMenuitemPermission) entityManager
					.createQuery(
							"select p from UserMenuitemPermission p where "
									+ "p.menuItem.id = :rid and "
									+ "p.usuario.id = :uid and "
									+ "p.modulo.nombre = :mnom and "
									+ "p.allowed=false")
					.setParameter("rid", functId.getId())
					.setParameter("uid", user.getId())
					.setParameter("mnom", functId.getModuleFatherName())
					.getResultList().get(0);
			user.getUserMenuitemPermissions().remove(permission);
			menuItem.getUserMenuitemPermissions().remove(permission);
			entityManager.remove(permission);
			entityManager.flush();
			dataLoader.getPermissionsTable().get(functId.getId())
					.getRestrictedUsersHashMap()
					.get(functId.getModuleFatherName()).remove(username);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void deletedPermitedUser(ITreeData functId, String username) {
		try {
			MenuItem menuItem = (MenuItem) entityManager
					.createQuery("from MenuItem r where r.id = :functid")
					.setParameter("functid", functId.getId()).getSingleResult();
			Usuario_permissions user = (Usuario_permissions) entityManager
					.createQuery(
							"from Usuario_permissions u where u.username = :username")
					.setParameter("username", username).getSingleResult();
			UserMenuitemPermission permission = (UserMenuitemPermission) entityManager
					.createQuery(
							"from UserMenuitemPermission p where "
									+ "p.menuItem.id = :rid and "
									+ "p.usuario.id = :uid and "
									+ "p.modulo.nombre = :mnom and "
									+ "p.allowed=true")
					.setParameter("rid", functId.getId())
					.setParameter("uid", user.getId())
					.setParameter("mnom", functId.getModuleFatherName())
					.getResultList().get(0);
			user.getUserMenuitemPermissions().remove(permission);
			menuItem.getUserMenuitemPermissions().remove(permission);
			entityManager.remove(permission);
			entityManager.flush();
			dataLoader.getPermissionsTable().get(functId.getId())
					.getPermittedUsersHashMap()
					.get(functId.getModuleFatherName()).remove(username);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void deletedRestrictedRole(ITreeData functId, String role) {
		try {
			MenuItem menuItem = (MenuItem) entityManager
					.createQuery("from MenuItem r where r.id = :funcid")
					.setParameter("funcid", functId.getId()).getSingleResult();
			Role_permissions rolep = (Role_permissions) entityManager
					.createQuery("from Role_permissions r where r.name = :role")
					.setParameter("role", role).getSingleResult();
			RoleMenuitemPermission permission = (RoleMenuitemPermission) entityManager
					.createQuery(
							"from RoleMenuitemPermission p where "
									+ "p.menuItem.id = :rid and "
									+ "p.role.id = :uid and "
									+ "p.modulo.nombre = :mnom and "
									+ "p.allowed=false")
					.setParameter("rid", functId.getId())
					.setParameter("uid", rolep.getId())
					.setParameter("mnom", functId.getModuleFatherName())
					.getResultList().get(0);
			rolep.getRoleMenuitemPermissions().remove(permission);
			menuItem.getRoleMenuitemPermissions().remove(permission);
			entityManager.remove(permission);
			entityManager.flush();
			dataLoader.getPermissionsTable().get(functId.getId())
					.getRestrictedRolesHashMap()
					.get(functId.getModuleFatherName()).remove(role);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void deletedPermitedRole(ITreeData functId, String role) {
		try {
			MenuItem menuItem = (MenuItem) entityManager
					.createQuery("from MenuItem r where r.id = :funcid")
					.setParameter("funcid", functId.getId()).getSingleResult();
			Role_permissions rolep = (Role_permissions) entityManager
					.createQuery("from Role_permissions r where r.name = :role")
					.setParameter("role", role).getSingleResult();
			RoleMenuitemPermission permission = (RoleMenuitemPermission) entityManager
					.createQuery(
							"from RoleMenuitemPermission p where "
									+ "p.menuItem.id = :rid and "
									+ "p.role.id = :uid and "
									+ "p.modulo.nombre = :mnom and "
									+ "p.allowed=true")
					.setParameter("rid", functId.getId())
					.setParameter("uid", rolep.getId())
					.setParameter("mnom", functId.getModuleFatherName())
					.getResultList().get(0);
			rolep.getRoleMenuitemPermissions().remove(permission);
			menuItem.getRoleMenuitemPermissions().remove(permission);
			entityManager.remove(permission);
			entityManager.flush();
			dataLoader.getPermissionsTable().get(functId.getId())
					.getPermittedRolesHashMap()
					.get(functId.getModuleFatherName()).remove(role);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	// GETTERS AND SETTERS

	public String getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(String selectedUser) {
		this.selectedUser = selectedUser;
	}

	public String getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(String selectedRole) {
		this.selectedRole = selectedRole;
	}
}
