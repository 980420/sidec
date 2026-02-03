package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("categoriaCamaList_configuracion")
public class CategoriaCamaList_configuracion extends
		EntityQuery<CategoriaCama_configuracion> {

	private static final String EJBQL = "select categoriaCama from CategoriaCama categoriaCama";

	private static final String[] RESTRICTIONS = { "lower(categoriaCama.valor) like concat(lower(#{categoriaCamaList.categoriaCama.valor}),'%')", };

	private CategoriaCama_configuracion categoriaCama = new CategoriaCama_configuracion();

	public CategoriaCamaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CategoriaCama_configuracion getCategoriaCama() {
		return categoriaCama;
	}
}
