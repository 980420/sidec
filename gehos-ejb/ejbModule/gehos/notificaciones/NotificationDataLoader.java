package gehos.notificaciones;

import gehos.autenticacion.entity.Usuario;
import gehos.notificaciones.entity.NotificacionInUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name("notificationDataLoader")
@Scope(ScopeType.APPLICATION)
public class NotificationDataLoader {

	@In	EntityManager entityManager;
	@In(required=false) Usuario user;
	ConcurrentHashMap<String, List<NotificacionInUser>> notificaciones = new ConcurrentHashMap<String, List<NotificacionInUser>>();
	ConcurrentHashMap<String, Boolean> visible_notificaciones = new ConcurrentHashMap<String, Boolean>();

	public List<NotificacionInUser> notificationForUser(){
		if(this.notificaciones.containsKey(user.getUsername()) 
				&& this.notificaciones.get(user.getUsername()).size() > 0){
			NotificacionInUser[] copy = 
				new NotificacionInUser[this.notificaciones.get(user.getUsername()).size()];
			this.notificaciones.get(user.getUsername()).toArray(copy);
			this.notificaciones.get(user.getUsername()).clear();
			
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			aFacesContext.getExternalContext().getRequestMap().put("notificaciones", Arrays.asList(copy));
			
			return Arrays.asList(copy);
		}
		else{
			if(this.visible_notificaciones.containsKey(user.getUsername())){
				this.visible_notificaciones.replace(user.getUsername(), false);
			}
			return new ArrayList<NotificacionInUser>();
		}
	}
	public boolean visiblenotificationForUser(){
		if(this.visible_notificaciones.containsKey(user.getUsername()) 
				&& this.visible_notificaciones.get(user.getUsername()).booleanValue() == true){
			return this.visible_notificaciones.get(user.getUsername());
		}
		return false;
	}

	public void putNotificationForUser(String userName, NotificacionInUser notification){
		if (!this.notificaciones.containsKey(userName)) {
			this.notificaciones.put(userName, new ArrayList<NotificacionInUser>());
			this.notificaciones.get(userName).add(notification);
		}
		else{
			this.notificaciones.get(userName).add(0, notification);
		}
		if(!visible_notificaciones.containsKey(userName)){
			this.visible_notificaciones.put(userName, true);
		}
		else{
			this.visible_notificaciones.replace(userName, true);
		}			
	}
	
	@SuppressWarnings("unchecked")
	public void loadNewNotifications(Date when, Long interval, String text) {
		notificaciones = new ConcurrentHashMap<String, List<NotificacionInUser>>();
		List<NotificacionInUser> new_notifications = entityManager.createQuery("from NotificacionInUser notif " +
				"where notif.notificada = false order by notif.notificacion.hora desc").getResultList();
		if(new_notifications.size() > 0)
			entityManager.createQuery("update NotificacionInUser notif set notif.notificada = true  where notif.notificada = false ").executeUpdate();
		for (NotificacionInUser notificacionInUser : new_notifications) {
			if (!this.notificaciones.containsKey(notificacionInUser.getUsuario().getUsername())) {
				this.notificaciones.put(notificacionInUser.getUsuario().getUsername(), new ArrayList<NotificacionInUser>());
				this.notificaciones.get(notificacionInUser.getUsuario().getUsername()).add(notificacionInUser);
			}
			else{
				this.notificaciones.get(notificacionInUser.getUsuario().getUsername()).add(0, notificacionInUser);
			}
		}		
	}

	public ConcurrentHashMap<String, List<NotificacionInUser>> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(
			ConcurrentHashMap<String, List<NotificacionInUser>> notificaciones) {
		this.notificaciones = notificaciones;
	}

	public ConcurrentHashMap<String, Boolean> getVisible_notificaciones() {
		return visible_notificaciones;
	}

	public void setVisible_notificaciones(
			ConcurrentHashMap<String, Boolean> visible_notificaciones) {
		this.visible_notificaciones = visible_notificaciones;
	}

/*	@Asynchronous
	@Transactional
	public QuartzTriggerHandle createQuartzTimer(@Expiration
		    Date when, @IntervalDuration
		    Long interval, String text) {
		loadNewNotifications(when, interval, text);
		return null;
	}*/

}


