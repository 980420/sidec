package gehos.autorizacion.session.common.auto;

import gehos.autorizacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("roleList_permissions")
public class RoleList_permissions extends EntityQuery {

	private static final String[] RESTRICTIONS = { "lower(role.name) like concat(lower(#{roleList_permissions.role.name}),'%')", };

	private Role_permissions role = new Role_permissions();

	@Override
	public String getEjbql() {
		return "select role from Role_permissions role";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Role_permissions getRole() {
		return role;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
