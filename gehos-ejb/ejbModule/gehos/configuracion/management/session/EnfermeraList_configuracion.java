package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("enfermeraList_configuracion")
public class EnfermeraList_configuracion extends
		EntityQuery<Enfermera_configuracion> {

	private static final String EJBQL = "select enfermera from Enfermera enfermera";

	private static final String[] RESTRICTIONS = { "lower(enfermera.matriculaColegioEnfermeria) like concat(lower(#{enfermeraList.enfermera.matriculaColegioEnfermeria}),'%')", };

	private Enfermera_configuracion enfermera = new Enfermera_configuracion();

	public EnfermeraList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Enfermera_configuracion getEnfermera() {
		return enfermera;
	}
}
