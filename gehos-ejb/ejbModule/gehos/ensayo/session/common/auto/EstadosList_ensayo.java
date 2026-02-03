package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadosList_ensayo")
public class EstadosList_ensayo extends EntityQuery<Estados_ensayo> {

	private static final String EJBQL = "select estados from Estados_ensayo estados";

	private static final String[] RESTRICTIONS = {
			"lower(estados.nombre) like concat(lower(#{estadosList_ensayo.estados.nombre}),'%')",
			"lower(estados.descripcion) like concat(lower(#{estadosList_ensayo.estados.descripcion}),'%')", };

	private Estados_ensayo estados = new Estados_ensayo();

	public EstadosList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Estados_ensayo getEstados() {
		return estados;
	}
}
