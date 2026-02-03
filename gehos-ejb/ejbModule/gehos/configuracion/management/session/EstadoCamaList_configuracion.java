package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoCamaList_configuracion")
public class EstadoCamaList_configuracion extends
		EntityQuery<EstadoCama_configuracion> {

	private static final String EJBQL = "select estadoCama from EstadoCama estadoCama";

	private static final String[] RESTRICTIONS = {
			"lower(estadoCama.valor) like concat(lower(#{estadoCamaList.estadoCama.valor}),'%')",
			"lower(estadoCama.codigo) like concat(lower(#{estadoCamaList.estadoCama.codigo}),'%')", };

	private EstadoCama_configuracion estadoCama = new EstadoCama_configuracion();

	public EstadoCamaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoCama_configuracion getEstadoCama() {
		return estadoCama;
	}
}
