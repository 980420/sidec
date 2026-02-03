package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("parametrosList_ensayo")
public class ParametrosList_ensayo extends EntityQuery<Parametros_ensayo> {

	private static final String EJBQL = "select parametros from Parametros_ensayo parametros";

	private static final String[] RESTRICTIONS = {
			"lower(parametros.nombre) like concat(lower(#{parametrosList_ensayo.parametros.nombre}),'%')",
			"lower(parametros.codigo) like concat(lower(#{parametrosList_ensayo.parametros.codigo}),'%')", };

	private Parametros_ensayo parametros = new Parametros_ensayo();

	public ParametrosList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Parametros_ensayo getParametros() {
		return parametros;
	}
}
