package gehos.bitacora.session.common.auto;

import gehos.bitacora.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("userList_bitacora")
public class UserList_bitacora extends EntityQuery {

	private static final String[] RESTRICTIONS = {
			"lower(user.name) like concat(lower(#{userList_bitacora.user.name}),'%')",
			"lower(user.username) like concat(lower(#{userList_bitacora.user.username}),'%')",
			"lower(user.password) like concat(lower(#{userList_bitacora.user.password}),'%')",
			"lower(user.occupation) like concat(lower(#{userList_bitacora.user.occupation}),'%')",
			"lower(user.localeString) like concat(lower(#{userList_bitacora.user.localeString}),'%')",
			"lower(user.theme) like concat(lower(#{userList_bitacora.user.theme}),'%')", };

	private User_Bitacora user = new User_Bitacora();

	@Override
	public String getEjbql() {
		return "select user from User user";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public User_Bitacora getUser() {
		return user;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
