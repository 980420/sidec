package gehos.notificaciones.session.auto;

import gehos.notificaciones.entity.TipoNotificacion;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("tipoNotificacionList")
public class TipoNotificacionList extends EntityQuery<TipoNotificacion> {

	private static final String EJBQL = "select tipoNotificacion from TipoNotificacion tipoNotificacion";

	private static final String[] RESTRICTIONS = {
			"lower(tipoNotificacion.descripcion) like concat(lower(#{tipoNotificacionList.tipoNotificacion.descripcion}),'%')",
			"lower(tipoNotificacion.codigo) like concat(lower(#{tipoNotificacionList.tipoNotificacion.codigo}),'%')",
			"lower(tipoNotificacion.url) like concat(lower(#{tipoNotificacionList.tipoNotificacion.url}),'%')", };

	private TipoNotificacion tipoNotificacion = new TipoNotificacion();

	public TipoNotificacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoNotificacion getTipoNotificacion() {
		return tipoNotificacion;
	}
}
