package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("hojaCrdList_ensayo")
public class HojaCrdList_ensayo extends EntityQuery<HojaCrd_ensayo> {

	private static final String EJBQL = "select hojaCrd from HojaCrd_ensayo hojaCrd";

	private static final String[] RESTRICTIONS = {
			"lower(hojaCrd.nombreHoja) like concat(lower(#{hojaCrdList_ensayo.hojaCrd.nombreHoja}),'%')",
			"lower(hojaCrd.descripcion) like concat(lower(#{hojaCrdList_ensayo.hojaCrd.descripcion}),'%')", };

	private HojaCrd_ensayo hojaCrd = new HojaCrd_ensayo();

	public HojaCrdList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public HojaCrd_ensayo getHojaCrd() {
		return hojaCrd;
	}
}
