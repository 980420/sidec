package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("nacionList_configuracion")
public class NacionList_configuracion extends EntityQuery<Nacion_configuracion> {

	private static final String EJBQL = "select nacion from Nacion nacion";

	private static final String[] RESTRICTIONS = {
			"lower(nacion.valor) like concat(lower(#{nacionList.nacion.valor}),'%')",
			"lower(nacion.nacionalidad) like concat(lower(#{nacionList.nacion.nacionalidad}),'%')",
			"lower(nacion.codigo) like concat(lower(#{nacionList.nacion.codigo}),'%')", };

	private Nacion_configuracion nacion = new Nacion_configuracion();

	public NacionList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Nacion_configuracion getNacion() {
		return nacion;
	}
}
