package gehos.configuracion.samples.notifications;

import gehos.notificaciones.NotificationManager;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("notificationTest")
public class NotificationTest {

	@In
	NotificationManager notificationManager;

	private String selectedCode;
	private Boolean important;

	public String getSelectedCode() {
		return selectedCode;
	}

	public void setSelectedCode(String selectedCode) {
		this.selectedCode = selectedCode;
	}

	public void notificationToUser() {
		List<String> users = new ArrayList<String>();
		users.add("root");
		notificationManager.generateNotification(this.selectedCode, users,
				new ArrayList<String>(), important, 3L);
	}

	public Boolean getImportant() {
		return important;
	}

	public void setImportant(Boolean important) {
		this.important = important;
	}

}
