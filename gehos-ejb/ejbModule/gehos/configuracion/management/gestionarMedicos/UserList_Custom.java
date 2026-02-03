package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.Usuario_configuracion;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("medicouserList_Custom")
public class UserList_Custom extends EntityQuery<Usuario_configuracion> {

	private static final String[] RESTRICTIONS = {
		"lower(user.nombre) like concat(lower(#{userList_security.user.nombre}),'%')",
		"lower(user.username) like concat(lower(#{userList_security.user.username}),'%')",
		
		
		"lower(user.primerApellido) like concat(lower(#{userList_security.user.primerApellido}),'%')",
		"lower(user.segundoApellido) like concat(lower(#{userList_security.user.segundoApellido}),'%')",		
	 };

	private Usuario_configuracion user = new Usuario_configuracion();
	
	public UserList_Custom() {
		setEjbql(this.getEjbql());
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
	}

	@Override
	public String getEjbql() {
		return "select user from Usuario user where user.id not in (select medico.id from Medico_configuracion medico)";
	}

	@Override
	public Integer getMaxResults() {
		return 5;
	}

	public Usuario_configuracion getUser() {
		return user;
	}


}
