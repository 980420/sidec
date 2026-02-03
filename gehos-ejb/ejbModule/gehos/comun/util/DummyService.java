package gehos.comun.util;

import gehos.comun.util.url.ArrayURL;

import java.io.InputStream;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;

import org.jboss.deployment.DeploymentInfo;
import org.jboss.deployment.MainDeployerMBean;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

@Name("dummy")
@Scope(ScopeType.APPLICATION)
@Install(classDependencies = { "org.jboss.deployment.MainDeployerMBean" })
@Startup
public class DummyService {

	private MainDeployerMBean mbean;

	@Logger
	private Log log;
	private DeploymentInfo di;

	@Create
	public void seamCreate() throws Exception {
		log.info("seamCreate");
		MBeanServer jboss = MBeanServerLocator.locateJBoss();
		mbean = (MainDeployerMBean) MBeanServerInvocationHandler
				.newProxyInstance(jboss, MainDeployerMBean.OBJECT_NAME,
						MainDeployerMBean.class, false);
		String name = "dummy.xml";
		InputStream is = getClass().getResourceAsStream("/" + name);
		di = new DeploymentInfo(ArrayURL.create("jboss-service.xml", is), null,
				jboss);
		mbean.deploy(di);
	}

	@Destroy
	public void seamDestroy() throws Exception {
		log.info("seamDestroy");
		mbean.undeploy(di);
	}

}
