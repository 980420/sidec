package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cieSubcategoriaList_ensayo")
public class CieSubcategoriaList_ensayo extends
		EntityQuery<CieSubcategoria_ensayo> {

	private static final String EJBQL = "select cieSubcategoria from CieSubcategoria_ensayo cieSubcategoria";

	private static final String[] RESTRICTIONS = {
			"lower(cieSubcategoria.codigoSubcategoria) like concat(lower(#{cieSubcategoriaList_ensayo.cieSubcategoria.codigoSubcategoria}),'%')",
			"lower(cieSubcategoria.descripcion) like concat(lower(#{cieSubcategoriaList_ensayo.cieSubcategoria.descripcion}),'%')",
			"lower(cieSubcategoria.incluye) like concat(lower(#{cieSubcategoriaList_ensayo.cieSubcategoria.incluye}),'%')",
			"lower(cieSubcategoria.excluye) like concat(lower(#{cieSubcategoriaList_ensayo.cieSubcategoria.excluye}),'%')",
			"lower(cieSubcategoria.observaciones) like concat(lower(#{cieSubcategoriaList_ensayo.cieSubcategoria.observaciones}),'%')", };

	private CieSubcategoria_ensayo cieSubcategoria = new CieSubcategoria_ensayo();

	public CieSubcategoriaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CieSubcategoria_ensayo getCieSubcategoria() {
		return cieSubcategoria;
	}
}
