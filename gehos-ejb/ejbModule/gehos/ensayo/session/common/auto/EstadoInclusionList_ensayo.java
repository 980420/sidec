package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estadoInclusionList_ensayo")
public class EstadoInclusionList_ensayo extends
		EntityQuery<EstadoInclusion_ensayo> {

	private static final String EJBQL = "select estadoInclusion from EstadoInclusion_ensayo estadoInclusion";

	private static final String[] RESTRICTIONS = {
			"lower(estadoInclusion.nombre) like concat(lower(#{estadoInclusionList_ensayo.estadoInclusion.nombre}),'%')",
			"lower(estadoInclusion.descripcion) like concat(lower(#{estadoInclusionList_ensayo.estadoInclusion.descripcion}),'%')", };

	private EstadoInclusion_ensayo estadoInclusion = new EstadoInclusion_ensayo();

	public EstadoInclusionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstadoInclusion_ensayo getEstadoInclusion() {
		return estadoInclusion;
	}
}
