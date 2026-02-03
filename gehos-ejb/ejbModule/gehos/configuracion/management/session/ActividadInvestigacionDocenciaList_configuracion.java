package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("actividadInvestigacionDocenciaList_configuracion")
public class ActividadInvestigacionDocenciaList_configuracion extends
		EntityQuery<ActividadInvestigacionDocencia_configuracion> {

	private static final String EJBQL = "select actividadInvestigacionDocencia from ActividadInvestigacionDocencia actividadInvestigacionDocencia";

	private static final String[] RESTRICTIONS = { "lower(actividadInvestigacionDocencia.valor) like concat(lower(#{actividadInvestigacionDocenciaList.actividadInvestigacionDocencia.valor}),'%')", };

	private ActividadInvestigacionDocencia_configuracion actividadInvestigacionDocencia = new ActividadInvestigacionDocencia_configuracion();

	public ActividadInvestigacionDocenciaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ActividadInvestigacionDocencia_configuracion getActividadInvestigacionDocencia() {
		return actividadInvestigacionDocencia;
	}
}
