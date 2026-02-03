package gehos.autorizacion.management.physical;

import gehos.autorizacion.entity.Resource;
import gehos.autorizacion.entity.RoleResourcePermission;
import gehos.autorizacion.entity.Role_permissions;
import gehos.autorizacion.entity.UserResourcePermission;
import gehos.autorizacion.entity.Usuario_permissions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

@Name("permissionManager")
public class PermissionManager {
	
	private String selectedResource;
	
	private String presentableSelectedResource;
	@In
	private EntityManager entityManager;	
	
	@In("permissionResolverDataLoader")
	PermissionResolverDataLoader dataLoader;	
	@In
	FacesMessages facesMessages;
	
	
	@SuppressWarnings("unchecked")
	public List<String> allUsers(){
		return entityManager.createQuery("select u.username from Usuario_permissions u").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> allRoles(){
		return entityManager.createQuery("select r.name from Role_permissions r where r.eliminado is null or r.eliminado = false order by r.name asc").getResultList();
	}
	
	@Transactional
	public void deletedRestrictedUser(String resource, String username){
		dataLoader.getPermissionsTable().get(resource).getRestrictedUsersHashMap().remove(username);
		Resource r = (Resource) entityManager.createQuery(
				"from Resource r where r.virtualPath = :path")
				.setParameter("path", resource).getSingleResult();
		Usuario_permissions user = (Usuario_permissions) entityManager.createQuery(
				"from Usuario_permissions u where u.username = :username")
				.setParameter("username", username).getSingleResult();
		UserResourcePermission permission = (UserResourcePermission)entityManager
			.createQuery("from UserResourcePermission p where p.resource.id = :rid and p.usuario.id = :uid and p.allowed=false")
			.setParameter("rid", r.getId())
			.setParameter("uid", user.getId())
			.getResultList().get(0);
		user.getUserResourcePermissions().remove(permission);
		r.getUserResourcePermissions().remove(permission);
		entityManager.remove(permission);
		entityManager.flush();
	}
	public void deletedPermitedUser(String resource, String username){
		dataLoader.getPermissionsTable().get(resource).getPermittedUsersHashMap().remove(username);
		Resource r = (Resource) entityManager.createQuery(
				"from Resource r where r.virtualPath = :path")
				.setParameter("path", resource).getSingleResult();
		Usuario_permissions user = (Usuario_permissions) entityManager.createQuery(
				"from Usuario_permissions u where u.username = :username")
				.setParameter("username", username).getSingleResult();
		UserResourcePermission permission = (UserResourcePermission)entityManager
			.createQuery("from UserResourcePermission p where p.resource.id = :rid and p.usuario.id = :uid and p.allowed=true")
			.setParameter("rid", r.getId())
			.setParameter("uid", user.getId())
			.getResultList().get(0);
		user.getUserResourcePermissions().remove(permission);
		r.getUserResourcePermissions().remove(permission);
		entityManager.remove(permission);
		entityManager.flush();
	}
	public void deletedRestrictedRole(String resource, String role){
		dataLoader.getPermissionsTable().get(resource).getRestrictedRolesHashMap().remove(role);
		Resource r = (Resource) entityManager.createQuery(
				"from Resource r where r.virtualPath = :path")
				.setParameter("path", resource).getSingleResult();
		Role_permissions rolep = (Role_permissions) entityManager.createQuery(
				"from Role_permissions r where r.name = :role")
				.setParameter("role", role).getSingleResult();
		RoleResourcePermission permission = (RoleResourcePermission)entityManager
			.createQuery("from RoleResourcePermission p where p.resource.id = :rid and p.role.id = :uid and p.allowed=false")
			.setParameter("rid", r.getId())
			.setParameter("uid", rolep.getId())
			.getResultList().get(0);
		rolep.getRoleResourcePermissions().remove(permission);
		r.getRoleResourcePermissions().remove(permission);
		entityManager.remove(permission);
		entityManager.flush();
	}
	public void deletedPermitedRole(String resource, String role){
		dataLoader.getPermissionsTable().get(resource).getPermittedRolesHashMap().remove(role);
		Resource r = (Resource) entityManager.createQuery(
				"from Resource r where r.virtualPath = :path")
				.setParameter("path", resource).getSingleResult();
		Role_permissions rolep = (Role_permissions) entityManager.createQuery(
				"from Role_permissions r where r.name = :role")
				.setParameter("role", role).getSingleResult();
		RoleResourcePermission permission = (RoleResourcePermission)entityManager
			.createQuery("from RoleResourcePermission p where p.resource.id = :rid and p.role.id = :uid and p.allowed=true")
			.setParameter("rid", r.getId())
			.setParameter("uid", rolep.getId())
			.getResultList().get(0);
		rolep.getRoleResourcePermissions().remove(permission);
		r.getRoleResourcePermissions().remove(permission);
		entityManager.remove(permission);
		entityManager.flush();
	}
	
	
	
	
	
	private String selectedUser;
	private String selectedRole;
	public void provideAccessToUser(){
		try {
			if(this.selectedResource == null || this.selectedUser == null)
				return;
			if(!dataLoader.getPermissionsTable().containsKey(this.selectedResource)){
				Resource r = new Resource();
				r.setVirtualPath(this.selectedResource);
				UserResourcePermission userResourcePermission = new UserResourcePermission();
				userResourcePermission.setResource(r);
				Usuario_permissions userp = (Usuario_permissions)entityManager.
									createQuery("from Usuario_permissions u " +
									"where u.username = :username")
									.setParameter("username", selectedUser)
									.getSingleResult();
				userResourcePermission.setUsuario(userp);
				userResourcePermission.setAllowed(true);
				r.getUserResourcePermissions().add(userResourcePermission);
				entityManager.persist(r);
				entityManager.persist(userResourcePermission);
				entityManager.flush();

				Permission p = new Permission();
				p.getPermittedUsersHashMap().put(selectedUser, true);
				dataLoader.getPermissionsTable().put(this.selectedResource, p);
			}
			else{
				Permission p = dataLoader.getPermissionsTable().get(this.selectedResource);
				if(!p.getPermittedUsersHashMap().containsKey(selectedUser)){
					Resource r = (Resource)entityManager.createQuery("from Resource r " +
								"where r.virtualPath = :virtualpath")
								.setParameter("virtualpath", this.selectedResource)
								.getSingleResult();
					UserResourcePermission userResourcePermission = new UserResourcePermission();
					userResourcePermission.setResource(r);
					Usuario_permissions userp = (Usuario_permissions)entityManager.
										createQuery("from Usuario_permissions u " +
										"where u.username = :username")
										.setParameter("username", selectedUser)
										.getSingleResult();
					userResourcePermission.setUsuario(userp);
					userResourcePermission.setAllowed(true);
					r.getUserResourcePermissions().add(userResourcePermission);
					entityManager.persist(userResourcePermission);
					entityManager.flush();

					p.getPermittedUsersHashMap().put(selectedUser, true);
				}
				else
					facesMessages.add(new FacesMessage("Usuario ya adicionado"));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	public void denyAccessToUser(){		
		try {
			if(this.selectedResource == null || this.selectedUser == null)
				return;		
			if(!dataLoader.getPermissionsTable().containsKey(this.selectedResource)){
				Resource r = new Resource();
				r.setVirtualPath(this.selectedResource);
				UserResourcePermission userResourcePermission = new UserResourcePermission();
				userResourcePermission.setResource(r);
				Usuario_permissions userp = (Usuario_permissions)entityManager.
									createQuery("from Usuario_permissions u " +
									"where u.username = :username")
									.setParameter("username", selectedUser)
									.getSingleResult();
				userResourcePermission.setUsuario(userp);
				userResourcePermission.setAllowed(false);
				r.getUserResourcePermissions().add(userResourcePermission);
				entityManager.persist(r);
				entityManager.persist(userResourcePermission);
				entityManager.flush();

				Permission p = new Permission();
				p.getRestrictedUsersHashMap().put(selectedUser, false);
				dataLoader.getPermissionsTable().put(this.selectedResource, p);
			}
			else{
				Permission p = dataLoader.getPermissionsTable().get(this.selectedResource);
				if(!p.getRestrictedUsersHashMap().containsKey(selectedUser)){
					Resource r = (Resource)entityManager.createQuery("from Resource r " +
								"where r.virtualPath = :virtualpath")
								.setParameter("virtualpath", this.selectedResource)
								.getSingleResult();
					UserResourcePermission userResourcePermission = new UserResourcePermission();
					userResourcePermission.setResource(r);
					Usuario_permissions userp = (Usuario_permissions)entityManager.
										createQuery("from Usuario_permissions u " +
										"where u.username = :username")
										.setParameter("username", selectedUser)
										.getSingleResult();
					userResourcePermission.setUsuario(userp);
					userResourcePermission.setAllowed(false);
					r.getUserResourcePermissions().add(userResourcePermission);
					entityManager.persist(userResourcePermission);
					entityManager.flush();
					
					p.getRestrictedUsersHashMap().put(selectedUser, false);
				}
				else
					facesMessages.add(new FacesMessage("Usuario ya adicionado"));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	public void provideAccessToRole(){
		try {
			if(this.selectedResource == null || this.selectedRole == null)
				return;
			if(!dataLoader.getPermissionsTable().containsKey(this.selectedResource)){
				Resource r = new Resource();
				r.setVirtualPath(this.selectedResource);
				RoleResourcePermission roleResourcePermission = new RoleResourcePermission();
				roleResourcePermission.setResource(r);
				Role_permissions rolep = (Role_permissions)entityManager.
									createQuery("from Role_permissions r " +
									"where r.name = :role")
									.setParameter("role", selectedRole)
									.getSingleResult();
				roleResourcePermission.setRole(rolep);
				roleResourcePermission.setAllowed(true);
				r.getRoleResourcePermissions().add(roleResourcePermission);
				entityManager.persist(r);
				entityManager.persist(roleResourcePermission);
				entityManager.flush();
				
				Permission p = new Permission();
				p.getPermittedRolesHashMap().put(selectedRole, true);
				dataLoader.getPermissionsTable().put(this.selectedResource, p);
			}
			else{
				Permission p = dataLoader.getPermissionsTable().get(this.selectedResource);
				if(!p.getPermittedRolesHashMap().containsKey(selectedRole)){
					Resource r = (Resource)entityManager.createQuery("from Resource r " +
								"where r.virtualPath = :virtualpath")
								.setParameter("virtualpath", this.selectedResource)
								.getSingleResult();
					RoleResourcePermission roleResourcePermission = new RoleResourcePermission();
					roleResourcePermission.setResource(r);
					Role_permissions rolep = (Role_permissions)entityManager.
										createQuery("from Role_permissions r " +
										"where r.name = :role")
										.setParameter("role", selectedRole)
										.getSingleResult();
					roleResourcePermission.setRole(rolep);
					roleResourcePermission.setAllowed(true);
					r.getRoleResourcePermissions().add(roleResourcePermission);
					entityManager.persist(roleResourcePermission);
					entityManager.flush();
					
					p.getPermittedRolesHashMap().put(selectedRole, true);
				}
				else
					facesMessages.add(new FacesMessage("Rol ya adicionado"));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	public void denyAccessToRole(){
		try {
			if(this.selectedResource == null || this.selectedRole == null)
				return;		
			if(!dataLoader.getPermissionsTable().containsKey(this.selectedResource)){
				Resource r = new Resource();
				r.setVirtualPath(this.selectedResource);
				RoleResourcePermission roleResourcePermission = new RoleResourcePermission();
				roleResourcePermission.setResource(r);
				Role_permissions rolep = (Role_permissions)entityManager.
									createQuery("from Role_permissions r " +
									"where r.name = :role")
									.setParameter("role", selectedRole)
									.getSingleResult();
				roleResourcePermission.setRole(rolep);
				roleResourcePermission.setAllowed(false);
				r.getRoleResourcePermissions().add(roleResourcePermission);
				entityManager.persist(r);
				entityManager.persist(roleResourcePermission);
				entityManager.flush();
				
				Permission p = new Permission();
				p.getRestrictedRolesHashMap().put(selectedRole, false);
				dataLoader.getPermissionsTable().put(this.selectedResource, p);
			}
			else{
				Permission p = dataLoader.getPermissionsTable().get(this.selectedResource);
				if(!p.getRestrictedRolesHashMap().containsKey(selectedRole)){
					Resource r = (Resource)entityManager.createQuery("from Resource r " +
								"where r.virtualPath = :virtualpath")
								.setParameter("virtualpath", this.selectedResource)
								.getSingleResult();
					RoleResourcePermission roleResourcePermission = new RoleResourcePermission();
					roleResourcePermission.setResource(r);
					Role_permissions rolep = (Role_permissions)entityManager.
										createQuery("from Role_permissions r " +
										"where r.name = :role")
										.setParameter("role", selectedRole)
										.getSingleResult();
					roleResourcePermission.setRole(rolep);
					roleResourcePermission.setAllowed(false);
					r.getRoleResourcePermissions().add(roleResourcePermission);
					entityManager.persist(roleResourcePermission);
					entityManager.flush();
					
					p.getRestrictedRolesHashMap().put(selectedRole, false);
				}
				else
					facesMessages.add(new FacesMessage("Rol ya adicionado"));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listHierarchy(){
		String viewId = this.selectedResource;
		List<String> result = new ArrayList<String>();
		if (viewId != null) {
			do {
				result.add(viewId);
				viewId = viewId.substring(0, viewId.lastIndexOf("/"));
			} while (viewId.length() > 1);
		}
		return result;
	}
	public String trimResourceName(String resource){
		if(resource.length() > 40)
			resource = "/..." + resource.substring(resource.length() - 40, resource.length());
		return resource;
	}
	public void initializePrecedenceTables(){
		this.userPrecedenceRules = new Hashtable<String, Boolean>();
		this.rolePrecedenceRules = new Hashtable<String, Boolean>();
	}
	private Hashtable<String, Boolean> userPrecedenceRules;
	private Hashtable<String, Boolean> rolePrecedenceRules;
	
	public boolean existMoreImportantRuleForUser(String user){
		return this.userPrecedenceRules.containsKey(user);
	}
	public boolean existMoreImportantRuleForRole(String rol){
		return this.rolePrecedenceRules.containsKey(rol);
	}
	
	public void updateUserPrecedenceTableForRestrictedOnes(String resource){
		if(!dataLoader.getPermissionsTable().containsKey(resource))	return;
		Set<String> temp = dataLoader.getPermissionsTable().get(resource).getRestrictedUsersHashMap().keySet();
		for (String user : temp) {
			if(!this.userPrecedenceRules.containsKey(user))
				this.userPrecedenceRules.put(user, true);
		}
	}
	public void updateUserPrecedenceTableForPermittedOnes(String resource){
		if(!dataLoader.getPermissionsTable().containsKey(resource))	return;
		Set<String> temp = dataLoader.getPermissionsTable().get(resource).getPermittedUsersHashMap().keySet();
		for (String user : temp) {
			if(!this.userPrecedenceRules.containsKey(user))
				this.userPrecedenceRules.put(user, true);
		}
	}
	public void updateRolesPrecedenceTableForRestrictedOnes(String resource){
		if(!dataLoader.getPermissionsTable().containsKey(resource))	return;
		Set<String> temp = dataLoader.getPermissionsTable().get(resource).getRestrictedRolesHashMap().keySet();
		for (String role : temp) {
			if(!this.rolePrecedenceRules.containsKey(role))
				this.rolePrecedenceRules.put(role, true);
		}
	}
	public void updateRolesPrecedenceTableForPermittedOnes(String resource){
		if(!dataLoader.getPermissionsTable().containsKey(resource))	return;
		Set<String> temp = dataLoader.getPermissionsTable().get(resource).getPermittedRolesHashMap().keySet();
		for (String role : temp) {
			if(!this.rolePrecedenceRules.containsKey(role))
				this.rolePrecedenceRules.put(role, true);
		}
	}
	
	public List<String> userDeniedForResource(String resource){
		if(dataLoader.getPermissionsTable().containsKey(resource))
			return new ArrayList<String>(dataLoader.getPermissionsTable().get(resource).getRestrictedUsersHashMap().keySet());
		return new ArrayList<String>();
	}
	public List<String> usersAllowedForResource(String resource){
		if(dataLoader.getPermissionsTable().containsKey(resource))
			return new ArrayList<String>(dataLoader.getPermissionsTable().get(resource).getPermittedUsersHashMap().keySet());
		return new ArrayList<String>();
	}
	public List<String> rolesDeniedForResource(String resource){
		if(dataLoader.getPermissionsTable().containsKey(resource))
			return new ArrayList<String>(dataLoader.getPermissionsTable().get(resource).getRestrictedRolesHashMap().keySet());
		return new ArrayList<String>();
	}
	public List<String> rolesAllowedForResource(String resource){
		if(dataLoader.getPermissionsTable().containsKey(resource))
			return new ArrayList<String>(dataLoader.getPermissionsTable().get(resource).getPermittedRolesHashMap().keySet());
		return new ArrayList<String>();
	}
	
	public void setSelectedResource(String selectedResource) {
		facesMessages.clear();
		this.selectedResource = selectedResource;
		int index = 0;
		StringBuffer buffer = new StringBuffer(
				this.selectedResource.substring(
						this.selectedResource.lastIndexOf("/") + 1,
						this.selectedResource.length())); 
		while(index < buffer.length() - 1){
			buffer.insert(index, "&shy;"); index += 10;
		}
		this.presentableSelectedResource = buffer.toString();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	//getters and setters
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	public String getSelectedResource() {
		return selectedResource;
	}

	public String getPresentableSelectedResource() {
		return presentableSelectedResource;
	}

	public void setPresentableSelectedResource(String presentableSelectedResource) {
		this.presentableSelectedResource = presentableSelectedResource;
	}

	public Hashtable<String, Boolean> getUserPrecedenceRules() {
		return userPrecedenceRules;
	}

	public void setUserPrecedenceRules(
			Hashtable<String, Boolean> userPrecedenceRules) {
		this.userPrecedenceRules = userPrecedenceRules;
	}

	public Hashtable<String, Boolean> getRolePrecedenceRules() {
		return rolePrecedenceRules;
	}

	public void setRolePrecedenceRules(
			Hashtable<String, Boolean> rolePrecedenceRules) {
		this.rolePrecedenceRules = rolePrecedenceRules;
	}

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
