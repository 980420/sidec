package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cieCapituloList_ensayo")
public class CieCapituloList_ensayo extends EntityQuery<CieCapitulo_ensayo> {

	private static final String EJBQL = "select cieCapitulo from CieCapitulo_ensayo cieCapitulo";

	private static final String[] RESTRICTIONS = {
			"lower(cieCapitulo.codigoCapitulo) like concat(lower(#{cieCapituloList_ensayo.cieCapitulo.codigoCapitulo}),'%')",
			"lower(cieCapitulo.descripcion) like concat(lower(#{cieCapituloList_ensayo.cieCapitulo.descripcion}),'%')",
			"lower(cieCapitulo.incluye) like concat(lower(#{cieCapituloList_ensayo.cieCapitulo.incluye}),'%')",
			"lower(cieCapitulo.excluye) like concat(lower(#{cieCapituloList_ensayo.cieCapitulo.excluye}),'%')",
			"lower(cieCapitulo.observaciones) like concat(lower(#{cieCapituloList_ensayo.cieCapitulo.observaciones}),'%')", };

	private CieCapitulo_ensayo cieCapitulo = new CieCapitulo_ensayo();

	public CieCapituloList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CieCapitulo_ensayo getCieCapitulo() {
		return cieCapitulo;
	}
}
