package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoList_ensayo")
public class EstadoList_ensayo extends EntityQuery<Estado_ensayo> {

	private static final String EJBQL = "select estado from Estado_ensayo estado";

	private static final String[] RESTRICTIONS = {
			"lower(estado.valor) like concat(lower(#{estadoList_ensayo.estado.valor}),'%')",
			"lower(estado.codigo) like concat(lower(#{estadoList_ensayo.estado.codigo}),'%')", };

	private Estado_ensayo estado = new Estado_ensayo();

	public EstadoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Estado_ensayo getEstado() {
		return estado;
	}
}
