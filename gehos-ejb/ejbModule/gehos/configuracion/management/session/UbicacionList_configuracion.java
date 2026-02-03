package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ubicacionList_configuracion")
public class UbicacionList_configuracion extends
		EntityQuery<Ubicacion_configuracion> {

	private static final String EJBQL = "select ubicacion from Ubicacion_configuracion ubicacion";

	private static final String[] RESTRICTIONS = { "lower(ubicacion.identificador) like concat(lower(#{ubicacionList.ubicacion.identificador}),'%')", };

	private Ubicacion_configuracion ubicacion = new Ubicacion_configuracion();

	public UbicacionList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Ubicacion_configuracion getUbicacion() {
		return ubicacion;
	}
}
