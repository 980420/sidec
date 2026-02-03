package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("profileList_configuracion")
public class ProfileList_configuracion extends
		EntityQuery<Profile_configuracion> {

	private static final String EJBQL = "select profile from Profile profile";

	private static final String[] RESTRICTIONS = {
			"lower(profile.localeString) like concat(lower(#{profileList.profile.localeString}),'%')",
			"lower(profile.theme) like concat(lower(#{profileList.profile.theme}),'%')", };

	private Profile_configuracion profile = new Profile_configuracion();

	public ProfileList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Profile_configuracion getProfile() {
		return profile;
	}
}
