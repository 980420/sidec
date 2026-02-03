package gehos.bitacora.session.common.auto;

import gehos.bitacora.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("trazaSessionList_bitacora")
public class TrazaSessionList_bitacora extends EntityQuery {

	private static final String[] RESTRICTIONS = { "lower(trazaSession.direccionIp) like concat(lower(#{trazaSessionList_bitacora.trazaSession.direccionIp}),'%')", };

	private TrazaSession trazaSession = new TrazaSession();

	@Override
	public String getEjbql() {
		return "select trazaSession from TrazaSession trazaSession";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public TrazaSession getTrazaSession() {
		return trazaSession;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
