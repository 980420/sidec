package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ctcCategoriaList_ensayo")
public class CtcCategoriaList_ensayo extends EntityQuery<CtcCategoria_ensayo> {

	private static final String EJBQL = "select ctcCategoria from CtcCategoria_ensayo ctcCategoria";

	private static final String[] RESTRICTIONS = {
			"lower(ctcCategoria.nombreCategoria) like concat(lower(#{ctcCategoriaList_ensayo.ctcCategoria.nombreCategoria}),'%')",
			"lower(ctcCategoria.descripcion) like concat(lower(#{ctcCategoriaList_ensayo.ctcCategoria.descripcion}),'%')", };

	private CtcCategoria_ensayo ctcCategoria = new CtcCategoria_ensayo();

	public CtcCategoriaList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CtcCategoria_ensayo getCtcCategoria() {
		return ctcCategoria;
	}
}
