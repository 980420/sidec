package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoReglaList_ensayo")
public class EstadoReglaList_ensayo extends EntityQuery<EstadoRegla_ensayo> {

	private static final String EJBQL = "select estadoRegla from EstadoRegla_ensayo estadoRegla";

	private static final String[] RESTRICTIONS = {
			"lower(estadoRegla.nombre) like concat(lower(#{estadoReglaList_ensayo.estadoRegla.nombre}),'%')",
			"lower(estadoRegla.descripcion) like concat(lower(#{estadoReglaList_ensayo.estadoRegla.descripcion}),'%')", };

	private EstadoRegla_ensayo estadoRegla = new EstadoRegla_ensayo();

	public EstadoReglaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoRegla_ensayo getEstadoRegla() {
		return estadoRegla;
	}
}
