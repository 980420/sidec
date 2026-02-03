package gehos.autenticacion.session.common.auto;

import gehos.autenticacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("userList_security")
public class UserList_security extends EntityQuery<Usuario> {

	private static final String[] RESTRICTIONS = {
		"lower(user.nombre) like concat(lower(#{userList_security.user.nombre}),'%')",
		"lower(user.username) like concat(lower(#{userList_security.user.username}),'%')",
		
		
		"lower(user.primerApellido) like concat(lower(#{userList_security.user.primerApellido}),'%')",
		"lower(user.segundoApellido) like concat(lower(#{userList_security.user.segundoApellido}),'%')",
		"lower(user.direccionParticular) like concat(lower(#{userList_security.user.direccionParticular}),'%')",
		"lower(user.cedula) like concat(lower(#{userList_security.user.cedula}),'%')",
	
		"lower(user.pasaporte) like concat(lower(#{userList_security.user.pasaporte}),'%')",
		"lower(user.telefono) like concat(lower(#{userList_security.user.telefono}),'%')",
	 };

	private Usuario user = new Usuario();
	
	public UserList_security() {
		setEjbql(this.getEjbql());
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(6);
	}

	@Override
	public String getEjbql() {
		return "select user from Usuario user";
	}

	@Override
	public Integer getMaxResults() {
		return 6;
	}

	public Usuario getUser() {
		return user;
	}


}
