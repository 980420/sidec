package gehos.bitacora.session.common.auto;

import gehos.bitacora.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("trazaAccionList_bitacora")
public class TrazaAccionList_bitacora extends EntityQuery {

	private static final String[] RESTRICTIONS = { "lower(trazaAccion.accionRealizada) like concat(lower(#{trazaAccionList_bitacora.trazaAccion.accionRealizada}),'%')", };

	private TrazaAccion trazaAccion = new TrazaAccion();

	@Override
	public String getEjbql() {
		return "select trazaAccion from TrazaAccion trazaAccion";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public TrazaAccion getTrazaAccion() {
		return trazaAccion;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
