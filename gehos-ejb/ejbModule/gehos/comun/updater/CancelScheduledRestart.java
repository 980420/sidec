package gehos.comun.updater;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

@Startup
@Scope(ScopeType.APPLICATION)
@Name("cancelScheduledRestart")
public class CancelScheduledRestart {

	@Create
	public void cancelRestart() throws SchedulerException{
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		sched.start();
		sched.unscheduleJob("restartTrigger", sched.DEFAULT_GROUP);
	}
	
	
}
