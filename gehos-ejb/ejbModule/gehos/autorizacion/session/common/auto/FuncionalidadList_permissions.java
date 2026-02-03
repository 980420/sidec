package gehos.autorizacion.session.common.auto;

import gehos.autorizacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("funcionalidadList_permissions")
public class FuncionalidadList_permissions extends EntityQuery {

	private static final String[] RESTRICTIONS = {
			"lower(funcionalidad.label) like concat(lower(#{funcionalidadList_permissions.funcionalidad.label}),'%')",
			"lower(funcionalidad.url) like concat(lower(#{funcionalidadList_permissions.funcionalidad.url}),'%')",
			"lower(funcionalidad.imagen) like concat(lower(#{funcionalidadList_permissions.funcionalidad.imagen}),'%')", };

	private Funcionalidad_permissions funcionalidad = new Funcionalidad_permissions();

	@Override
	public String getEjbql() {
		return "select funcionalidad from Funcionalidad_permissions funcionalidad";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Funcionalidad_permissions getFuncionalidad() {
		return funcionalidad;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
