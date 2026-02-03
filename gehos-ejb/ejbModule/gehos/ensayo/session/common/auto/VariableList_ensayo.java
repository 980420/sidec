package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("variableList_ensayo")
public class VariableList_ensayo extends EntityQuery<Variable_ensayo> {

	private static final String EJBQL = "select variable from Variable_ensayo variable";

	private static final String[] RESTRICTIONS = {
			"lower(variable.nombreVariable) like concat(lower(#{variableList_ensayo.variable.nombreVariable}),'%')",
			"lower(variable.descripcionVariable) like concat(lower(#{variableList_ensayo.variable.descripcionVariable}),'%')",
			"lower(variable.textoIzquierdaVariable) like concat(lower(#{variableList_ensayo.variable.textoIzquierdaVariable}),'%')",
			"lower(variable.unidadesVariable) like concat(lower(#{variableList_ensayo.variable.unidadesVariable}),'%')",
			"lower(variable.textoDerechaVariable) like concat(lower(#{variableList_ensayo.variable.textoDerechaVariable}),'%')",
			"lower(variable.encabezadoVariable) like concat(lower(#{variableList_ensayo.variable.encabezadoVariable}),'%')",
			"lower(variable.subencabezadoVariable) like concat(lower(#{variableList_ensayo.variable.subencabezadoVariable}),'%')",
			"lower(variable.ubicacionRespuesta) like concat(lower(#{variableList_ensayo.variable.ubicacionRespuesta}),'%')",
			"lower(variable.visibilidadVariable) like concat(lower(#{variableList_ensayo.variable.visibilidadVariable}),'%')",
			"lower(variable.condicionVisibilidadVariable) like concat(lower(#{variableList_ensayo.variable.condicionVisibilidadVariable}),'%')", };

	private Variable_ensayo variable = new Variable_ensayo();

	public VariableList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Variable_ensayo getVariable() {
		return variable;
	}
}
