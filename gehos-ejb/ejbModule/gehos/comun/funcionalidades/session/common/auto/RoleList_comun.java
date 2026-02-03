package gehos.comun.funcionalidades.session.common.auto;

import gehos.comun.funcionalidades.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("roleList_comun")
public class RoleList_comun extends EntityQuery {

	private static final String[] RESTRICTIONS = { "lower(role.name) like concat(lower(#{roleList_comun.role.name}),'%')", };

	private Role_comun role = new Role_comun();

	@Override
	public String getEjbql() {
		return "select role from Role_comun role";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Role_comun getRole() {
		return role;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
