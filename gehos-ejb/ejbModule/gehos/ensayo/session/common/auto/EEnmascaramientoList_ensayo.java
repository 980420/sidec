package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eEnmascaramientoList_ensayo")
public class EEnmascaramientoList_ensayo extends
		EntityQuery<EEnmascaramiento_ensayo> {

	private static final String EJBQL = "select eEnmascaramiento from EEnmascaramiento_ensayo eEnmascaramiento";

	private static final String[] RESTRICTIONS = {
			"lower(eEnmascaramiento.nombre) like concat(lower(#{eEnmascaramientoList_ensayo.eEnmascaramiento.nombre}),'%')",
			"lower(eEnmascaramiento.descripcion) like concat(lower(#{eEnmascaramientoList_ensayo.eEnmascaramiento.descripcion}),'%')", };

	private EEnmascaramiento_ensayo eEnmascaramiento = new EEnmascaramiento_ensayo();

	public EEnmascaramientoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EEnmascaramiento_ensayo getEEnmascaramiento() {
		return eEnmascaramiento;
	}
}
