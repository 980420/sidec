package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoTratamientoList_ensayo")
public class EstadoTratamientoList_ensayo extends
		EntityQuery<EstadoTratamiento_ensayo> {

	private static final String EJBQL = "select estadoTratamiento from EstadoTratamiento_ensayo estadoTratamiento";

	private static final String[] RESTRICTIONS = {
			"lower(estadoTratamiento.nombre) like concat(lower(#{estadoTratamientoList_ensayo.estadoTratamiento.nombre}),'%')",
			"lower(estadoTratamiento.descripcion) like concat(lower(#{estadoTratamientoList_ensayo.estadoTratamiento.descripcion}),'%')", };

	private EstadoTratamiento_ensayo estadoTratamiento = new EstadoTratamiento_ensayo();

	public EstadoTratamientoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoTratamiento_ensayo getEstadoTratamiento() {
		return estadoTratamiento;
	}
}
