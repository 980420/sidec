package gehos.notificaciones.session.auto;

import gehos.notificaciones.entity.Notificacion;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("notificacionList")
public class NotificacionList extends EntityQuery<Notificacion> {

	private static final String EJBQL = "select notificacion from Notificacion notificacion";

	private static final String[] RESTRICTIONS = {};

	private Notificacion notificacion = new Notificacion();

	public NotificacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Notificacion getNotificacion() {
		return notificacion;
	}
}
