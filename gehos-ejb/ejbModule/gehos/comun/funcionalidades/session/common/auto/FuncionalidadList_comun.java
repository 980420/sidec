package gehos.comun.funcionalidades.session.common.auto;

import gehos.comun.funcionalidades.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("funcionalidadList_comun")
public class FuncionalidadList_comun extends EntityQuery {

	private static final String[] RESTRICTIONS = {
			"lower(funcionalidad.label) like concat(lower(#{funcionalidadList_comun.funcionalidad.label}),'%')",
			"lower(funcionalidad.url) like concat(lower(#{funcionalidadList_comun.funcionalidad.url}),'%')", };

	private Funcionalidad funcionalidad = new Funcionalidad();

	@Override
	public String getEjbql() {
		return "select funcionalidad from Funcionalidad funcionalidad";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Funcionalidad getFuncionalidad() {
		return funcionalidad;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
