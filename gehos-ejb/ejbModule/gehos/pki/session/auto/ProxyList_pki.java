package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("proxyList_pki")
public class ProxyList_pki extends EntityQuery<Proxy_pki> {

	private static final String EJBQL = "select proxy from Proxy_pki proxy";

	private static final String[] RESTRICTIONS = {
			"lower(proxy.descripcion) like concat(lower(#{proxyList_pki.proxy.descripcion}),'%')",
			"lower(proxy.puerto) like concat(lower(#{proxyList_pki.proxy.puerto}),'%')",
			"lower(proxy.direccion) like concat(lower(#{proxyList_pki.proxy.direccion}),'%')", };

	private Proxy_pki proxy = new Proxy_pki();

	public ProxyList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Proxy_pki getProxy() {
		return proxy;
	}
}
