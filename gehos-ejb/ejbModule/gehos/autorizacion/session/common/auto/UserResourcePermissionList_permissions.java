package gehos.autorizacion.session.common.auto;

import gehos.autorizacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("userResourcePermissionList_permissions")
public class UserResourcePermissionList_permissions extends EntityQuery {

	private static final String[] RESTRICTIONS = {};

	private UserResourcePermission userResourcePermission = new UserResourcePermission();

	@Override
	public String getEjbql() {
		return "select userResourcePermission from UserResourcePermission userResourcePermission";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public UserResourcePermission getUserResourcePermission() {
		return userResourcePermission;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
