package gehos.autorizacion.session.common.auto;

import gehos.autorizacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("roleResourcePermissionList_permissions")
public class RoleResourcePermissionList_permissions extends EntityQuery {

	private static final String[] RESTRICTIONS = {};

	private RoleResourcePermission roleResourcePermission = new RoleResourcePermission();

	@Override
	public String getEjbql() {
		return "select roleResourcePermission from RoleResourcePermission roleResourcePermission";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public RoleResourcePermission getRoleResourcePermission() {
		return roleResourcePermission;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
