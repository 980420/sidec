package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoMomentoSeguimientoList_ensayo")
public class EstadoMomentoSeguimientoList_ensayo extends
		EntityQuery<EstadoMomentoSeguimiento_ensayo> {

	private static final String EJBQL = "select estadoMomentoSeguimiento from EstadoMomentoSeguimiento_ensayo estadoMomentoSeguimiento";

	private static final String[] RESTRICTIONS = {
			"lower(estadoMomentoSeguimiento.nombre) like concat(lower(#{estadoMomentoSeguimientoList_ensayo.estadoMomentoSeguimiento.nombre}),'%')",
			"lower(estadoMomentoSeguimiento.descripcion) like concat(lower(#{estadoMomentoSeguimientoList_ensayo.estadoMomentoSeguimiento.descripcion}),'%')", };

	private EstadoMomentoSeguimiento_ensayo estadoMomentoSeguimiento = new EstadoMomentoSeguimiento_ensayo();

	public EstadoMomentoSeguimientoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoMomentoSeguimiento_ensayo getEstadoMomentoSeguimiento() {
		return estadoMomentoSeguimiento;
	}
}
