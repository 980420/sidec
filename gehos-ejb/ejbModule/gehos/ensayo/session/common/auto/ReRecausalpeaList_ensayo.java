package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reRecausalpeaList_ensayo")
public class ReRecausalpeaList_ensayo extends EntityQuery<ReRecausalpea_ensayo> {

	private static final String EJBQL = "select reRecausalpea from ReRecausalpea_ensayo reRecausalpea";

	private static final String[] RESTRICTIONS = { "lower(reRecausalpea.descripcion) like concat(lower(#{reRecausalpeaList_ensayo.reRecausalpea.descripcion}),'%')", };

	private ReRecausalpea_ensayo reRecausalpea = new ReRecausalpea_ensayo();

	public ReRecausalpeaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReRecausalpea_ensayo getReRecausalpea() {
		return reRecausalpea;
	}
}
