package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("conjuntoDatosList_ensayo")
public class ConjuntoDatosList_ensayo extends EntityQuery<ConjuntoDatos_ensayo> {

	private static final String EJBQL = "select conjuntoDatos from ConjuntoDatos_ensayo conjuntoDatos";

	private static final String[] RESTRICTIONS = {
			"lower(conjuntoDatos.nombre) like concat(lower(#{conjuntoDatosList_ensayo.conjuntoDatos.nombre}),'%')",
			"lower(conjuntoDatos.descripcion) like concat(lower(#{conjuntoDatosList_ensayo.conjuntoDatos.descripcion}),'%')", };

	private ConjuntoDatos_ensayo conjuntoDatos = new ConjuntoDatos_ensayo();

	public ConjuntoDatosList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConjuntoDatos_ensayo getConjuntoDatos() {
		return conjuntoDatos;
	}

		
}
