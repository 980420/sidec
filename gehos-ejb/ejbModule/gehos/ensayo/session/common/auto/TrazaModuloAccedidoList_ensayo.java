package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("trazaModuloAccedidoList_ensayo")
public class TrazaModuloAccedidoList_ensayo extends
		EntityQuery<TrazaModuloAccedido_ensayo> {

	private static final String EJBQL = "select trazaModuloAccedido from TrazaModuloAccedido_ensayo trazaModuloAccedido";

	private static final String[] RESTRICTIONS = {};

	private TrazaModuloAccedido_ensayo trazaModuloAccedido = new TrazaModuloAccedido_ensayo();

	public TrazaModuloAccedidoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public TrazaModuloAccedido_ensayo getTrazaModuloAccedido() {
		return trazaModuloAccedido;
	}
}
