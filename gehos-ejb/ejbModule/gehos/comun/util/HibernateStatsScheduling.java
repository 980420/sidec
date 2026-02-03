package gehos.comun.util;

import java.io.IOException;

import org.hibernate.stat.Statistics;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

/**
 * @author yurien
 * 
 */

@Name("hibernateScheduling")
@Scope(ScopeType.APPLICATION)
public class HibernateStatsScheduling {

	@In(required = false, create = true)
	private Statistics stats;

	QuartzTriggerHandle quartzTestTriggerHandle;

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle createQuartzTestTimer(
			@IntervalCron String interval) {

		if(stats != null)
		{
			try {
				GenerateStatisticsLog.generateLog(stats);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
