package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reDiagnosticoList_ensayo")
public class ReDiagnosticoList_ensayo extends EntityQuery<ReDiagnostico_ensayo> {

	private static final String EJBQL = "select reDiagnostico from ReDiagnostico_ensayo reDiagnostico";

	private static final String[] RESTRICTIONS = { "lower(reDiagnostico.otra) like concat(lower(#{reDiagnosticoList_ensayo.reDiagnostico.otra}),'%')", };

	private ReDiagnostico_ensayo reDiagnostico = new ReDiagnostico_ensayo();

	public ReDiagnosticoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReDiagnostico_ensayo getReDiagnostico() {
		return reDiagnostico;
	}
}
