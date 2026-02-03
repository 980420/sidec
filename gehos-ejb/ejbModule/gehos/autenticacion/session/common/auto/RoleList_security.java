package gehos.autenticacion.session.common.auto;

import gehos.autenticacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("roleList_security")
public class RoleList_security extends EntityQuery<Role> {

	private static final String[] RESTRICTIONS = { "lower(role.name) like concat(lower(#{roleList_security.role.name}),'%')", };

	private Role role = new Role();

	public RoleList_security() {
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	}

	@Override
	public String getEjbql() {
		return "select role from Role role";
	}

	@Override
	public Integer getMaxResults() {
		return 7;
	}

	public Role getRole() {
		return role;
	}



}
