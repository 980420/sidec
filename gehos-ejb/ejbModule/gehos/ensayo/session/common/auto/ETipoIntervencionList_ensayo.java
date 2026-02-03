package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eTipoIntervencionList_ensayo")
public class ETipoIntervencionList_ensayo extends
		EntityQuery<ETipoIntervencion_ensayo> {

	private static final String EJBQL = "select eTipoIntervencion from ETipoIntervencion_ensayo eTipoIntervencion";

	private static final String[] RESTRICTIONS = {
			"lower(eTipoIntervencion.nombre) like concat(lower(#{eTipoIntervencionList_ensayo.eTipoIntervencion.nombre}),'%')",
			"lower(eTipoIntervencion.descripcion) like concat(lower(#{eTipoIntervencionList_ensayo.eTipoIntervencion.descripcion}),'%')", };

	private ETipoIntervencion_ensayo eTipoIntervencion = new ETipoIntervencion_ensayo();

	public ETipoIntervencionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ETipoIntervencion_ensayo getETipoIntervencion() {
		return eTipoIntervencion;
	}
}
