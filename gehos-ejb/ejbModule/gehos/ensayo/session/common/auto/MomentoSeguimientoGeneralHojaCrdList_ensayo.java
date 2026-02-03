package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("momentoSeguimientoGeneralHojaCrdList_ensayo")
public class MomentoSeguimientoGeneralHojaCrdList_ensayo extends
		EntityQuery<MomentoSeguimientoGeneralHojaCrd_ensayo> {

	private static final String EJBQL = "select momentoSeguimientoGeneralHojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoSeguimientoGeneralHojaCrd";

	private static final String[] RESTRICTIONS = {};

	private MomentoSeguimientoGeneralHojaCrd_ensayo momentoSeguimientoGeneralHojaCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();

	public MomentoSeguimientoGeneralHojaCrdList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MomentoSeguimientoGeneralHojaCrd_ensayo getMomentoSeguimientoGeneralHojaCrd() {
		return momentoSeguimientoGeneralHojaCrd;
	}
}
