package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("logEventosConduccionList_ensayo")
public class LogEventosConduccionList_ensayo extends
		EntityQuery<LogEventosConduccion_ensayo> {

	private static final String EJBQL = "select logEventosConduccion from LogEventosConduccion_ensayo logEventosConduccion";

	private static final String[] RESTRICTIONS = {
			"lower(logEventosConduccion.tablaAuditada) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.tablaAuditada}),'%')",
			"lower(logEventosConduccion.nombreAplicacion) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.nombreAplicacion}),'%')",
			"lower(logEventosConduccion.atributoModificado) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.atributoModificado}),'%')",
			"lower(logEventosConduccion.oldValue) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.oldValue}),'%')",
			"lower(logEventosConduccion.newValue) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.newValue}),'%')",
			"lower(logEventosConduccion.usuarioNombre) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.usuarioNombre}),'%')",
			"lower(logEventosConduccion.estudioNombre) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.estudioNombre}),'%')",
			"lower(logEventosConduccion.gsNombre) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.gsNombre}),'%')",
			"lower(logEventosConduccion.sujeto) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.sujeto}),'%')",
			"lower(logEventosConduccion.mseNombre) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.mseNombre}),'%')",
			"lower(logEventosConduccion.hojaCrd) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.hojaCrd}),'%')",
			"lower(logEventosConduccion.seccion) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.seccion}),'%')",
			"lower(logEventosConduccion.variableNombre) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.variableNombre}),'%')",
			"lower(logEventosConduccion.variableDatoValor) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.variableDatoValor}),'%')",
			"lower(logEventosConduccion.hora) like concat(lower(#{logEventosConduccionList_ensayo.logEventosConduccion.hora}),'%')", };

	private LogEventosConduccion_ensayo logEventosConduccion = new LogEventosConduccion_ensayo();

	public LogEventosConduccionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public LogEventosConduccion_ensayo getLogEventosConduccion() {
		return logEventosConduccion;
	}
}
