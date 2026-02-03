package gehos.bitacora.session.common.auto;

import gehos.bitacora.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("moduloList_bitacora")
public class ModuloList_bitacora extends EntityQuery {

	private static final String[] RESTRICTIONS = {
			"lower(modulo.nombre) like concat(lower(#{moduloList_bitacora.modulo.nombre}),'%')",
			"lower(modulo.imagen) like concat(lower(#{moduloList_bitacora.modulo.imagen}),'%')",
			"lower(modulo.homeUrl) like concat(lower(#{moduloList_bitacora.modulo.homeUrl}),'%')", };

	private Funcionalidad_Bitacora modulo = new Funcionalidad_Bitacora();

	@Override
	public String getEjbql() {
		return "from Funcionalidad_Bitacora  modulo";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Funcionalidad_Bitacora getModulo() {
		return modulo;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
