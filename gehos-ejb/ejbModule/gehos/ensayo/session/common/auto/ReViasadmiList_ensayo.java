package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reViasadmiList_ensayo")
public class ReViasadmiList_ensayo extends EntityQuery<ReViasadmi_ensayo> {

	private static final String EJBQL = "select reViasadmi from ReViasadmi_ensayo reViasadmi";

	private static final String[] RESTRICTIONS = { "lower(reViasadmi.descripcion) like concat(lower(#{reViasadmiList_ensayo.reViasadmi.descripcion}),'%')", };

	private ReViasadmi_ensayo reViasadmi = new ReViasadmi_ensayo();

	public ReViasadmiList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReViasadmi_ensayo getReViasadmi() {
		return reViasadmi;
	}
}
