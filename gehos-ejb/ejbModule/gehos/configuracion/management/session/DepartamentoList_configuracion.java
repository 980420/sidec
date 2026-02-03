package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("departamentoList_configuracion")
public class DepartamentoList_configuracion extends
		EntityQuery<Departamento_configuracion> {

	private static final String EJBQL = "select departamento from Departamento departamento";

	private static final String[] RESTRICTIONS = { "lower(departamento.nombre) like concat(lower(#{departamentoList.departamento.nombre}),'%')", };

	private Departamento_configuracion departamento = new Departamento_configuracion();

	public DepartamentoList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Departamento_configuracion getDepartamento() {
		return departamento;
	}
}
