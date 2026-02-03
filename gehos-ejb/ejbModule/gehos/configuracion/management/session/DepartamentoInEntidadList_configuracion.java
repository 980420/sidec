package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("departamentoInEntidadList_configuracion")
public class DepartamentoInEntidadList_configuracion extends
		EntityQuery<DepartamentoInEntidad_configuracion> {

	private static final String EJBQL = "select departamentoInEntidad from DepartamentoInEntidad departamentoInEntidad";

	private static final String[] RESTRICTIONS = {};

	private DepartamentoInEntidad_configuracion departamentoInEntidad = new DepartamentoInEntidad_configuracion();

	public DepartamentoInEntidadList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DepartamentoInEntidad_configuracion getDepartamentoInEntidad() {
		return departamentoInEntidad;
	}
}
