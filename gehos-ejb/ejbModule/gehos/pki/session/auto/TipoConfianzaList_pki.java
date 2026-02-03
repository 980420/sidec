package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoConfianzaList_pki")
public class TipoConfianzaList_pki extends EntityQuery<TipoConfianza_pki> {

	private static final String EJBQL = "select tipoConfianza from TipoConfianza_pki tipoConfianza";

	private static final String[] RESTRICTIONS = {
			"lower(tipoConfianza.valor) like concat(lower(#{tipoConfianzaList_pki.tipoConfianza.valor}),'%')",
			"lower(tipoConfianza.descripcion) like concat(lower(#{tipoConfianzaList_pki.tipoConfianza.descripcion}),'%')", };

	private TipoConfianza_pki tipoConfianza = new TipoConfianza_pki();

	public TipoConfianzaList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoConfianza_pki getTipoConfianza() {
		return tipoConfianza;
	}
}
