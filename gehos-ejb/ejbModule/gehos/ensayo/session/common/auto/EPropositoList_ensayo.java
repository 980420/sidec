package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ePropositoList_ensayo")
public class EPropositoList_ensayo extends EntityQuery<EProposito_ensayo> {

	private static final String EJBQL = "select eProposito from EProposito_ensayo eProposito";

	private static final String[] RESTRICTIONS = {
			"lower(eProposito.nombre) like concat(lower(#{ePropositoList_ensayo.eProposito.nombre}),'%')",
			"lower(eProposito.descripcion) like concat(lower(#{ePropositoList_ensayo.eProposito.descripcion}),'%')", };

	private EProposito_ensayo eProposito = new EProposito_ensayo();

	public EPropositoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EProposito_ensayo getEProposito() {
		return eProposito;
	}
}
