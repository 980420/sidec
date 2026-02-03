package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("trazaAccionList_ensayo")
public class TrazaAccionList_ensayo extends EntityQuery<TrazaAccion_ensayo> {

	private static final String EJBQL = "select trazaAccion from TrazaAccion_ensayo trazaAccion";

	private static final String[] RESTRICTIONS = { "lower(trazaAccion.accionRealizada) like concat(lower(#{trazaAccionList_ensayo.trazaAccion.accionRealizada}),'%')", };

	private TrazaAccion_ensayo trazaAccion = new TrazaAccion_ensayo();

	public TrazaAccionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TrazaAccion_ensayo getTrazaAccion() {
		return trazaAccion;
	}
}
