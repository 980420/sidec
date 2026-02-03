package gehos.comun.incidencias.session;

import gehos.autenticacion.entity.Role;
import gehos.autenticacion.entity.Usuario;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("userRoleRoot")
@Scope(ScopeType.SESSION)
public class UserRoleRoot {
	
	private Boolean root;
	
	@In
	private Usuario user;
	
	public boolean isRoot(){
		if(root == null){
			root = false;
			for (Role rol : user.getRoles()) {
				if(rol.getName().equals("root")){
					root = true;
					break;
				}
			}
		}
		return root.booleanValue();
	}
	
}
