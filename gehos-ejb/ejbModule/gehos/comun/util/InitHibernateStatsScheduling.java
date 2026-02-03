package gehos.comun.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.async.QuartzTriggerHandle;

@Name("initHibernateStatsScheduling")
@Scope(ScopeType.APPLICATION)
@Startup
public class InitHibernateStatsScheduling {

	 @In(required = false,create = true)
	 HibernateStatsScheduling hibernateScheduling;
	
	 QuartzTriggerHandle quartzTestTriggerHandle;
	 
	 private static final String CRON_INTERVAL = "0 0 0/1 * * ?";
	
	 @Create
	 public void scheduleTimer() {
		 
		 try {
			 quartzTestTriggerHandle =
					 hibernateScheduling.createQuartzTestTimer(CRON_INTERVAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
}

