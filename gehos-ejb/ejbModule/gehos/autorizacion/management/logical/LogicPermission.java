package gehos.autorizacion.management.logical;

import java.util.concurrent.ConcurrentHashMap;

public class LogicPermission {
	private ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> permittedUsersHashMap;
	private ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> restrictedUsersHashMap;
	private ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> permittedRolesHashMap;
	private ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> restrictedRolesHashMap;

	public LogicPermission(){
		permittedUsersHashMap = new ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>>();
		restrictedUsersHashMap = new ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>>();
		permittedRolesHashMap = new ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>>();
		restrictedRolesHashMap = new ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>>();
	}

	public ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> getPermittedUsersHashMap() {
		return permittedUsersHashMap;
	}

	public void setPermittedUsersHashMap(
			ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> permittedUsersHashMap) {
		this.permittedUsersHashMap = permittedUsersHashMap;
	}

	public ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> getRestrictedUsersHashMap() {
		return restrictedUsersHashMap;
	}

	public void setRestrictedUsersHashMap(
			ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> restrictedUsersHashMap) {
		this.restrictedUsersHashMap = restrictedUsersHashMap;
	}

	public ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> getPermittedRolesHashMap() {
		return permittedRolesHashMap;
	}

	public void setPermittedRolesHashMap(
			ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> permittedRolesHashMap) {
		this.permittedRolesHashMap = permittedRolesHashMap;
	}

	public ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> getRestrictedRolesHashMap() {
		return restrictedRolesHashMap;
	}

	public void setRestrictedRolesHashMap(
			ConcurrentHashMap<String,ConcurrentHashMap<String, Boolean>> restrictedRolesHashMap) {
		this.restrictedRolesHashMap = restrictedRolesHashMap;
	}

}
