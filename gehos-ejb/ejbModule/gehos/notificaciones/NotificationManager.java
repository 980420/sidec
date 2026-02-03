package gehos.notificaciones;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.shell.IActiveModule;
import gehos.notificaciones.entity.Notificacion;
import gehos.notificaciones.entity.NotificacionInUser;
import gehos.notificaciones.entity.TipoNotificacion;
import gehos.notificaciones.entity.Usuario_notifications;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@AutoCreate
@Name("notificationManager")
public class NotificationManager {

	@In	EntityManager entityManager;
	@In	IActiveModule activeModule;
	@In Usuario user;
	@In NotificationDataLoader notificationDataLoader;
	
	private Long notificationId;
	public Long getNotificationId() {return notificationId;}
	public void setNotificationId(Long notificationId) {	this.notificationId = notificationId;}

	public void markNotificationAsReaded(){
		NotificacionInUser notificacionInUser = entityManager.find(NotificacionInUser.class, notificationId);
		if(notificacionInUser.getLeida())
			notificacionInUser.setLeida(false);
		else
			notificacionInUser.setLeida(true);
		entityManager.persist(notificacionInUser);
	}

	public void markNotificationAsReaded(Long notificationId){
		NotificacionInUser notificacionInUser = entityManager.find(NotificacionInUser.class, notificationId);
		if(notificacionInUser.getLeida())
			notificacionInUser.setLeida(false);
		else
			notificacionInUser.setLeida(true);
		entityManager.persist(notificacionInUser);
	}

	public Long unreadedNotificationsCountByUserAndModule(){
		/*if (activeModule.getActiveModule() != null) {
			return (Long) entityManager
					.createQuery(
							"select count(*) from NotificacionInUser notinuser "
									+ "where notinuser.notificacion.tipoNotificacion.idModulo = :idmodule "
									+ "and notinuser.usuario.id = :idusuario "
									+ "and notinuser.leida = false")
					.setParameter("idmodule",
							activeModule.getActiveModule().getFuncionalidadPadre().getId())
					.setParameter("idusuario", user.getId()).getSingleResult();
		}*/
		return 0L;
	}

	@SuppressWarnings("unchecked")
	public List<NotificacionInUser> notificationsByUserAndType(String notificationCode){
		return entityManager.createQuery("from NotificacionInUser notif " +
				"where notif.notificacion.tipoNotificacion.codigo = :notificationcode " +
				"and notif.usuario.id = :userid  " +
				"and (notif.eliminado = false or notif.eliminado is null)" +
				"order by notif.notificacion.fecha desc, notif.notificacion.hora desc ")
				.setParameter("notificationcode", notificationCode)
				.setParameter("userid", this.user.getId())
				.getResultList();
	}
	
	public void eliminar(long id){
		NotificacionInUser notif = entityManager.find(NotificacionInUser.class, id);
		notif.setEliminado(true);
		notif.setLeida(true);
		entityManager.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoNotificacion> notificationsByModule() {
		return entityManager.createQuery(
				"from TipoNotificacion notif where notif.idModulo = :modid")
				.setParameter("modid", activeModule.getActiveModule().getFuncionalidadPadre().getId())
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> notificationsByModule(String modulename) {
		Funcionalidad modulo = (Funcionalidad) entityManager.createQuery(
				"from Funcionalidad f " + "where f.nombre = :nombre")
				.setParameter("nombre", modulename).getSingleResult();
		return entityManager
				.createQuery(
						"select notif.codigo from TipoNotificacion notif where notif.idModulo = :modid")
				.setParameter("modid", modulo.getId()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public void generateNotification(String code, List<String> users, List<String> roles, Boolean important, Long param) {
		Notificacion notificacion = new Notificacion();
		notificacion.setFecha(new Date());
		notificacion.setHora(new Date());
		Usuario_notifications creador = entityManager.find(Usuario_notifications.class, user.getId()); 
		notificacion.setUsuario(creador);
		
		try {
			TipoNotificacion tipo = (TipoNotificacion) entityManager
					.createQuery(
							"from TipoNotificacion notif "
									+ "where notif.codigo = :code")
					.setParameter("code", code).getSingleResult();
			notificacion.setTipoNotificacion(tipo);
			notificacion.setParamValue(param);
			notificacion.setImportante(important);
			entityManager.persist(notificacion);
			for (String username : users) {
				Usuario_notifications usuario = (Usuario_notifications) entityManager
						.createQuery(
								"from Usuario_notifications usuario "
										+ "where usuario.username = :username")
						.setParameter("username", username).getSingleResult();
				NotificacionInUser notificacionInUser = new NotificacionInUser();
				notificacionInUser.setUsuario(usuario);
				notificacionInUser.setLeida(false);
				notificacionInUser.setNotificada(false);
				notificacionInUser.setNotificacion(notificacion);
				notificacion.getNotificacionInUsers().add(notificacionInUser);
				entityManager.persist(notificacionInUser);
				notificationDataLoader.putNotificationForUser(usuario
						.getUsername(), notificacionInUser);
			}
			for (String rol : roles) {
				List<String> usersinrole = entityManager
						.createQuery(
								"select u.username from Usuario u inner join u.roles rol where rol.name = :role")
						.setParameter("role", rol).getResultList();
				for (String userinrole : usersinrole) {
					Usuario_notifications usuario = (Usuario_notifications) entityManager
							.createQuery(
									"from Usuario_notifications usuario "
											+ "where usuario.username = :username")
							.setParameter("username", userinrole)
							.getSingleResult();
					NotificacionInUser notificacionInUser = new NotificacionInUser();
					notificacionInUser.setUsuario(usuario);
					notificacionInUser.setLeida(false);
					notificacionInUser.setNotificada(false);
					notificacionInUser.setNotificacion(notificacion);
					notificacion.getNotificacionInUsers().add(
							notificacionInUser);
					entityManager.persist(notificacionInUser);
					notificationDataLoader.putNotificationForUser(usuario
							.getUsername(), notificacionInUser);
				}
			}
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
