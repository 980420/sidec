package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eIntervencionList_ensayo")
public class EIntervencionList_ensayo extends EntityQuery<EIntervencion_ensayo> {

	private static final String EJBQL = "select eIntervencion from EIntervencion_ensayo eIntervencion";

	private static final String[] RESTRICTIONS = {
			"lower(eIntervencion.nombre) like concat(lower(#{eIntervencionList_ensayo.eIntervencion.nombre}),'%')",
			"lower(eIntervencion.descripcion) like concat(lower(#{eIntervencionList_ensayo.eIntervencion.descripcion}),'%')", };

	private EIntervencion_ensayo eIntervencion = new EIntervencion_ensayo();

	public EIntervencionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EIntervencion_ensayo getEIntervencion() {
		return eIntervencion;
	}
}
