package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reTrazabilidadreporteList_ensayo")
public class ReTrazabilidadreporteList_ensayo extends
		EntityQuery<ReTrazabilidadreporte_ensayo> {

	private static final String EJBQL = "select reTrazabilidadreporte from ReTrazabilidadreporte_ensayo reTrazabilidadreporte";

	private static final String[] RESTRICTIONS = {
			"lower(reTrazabilidadreporte.nombreapellido) like concat(lower(#{reTrazabilidadreporteList_ensayo.reTrazabilidadreporte.nombreapellido}),'%')",
			"lower(reTrazabilidadreporte.responsabilidad) like concat(lower(#{reTrazabilidadreporteList_ensayo.reTrazabilidadreporte.responsabilidad}),'%')", };

	private ReTrazabilidadreporte_ensayo reTrazabilidadreporte = new ReTrazabilidadreporte_ensayo();

	public ReTrazabilidadreporteList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReTrazabilidadreporte_ensayo getReTrazabilidadreporte() {
		return reTrazabilidadreporte;
	}
}
