package gehos.autorizacion.management.logical;

import gehos.autorizacion.entity.MenuItem;
import gehos.autorizacion.entity.RoleMenuitemPermission;
import gehos.autorizacion.entity.UserMenuitemPermission;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Startup
@Name("logicPermissionResolverDataLoader")
@Scope(ScopeType.APPLICATION)
public class LogicPermissionResolverDataLoader {

	@In
	EntityManager entityManager;
	private ConcurrentHashMap<Long, LogicPermission> permissionsTable = new ConcurrentHashMap<Long, LogicPermission>();
	//private ConcurrentHashMap<String, ConcurrentHashMap<Integer, LogicPermission>> permissionsTable = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, LogicPermission>>();
	
	private List<MenuItem> menuItemList;
	
	@SuppressWarnings("unchecked")
	@Create
	public void loadAccessControlLists() {
		System.out.print("loading logical app access rules!!");
		permissionsTable.clear();
		entityManager.clear();
		menuItemList = entityManager.createQuery("select m from MenuItem m").getResultList();
		for (int i = 0; i < menuItemList.size(); i++) {
			MenuItem menuitem = menuItemList.get(i);
			LogicPermission p = new LogicPermission();
			Set<UserMenuitemPermission> userpermissionSet = menuitem.getUserMenuitemPermissions();
			for (UserMenuitemPermission userMenuitemPermission : userpermissionSet) {
				if(userMenuitemPermission.getAllowed()){
					if(!p.getPermittedUsersHashMap().containsKey(userMenuitemPermission.getModulo().getNombre()))
						p.getPermittedUsersHashMap().put(userMenuitemPermission.getModulo().getNombre(), 
								new ConcurrentHashMap<String, Boolean>());
					p.getPermittedUsersHashMap().get(userMenuitemPermission.getModulo().getNombre())
							.put(userMenuitemPermission.getUsuario().getUsername(), true);
				}
				else{
					if(!p.getRestrictedUsersHashMap().containsKey(userMenuitemPermission.getModulo().getNombre()))
						p.getRestrictedUsersHashMap().put(userMenuitemPermission.getModulo().getNombre(), 
								new ConcurrentHashMap<String, Boolean>());
					p.getRestrictedUsersHashMap().get(userMenuitemPermission.getModulo().getNombre())
							.put(userMenuitemPermission.getUsuario().getUsername(), false);
				}
			}
			Set<RoleMenuitemPermission> rolepermissionSet = menuitem.getRoleMenuitemPermissions();
			for (RoleMenuitemPermission roleMenuitemPermission : rolepermissionSet) {
				if(roleMenuitemPermission.getAllowed()){
					if(!p.getPermittedRolesHashMap().containsKey(roleMenuitemPermission.getModulo().getNombre()))
						p.getPermittedRolesHashMap().put(roleMenuitemPermission.getModulo().getNombre(), 
								new ConcurrentHashMap<String, Boolean>());
					p.getPermittedRolesHashMap().get(roleMenuitemPermission.getModulo().getNombre())
							.put(roleMenuitemPermission.getRole().getName(), true);
				}
				else{
					if(!p.getRestrictedRolesHashMap().containsKey(roleMenuitemPermission.getModulo().getNombre()))
						p.getRestrictedRolesHashMap().put(roleMenuitemPermission.getModulo().getNombre(), 
								new ConcurrentHashMap<String, Boolean>());
					p.getRestrictedRolesHashMap().get(roleMenuitemPermission.getModulo().getNombre())
							.put(roleMenuitemPermission.getRole().getName(), false);
				}
			}
			permissionsTable.put(menuitem.getId(), p);
		}
		
		System.out.print("finished loading logical app access rules!!");
	}
	
	public ConcurrentHashMap<Long, LogicPermission> getPermissionsTable() {
		return permissionsTable;
	}
	public void setPermissionsTable(
			ConcurrentHashMap<Long, LogicPermission> permissionsTable) {
		this.permissionsTable = permissionsTable;
	}

	
	
}
