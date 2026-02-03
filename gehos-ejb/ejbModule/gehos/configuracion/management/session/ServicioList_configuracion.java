package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("servicioList_configuracion")
public class ServicioList_configuracion extends
		EntityQuery<Servicio_configuracion> {

	private static final String EJBQL = "select servicio from Servicio servicio";

	private static final String[] RESTRICTIONS = {
			"lower(servicio.nombre) like concat(lower(#{servicioList.servicio.nombre}),'%')",
			"lower(servicio.codigo) like concat(lower(#{servicioList.servicio.codigo}),'%')", };

	private Servicio_configuracion servicio = new Servicio_configuracion();

	public ServicioList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Servicio_configuracion getServicio() {
		return servicio;
	}
}
