package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("logTipoAuditoriaList_ensayo")
public class LogTipoAuditoriaList_ensayo extends
		EntityQuery<LogTipoAuditoria_ensayo> {

	private static final String EJBQL = "select logTipoAuditoria from LogTipoAuditoria_ensayo logTipoAuditoria";

	private static final String[] RESTRICTIONS = { "lower(logTipoAuditoria.nombre) like concat(lower(#{logTipoAuditoriaList_ensayo.logTipoAuditoria.nombre}),'%')", };

	private LogTipoAuditoria_ensayo logTipoAuditoria = new LogTipoAuditoria_ensayo();

	public LogTipoAuditoriaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public LogTipoAuditoria_ensayo getLogTipoAuditoria() {
		return logTipoAuditoria;
	}
}
