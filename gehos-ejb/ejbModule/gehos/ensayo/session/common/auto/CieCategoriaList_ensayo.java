package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cieCategoriaList_ensayo")
public class CieCategoriaList_ensayo extends
		EntityQuery<CieCategoria_ensayo> {

	private static final String EJBQL = "select cieCategoria from CieCategoria_ensayo cieCategoria";

	private static final String[] RESTRICTIONS = {
			"lower(cieCategoria.codigoCategoria) like concat(lower(#{cieCategoriaList_ensayo.cieCategoria.codigoCategoria}),'%')",
			"lower(cieCategoria.descripcion) like concat(lower(#{cieCategoriaList_ensayo.cieCategoria.descripcion}),'%')",
			"lower(cieCategoria.incluye) like concat(lower(#{cieCategoriaList_ensayo.cieCategoria.incluye}),'%')",
			"lower(cieCategoria.excluye) like concat(lower(#{cieCategoriaList_ensayo.cieCategoria.excluye}),'%')",
			"lower(cieCategoria.observaciones) like concat(lower(#{cieCategoriaList_ensayo.cieCategoria.observaciones}),'%')", };

	private CieCategoria_ensayo cieCategoria = new CieCategoria_ensayo();

	public CieCategoriaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CieCategoria_ensayo getCieCategoria() {
		return cieCategoria;
	}
}
