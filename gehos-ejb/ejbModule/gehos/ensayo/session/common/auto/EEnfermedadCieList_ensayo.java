package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eEnfermedadCieList_ensayo")
public class EEnfermedadCieList_ensayo extends
		EntityQuery<EEnfermedadCie_ensayo> {

	private static final String EJBQL = "select eEnfermedadCie from EEnfermedadCie_ensayo eEnfermedadCie";

	private static final String[] RESTRICTIONS = {
			"lower(eEnfermedadCie.codigoEnfermedad) like concat(lower(#{eEnfermedadCieList_ensayo.eEnfermedadCie.codigoEnfermedad}),'%')",
			"lower(eEnfermedadCie.descripcionEnfermedad) like concat(lower(#{eEnfermedadCieList_ensayo.eEnfermedadCie.descripcionEnfermedad}),'%')", };

	private EEnfermedadCie_ensayo eEnfermedadCie = new EEnfermedadCie_ensayo();

	public EEnfermedadCieList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EEnfermedadCie_ensayo getEEnfermedadCie() {
		return eEnfermedadCie;
	}
}
