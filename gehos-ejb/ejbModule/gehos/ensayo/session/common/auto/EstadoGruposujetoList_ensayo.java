package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoGruposujetoList_ensayo")
public class EstadoGruposujetoList_ensayo extends
		EntityQuery<EstadoGruposujeto_ensayo> {

	private static final String EJBQL = "select estadoGruposujeto from EstadoGruposujeto_ensayo estadoGruposujeto";

	private static final String[] RESTRICTIONS = {
			"lower(estadoGruposujeto.nombre) like concat(lower(#{estadoGruposujetoList_ensayo.estadoGruposujeto.nombre}),'%')",
			"lower(estadoGruposujeto.descripcion) like concat(lower(#{estadoGruposujetoList_ensayo.estadoGruposujeto.descripcion}),'%')", };

	private EstadoGruposujeto_ensayo estadoGruposujeto = new EstadoGruposujeto_ensayo();

	public EstadoGruposujetoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoGruposujeto_ensayo getEstadoGruposujeto() {
		return estadoGruposujeto;
	}
}
