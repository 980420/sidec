package gehos.notificaciones;

import java.io.Serializable;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.Log;

@AutoCreate
@Name("scheduleController")
public class ScheduleController implements Serializable {

	private static final Long serialVersionUID = -6332836501640042340L;

	@In
	NotificationDataLoader notificationDataLoader;

	@Logger	Log log;

	@SuppressWarnings("unused")
	private String text = "test";

	@SuppressWarnings("unused")
	private QuartzTriggerHandle quartzTriggerHandle;

	public void scheduleTimer() {
		@SuppressWarnings("unused")
		Long pollingInterval = 5000L;

/*		quartzTriggerHandle = notificationDataLoader.createQuartzTimer(new Date(),
					pollingInterval, text);*/

	}

}