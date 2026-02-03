package gehos.bitacora.session.common.auto;

import gehos.bitacora.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("trazaModuloAccedidoList_bitacora")
public class TrazaModuloAccedidoList_bitacora extends EntityQuery {

	private static final String[] RESTRICTIONS = {};

	private TrazaModuloAccedido trazaModuloAccedido = new TrazaModuloAccedido();

	@Override
	public String getEjbql() {
		return "select trazaModuloAccedido from TrazaModuloAccedido trazaModuloAccedido";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public TrazaModuloAccedido getTrazaModuloAccedido() {
		return trazaModuloAccedido;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
