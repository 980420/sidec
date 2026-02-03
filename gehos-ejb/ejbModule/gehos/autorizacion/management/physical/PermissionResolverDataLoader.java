package gehos.autorizacion.management.physical;

import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.PasswordChgController;
import gehos.autenticacion.session.custom.PasswordStrength;
import gehos.autorizacion.entity.Resource;
import gehos.autorizacion.entity.RoleResourcePermission;
import gehos.autorizacion.entity.UserResourcePermission;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Startup
@Name("permissionResolverDataLoader")
@Scope(ScopeType.APPLICATION)
public class PermissionResolverDataLoader {

	@In(required=false) 
	PasswordChgController passwordChgController;
	
	@In
	EntityManager entityManager;
	
	@In 
	private RulesParser rulesParser; 
	
	//private ConcurrentHashMap<String, Permission> permissionsTable = new ConcurrentHashMap<String, Permission>();
	private ConcurrentHashMap<String, Permission> permissionsTable = new ConcurrentHashMap<String, Permission>();
	
	private List<Resource> recursosList;

	@SuppressWarnings("unchecked")
	@Create
	public void loadAccessControlLists() {
		System.out.print("loading physical app access rules!!");
		permissionsTable.clear();
		entityManager.clear();
		recursosList = entityManager.createQuery("select r from Resource r").getResultList();
		for (int i = 0; i < recursosList.size(); i++) {
			Resource resource = recursosList.get(i);
			Permission p = new Permission();
			Set<UserResourcePermission> userpermissionSet = resource.getUserResourcePermissions();
			for (UserResourcePermission userResourcePermission : userpermissionSet) {
				if(userResourcePermission.isAllowed())
					p.getPermittedUsersHashMap().put(userResourcePermission.getUsuario().getUsername(), true);
				else
					p.getRestrictedUsersHashMap().put(userResourcePermission.getUsuario().getUsername(), false);
			}
			Set<RoleResourcePermission> rolepermissionSet = resource.getRoleResourcePermissions();
			for (RoleResourcePermission roleResourcePermission : rolepermissionSet) {
				if(roleResourcePermission.isAllowed())
					p.getPermittedRolesHashMap().put(roleResourcePermission.getRole().getName(), true);
				else
					p.getRestrictedRolesHashMap().put(roleResourcePermission.getRole().getName(), false);
			}
			permissionsTable.put(resource.getVirtualPath(), p);
		}
		
		System.out.print("finished loading physical app access rules!!");
	}

	public ConcurrentHashMap<String, Permission> getPermissionsTable() {
		return permissionsTable;
	}
	
	private PasswordStrength getPasswordStrength() { 
	    RuleBase ruleBase = null; 
	    try { 
	 
	      ruleBase = rulesParser.readRule("/comun/passwordStrength.drl", 
	          RulesDirectoryBase.business_rules); 
	    } catch (Exception e) { 
	      e.printStackTrace(); 
	    } 
	    PasswordStrength passwordStrength = new PasswordStrength(); 
	     
	    StatefulSession session = ruleBase.newStatefulSession(); 
	    session.insert(passwordStrength); 
	    session.fireAllRules();     
	    session = ruleBase.newStatefulSession(); 
	    session.insert(passwordStrength); 
	    session.fireAllRules(); 
	    return passwordStrength; 
	  } 
	     
	  public boolean isPasswordExpired(Usuario usuario) { 
	    PasswordStrength passwordStrength = this.getPasswordStrength(); 
	    Calendar passwordDate = Calendar.getInstance(); 
	    passwordDate.setTime(usuario.getPasswordDate()); 
	    passwordDate.add(Calendar.DAY_OF_MONTH, passwordStrength.getPasswordLifeCicle()); 
	    Calendar today = Calendar.getInstance(); 
	    if(today.getTime().after(passwordDate.getTime())) { 
	      return true; 
	    } 
	    return false; 
	  } 

	public void setPermissionsTable(
			ConcurrentHashMap<String, Permission> resourcePermissionsTable) {
		this.permissionsTable = resourcePermissionsTable;
	}




}
