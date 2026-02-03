package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reEnfermedadesList_ensayo")
public class ReEnfermedadesList_ensayo extends
		EntityQuery<ReEnfermedades_ensayo> {

	private static final String EJBQL = "select reEnfermedades from ReEnfermedades_ensayo reEnfermedades";

	private static final String[] RESTRICTIONS = { "lower(reEnfermedades.descripcion) like concat(lower(#{reEnfermedadesList_ensayo.reEnfermedades.descripcion}),'%')", };

	private ReEnfermedades_ensayo reEnfermedades = new ReEnfermedades_ensayo();

	public ReEnfermedadesList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReEnfermedades_ensayo getReEnfermedades() {
		return reEnfermedades;
	}
}
