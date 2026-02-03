package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cieList_ensayo")
public class CieList_ensayo extends EntityQuery<Cie_ensayo> {

	private static final String EJBQL = "select cie from Cie_ensayo cie";

	private static final String[] RESTRICTIONS = {
			"lower(cie.codigo) like concat(lower(#{cieList_ensayo.cie.codigo}),'%')",
			"lower(cie.descripcion) like concat(lower(#{cieList_ensayo.cie.descripcion}),'%')",
			"lower(cie.incluye) like concat(lower(#{cieList_ensayo.cie.incluye}),'%')",
			"lower(cie.excluye) like concat(lower(#{cieList_ensayo.cie.excluye}),'%')",
			"lower(cie.observaciones) like concat(lower(#{cieList_ensayo.cie.observaciones}),'%')", };

	private Cie_ensayo cie = new Cie_ensayo();

	public CieList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Cie_ensayo getCie() {
		return cie;
	}
}
