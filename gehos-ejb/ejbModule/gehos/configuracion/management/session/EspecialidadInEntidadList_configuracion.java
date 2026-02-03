package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("especialidadInEntidadList_configuracion")
public class EspecialidadInEntidadList_configuracion extends
		EntityQuery<EspecialidadInEntidad_configuracion> {

	private static final String EJBQL = "select especialidadInEntidad from EspecialidadInEntidad especialidadInEntidad";

	private static final String[] RESTRICTIONS = {};

	private EspecialidadInEntidad_configuracion especialidadInEntidad = new EspecialidadInEntidad_configuracion();

	public EspecialidadInEntidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EspecialidadInEntidad_configuracion getEspecialidadInEntidad() {
		return especialidadInEntidad;
	}
}
