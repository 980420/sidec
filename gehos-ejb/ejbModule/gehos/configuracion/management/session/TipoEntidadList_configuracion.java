package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoEntidadList_configuracion")
public class TipoEntidadList_configuracion extends
		EntityQuery<TipoEntidad_configuracion> {

	private static final String EJBQL = "select tipoEntidad from TipoEntidad tipoEntidad";

	private static final String[] RESTRICTIONS = { "lower(tipoEntidad.valor) like concat(lower(#{tipoEntidadList.tipoEntidad.valor}),'%')", };

	private TipoEntidad_configuracion tipoEntidad = new TipoEntidad_configuracion();

	public TipoEntidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoEntidad_configuracion getTipoEntidad() {
		return tipoEntidad;
	}
}
