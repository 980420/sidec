package gehos.autorizacion.management.logical;

import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.UserTools;
import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.util.HashSet;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name("logicPermissionResolver")
@Scope(ScopeType.SESSION)
public class LogicPermissionResolver {
	@In(required = false)
	Usuario user;
	@In(required = false)
	UserTools userTools;
	@In(value = "logicPermissionResolverDataLoader")
	LogicPermissionResolverDataLoader dataLoader;

	public boolean currentUserCanSeeThisFunctionality(
			Funcionalidad funcionalidad, Funcionalidad currentModule,
			boolean presumptionOfPermission) {
		if (user.getUsername().equals("root"))
			return true;
		Funcionalidad mod = currentModule;
		Funcionalidad funcMod = funcionalidad;
		// if(funcionalidad.getEsModulo())
		funcMod = funcionalidad.getFuncionalidadPadre();
		while (funcMod != null && !funcMod.getEsModulo())
			funcMod = funcMod.getFuncionalidadPadre();
		if (funcMod != null) {
			while (true) {
				if (dataLoader.getPermissionsTable().containsKey(
						funcionalidad.getId())) {
					LogicPermission temp = dataLoader.getPermissionsTable()
							.get(funcionalidad.getId());
					if (temp.getRestrictedUsersHashMap().containsKey(
							mod.getNombre()))
						if (temp.getRestrictedUsersHashMap().get(
								mod.getNombre()).containsKey(
								this.user.getUsername()))
							return false;
					if (temp.getPermittedUsersHashMap().containsKey(
							mod.getNombre()))
						if (temp.getPermittedUsersHashMap()
								.get(mod.getNombre()).containsKey(
										this.user.getUsername()))
							return true;
					if (temp.getRestrictedRolesHashMap().containsKey(
							mod.getNombre())) {
						HashSet<String> intersection = new HashSet<String>(temp
								.getRestrictedRolesHashMap().get(
										mod.getNombre()).keySet());
						intersection.retainAll(userTools.getRolesStrings());
						if (intersection.size() >= 1)
							return false;
					}
					if (temp.getPermittedRolesHashMap().containsKey(
							mod.getNombre())) {
						HashSet<String> intersection2 = new HashSet<String>(
								temp.getPermittedRolesHashMap().get(
										mod.getNombre()).keySet());
						intersection2.retainAll(userTools.getRolesStrings());
						if (intersection2.size() >= 1)
							return true;
					}
				}
				if (mod.getNombre().equals(funcMod.getNombre()))
					break;
				else
					mod = mod.getFuncionalidadPadre();
			}
		}
		return presumptionOfPermission;
	}

	public boolean userCanSeeThisFunctionality(Usuario user,
			Funcionalidad funcionalidad, Funcionalidad currentModule,
			boolean presumptionOfPermission) {
		if (user.getUsername().equals("root"))
			return true;
		HashSet<String> rolesString = new HashSet<String>();
		Funcionalidad mod = currentModule;
		Funcionalidad funcMod = funcionalidad;
		// if(funcionalidad.getEsModulo())
		funcMod = funcionalidad.getFuncionalidadPadre();
		while (funcMod != null && !funcMod.getEsModulo())
			funcMod = funcMod.getFuncionalidadPadre();
		if (funcMod != null) {
			while (true) {
				if (dataLoader.getPermissionsTable().containsKey(
						funcionalidad.getId())) {
					LogicPermission temp = dataLoader.getPermissionsTable()
							.get(funcionalidad.getId());
					if (temp.getRestrictedUsersHashMap().containsKey(
							mod.getNombre()))
						if (temp.getRestrictedUsersHashMap().get(
								mod.getNombre())
								.containsKey(user.getUsername()))
							return false;
					if (temp.getPermittedUsersHashMap().containsKey(
							mod.getNombre()))
						if (temp.getPermittedUsersHashMap()
								.get(mod.getNombre()).containsKey(
										user.getUsername()))
							return true;
					if (user.getRoles() != null) {
						for (Role mr : user.getRoles()) {
							rolesString.add(mr.getName());
						}
					}
					if (temp.getRestrictedRolesHashMap().containsKey(
							mod.getNombre())) {
						HashSet<String> intersection = new HashSet<String>(temp
								.getRestrictedRolesHashMap().get(
										mod.getNombre()).keySet());
						intersection.retainAll(rolesString);
						if (intersection.size() >= 1)
							return false;
					}
					if (temp.getPermittedRolesHashMap().containsKey(
							mod.getNombre())) {
						HashSet<String> intersection2 = new HashSet<String>(
								temp.getPermittedRolesHashMap().get(
										mod.getNombre()).keySet());
						intersection2.retainAll(rolesString);
						if (intersection2.size() >= 1)
							return true;
					}
				}
				if (mod.getNombre().equals(funcMod.getNombre()))
					break;
				else
					mod = mod.getFuncionalidadPadre();
			}
		}
		return presumptionOfPermission;
	}

	/*
	 * public ArrayList<Funcionalidad> filterFunctionalitiesByPermissions(
	 * ArrayList<Funcionalidad> funcs, Funcionalidad currentModule) {
	 * ArrayList<Funcionalidad> result = new ArrayList<Funcionalidad>(funcs);
	 * for (int i = 0; i < funcs.size(); i++) { Funcionalidad fun =
	 * funcs.get(i); Funcionalidad mod = currentModule; while(true){ // ver los
	 * permisos osi hay permisos para estemodulo en particular
	 * 
	 * 
	 * if(mod.getNombre().equals(fun.getFuncionalidadPadre().getNombre()))
	 * break; else mod = mod.getFuncionalidadPadre(); } } return result; }
	 */

}
