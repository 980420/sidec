package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoVariableDatoList_ensayo")
public class EstadoVariableDatoList_ensayo extends
		EntityQuery<EstadoVariableDato_ensayo> {

	private static final String EJBQL = "select estadoVariableDato from EstadoVariableDato_ensayo estadoVariableDato";

	private static final String[] RESTRICTIONS = {
			"lower(estadoVariableDato.nombre) like concat(lower(#{estadoVariableDatoList_ensayo.estadoVariableDato.nombre}),'%')",
			"lower(estadoVariableDato.descripcion) like concat(lower(#{estadoVariableDatoList_ensayo.estadoVariableDato.descripcion}),'%')", };

	private EstadoVariableDato_ensayo estadoVariableDato = new EstadoVariableDato_ensayo();

	public EstadoVariableDatoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoVariableDato_ensayo getEstadoVariableDato() {
		return estadoVariableDato;
	}
}
