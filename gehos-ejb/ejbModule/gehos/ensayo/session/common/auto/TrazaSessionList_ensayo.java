package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("trazaSessionList_ensayo")
public class TrazaSessionList_ensayo extends EntityQuery<TrazaSession_ensayo> {

	private static final String EJBQL = "select trazaSession from TrazaSession_ensayo trazaSession";

	private static final String[] RESTRICTIONS = { "lower(trazaSession.direccionIp) like concat(lower(#{trazaSessionList_ensayo.trazaSession.direccionIp}),'%')", };

	private TrazaSession_ensayo trazaSession = new TrazaSession_ensayo();

	public TrazaSessionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TrazaSession_ensayo getTrazaSession() {
		return trazaSession;
	}
}
