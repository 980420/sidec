package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoHojaCrdList_ensayo")
public class EstadoHojaCrdList_ensayo extends EntityQuery<EstadoHojaCrd_ensayo> {

	private static final String EJBQL = "select estadoHojaCrd from EstadoHojaCrd_ensayo estadoHojaCrd";

	private static final String[] RESTRICTIONS = {
			"lower(estadoHojaCrd.nombre) like concat(lower(#{estadoHojaCrdList_ensayo.estadoHojaCrd.nombre}),'%')",
			"lower(estadoHojaCrd.descripcion) like concat(lower(#{estadoHojaCrdList_ensayo.estadoHojaCrd.descripcion}),'%')", };

	private EstadoHojaCrd_ensayo estadoHojaCrd = new EstadoHojaCrd_ensayo();

	public EstadoHojaCrdList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoHojaCrd_ensayo getEstadoHojaCrd() {
		return estadoHojaCrd;
	}
}
