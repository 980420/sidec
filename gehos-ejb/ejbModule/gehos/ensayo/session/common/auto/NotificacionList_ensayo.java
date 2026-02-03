package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("notificacionList_ensayo")
public class NotificacionList_ensayo extends EntityQuery<Notificacion_ensayo> {

	private static final String EJBQL = "select notificacion from Notificacion_ensayo notificacion";

	private static final String[] RESTRICTIONS = {
			"lower(notificacion.idSujeto) like concat(lower(#{notificacionList_ensayo.notificacion.idSujeto}),'%')",
			"lower(notificacion.descrEven) like concat(lower(#{notificacionList_ensayo.notificacion.descrEven}),'%')", };

	private Notificacion_ensayo notificacion = new Notificacion_ensayo();

	public NotificacionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Notificacion_ensayo getNotificacion() {
		return notificacion;
	}
}
