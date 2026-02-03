package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoUbicacionList_configuracion")
public class TipoUbicacionList_configuracion extends
		EntityQuery<TipoUbicacion_configuracion> {

	private static final String EJBQL = "select tipoUbicacion from TipoUbicacion tipoUbicacion";

	private static final String[] RESTRICTIONS = {
			"lower(tipoUbicacion.codigo) like concat(lower(#{tipoUbicacionList.tipoUbicacion.codigo}),'%')",
			"lower(tipoUbicacion.descripcion) like concat(lower(#{tipoUbicacionList.tipoUbicacion.descripcion}),'%')",
			"lower(tipoUbicacion.icono) like concat(lower(#{tipoUbicacionList.tipoUbicacion.icono}),'%')", };

	private TipoUbicacion_configuracion tipoUbicacion = new TipoUbicacion_configuracion();

	public TipoUbicacionList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoUbicacion_configuracion getTipoUbicacion() {
		return tipoUbicacion;
	}
}
