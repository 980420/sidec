package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reResultadofinalList_ensayo")
public class ReResultadofinalList_ensayo extends
		EntityQuery<ReResultadofinal_ensayo> {

	private static final String EJBQL = "select reResultadofinal from ReResultadofinal_ensayo reResultadofinal";

	private static final String[] RESTRICTIONS = { "lower(reResultadofinal.descripcion) like concat(lower(#{reResultadofinalList_ensayo.reResultadofinal.descripcion}),'%')", };

	private ReResultadofinal_ensayo reResultadofinal = new ReResultadofinal_ensayo();

	public ReResultadofinalList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReResultadofinal_ensayo getReResultadofinal() {
		return reResultadofinal;
	}
}
