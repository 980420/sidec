package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("poblacionAreaInfluenciaList_configuracion")
public class PoblacionAreaInfluenciaList_configuracion extends
		EntityQuery<PoblacionAreaInfluencia_configuracion> {

	private static final String EJBQL = "select poblacionAreaInfluencia from PoblacionAreaInfluencia poblacionAreaInfluencia";

	private static final String[] RESTRICTIONS = {};

	private PoblacionAreaInfluencia_configuracion poblacionAreaInfluencia = new PoblacionAreaInfluencia_configuracion();

	public PoblacionAreaInfluenciaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PoblacionAreaInfluencia_configuracion getPoblacionAreaInfluencia() {
		return poblacionAreaInfluencia;
	}
}
