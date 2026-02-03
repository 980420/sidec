package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoVariableList_ensayo")
public class EstadoVariableList_ensayo extends
		EntityQuery<EstadoVariable_ensayo> {

	private static final String EJBQL = "select estadoVariable from EstadoVariable_ensayo estadoVariable";

	private static final String[] RESTRICTIONS = {
			"lower(estadoVariable.nombre) like concat(lower(#{estadoVariableList_ensayo.estadoVariable.nombre}),'%')",
			"lower(estadoVariable.descripcion) like concat(lower(#{estadoVariableList_ensayo.estadoVariable.descripcion}),'%')", };

	private EstadoVariable_ensayo estadoVariable = new EstadoVariable_ensayo();

	public EstadoVariableList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoVariable_ensayo getEstadoVariable() {
		return estadoVariable;
	}
}
