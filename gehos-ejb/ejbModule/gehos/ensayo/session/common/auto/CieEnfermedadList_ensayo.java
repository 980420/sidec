package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cieEnfermedadList_ensayo")
public class CieEnfermedadList_ensayo extends
		EntityQuery<CieEnfermedad_ensayo> {

	private static final String EJBQL = "select cieEnfermedad from CieEnfermedad_ensayo cieEnfermedad";

	private static final String[] RESTRICTIONS = {
			"lower(cieEnfermedad.codigoEnfermedad) like concat(lower(#{cieEnfermedadList_ensayo.cieEnfermedad.codigoEnfermedad}),'%')",
			"lower(cieEnfermedad.descripcion) like concat(lower(#{cieEnfermedadList_ensayo.cieEnfermedad.descripcion}),'%')", };

	private CieEnfermedad_ensayo cieEnfermedad = new CieEnfermedad_ensayo();

	public CieEnfermedadList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CieEnfermedad_ensayo getCieEnfermedad() {
		return cieEnfermedad;
	}
}
