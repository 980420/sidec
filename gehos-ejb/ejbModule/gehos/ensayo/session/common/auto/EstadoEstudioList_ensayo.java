package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoEstudioList_ensayo")
public class EstadoEstudioList_ensayo extends EntityQuery<EstadoEstudio_ensayo> {

	private static final String EJBQL = "select estadoEstudio from EstadoEstudio_ensayo estadoEstudio";

	private static final String[] RESTRICTIONS = {
			"lower(estadoEstudio.nombre) like concat(lower(#{estadoEstudioList_ensayo.estadoEstudio.nombre}),'%')",
			"lower(estadoEstudio.descripcion) like concat(lower(#{estadoEstudioList_ensayo.estadoEstudio.descripcion}),'%')", };

	private EstadoEstudio_ensayo estadoEstudio = new EstadoEstudio_ensayo();

	public EstadoEstudioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoEstudio_ensayo getEstadoEstudio() {
		return estadoEstudio;
	}
}
