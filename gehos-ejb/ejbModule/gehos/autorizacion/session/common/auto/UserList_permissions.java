package gehos.autorizacion.session.common.auto;

import gehos.autorizacion.entity.Usuario_permissions;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("userList_permissions")
public class UserList_permissions extends EntityQuery<Usuario_permissions> {

	private static final Long serialVersionUID = 1L;

	private static final String[] RESTRICTIONS = {
			"lower(user.nombre) like concat(lower(#{userList_permissions.user.nombre}),'%')",
			"lower(user.username) like concat(lower(#{userList_permissions.user.username}),'%')",
			/*"lower(user.password) like concat(lower(#{userList_permissions.user.password}),'%')",*/
			/*"lower(user.occupation) like concat(lower(#{userList_permissions.user.occupation}),'%')",
			"lower(user.localeString) like concat(lower(#{userList_permissions.user.localeString}),'%')",
			"lower(user.theme) like concat(lower(#{userList_permissions.user.theme}),'%')",
			"lower(user.descripcion) like concat(lower(#{userList_permissions.user.descripcion}),'%')",*/
			/*"lower(user.skin) like concat(lower(#{userList_permissions.user.skin}),'%')"*/ };

	private Usuario_permissions user = new Usuario_permissions();
	
	private static final String EJBQL = "select user from Usuario_permissions user where user.eliminado = false";

	
	public UserList_permissions(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(8);
		setOrder("user.username asc");
	}


	public Usuario_permissions getUser() {
		return user;
	}



}
