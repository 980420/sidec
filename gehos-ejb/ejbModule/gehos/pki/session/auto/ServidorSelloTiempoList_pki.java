package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("servidorSelloTiempoList_pki")
public class ServidorSelloTiempoList_pki extends
		EntityQuery<ServidorSelloTiempo_pki> {

	private static final String EJBQL = "select servidorSelloTiempo from ServidorSelloTiempo_pki servidorSelloTiempo";

	private static final String[] RESTRICTIONS = {
			"lower(servidorSelloTiempo.descripcion) like concat(lower(#{servidorSelloTiempoList_pki.servidorSelloTiempo.descripcion}),'%')",
			"lower(servidorSelloTiempo.url) like concat(lower(#{servidorSelloTiempoList_pki.servidorSelloTiempo.url}),'%')", };

	private ServidorSelloTiempo_pki servidorSelloTiempo = new ServidorSelloTiempo_pki();

	public ServidorSelloTiempoList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ServidorSelloTiempo_pki getServidorSelloTiempo() {
		return servidorSelloTiempo;
	}
}
