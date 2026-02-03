package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reMedicacionrecienteList_ensayo")
public class ReMedicacionrecienteList_ensayo extends
		EntityQuery<ReMedicacionreciente_ensayo> {

	private static final String EJBQL = "select reMedicacionreciente from ReMedicacionreciente_ensayo reMedicacionreciente";

	private static final String[] RESTRICTIONS = {};

	private ReMedicacionreciente_ensayo reMedicacionreciente = new ReMedicacionreciente_ensayo();

	public ReMedicacionrecienteList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReMedicacionreciente_ensayo getReMedicacionreciente() {
		return reMedicacionreciente;
	}
}
