package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("roleList_configuracion")
public class RoleList_configuracion extends EntityQuery<Role_configuracion> {

	private static final String EJBQL = "select role from Role role";

	private static final String[] RESTRICTIONS = { "lower(role.name) like concat(lower(#{roleList.role.name}),'%')", };

	private Role_configuracion role = new Role_configuracion();

	public RoleList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Role_configuracion getRole() {
		return role;
	}
}
