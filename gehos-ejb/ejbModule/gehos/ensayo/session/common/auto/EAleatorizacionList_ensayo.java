package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eAleatorizacionList_ensayo")
public class EAleatorizacionList_ensayo extends
		EntityQuery<EAleatorizacion_ensayo> {

	private static final String EJBQL = "select eAleatorizacion from EAleatorizacion_ensayo eAleatorizacion";

	private static final String[] RESTRICTIONS = {
			"lower(eAleatorizacion.nombre) like concat(lower(#{eAleatorizacionList_ensayo.eAleatorizacion.nombre}),'%')",
			"lower(eAleatorizacion.descripcion) like concat(lower(#{eAleatorizacionList_ensayo.eAleatorizacion.descripcion}),'%')", };

	private EAleatorizacion_ensayo eAleatorizacion = new EAleatorizacion_ensayo();

	public EAleatorizacionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EAleatorizacion_ensayo getEAleatorizacion() {
		return eAleatorizacion;
	}
}
