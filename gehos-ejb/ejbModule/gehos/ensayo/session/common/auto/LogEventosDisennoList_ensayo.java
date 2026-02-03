package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("logEventosDisennoList_ensayo")
public class LogEventosDisennoList_ensayo extends
		EntityQuery<LogEventosDisenno_ensayo> {

	private static final String EJBQL = "select logEventosDisenno from LogEventosDisenno_ensayo logEventosDisenno";

	private static final String[] RESTRICTIONS = {
			"lower(logEventosDisenno.tablaAuditada) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.tablaAuditada}),'%')",
			"lower(logEventosDisenno.nombreAplicacion) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.nombreAplicacion}),'%')",
			"lower(logEventosDisenno.atributoModificado) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.atributoModificado}),'%')",
			"lower(logEventosDisenno.oldValue) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.oldValue}),'%')",
			"lower(logEventosDisenno.newValue) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.newValue}),'%')",
			"lower(logEventosDisenno.usuarioNombre) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.usuarioNombre}),'%')",
			"lower(logEventosDisenno.estudioNombre) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.estudioNombre}),'%')",
			"lower(logEventosDisenno.gsNombre) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.gsNombre}),'%')",
			"lower(logEventosDisenno.cronograma) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.cronograma}),'%')",
			"lower(logEventosDisenno.etapa) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.etapa}),'%')",
			"lower(logEventosDisenno.msgNombre) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.msgNombre}),'%')",
			"lower(logEventosDisenno.hojaCrd) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.hojaCrd}),'%')",
			"lower(logEventosDisenno.seccion) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.seccion}),'%')",
			"lower(logEventosDisenno.variableNombre) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.variableNombre}),'%')",
			"lower(logEventosDisenno.regla) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.regla}),'%')",
			"lower(logEventosDisenno.hora) like concat(lower(#{logEventosDisennoList_ensayo.logEventosDisenno.hora}),'%')", };

	private LogEventosDisenno_ensayo logEventosDisenno = new LogEventosDisenno_ensayo();

	public LogEventosDisennoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public LogEventosDisenno_ensayo getLogEventosDisenno() {
		return logEventosDisenno;
	}
}
