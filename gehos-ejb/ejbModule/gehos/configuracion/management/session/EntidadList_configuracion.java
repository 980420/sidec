package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("entidadList_configuracion")
public class EntidadList_configuracion extends
		EntityQuery<Entidad_configuracion> {

	private static final String EJBQL = "select entidad from Entidad entidad";

	private static final String[] RESTRICTIONS = {
			"lower(entidad.nombre) like concat(lower(#{entidadList.entidad.nombre}),'%')",
			"lower(entidad.direccion) like concat(lower(#{entidadList.entidad.direccion}),'%')",
			"lower(entidad.telefonos) like concat(lower(#{entidadList.entidad.telefonos}),'%')",
			"lower(entidad.fax) like concat(lower(#{entidadList.entidad.fax}),'%')",
			"lower(entidad.correo) like concat(lower(#{entidadList.entidad.correo}),'%')",
			"lower(entidad.logo) like concat(lower(#{entidadList.entidad.logo}),'%')", };

	private Entidad_configuracion entidad = new Entidad_configuracion();

	public EntidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}
}
