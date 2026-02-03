package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("roleList_ensayo")
public class RoleList_ensayo extends EntityQuery<Role_ensayo> {

	private static final String EJBQL = "select role from Role_ensayo role";

	private static final String[] RESTRICTIONS = {
			"lower(role.name) like concat(lower(#{roleList_ensayo.role.name}),'%')",
			"lower(role.codigo) like concat(lower(#{roleList_ensayo.role.codigo}),'%')", };

	private Role_ensayo role = new Role_ensayo();

	public RoleList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Role_ensayo getRole() {
		return role;
	}
}
