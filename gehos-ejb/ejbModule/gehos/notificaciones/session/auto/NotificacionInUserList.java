package gehos.notificaciones.session.auto;

import gehos.notificaciones.entity.NotificacionInUser;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("notificacionInUserList")
public class NotificacionInUserList extends EntityQuery<NotificacionInUser> {

	private static final String EJBQL = "select notificacionInUser from NotificacionInUser notificacionInUser";

	private static final String[] RESTRICTIONS = {};

	private NotificacionInUser notificacionInUser = new NotificacionInUser();

	public NotificacionInUserList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public NotificacionInUser getNotificacionInUser() {
		return notificacionInUser;
	}
}
