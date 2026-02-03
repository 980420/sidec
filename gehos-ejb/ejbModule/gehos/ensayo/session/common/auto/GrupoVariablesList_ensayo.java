package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("grupoVariablesList_ensayo")
public class GrupoVariablesList_ensayo extends
		EntityQuery<GrupoVariables_ensayo> {

	private static final String EJBQL = "select grupoVariables from GrupoVariables_ensayo grupoVariables";

	private static final String[] RESTRICTIONS = {
			"lower(grupoVariables.etiquetaGrupo) like concat(lower(#{grupoVariablesList_ensayo.grupoVariables.etiquetaGrupo}),'%')",
			"lower(grupoVariables.descripcionGrupo) like concat(lower(#{grupoVariablesList_ensayo.grupoVariables.descripcionGrupo}),'%')",
			"lower(grupoVariables.encabezado) like concat(lower(#{grupoVariablesList_ensayo.grupoVariables.encabezado}),'%')",
			"lower(grupoVariables.estadoGrupo) like concat(lower(#{grupoVariablesList_ensayo.grupoVariables.estadoGrupo}),'%')", };

	private GrupoVariables_ensayo grupoVariables = new GrupoVariables_ensayo();

	public GrupoVariablesList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public GrupoVariables_ensayo getGrupoVariables() {
		return grupoVariables;
	}
}
