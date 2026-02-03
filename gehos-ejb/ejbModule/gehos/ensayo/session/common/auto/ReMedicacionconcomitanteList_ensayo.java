package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reMedicacionconcomitanteList_ensayo")
public class ReMedicacionconcomitanteList_ensayo extends
		EntityQuery<ReMedicacionconcomitante_ensayo> {

	private static final String EJBQL = "select reMedicacionconcomitante from ReMedicacionconcomitante_ensayo reMedicacionconcomitante";

	private static final String[] RESTRICTIONS = {};

	private ReMedicacionconcomitante_ensayo reMedicacionconcomitante = new ReMedicacionconcomitante_ensayo();

	public ReMedicacionconcomitanteList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReMedicacionconcomitante_ensayo getReMedicacionconcomitante() {
		return reMedicacionconcomitante;
	}
}
