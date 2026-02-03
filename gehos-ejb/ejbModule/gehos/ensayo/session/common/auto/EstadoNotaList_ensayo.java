package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoNotaList_ensayo")
public class EstadoNotaList_ensayo extends EntityQuery<EstadoNota_ensayo> {

	private static final String EJBQL = "select estadoNota from EstadoNota_ensayo estadoNota";

	private static final String[] RESTRICTIONS = {
			"lower(estadoNota.nombre) like concat(lower(#{estadoNotaList_ensayo.estadoNota.nombre}),'%')",
			"lower(estadoNota.descripcion) like concat(lower(#{estadoNotaList_ensayo.estadoNota.descripcion}),'%')", };

	private EstadoNota_ensayo estadoNota = new EstadoNota_ensayo();

	public EstadoNotaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoNota_ensayo getEstadoNota() {
		return estadoNota;
	}
}
