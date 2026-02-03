package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reIntensidadList_ensayo")
public class ReIntensidadList_ensayo extends EntityQuery<ReIntensidad_ensayo> {

	private static final String EJBQL = "select reIntensidad from ReIntensidad_ensayo reIntensidad";

	private static final String[] RESTRICTIONS = {};

	private ReIntensidad_ensayo reIntensidad = new ReIntensidad_ensayo();

	public ReIntensidadList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReIntensidad_ensayo getReIntensidad() {
		return reIntensidad;
	}
}
