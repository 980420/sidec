package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoList_configuracion")
public class EstadoList_configuracion extends EntityQuery<Estado_configuracion> {

	private static final String EJBQL = "select estado from Estado estado";

	private static final String[] RESTRICTIONS = {
			"lower(estado.valor) like concat(lower(#{estadoList.estado.valor}),'%')",
			"lower(estado.codigo) like concat(lower(#{estadoList.estado.codigo}),'%')", };

	private Estado_configuracion estado = new Estado_configuracion();

	public EstadoList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Estado_configuracion getEstado() {
		return estado;
	}
}
