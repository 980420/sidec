package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eSeleccionList_ensayo")
public class ESeleccionList_ensayo extends EntityQuery<ESeleccion_ensayo> {

	private static final String EJBQL = "select eSeleccion from ESeleccion_ensayo eSeleccion";

	private static final String[] RESTRICTIONS = {
			"lower(eSeleccion.nombre) like concat(lower(#{eSeleccionList_ensayo.eSeleccion.nombre}),'%')",
			"lower(eSeleccion.descripcion) like concat(lower(#{eSeleccionList_ensayo.eSeleccion.descripcion}),'%')", };

	private ESeleccion_ensayo eSeleccion = new ESeleccion_ensayo();

	public ESeleccionList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ESeleccion_ensayo getESeleccion() {
		return eSeleccion;
	}
}
