package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("referenciaList_configuracion")
public class ReferenciaList_configuracion extends
		EntityQuery<Referencia_configuracion> {

	private static final String EJBQL = "select referencia from Referencia referencia";

	private static final String[] RESTRICTIONS = {};

	private Referencia_configuracion referencia = new Referencia_configuracion();

	public ReferenciaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Referencia_configuracion getReferencia() {
		return referencia;
	}
}
