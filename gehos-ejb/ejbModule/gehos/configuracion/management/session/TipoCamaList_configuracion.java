package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("tipoCamaList_configuracion")
public class TipoCamaList_configuracion extends
		EntityQuery<TipoCama_configuracion> {

	private static final String EJBQL = "select tipoCama from TipoCama tipoCama";

	private static final String[] RESTRICTIONS = {
			"lower(tipoCama.valor) like concat(lower(#{tipoCamaList.tipoCama.valor}),'%')",
			"lower(tipoCama.codigo) like concat(lower(#{tipoCamaList.tipoCama.codigo}),'%')", };

	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();

	public TipoCamaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}
}
