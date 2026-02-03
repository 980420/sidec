package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eAsignacionList_ensayo")
public class EAsignacionList_ensayo extends EntityQuery<EAsignacion_ensayo> {

	private static final String EJBQL = "select eAsignacion from EAsignacion_ensayo eAsignacion";

	private static final String[] RESTRICTIONS = {
			"lower(eAsignacion.nombre) like concat(lower(#{eAsignacionList_ensayo.eAsignacion.nombre}),'%')",
			"lower(eAsignacion.descripcion) like concat(lower(#{eAsignacionList_ensayo.eAsignacion.descripcion}),'%')", };

	private EAsignacion_ensayo eAsignacion = new EAsignacion_ensayo();

	public EAsignacionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EAsignacion_ensayo getEAsignacion() {
		return eAsignacion;
	}
}
