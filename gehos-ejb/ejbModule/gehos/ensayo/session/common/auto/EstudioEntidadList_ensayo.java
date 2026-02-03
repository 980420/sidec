package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estudioEntidadList_ensayo")
public class EstudioEntidadList_ensayo extends
		EntityQuery<EstudioEntidad_ensayo> {

	private static final String EJBQL = "select estudioEntidad from EstudioEntidad_ensayo estudioEntidad";

	private static final String[] RESTRICTIONS = {};

	private EstudioEntidad_ensayo estudioEntidad = new EstudioEntidad_ensayo();

	public EstudioEntidadList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstudioEntidad_ensayo getEstudioEntidad() {
		return estudioEntidad;
	}
}
