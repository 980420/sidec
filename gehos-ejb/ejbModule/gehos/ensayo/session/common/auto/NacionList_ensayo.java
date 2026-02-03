package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("nacionList_ensayo")
public class NacionList_ensayo extends EntityQuery<Nacion_ensayo> {

	private static final String EJBQL = "select nacion from Nacion_ensayo nacion";

	private static final String[] RESTRICTIONS = {
			"lower(nacion.valor) like concat(lower(#{nacionList_ensayo.nacion.valor}),'%')",
			"lower(nacion.nacionalidad) like concat(lower(#{nacionList_ensayo.nacion.nacionalidad}),'%')",
			"lower(nacion.codigo) like concat(lower(#{nacionList_ensayo.nacion.codigo}),'%')", };

	private Nacion_ensayo nacion = new Nacion_ensayo();

	public NacionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Nacion_ensayo getNacion() {
		return nacion;
	}
}
