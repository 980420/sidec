package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("servicioSubmoduloList_configuracion")
public class ServicioSubmoduloList_configuracion extends
		EntityQuery<ServicioSubmodulo_configuracion> {

	private static final String EJBQL = "select servicioSubmodulo from ServicioSubmodulo servicioSubmodulo";

	private static final String[] RESTRICTIONS = {};

	private ServicioSubmodulo_configuracion servicioSubmodulo = new ServicioSubmodulo_configuracion();

	public ServicioSubmoduloList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ServicioSubmodulo_configuracion getServicioSubmodulo() {
		return servicioSubmodulo;
	}
}
