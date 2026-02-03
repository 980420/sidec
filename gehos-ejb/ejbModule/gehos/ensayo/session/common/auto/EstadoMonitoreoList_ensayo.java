package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoMonitoreoList_ensayo")
public class EstadoMonitoreoList_ensayo extends
		EntityQuery<EstadoMonitoreo_ensayo> {

	private static final String EJBQL = "select estadoMonitoreo from EstadoMonitoreo_ensayo estadoMonitoreo";

	private static final String[] RESTRICTIONS = {
			"lower(estadoMonitoreo.nombre) like concat(lower(#{estadoMonitoreoList_ensayo.estadoMonitoreo.nombre}),'%')",
			"lower(estadoMonitoreo.descripcion) like concat(lower(#{estadoMonitoreoList_ensayo.estadoMonitoreo.descripcion}),'%')", };

	private EstadoMonitoreo_ensayo estadoMonitoreo = new EstadoMonitoreo_ensayo();

	public EstadoMonitoreoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoMonitoreo_ensayo getEstadoMonitoreo() {
		return estadoMonitoreo;
	}
}
