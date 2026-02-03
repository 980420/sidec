package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("funcionalidadList_configuracion")
public class FuncionalidadList_configuracion extends
		EntityQuery<Funcionalidad_configuracion> {

	private static final String EJBQL = "select funcionalidad from Funcionalidad funcionalidad";

	private static final String[] RESTRICTIONS = {
			"lower(funcionalidad.label) like concat(lower(#{funcionalidadList.funcionalidad.label}),'%')",
			"lower(funcionalidad.url) like concat(lower(#{funcionalidadList.funcionalidad.url}),'%')",
			"lower(funcionalidad.imagen) like concat(lower(#{funcionalidadList.funcionalidad.imagen}),'%')",
			"lower(funcionalidad.nombre) like concat(lower(#{funcionalidadList.funcionalidad.nombre}),'%')",
			"lower(funcionalidad.codebase) like concat(lower(#{funcionalidadList.funcionalidad.codebase}),'%')",
			"lower(funcionalidad.descripcion) like concat(lower(#{funcionalidadList.funcionalidad.descripcion}),'%')",
			"lower(funcionalidad.grupo) like concat(lower(#{funcionalidadList.funcionalidad.grupo}),'%')", };

	private Funcionalidad_configuracion funcionalidad = new Funcionalidad_configuracion();

	public FuncionalidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Funcionalidad_configuracion getFuncionalidad() {
		return funcionalidad;
	}
}
