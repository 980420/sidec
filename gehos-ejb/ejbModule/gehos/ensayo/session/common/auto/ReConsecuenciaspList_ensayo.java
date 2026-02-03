package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reConsecuenciaspList_ensayo")
public class ReConsecuenciaspList_ensayo extends
		EntityQuery<ReConsecuenciaspaciente_ensayo> {

	private static final String EJBQL = "select reConsecuenciasp from ReConsecuenciasp_ensayo reConsecuenciasp";

	private static final String[] RESTRICTIONS = { "lower(reConsecuenciasp.descripcion) like concat(lower(#{reConsecuenciaspList_ensayo.reConsecuenciasp.descripcion}),'%')", };

	private ReConsecuenciaspaciente_ensayo reConsecuenciasp = new ReConsecuenciaspaciente_ensayo();

	public ReConsecuenciaspList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReConsecuenciaspaciente_ensayo getReConsecuenciasp() {
		return reConsecuenciasp;
	}
}
