package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("logEventosAuditoriaList_ensayo")
public class LogEventosAuditoriaList_ensayo extends
		EntityQuery<LogEventosAuditoria_ensayo> {

	private static final String EJBQL = "select logEventosAuditoria from LogEventosAuditoria_ensayo logEventosAuditoria";

	private static final String[] RESTRICTIONS = {
			"lower(logEventosAuditoria.tablaAuditada) like concat(lower(#{logEventosAuditoriaList_ensayo.logEventosAuditoria.tablaAuditada}),'%')",
			"lower(logEventosAuditoria.nombreAplicacion) like concat(lower(#{logEventosAuditoriaList_ensayo.logEventosAuditoria.nombreAplicacion}),'%')",
			"lower(logEventosAuditoria.atributoModificado) like concat(lower(#{logEventosAuditoriaList_ensayo.logEventosAuditoria.atributoModificado}),'%')",
			"lower(logEventosAuditoria.oldValue) like concat(lower(#{logEventosAuditoriaList_ensayo.logEventosAuditoria.oldValue}),'%')",
			"lower(logEventosAuditoria.newValue) like concat(lower(#{logEventosAuditoriaList_ensayo.logEventosAuditoria.newValue}),'%')",
			"lower(logEventosAuditoria.tablaEstado) like concat(lower(#{logEventosAuditoriaList_ensayo.logEventosAuditoria.tablaEstado}),'%')", };

	private LogEventosAuditoria_ensayo logEventosAuditoria = new LogEventosAuditoria_ensayo();

	public LogEventosAuditoriaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public LogEventosAuditoria_ensayo getLogEventosAuditoria() {
		return logEventosAuditoria;
	}
}
