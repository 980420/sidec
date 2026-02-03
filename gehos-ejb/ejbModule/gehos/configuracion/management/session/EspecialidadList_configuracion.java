package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("especialidadList_configuracion")
public class EspecialidadList_configuracion extends
		EntityQuery<Especialidad_configuracion> {

	private static final String EJBQL = "select especialidad from Especialidad especialidad";

	private static final String[] RESTRICTIONS = {
			"lower(especialidad.nombre) like concat(lower(#{especialidadList.especialidad.nombre}),'%')",
			"lower(especialidad.codigo) like concat(lower(#{especialidadList.especialidad.codigo}),'%')", };

	private Especialidad_configuracion especialidad = new Especialidad_configuracion();

	public EspecialidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Especialidad_configuracion getEspecialidad() {
		return especialidad;
	}
}
