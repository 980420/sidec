package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("localidadList_configuracion")
public class LocalidadList_configuracion extends
		EntityQuery<Localidad_configuracion> {

	private static final String EJBQL = "select localidad from Localidad localidad";

	private static final String[] RESTRICTIONS = {
			"lower(localidad.valor) like concat(lower(#{localidadList.localidad.valor}),'%')",
			"lower(localidad.codigo) like concat(lower(#{localidadList.localidad.codigo}),'%')", };

	private Localidad_configuracion localidad = new Localidad_configuracion();

	public LocalidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Localidad_configuracion getLocalidad() {
		return localidad;
	}
}
