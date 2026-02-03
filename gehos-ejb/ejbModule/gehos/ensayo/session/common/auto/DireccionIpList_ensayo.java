package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("direccionIpList_ensayo")
public class DireccionIpList_ensayo extends EntityQuery<DireccionIp_ensayo> {

	private static final String EJBQL = "select direccionIp from DireccionIp_ensayo direccionIp";

	private static final String[] RESTRICTIONS = {
			"lower(direccionIp.ip) like concat(lower(#{direccionIpList_ensayo.direccionIp.ip}),'%')",
			"lower(direccionIp.ipFinal) like concat(lower(#{direccionIpList_ensayo.direccionIp.ipFinal}),'%')", };

	private DireccionIp_ensayo direccionIp = new DireccionIp_ensayo();

	public DireccionIpList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DireccionIp_ensayo getDireccionIp() {
		return direccionIp;
	}
}
