package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ePuntoFinalList_ensayo")
public class EPuntoFinalList_ensayo extends EntityQuery<EPuntoFinal_ensayo> {

	private static final String EJBQL = "select ePuntoFinal from EPuntoFinal_ensayo ePuntoFinal";

	private static final String[] RESTRICTIONS = {
			"lower(ePuntoFinal.nombre) like concat(lower(#{ePuntoFinalList_ensayo.ePuntoFinal.nombre}),'%')",
			"lower(ePuntoFinal.descripcion) like concat(lower(#{ePuntoFinalList_ensayo.ePuntoFinal.descripcion}),'%')", };

	private EPuntoFinal_ensayo ePuntoFinal = new EPuntoFinal_ensayo();

	public EPuntoFinalList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EPuntoFinal_ensayo getEPuntoFinal() {
		return ePuntoFinal;
	}
}
