package gehos.configuracion.management.session;

import gehos.configuracion.management.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("camaList_configuracion")
public class CamaList_configuracion extends EntityQuery<Cama_configuracion> {

	private static final String EJBQL = "select cama from Cama cama";

	private static final String[] RESTRICTIONS = { "lower(cama.descripcion) like concat(lower(#{camaList.cama.descripcion}),'%')", };

	private Cama_configuracion cama = new Cama_configuracion();

	public CamaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Cama_configuracion getCama() {
		return cama;
	}
}
