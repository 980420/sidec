package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("causaList_ensayo")
public class CausaList_ensayo extends EntityQuery<Causa_ensayo> {

	private static final String EJBQL = "select causa from Causa_ensayo causa";

	private static final String[] RESTRICTIONS = {
			"lower(causa.descripcion) like concat(lower(#{causaList_ensayo.causa.descripcion}),'%')",
			"lower(causa.tipoCausa) like concat(lower(#{causaList_ensayo.causa.tipoCausa}),'%')", };

	private Causa_ensayo causa = new Causa_ensayo();

	public CausaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Causa_ensayo getCausa() {
		return causa;
	}
}
