package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eAjusteTemporalList_ensayo")
public class EAjusteTemporalList_ensayo extends
		EntityQuery<EAjusteTemporal_ensayo> {

	private static final String EJBQL = "select eAjusteTemporal from EAjusteTemporal_ensayo eAjusteTemporal";

	private static final String[] RESTRICTIONS = {
			"lower(eAjusteTemporal.nombre) like concat(lower(#{eAjusteTemporalList_ensayo.eAjusteTemporal.nombre}),'%')",
			"lower(eAjusteTemporal.descripcion) like concat(lower(#{eAjusteTemporalList_ensayo.eAjusteTemporal.descripcion}),'%')", };

	private EAjusteTemporal_ensayo eAjusteTemporal = new EAjusteTemporal_ensayo();

	public EAjusteTemporalList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EAjusteTemporal_ensayo getEAjusteTemporal() {
		return eAjusteTemporal;
	}
}
