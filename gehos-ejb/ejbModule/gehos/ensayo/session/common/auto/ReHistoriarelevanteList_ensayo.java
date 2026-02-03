package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reHistoriarelevanteList_ensayo")
public class ReHistoriarelevanteList_ensayo extends
		EntityQuery<ReHistoriarelevante_ensayo> {

	private static final String EJBQL = "select reHistoriarelevante from ReHistoriarelevante_ensayo reHistoriarelevante";

	private static final String[] RESTRICTIONS = { "lower(reHistoriarelevante.otra) like concat(lower(#{reHistoriarelevanteList_ensayo.reHistoriarelevante.otra}),'%')", };

	private ReHistoriarelevante_ensayo reHistoriarelevante = new ReHistoriarelevante_ensayo();

	public ReHistoriarelevanteList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReHistoriarelevante_ensayo getReHistoriarelevante() {
		return reHistoriarelevante;
	}
}
