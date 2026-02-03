package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoMomentoSeguimientoGeneralList_ensayo")
public class EstadoMomentoSeguimientoGeneralList_ensayo extends
		EntityQuery<EstadoMomentoSeguimientoGeneral_ensayo> {

	private static final String EJBQL = "select estadoMomentoSeguimientoGeneral from EstadoMomentoSeguimientoGeneral_ensayo estadoMomentoSeguimientoGeneral";

	private static final String[] RESTRICTIONS = {
			"lower(estadoMomentoSeguimientoGeneral.nombre) like concat(lower(#{estadoMomentoSeguimientoGeneralList_ensayo.estadoMomentoSeguimientoGeneral.nombre}),'%')",
			"lower(estadoMomentoSeguimientoGeneral.descripcion) like concat(lower(#{estadoMomentoSeguimientoGeneralList_ensayo.estadoMomentoSeguimientoGeneral.descripcion}),'%')", };

	private EstadoMomentoSeguimientoGeneral_ensayo estadoMomentoSeguimientoGeneral = new EstadoMomentoSeguimientoGeneral_ensayo();

	public EstadoMomentoSeguimientoGeneralList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoMomentoSeguimientoGeneral_ensayo getEstadoMomentoSeguimientoGeneral() {
		return estadoMomentoSeguimientoGeneral;
	}
}
