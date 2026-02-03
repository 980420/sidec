package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoSujetoList_ensayo")
public class EstadoSujetoList_ensayo extends EntityQuery<EstadoSujeto_ensayo> {

	private static final String EJBQL = "select estadoSujeto from EstadoSujeto_ensayo estadoSujeto";

	private static final String[] RESTRICTIONS = {
			"lower(estadoSujeto.nombre) like concat(lower(#{estadoSujetoList_ensayo.estadoSujeto.nombre}),'%')",
			"lower(estadoSujeto.descripcion) like concat(lower(#{estadoSujetoList_ensayo.estadoSujeto.descripcion}),'%')", };

	private EstadoSujeto_ensayo estadoSujeto = new EstadoSujeto_ensayo();

	public EstadoSujetoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoSujeto_ensayo getEstadoSujeto() {
		return estadoSujeto;
	}
}
