package gehos.comun.util;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.persistence.EntityManager;

import org.hibernate.jmx.StatisticsService;
import org.hibernate.stat.Statistics;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.persistence.HibernateSessionProxy;

@Name("hibernateStatistics")
@Scope(ScopeType.APPLICATION)
@Startup
public class HibernateUtils {
	@In
	private EntityManager entityManager;
	
	@Out(required = false)
	Statistics stats;

	@Create
	public void onStartup() {
		if (entityManager != null) {
			try {
				

				stats = ((HibernateSessionProxy) entityManager.getDelegate())
						.getSessionFactory().getStatistics();
				MBeanServer server = (MBeanServer) MBeanServerFactory.findMBeanServer(null)
						.get(0);
				StatisticsService mBean = new StatisticsService();
				
				ObjectName objectName = new ObjectName("Hibernate:application=gehos,type=statistics");
				server.registerMBean(mBean, objectName);
				mBean.setSessionFactory(((HibernateSessionProxy) entityManager
						.getDelegate()).getSessionFactory());
				
				
			} catch (Exception e) {
				throw new RuntimeException("The persistence context "
						+ entityManager.toString() + "is not properly configured.", e);
			}
		}
	}
	
}