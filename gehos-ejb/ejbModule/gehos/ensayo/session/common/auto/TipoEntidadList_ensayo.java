package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoEntidadList_ensayo")
public class TipoEntidadList_ensayo extends EntityQuery<TipoEntidad_ensayo> {

	private static final String EJBQL = "select tipoEntidad from TipoEntidad_ensayo tipoEntidad";

	private static final String[] RESTRICTIONS = { "lower(tipoEntidad.valor) like concat(lower(#{tipoEntidadList_ensayo.tipoEntidad.valor}),'%')", };

	private TipoEntidad_ensayo tipoEntidad = new TipoEntidad_ensayo();

	public TipoEntidadList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoEntidad_ensayo getTipoEntidad() {
		return tipoEntidad;
	}
}
