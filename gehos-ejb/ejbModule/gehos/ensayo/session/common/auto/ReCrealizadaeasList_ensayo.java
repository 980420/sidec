package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reCrealizadaeasList_ensayo")
public class ReCrealizadaeasList_ensayo extends
		EntityQuery<ReCrealizadaeas_ensayo> {

	private static final String EJBQL = "select reCrealizadaeas from ReCrealizadaeas_ensayo reCrealizadaeas";

	private static final String[] RESTRICTIONS = { "lower(reCrealizadaeas.descripcion) like concat(lower(#{reCrealizadaeasList_ensayo.reCrealizadaeas.descripcion}),'%')", };

	private ReCrealizadaeas_ensayo reCrealizadaeas = new ReCrealizadaeas_ensayo();

	public ReCrealizadaeasList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReCrealizadaeas_ensayo getReCrealizadaeas() {
		return reCrealizadaeas;
	}
}
