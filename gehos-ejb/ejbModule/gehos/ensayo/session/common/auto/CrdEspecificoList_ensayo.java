package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("crdEspecificoList_ensayo")
public class CrdEspecificoList_ensayo extends EntityQuery<CrdEspecifico_ensayo> {

	private static final String EJBQL = "select crdEspecifico from CrdEspecifico_ensayo crdEspecifico";

	private static final String[] RESTRICTIONS = {};

	private CrdEspecifico_ensayo crdEspecifico = new CrdEspecifico_ensayo();

	public CrdEspecificoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CrdEspecifico_ensayo getCrdEspecifico() {
		return crdEspecifico;
	}
}
