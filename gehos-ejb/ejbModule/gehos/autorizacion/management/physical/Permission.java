package gehos.autorizacion.management.physical;

import java.util.concurrent.ConcurrentHashMap;

public class Permission {
	private ConcurrentHashMap<String, Boolean> permittedUsersHashMap;
	private ConcurrentHashMap<String, Boolean> restrictedUsersHashMap;
	private ConcurrentHashMap<String, Boolean> permittedRolesHashMap;
	private ConcurrentHashMap<String, Boolean> restrictedRolesHashMap;

	public Permission(){
		permittedUsersHashMap = new ConcurrentHashMap<String, Boolean>();
		restrictedUsersHashMap = new ConcurrentHashMap<String, Boolean>();
		permittedRolesHashMap = new ConcurrentHashMap<String, Boolean>();
		restrictedRolesHashMap = new ConcurrentHashMap<String, Boolean>();
	}

	public ConcurrentHashMap<String, Boolean> getPermittedUsersHashMap() {
		return permittedUsersHashMap;
	}

	public void setPermittedUsersHashMap(
			ConcurrentHashMap<String, Boolean> permittedUsersHashMap) {
		this.permittedUsersHashMap = permittedUsersHashMap;
	}

	public ConcurrentHashMap<String, Boolean> getRestrictedUsersHashMap() {
		return restrictedUsersHashMap;
	}

	public void setRestrictedUsersHashMap(
			ConcurrentHashMap<String, Boolean> restrictedUsersHashMap) {
		this.restrictedUsersHashMap = restrictedUsersHashMap;
	}

	public ConcurrentHashMap<String, Boolean> getPermittedRolesHashMap() {
		return permittedRolesHashMap;
	}

	public void setPermittedRolesHashMap(
			ConcurrentHashMap<String, Boolean> permittedRolesHashMap) {
		this.permittedRolesHashMap = permittedRolesHashMap;
	}

	public ConcurrentHashMap<String, Boolean> getRestrictedRolesHashMap() {
		return restrictedRolesHashMap;
	}

	public void setRestrictedRolesHashMap(
			ConcurrentHashMap<String, Boolean> restrictedRolesHashMap) {
		this.restrictedRolesHashMap = restrictedRolesHashMap;
	}




}
